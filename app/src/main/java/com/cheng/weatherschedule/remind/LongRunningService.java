package com.cheng.weatherschedule.remind;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.cheng.weatherschedule.R;
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
                    startRemind(hour,minutes,id);
                }
            }
        }
        //设为前台线程
       // Intent notificationIntent = new Intent(this, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentTitle("天气日程通");
        builder.setContentText("running");
        Notification noti=builder.build();
        startForeground(12346, noti);
        //手动返回START_STICKY，亲测当service因内存不足被kill，当内存又有的时候，service又被重新创建
        flags = START_STICKY;
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
        Intent sevice = new Intent(this, LongRunningService.class);
        this.startService(sevice);
        //
        stopForeground(true);
    }

    /**
     *
     * @param hour  设定闹钟的小时
     * @param minutes 设定闹钟的分钟
     * @param id    设定多个闹钟，需要每次不一样的参数，使用plandid较合适
     *
     */
    private void startRemind(int hour, int minutes,int id) {

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

        // 如果当前时间大于设置的时间，直接返回，不设置提醒
        if (systemTime > selectTime) {
            //或者第二天再提醒
            // mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            //selectTime = mCalendar.getTimeInMillis();
            return;
        }
        Intent i = new Intent(this, AlarmReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(this,id, i, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pi = PendingIntent.getBroadcast(this,id, i, 0);
        //ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机算起(相对时间），并且会唤醒CPU。
        //manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), pi);
        //第一个参数是AlarmManager.RTC_WAKEUP时,当系统时间大于设定的selectTime,执行pi
        manager.set(AlarmManager.RTC_WAKEUP, selectTime, pi);

    }

    /**
     * 关闭提醒
     */
    private void stopRemind(int deleteId) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, deleteId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
        //Toast.makeText(this,"关闭了提醒",Toast.LENGTH_SHORT).show();
    }
}
