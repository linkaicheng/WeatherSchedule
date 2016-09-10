package com.cheng.weatherschedule.daoImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cheng.weatherschedule.bean.Plan;
import com.cheng.weatherschedule.dao.PlanDao;
import com.cheng.weatherschedule.db.PlanHelper;

import java.util.List;

/**
 * Created by cheng on 2016/9/9 0009.
 */
public class PlanDaoImpl implements PlanDao {

    private PlanHelper helper;
    public PlanDaoImpl(Context context){
        helper=new PlanHelper(context);
    }
    @Override
    public long addPlan(Plan plan) {
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("title",plan.getTitle());
        values.put("date",plan.getDate());
        values.put("place",plan.getPlace());
        values.put("time",plan.getTime());
        values.put("remindTime",plan.getRemindTime());
        values.put("explain",plan.getExplain());
        long count=db.insert("plans",null,values);
        db.close();
        helper.close();
        return count;
    }

    @Override
    public int deletePlan(int id) {
        SQLiteDatabase db=helper.getWritableDatabase();
        int count=db.delete("plans","id=?",new String[]{String.valueOf(id)});
        db.close();
        return count;
    }

    @Override
    public int deletePlanByDate(String date) {
        SQLiteDatabase db=helper.getWritableDatabase();
        int count=db.delete("plans","date=?",new String[]{date});
        db.close();
        return count;
    }

    @Override
    public int updatePlan(Plan plan) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",plan.getTitle());
     values.put("date",plan.getDate());
        values.put("explain",plan.getExplain());
        values.put("place",plan.getPlace());
        values.put("remindTime",plan.getRemindTime());
        values.put("time",plan.getTime());
        int count = db.update("plans", values, "id=?", new String[]{String.valueOf(plan.getId())});
        db.close();
        return count;

    }

    @Override
    public Plan findById(int id) {
        SQLiteDatabase db=helper.getReadableDatabase();
        Plan plan=null;
        String sql="select * from plans where id=?";
        Cursor cursor=db.rawQuery(sql,new String[]{String.valueOf(id)});
        if(cursor.moveToNext()){
            plan=new Plan();
            plan.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            plan.setExplain(cursor.getString(cursor.getColumnIndex("explain")));
            plan.setPlace(cursor.getString(cursor.getColumnIndex("place")));
            plan.setRemindTime(cursor.getString(cursor.getColumnIndex("remindTime")));
            plan.setTime(cursor.getString(cursor.getColumnIndex("time")));
        }

        return plan;
    }

    @Override
    public List<Plan> findAll() {
        return null;
    }
}
