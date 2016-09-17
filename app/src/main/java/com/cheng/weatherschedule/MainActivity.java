package com.cheng.weatherschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cheng.weatherschedule.adapter.MyFragmentAdapter;
import com.cheng.weatherschedule.fragment.CalendarFragment;
import com.cheng.weatherschedule.fragment.RemindFragment;
import com.cheng.weatherschedule.fragment.WeatherFragment;
import com.cheng.weatherschedule.remind.LongRunningService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RadioButton rbWeather,rbCalendar,rbRemind;
    private RadioGroup rgMain;
    private ViewPager vpMain;
    List<Fragment> fragments;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initViewPage();
        serviceIntent = new Intent(this, LongRunningService.class);
        //开启关闭Service
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在Activity被关闭后，关闭Service
       // stopService(serviceIntent);
    }

    private void initView(){
        rbWeather = (RadioButton)findViewById(R.id.rbWeather);
        rbCalendar = (RadioButton)findViewById(R.id.rbCalendar);
        rbRemind = (RadioButton)findViewById(R.id.rbRemind);
        rgMain = (RadioGroup)findViewById(R.id.rgMain);
        vpMain = (ViewPager)findViewById(R.id.vpMain);
        rgMain.setOnCheckedChangeListener(new MyOnCheckChangeListener());

    }
    private void initViewPage(){
        WeatherFragment weatherFragment=new WeatherFragment();
        CalendarFragment calendarFragment=new CalendarFragment();
        RemindFragment remindFragment=new RemindFragment();
        fragments=new ArrayList<>();
        fragments.add(weatherFragment);
        fragments.add(calendarFragment);
        fragments.add(remindFragment);
        vpMain.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(),fragments));
        vpMain.setCurrentItem(0);
        vpMain.addOnPageChangeListener(new MyPageChangeListener());
    }
    private class MyOnCheckChangeListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case  R.id.rbWeather:
                    vpMain.setCurrentItem(0,false);
                    break;
                case  R.id.rbCalendar:
                    vpMain.setCurrentItem(1,false);
                    break;
                case  R.id.rbRemind:
                    vpMain.setCurrentItem(2,false);
                    break;
            }
        }
    }
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case  0:
                    rgMain.check(R.id.rbWeather);
                    break;
                case  1:
                    rgMain.check(R.id.rbCalendar);
                    break;
                case  2:
                    rgMain.check(R.id.rbRemind);
                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
