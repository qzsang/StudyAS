package com.example.administrator.rocking.bean;

/**
 * Created by Administrator on 2015/12/3.
 */
public class UserBean {
    private int id;
    private String username;
    private String pwd;

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
