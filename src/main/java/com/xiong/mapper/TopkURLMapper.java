package com.xiong.mapper;

import com.xiong.bean.FlowBean;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class TopkURLMapper extends Mapper<LongWritable, Text, Text, FlowBean> {
    private FlowBean flowBean = new FlowBean();
    private Text text = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] fields = StringUtils.split(line, "\t");
        try {
            if (fields.length > 32 && StringUtils.isNotEmpty(fields[26]) && fields[26].startsWith("http")) {
                String url = fields[26];
                long up_flow = Long.parseLong(fields[30]);
                long d_flow = Long.parseLong(fields[31]);
                text.set(url);
                flowBean.set("", up_flow, d_flow);
                context.write(text, flowBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
