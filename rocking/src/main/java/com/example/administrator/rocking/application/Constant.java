package com.example.administrator.rocking.application;

/**
 * Created by Administrator on 2015/10/27.
 */
public class Constant {
    static public final String serverUrl = "http://192.168.191.1:8080/Rocking/";//服务器地址
    static public final String heartData = serverUrl + "system/heartTextAction_list.do";//心率接
    static public final String weightData = serverUrl + "system/weightTextAction_list.do";//重力接
    static public final String login = serverUrl + "system/testAction_login.do";//登录接口
    static public String username = "admin";
}
