package com.example.administrator.rocking.utils;

import com.example.administrator.rocking.bean.HeartBean;
import com.example.administrator.rocking.bean.WeightBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/27.
 * 解析json工具类
 */
public class ParseUtil {


    //解析菜单

    public static List<HeartBean> parseHeart(String json) {
        List<HeartBean> heartBeans = new ArrayList<HeartBean>();

        try {
            JSONObject data = new JSONObject(json);

            JSONArray arrary = data.getJSONArray("list");
            for (int i = 0;i < arrary.length();i++) {

                JSONObject object = arrary.getJSONObject(i);

                HeartBean heartBean = new HeartBean();
                heartBean.id = object.getInt("heartID");
                heartBean.hdate = object.getString("heartDate");
                heartBean.htime = object.getString("heartTime");

                heartBeans.add(heartBean);


            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }



        return heartBeans;
    }

    //解析

    public static List<WeightBean> parseWeight(String json) {
        List<WeightBean> heartBeans = new ArrayList<WeightBean>();

        try {
            JSONObject data = new JSONObject(json);

            JSONArray arrary = data.getJSONArray("data");
            for (int i = 0;i < arrary.length();i++) {

                JSONObject object = arrary.getJSONObject(i);

                WeightBean weightBean = new WeightBean();
                weightBean.id = object.getInt("weightId");
                weightBean.data = object.getString("weightData");
                weightBean.date = object.getString("weightDate");

                heartBeans.add(weightBean);


            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }



        return heartBeans;
    }



}
