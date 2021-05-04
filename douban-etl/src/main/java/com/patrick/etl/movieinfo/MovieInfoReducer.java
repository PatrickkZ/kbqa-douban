package com.patrick.etl.movieinfo;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MovieInfoReducer extends Reducer<Text, Text, Text, NullWritable> {
    private Text outKey = new Text();
    /**
     * map程序结果自动按电影id进行了合并，因此只要取出第一个结果就可以实现数据去重效果
     * @param key 电影id
     * @param values 对应的json(对于重复爬取的数据可能有多个json行)
     * @param context 上下文
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // 选择第一个json
        for(Text value: values){
            outKey.set(value);
            break;
        }
        context.write(outKey, NullWritable.get());
    }
}
