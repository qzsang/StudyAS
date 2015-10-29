package com.example.administrator.rocking.bean;

/**
 * Created by Administrator on 2015/10/27.
 */
public class HeartBean {
    public Integer hid;
    public String hdate;
    public String htime;

    public Integer getHid() {
        return hid;
    }

    public void setHid(Integer hid) {
        this.hid = hid;
    }

    public String getHdate() {
        return hdate;
    }

    public void setHdate(String hdate) {
        this.hdate = hdate;
    }

    public String getHtime() {
        return htime;
    }

    public void setHtime(String htime) {
        this.htime = htime;
    }

    @Override
    public String toString() {
        return "HeartBean{" +
                "hid=" + hid +
                ", hdate='" + hdate + '\'' +
                ", htime='" + htime + '\'' +
                '}';
    }
}
