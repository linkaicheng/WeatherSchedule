package com.cheng.weatherschedule.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建数据库，保存查询的城市历史
 * Created by cheng on 2016/9/7.
 */
public class CityHistory extends SQLiteOpenHelper {
    private static final String DATABASENAME="cityHistory.db";
    private static final int VERSION=1;

public CityHistory(Context context){
    super(context,DATABASENAME,null,VERSION);

}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table cities(id integer primary key autoincrement,rec text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="drop table if exist cities";
        db.execSQL(sql);
        this.onCreate(db);
    }
}
