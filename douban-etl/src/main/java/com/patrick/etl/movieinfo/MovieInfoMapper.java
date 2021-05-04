package com.patrick.etl.movieinfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieInfoMapper extends Mapper<LongWritable, Text, Text, Text> {
    private Text outKey = new Text();
    private Text outValue = new Text();

    /**
     * 对每一行的json字符串进行处理
     * @param key 输入偏移量
     * @param value 对应的json字符串
     * @param context 联系mapper和reducer
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 获取一行json字符串
        String line = value.toString();

        JSONObject jsonObject = JSON.parseObject(line, Feature.OrderedField);
        String movieID = (String) jsonObject.get("id");
        String movieName = (String) jsonObject.get("name");
        // 非空检查
        if(movieID == null || movieName == null){
            return;
        }
        // 对该json字符串进行相应的处理
        // 1. 爬取的电影名格式都形如(肖申克的救赎 The Shawshank Redemption)，需要去掉后面的英文名，或者如果只有英文名那就直接保留英文
        // 2. 将电影人物中的链接去掉，并加一个id字段
        parseName(jsonObject);
        parsePerson(jsonObject);

        outKey.set(movieID);
        outValue.set(jsonObject.toString());

        // 写出map结果，key为电影id，value为处理后的字符串
        context.write(outKey, outValue);
    }

    private void parsePerson(JSONObject jsonObject) {
        // 处理导演信息
        JSONArray directors = jsonObject.getJSONArray("directors");
        for(int i=0;i<directors.size();i++){
            JSONObject director = directors.getJSONObject(i);
            director.put("id", ((String)director.get("href")).replace("celebrity", "").replace("/", ""));
            director.remove("href");
        }

        // 处理编剧信息
        JSONArray writers = jsonObject.getJSONArray("writers");
        for(int i=0;i<writers.size();i++){
            JSONObject writer = writers.getJSONObject(i);
            writer.put("id", ((String)writer.get("href")).replace("celebrity", "").replace("/", ""));
            writer.remove("href");
        }

        // 处理演员信息
        JSONArray actors = jsonObject.getJSONArray("actors");
        for(int i=0;i<actors.size();i++){
            JSONObject actor = actors.getJSONObject(i);
            actor.put("id", ((String)actor.get("href")).replace("celebrity", "").replace("/", ""));
            actor.remove("href");
        }
    }

    private void parseName(JSONObject jsonObject) {
        String name = (String) jsonObject.get("name");
        String newName;
        String tempName = name.replace(" ","").replace(":","")
                .replace("-","").replace("&","").replace("'","")
                .replace("(","").replace(")","").replace("\\", "")
                .replace("\"","").replace(".","").replace("+","")
                .replace(",","").replace("/","").replace("?","");
        for(int i=0;i<10;i++){
            tempName = tempName.replace(i+"","");
        }
        // 全英文不必处理，非全英文保留中文电影名
        if(!tempName.matches("[a-zA-Z]+")){
            // 处理第*季的问题
            Matcher m = Pattern.compile("第.*季").matcher(name);
            if(m.find()){
                newName = name.split("季")[0].replace(" ", "") + "季";
                jsonObject.put("name", newName);
                return;
            }
            // 处理第*部的问题
            m = Pattern.compile("第.*部").matcher(name);
            if(m.find()){
                newName = name.split("部")[0].replace(" ","") + "部";
                jsonObject.put("name", newName);
                return;
            }
            newName = name.split(" ")[0].replace(" ","");
            jsonObject.put("name", newName);
        }
    }
}
