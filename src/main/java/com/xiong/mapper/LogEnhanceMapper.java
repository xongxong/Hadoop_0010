package com.xiong.mapper;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.util.HashMap;

/**
 * 读入原始日志数据，抽取其中的url，查询规则库，获得该url指向的网页内容的分析结果，追加到原始日志后
 *
 * @author duanhaitao@itcast.cn
 */

// 读入原始数据 （47个字段） 时间戳 ..... destip srcip ... url .. . get 200 ...
// 抽取其中的url查询规则库得到众多的内容识别信息 网站类别，频道类别，主题词，关键词，影片名，主演，导演。。。。
// 将分析结果追加到原始日志后面
// context.write( 时间戳 ..... destip srcip ... url .. . get 200 ...
// 网站类别，频道类别，主题词，关键词，影片名，主演，导演。。。。)
// 如果某条url在规则库中查不到结果，则输出到带爬清单
// context.write( url tocrawl)
public class LogEnhanceMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
    private HashMap<String, String> ruleMap = new HashMap();

}
