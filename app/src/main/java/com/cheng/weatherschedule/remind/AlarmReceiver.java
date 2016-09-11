package com.cheng.weatherschedule.remind;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.cheng.weatherschedule.R;

/**
 * Created by cheng on 2016/9/10 0010.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        //设置通知内容并在onReceive()这个函数执行时开启
        Log.d(TAG,"=======onreceive");
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.warn);
        builder.setContentTitle("Alarm....");
        builder.setContentText("成功");
        Notification notification=builder.build();
        NotificationManager manager=(NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.notify(1200,notification);

        //再次开启LongRunningService这个服务
        Intent i = new Intent(context, LongRunningService.class);
        context.startService(i);
    }
}
