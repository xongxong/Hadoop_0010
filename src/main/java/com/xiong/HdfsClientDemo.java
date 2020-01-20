package com.xiong;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class HdfsClientDemo {

    public static void main(String[] args) {
        Configuration conf = new Configuration();
        System.out.println("Running MapReduce");
        conf.set("dfs.replication", "3");
        try {
            String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

            FileSystem fs = FileSystem.get(conf);
            System.out.println(fs.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
