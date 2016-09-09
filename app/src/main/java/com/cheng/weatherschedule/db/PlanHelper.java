package com.cheng.weatherschedule.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * 创建数据库，保存计划
 * Created by cheng on 2016/9/9.
 */
public class PlanHelper extends SQLiteOpenHelper {
    private static final String DATABASENAME="calendar.db";
    private static final int VERSION=1;
    public PlanHelper(Context context){
        super(context,DATABASENAME,null,VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table plans(id integer primary key autoincrement,title text," +
                "place text,time text,remindTime text,explain text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="drop table if exist plans";
        db.execSQL(sql);
        this.onCreate(db);
    }
}
