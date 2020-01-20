package com.xiong.bean;

import java.io.Serializable;
import java.util.Date;

public class Equipment implements Serializable {
    private int id;
    private String equStatue;
    private String equName;
    private Date startUpTime;

    public Equipment() {

    }

    public Equipment(int id, String equStatue, String equName, Date startUpTime) {
        this.id = id;
        this.equStatue = equStatue;
        this.equName = equName;
        this.startUpTime = startUpTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEquStatue() {
        return equStatue;
    }

    public void setEquStatue(String equStatue) {
        this.equStatue = equStatue;
    }

    public String getEquName() {
        return equName;
    }

    public void setEquName(String equName) {
        this.equName = equName;
    }

    public Date getStartUpTime() {
        return startUpTime;
    }

    public void setStartUpTime(Date startUpTime) {
        this.startUpTime = startUpTime;
    }
}
