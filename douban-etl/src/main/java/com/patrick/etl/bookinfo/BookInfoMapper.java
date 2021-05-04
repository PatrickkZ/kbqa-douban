package com.patrick.etl.bookinfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class BookInfoMapper extends Mapper<LongWritable, Text, Text, Text> {
    private Text outKey = new Text();
    private Text outValue = new Text();
    /**
     * 对每一行的json字符串进行处理
     * @param key 偏移量
     * @param value 对应的json字符串
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        JSONObject jsonObject = JSON.parseObject(line, Feature.OrderedField);
        String bookID = (String) jsonObject.get("id");
        String bookName = (String) jsonObject.get("name");

        if(bookID == null || bookName == null){
            return;
        }

        parsePerson(jsonObject);

        outKey.set(bookID);
        outValue.set(jsonObject.toString());
        context.write(outKey, outValue);
    }

    private void parsePerson(JSONObject jsonObject) {
        // 处理作者信息
        JSONArray authors = jsonObject.getJSONArray("author");
        for(int i=0;i<authors.size();i++){
            JSONObject author = authors.getJSONObject(i);
            author.put("id", ((String)author.get("href")).replace("author", "")
                    .replace("/", ""));
            author.remove("href");
        }

        // 处理译者信息
        JSONArray translators = jsonObject.getJSONArray("translator");
        for(int i=0;i<translators.size();i++){
            JSONObject translator = translators.getJSONObject(i);
            translator.put("id", ((String)translator.get("href")).replace("author", "")
                    .replace("/", ""));
            translator.remove("href");
        }
    }
}
