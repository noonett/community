package com.nowcoder.community;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TempTests {
    public static void main(String[] args) {
        List<Integer> idList = new ArrayList<>();
        idList.add(1); idList.add(2);
        JSONObject json = new JSONObject();
        JSONArray jsonObject = new JSONArray();
        jsonObject.addAll(idList);
        System.out.println(jsonObject.toJSONString());
        List<Integer> getList = JSONObject.parseObject(jsonObject.toJSONString(), ArrayList.class);
        for (int i: getList){
            System.out.println(i);
        }
    }
}
