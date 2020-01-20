package com.xiong.weiboCount;

import com.xiong.Test;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WeiboInputFormat extends FileInputFormat<Text, Weibo> {
    @Override
    public RecordReader<Text, Weibo> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        //自定义WeiboRecordReader
        return new WeiboRecordReader();
    }

    public class WeiboRecordReader extends RecordReader {
        public LineReader in;
        //声明key类型
        public Text lineKey = new Text();
        //声明value类型
        public Weibo lineValue = new Weibo();

        @Override
        public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            //获取split
            FileSplit fileSplie = (FileSplit) inputSplit;
            //获取配置
            Configuration conf = taskAttemptContext.getConfiguration();
            //分片路径
            Path file = fileSplie.getPath();
            FileSystem fileSystem = file.getFileSystem(conf);
            //打开文件
            FSDataInputStream filein = fileSystem.open(file);
            in = new LineReader(filein, conf);
        }

        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            Text line = new Text();
            int linesize = in.readLine(line);
            if (linesize == 0)
                return false;
            //通过分隔符'\t',将每行数据解析成数组
            String[] pieces = line.toString().split("\t");
            if (pieces.length != 5) {
                throw new IOException("Invalid record received");
            }
            int a, b, c;
            try {
                //粉丝
                a = Integer.valueOf(pieces[2].trim());
                //关注
                b = Integer.valueOf(pieces[3].trim());
                //微博数
                c = Integer.valueOf(pieces[4].trim());
            } catch (NumberFormatException e) {
                throw new IOException("Error parsing floating poing value in record");
            }
            lineKey.set(pieces[0]);
            lineValue.setWeibo(a, b, c);
            return true;
        }

        @Override
        public Object getCurrentKey() throws IOException, InterruptedException {
            return lineKey;
        }

        @Override
        public Object getCurrentValue() throws IOException, InterruptedException {
            return lineValue;
        }

        @Override
        public float getProgress() throws IOException, InterruptedException {
            return 0;
        }

        @Override
        public void close() throws IOException {
            if (in != null)
                in.close();
        }
    }
}
