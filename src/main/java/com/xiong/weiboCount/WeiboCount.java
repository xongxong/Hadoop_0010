package com.xiong.weiboCount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.*;

public class WeiboCount extends Configuration implements Tool {
    //tab分隔符
    private static String TAB_SEPARATOR = "\t";
    //粉丝
    private static String FAN = "fan";
    //关注
    private static String FOLLOWERS = "followers";
    //微博数
    private static String MICROBLOGS = "microblogs";

    @Override
    public void setConf(Configuration configuration) {
    }
    @Override
    public Configuration getConf() {
        return null;
    }


    public static class WeiBoMapper extends Mapper<Text, Weibo, Text, Text> {
        @Override
        protected void map(Text key, Weibo value, Context context) throws IOException, InterruptedException {
            //粉丝
            context.write(new Text(FAN), new Text(key.toString() + TAB_SEPARATOR + value.getFan()));
            //关注
            context.write(new Text(FOLLOWERS), new Text(key.toString() + TAB_SEPARATOR + value.getFollower()));
            //微博数
            context.write(new Text(MICROBLOGS), new Text(key.toString() + TAB_SEPARATOR + value.getMicroblogs()));
        }
    }

    public static class WeiBoReducer extends Reducer<Text, Text, Text, IntWritable> {
        private MultipleOutputs<Text, IntWritable> mos;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            mos = new MultipleOutputs<Text, IntWritable>(context);
        }

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Map<String, Integer> map = new HashMap<>();
            for (Text value : values) {
                String[] records = value.toString().split(TAB_SEPARATOR);
                map.put(records[0], Integer.parseInt(records[1]));
            }
            Map.Entry<String, Integer>[] entries = getSortedHashtableByValue(map);
            for (int i = 0; i < entries.length; i++) {
                mos.write(key.toString(), entries[i].getKey(), entries[i].getValue());
            }
        }

        @SuppressWarnings("unchecked")
        public static Map.Entry<String, Integer>[] getSortedHashtableByValue(Map<String, Integer> h) {
            Map.Entry<String, Integer>[] entries = (Map.Entry<String, Integer>[]) h.entrySet().toArray(new Map.Entry[0]);
            // 排序
            Arrays.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                    return entry2.getValue().compareTo(entry1.getValue());
                }
            });
            return entries;
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public int run(String[] strings) throws Exception {
        Configuration conf = new Configuration();
        //判断路径是否存在
        Path mypath = new Path(strings[1]);
        FileSystem hdfs = mypath.getFileSystem(conf);
        if (hdfs.isDirectory(mypath)) {
            hdfs.delete(mypath, true);
        }
        //构造任务
        Job job = new Job(conf, "weibo");
        //主类
        job.setJarByClass(WeiboCount.class);
        //Mapper
        job.setMapperClass(WeiBoMapper.class);
        //Mapper key输出类型
        job.setMapOutputKeyClass(Text.class);
        //Mapper value输出类型
        job.setMapOutputValueClass(Text.class);

        //Reducer
        job.setReducerClass(WeiBoReducer.class);
        //Reducer key输出类型
        job.setOutputKeyClass(Text.class);
        //Reducer value输出类型
        job.setOutputValueClass(IntWritable.class);
        //乱码问题
        job.setOutputFormatClass(TextOutputFormat.class);
        //输入路径
        FileInputFormat.addInputPath(job, new Path(strings[0]));
        //输出路径
        FileOutputFormat.setOutputPath(job, new Path(strings[1]));

        //自定义输入格式
        job.setInputFormatClass(WeiboInputFormat.class);
        //自定义文件输出格式
        MultipleOutputs.addNamedOutput(job, FAN, TextOutputFormat.class, Text.class, IntWritable.class);
        MultipleOutputs.addNamedOutput(job, FOLLOWERS, TextOutputFormat.class, Text.class, IntWritable.class);
        MultipleOutputs.addNamedOutput(job, MICROBLOGS, TextOutputFormat.class, Text.class, IntWritable.class);
        //去掉job设置outputFormatClass,改为通过LazyOutputFormat设置
        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        String[] args0 = {"C:\\Users\\HJ18031701\\Desktop\\weibo.txt", "C:\\Users\\HJ18031701\\Desktop\\out"};
        int ec = ToolRunner.run(new Configuration(), new WeiboCount(), args0);
        System.exit(ec);
    }

}
