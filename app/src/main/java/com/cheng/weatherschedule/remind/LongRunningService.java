package com.cheng.weatherschedule.remind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.cheng.weatherschedule.bean.Plan;
import com.cheng.weatherschedule.dao.PlanDao;
import com.cheng.weatherschedule.daoImpl.PlanDaoImpl;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by cheng on 2016/9/11 0011.
 */
public class LongRunningService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //删除计划的时候会传入要删除计划的id
        int deleteId=intent.getIntExtra("id",-1);//删除单条计划
        Log.e("cheng","**************intent id"+deleteId);
        List<Integer> deleteIds= (List<Integer>) intent.getSerializableExtra("ids");
//        if(deleteId!=-1){
//            if(deleteIds==null){
//                deleteIds=new ArrayList<>();
//            }
//            deleteIds.add(deleteId);
//        }
        //获取数据库中所有计划
        PlanDao planDao=new PlanDaoImpl(LongRunningService.this);
        List<Plan> plans=planDao.findAll();
        Calendar calendar=Calendar.getInstance();
        //获取当前日期
        int nowYear=calendar.get(Calendar.YEAR);
        int nowMonth=calendar.get(Calendar.MONTH)+1;//月份从零开始，要加1
        int nowDay=calendar.get(Calendar.DAY_OF_MONTH);
        String date=nowYear+"-"+nowMonth+"-"+nowDay;
        if(plans!=null){
            for(Plan plan:plans){
                if(date.equals(plan.getDate())){//给今天的计划设闹钟
                    String remindTime=plan.getRemindTime();
                    int hour=Integer.parseInt(remindTime.split(":")[0]);
                    int minutes=Integer.parseInt(remindTime.split(":")[1]);
                    int id=plan.getId();
                    startRemind(hour,minutes,id,deleteId,deleteIds);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在Service结束后关闭AlarmManager
        PlanDao planDao=new PlanDaoImpl(LongRunningService.this);
        List<Plan> plans=planDao.findAll();
        for(Plan plan:plans){
            stopRemind(plan.getId());
        }
    }

    /**
     *
     * @param hour  设定闹钟的小时
     * @param minutes 设定闹钟的分钟
     * @param id    设定多个闹钟，需要每次不一样的参数，使用plandid
     * @param  deleteIds 删除计划的时候，要删除id对应的提醒
     */
    private void startRemind(int hour, int minutes,int id,int deleteId,List<Integer> deleteIds) {
        //删除计划的时候，要删除id对应的提醒
        if(deleteIds!=null&&deleteIds.size()!=0){
            for(int deId:deleteIds){
                //Log.e("cheng","**********for"+deleteId);
                stopRemind(deId);
            }
        }
        if(deleteId!=-1){
            stopRemind(deleteId);
        }
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //得到日历实例，主要是为了下面的获取时间
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        //获取当前毫秒值
        long systemTime = System.currentTimeMillis();

        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //设置在几点提醒
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        //设置在几分提醒
        mCalendar.set(Calendar.MINUTE, minutes);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        //获取上面设置时间的毫秒值
        long selectTime = mCalendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，直接返回，不设置闹钟
        if (systemTime > selectTime) {
//            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
//            selectTime = mCalendar.getTimeInMillis();
            return;
        }
        Intent i = new Intent(this, AlarmReceiver.class);

        //PendingIntent pi = PendingIntent.getBroadcast(this,id, i, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pi = PendingIntent.getBroadcast(this,id, i, 0);
        //ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机算起(相对时间），并且会唤醒CPU。
        //manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), pi);
        //第一个参数是AlarmManager.RTC_WAKEUP时,当系统时间大于设定的selectTime,执行pi
        manager.set(AlarmManager.RTC_WAKEUP, selectTime, pi);

    }

    /**
     * 关闭提醒
     */
    private void stopRemind(int deleteId) {
        Log.e("cheng","**********stopRemind"+deleteId);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, deleteId,
                intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
        Toast.makeText(this, "关闭了提醒",Toast.LENGTH_SHORT).show();
    }
}
