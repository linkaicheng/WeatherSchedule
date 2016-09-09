package com.cheng.weatherschedule.dao;

import com.cheng.weatherschedule.bean.Plan;

import java.util.List;

/**
 * Created by cheng on 2016/9/9 0009.
 */
public interface PlanDao {
    //添计划
    public long addPlan(Plan plan);
    //根据id删除计划
    public int deletePlan(int id);
    //根据日期删除计划
    public int deletePlanByDate(String date);
    //修改
    public int updatePlan(Plan plan);
    //查询一条记录
    public Plan findById(int id);
    //查询所有记录
    public List<Plan> findAll();
}
