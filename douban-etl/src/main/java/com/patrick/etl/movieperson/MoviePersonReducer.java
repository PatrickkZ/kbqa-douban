package com.patrick.etl.movieperson;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MoviePersonReducer extends Reducer<Text, Text, Text, NullWritable> {
    private Text outKey = new Text();
    /**
     * map程序结果自动按人物id进行了合并，因此只要取出第一个结果就可以实现数据去重效果
     * @param key
     * @param values
     * @param context
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
