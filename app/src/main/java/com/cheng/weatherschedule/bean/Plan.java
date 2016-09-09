package com.cheng.weatherschedule.bean;

/**
 * Created by cheng on 2016/9/9 0009.
 */
public class Plan {

    private int id;
    private String title;
    private String date;
    private String place;
    private String time;
    private String remindTime;
    private String explain;

    public Plan() {

    }

    public Plan(String title, String date, String place, String time, String remindTime, String explain) {
        this.title = title;
        this.date = date;
        this.place = place;
        this.time = time;
        this.remindTime = remindTime;
        this.explain = explain;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}
