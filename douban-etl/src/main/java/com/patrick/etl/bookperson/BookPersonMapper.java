package com.patrick.etl.bookperson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class BookPersonMapper extends Mapper<LongWritable, Text, Text, Text> {
    private Text outKey = new Text();
    private Text outValue = new Text();
    /**
     * 对每一个人物json进行处理
     * @param key 偏移量
     * @param value json字符串
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        JSONObject jsonObject = JSON.parseObject(line, Feature.OrderedField);
        String personID = (String) jsonObject.get("id");
        String personName = (String) jsonObject.get("name");

        if(personID == null || personName == null){
            return;
        }
        
        parseID(jsonObject);
        parseName(jsonObject);

        outKey.set((String) jsonObject.get("id"));
        outValue.set(jsonObject.toString());
        context.write(outKey, outValue);
    }

    private void parseName(JSONObject jsonObject) {
        String name = (String) jsonObject.get("name");
        String tempName = name.replace(" ","").replace("·","");
        // 全英文不必处理，非全英文保留中文名
        if(!tempName.matches("[a-zA-Z]+")){
            jsonObject.put("name", name.split(" ")[0].replace(" ",""));
        }
    }

    private void parseID(JSONObject jsonObject) {
        String newID = ((String) jsonObject.get("id")).replace("author", "")
                .replace("/","");
        jsonObject.put("id", newID);
    }
}
