package com.cheng.weatherschedule.remind;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.Plan;
import com.cheng.weatherschedule.dao.PlanDao;
import com.cheng.weatherschedule.daoImpl.PlanDaoImpl;

import java.util.Calendar;
import java.util.List;

/**
 * Created by cheng on 2016/9/10 0010.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        //获取数据库中所有计划
        PlanDao planDao=new PlanDaoImpl(context);
        List<Plan> plans=planDao.findAll();
        Calendar calendar=Calendar.getInstance();
       String title=null;
        String text=null;
        if(plans!=null){
            for(Plan plan:plans){
                //如果计划设定的提醒时间是当前时间，获取，title和text
                if(plan.getRemindTime().equals(calendar.get(Calendar.HOUR_OF_DAY)
                        +":"+calendar.get(Calendar.MINUTE))){
                    title=plan.getTitle();
                    text=plan.getExplain();
                    //设置通知内容并在onReceive()这个函数执行时开启
                    Log.d(TAG,"=======onreceive");
                    //调用系统铃声
                    MediaPlayer mp = new MediaPlayer();
                    try {
                        mp.setDataSource(context, RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        mp.prepare();
                        mp.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //通知栏发一个通知
                    NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
                    builder.setSmallIcon(R.mipmap.warn);
                    builder.setContentTitle(title);
                    builder.setContentText(text);
                    Notification notification=builder.build();
                    NotificationManager manager=(NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
                    manager.notify(1200,notification);

                    //再次开启LongRunningService这个服务
                    Intent i = new Intent(context, LongRunningService.class);
                    context.startService(i);
                }
            }
        }

    }
}
