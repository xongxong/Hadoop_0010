package com.xiong.mapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/*
LogEnhanceOutputFormat.java：默认是TextOutputFormat，这里我需要实现将不同的结果输到不同的文件中，而不是_SUCCESS中，所以我需要重写一个format。
 */
public class LogEnhanceOutputFormat<K, V> extends FileOutputFormat<K, V> {
    @Override
    public RecordWriter<K, V> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        FileSystem fs = FileSystem.get(new Configuration());
        FSDataOutputStream enhancedOs = fs.create(new Path(""));
        FSDataOutputStream tocrawlOs = fs.create(new Path(""));
        return new LogEnhanceRecordWriter<K, V>(enhancedOs, tocrawlOs);
    }

    public static class LogEnhanceRecordWriter<K, V> extends RecordWriter<K, V> {
        private FSDataOutputStream enhancedOs = null;
        private FSDataOutputStream tocrawlOs = null;

        public LogEnhanceRecordWriter(FSDataOutputStream enhancedOs, FSDataOutputStream tocrawlOs) {

            this.enhancedOs = enhancedOs;
            this.tocrawlOs = tocrawlOs;
        }

        @Override
        public void write(K k, V v) throws IOException, InterruptedException {
            if (k.toString().contains("tocrawl")) {
                tocrawlOs.write(k.toString().getBytes());
            } else {
                enhancedOs.write(k.toString().getBytes());
            }
        }

        @Override
        public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            if (enhancedOs != null)
                enhancedOs.close();
            if (tocrawlOs != null)
                tocrawlOs.close();
        }
    }
}
