package com.cheng.weatherschedule.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.LifeSuggestion;
import com.cheng.weatherschedule.bean.WeatherDaily;
import com.cheng.weatherschedule.db.CityHistory;
import com.cheng.weatherschedule.utils.DensityUtil;
import com.cheng.weatherschedule.utils.NetUtils;
import com.cheng.weatherschedule.utils.URLConnManager;
import com.cheng.weatherschedule.weather.ChangeCityActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 2016/9/5.
 */
public class WeatherFragment extends Fragment {

    private List<Map<String, Object>> data = null;
    private ProgressDialog pDialog;
    private String action;
    private TextView tvCity;
    private ImageView imWeather, imWeatherToday, imWeatherTomorrow, imWeaAfterTomo;
    private TextView tvWeather, tvTemp, tvWindDirection, tvUv, tvWindScale, tvTempToday, tvTextToday, tvTempTomorrow, tvTextTomorrow, tvTempAfterTomo, tvTextAfterTomo, tvDate;
    //添加城市
    private TextView tvChangeCity;
    //保存天气代码
    private int codeDayId;
    //左上角菜单
    private ImageView immenu;
    //菜单选项
    private TextView tvShare, tvSetDefault, tvOut,tvSuggestion;
    // 菜单
    PopupWindow popupWindow;
    //保存当前的城市名
    private String cityName;
    private String suggestion;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    /**
     * 让fragment可见时才去网络请求
     *
     * @param// isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            //每次启动显示的城市为之前（切换城市）所设定的城市，从数据库中获取
            CityHistory helper = new CityHistory(getActivity());
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query("cities", null, null, null, null, null, "id desc", "1");
            if (cursor.moveToNext()) {
                try {
                    cityName = cursor.getString(cursor.getColumnIndex("rec"));
                    cityName = URLEncoder.encode(cityName, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                cityName = "beijing";
            }
            data = new ArrayList<>();
            String url = null;
            url = "https://api.thinkpage.cn/v3/weather/daily.json?key=r3b44bu4dzqzadlr&language=zh-Hans&unit=c&start=0&days=5"
                    + "&location=" + cityName;
            action = "daily";
        //网络是否可用
        if(!(NetUtils.check(getActivity()))){
            Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }
            new WeatherTask().execute(url);
        }
    }

    private void initView() {
        tvCity = (TextView) getActivity().findViewById(R.id.tvCity);
        imWeather = (ImageView) getActivity().findViewById(R.id.imWeather);
        imWeatherToday = (ImageView) getActivity().findViewById(R.id.imWeatherToday);
        imWeatherTomorrow = (ImageView) getActivity().findViewById(R.id.imWeatherTomorrow);
        imWeaAfterTomo = (ImageView) getActivity().findViewById(R.id.imWeaAfterTomo);
        immenu = (ImageView) getActivity().findViewById(R.id.immenu);
        tvChangeCity = (TextView) getActivity().findViewById(R.id.tvChangeCity);


        tvWeather = (TextView) getActivity().findViewById(R.id.tvWeather);
        tvTemp = (TextView) getActivity().findViewById(R.id.tvTemp);
        tvWindDirection = (TextView) getActivity().findViewById(R.id.tvWindDirection);
        tvUv = (TextView) getActivity().findViewById(R.id.tvUv);
        tvWindScale = (TextView) getActivity().findViewById(R.id.tvWindScale);
        tvTempToday = (TextView) getActivity().findViewById(R.id.tvTempToday);
        tvTextToday = (TextView) getActivity().findViewById(R.id.tvTextToday);
        tvTempTomorrow = (TextView) getActivity().findViewById(R.id.tvTempTomorrow);
        tvTextTomorrow = (TextView) getActivity().findViewById(R.id.tvTextTomorrow);
        tvTempAfterTomo = (TextView) getActivity().findViewById(R.id.tvTempAfterTomo);
        tvTextAfterTomo = (TextView) getActivity().findViewById(R.id.tvTextAfterTomo);
        tvDate = (TextView) getActivity().findViewById(R.id.tvDate);
        //右上角添加城市
        tvChangeCity.setOnClickListener(new tvChangeCityOnCkListener());
        //左上角菜单
        immenu.setOnClickListener(new ImmenuOnCkListener());
    }

    //左上角菜单点击监听
    private class ImmenuOnCkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
                showPw();
        }
    }

    /**
     * 显示菜单
     */
    private void showPw() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_item, null);
        tvShare = (TextView) contentView.findViewById(R.id.tvShare);
        tvSetDefault = (TextView) contentView.findViewById(R.id.tvSetDefault);
        tvOut = (TextView) contentView.findViewById(R.id.tvOut);
        tvSuggestion = (TextView) contentView.findViewById(R.id.tvSuggestion);
        tvShare.setOnClickListener(new MenuItemOnCkListener());
        tvSetDefault.setOnClickListener(new MenuItemOnCkListener());
        tvOut.setOnClickListener(new MenuItemOnCkListener());
        tvSuggestion.setOnClickListener(new MenuItemOnCkListener());
        popupWindow = new PopupWindow(contentView);
        int dip = 100;
        int px = DensityUtil.dip2px(getActivity(), dip);
        popupWindow.setWidth(px);
        px = DensityUtil.dip2px(getActivity(), 160);
        popupWindow.setHeight(px);

        //背景色，透明
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(immenu);

    }

    /**
     * 左上角菜单选项的点击监听
     */
    private class MenuItemOnCkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvShare:
                    popupWindow.dismiss();
                    //网络是否可用
                    if(!(NetUtils.check(getActivity()))){
                        Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendMessage();
                    break;
                case R.id.tvSetDefault:
                    popupWindow.dismiss();
                    //将查询的城市保存到数据库
                    CityHistory helper = new CityHistory(getActivity());
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("rec", tvCity.getText().toString());
                    db.insert("cities", null, values);
                    db.close();
                    helper.close();
                    Toast.makeText(getActivity(), "已将"+tvCity.getText().toString()+"设为默认城市", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tvSuggestion:
                    popupWindow.dismiss();
                    //用一个对话框显示出行建议
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("建议");
                    TextView tvSuggestion=new TextView(getActivity());
                    tvSuggestion.setPadding(0,50,0,0);
                    tvSuggestion.setText(suggestion);
                    builder.setView(tvSuggestion);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    break;
                case R.id.tvOut:
                    Toast.makeText(getActivity(), "退出", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    popupWindow.dismiss();
                    break;
            }

        }
    }

    //以短信的形式分享天气信息
    private void sendMessage() {
        //读通读录权限（兼容android M（api23)需要这段代码）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasWriteContactsPermission = getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS);
            if (hasWriteContactsPermission
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 123);
            }
        }
        //从通讯录中拿到用户名和电话
        //获得内容解析器
        ContentResolver cr = getActivity().getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //查询联系人数据
        Cursor cursor = cr.query(uri, null, null, null, null);
        List<String> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));//联系人姓名
            String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));//联系人号码
            contacts.add(name + "(" + phone + ")");
        }
        cursor.close();
        if (contacts.size() == 0) {
            new AlertDialog.Builder(getActivity()).setTitle("请选择")
                    .setMessage("通讯录为空")
                    .setNegativeButton("取消", null).show();
        } else {
            final String[] items = new String[contacts.size()];
            contacts.toArray(items);
            new AlertDialog.Builder(getActivity())
                    .setTitle("请选择")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String select = items[which];
                            String phone = select.substring(select.indexOf("(") + 1, select.indexOf(")"));
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            String sms="城市："+tvCity.getText().toString()
                                    +"，温度："+tvTemp.getText().toString()
                                    +"，天气："+tvWeather.getText().toString()
                                    +"，"+tvWindDirection.getText().toString()
                                    +"，"+tvUv.getText().toString()
                                    +"("+tvDate.getText().toString()+")";
                            intent.setData(Uri.parse( "smsto:"+phone));
                            //携带额外数据
                            intent.putExtra( "sms_body", sms);
                            startActivity(intent);

                        }
                    }).setNegativeButton("取消", null).show();
        }
    }

    //点击加号，到添加城市界面
    private class tvChangeCityOnCkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ChangeCityActivity.class);
            intent.putExtra("city", tvCity.getText().toString());
            intent.putExtra("codeDay", codeDayId);
            intent.putExtra("temp", tvTemp.getText().toString());
            intent.putExtra("weather", tvWeather.getText().toString());
            startActivityForResult(intent, 1);
        }
    }

    /**
     * 处理选择城市界面回传的城市名,根据回传的城市名更新界面
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                try {
                   cityName = data.getStringExtra("city");

                    cityName = URLEncoder.encode(cityName, "utf-8");
                    String url = "https://api.thinkpage.cn/v3/weather/daily.json?key=r3b44bu4dzqzadlr&language=zh-Hans&unit=c&start=0&days=5";
                    url = url + "&location=" + cityName;
                    action = "daily";
                    //网络是否可用
                    if(!(NetUtils.check(getActivity()))){
                        Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new WeatherTask().execute(url);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private class WeatherTask extends AsyncTask<String, Void, Object> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //显示进度对话框
            pDialog = ProgressDialog.show(getActivity(), null, "正从服务器获取数据，请稍候", false, true);
        }

        @Override
        protected Object doInBackground(String... params) {
            String result = null;
            InputStream inputStream = null;
            HttpURLConnection conn = null;
            String url = params[0];
            try {
                //获取连接
                conn = URLConnManager.getHttpURLConnection(url);
                //连接
                conn.connect();
                //获得响应码
                int code = conn.getResponseCode();
                //Log.e("cheng","***********"+code);
                if (code == 200) {//连接成功
                    //获取服务器返回的数据
                    inputStream = conn.getInputStream();
                    //将输入流转成字符串
                    String response = URLConnManager.converStreamToString(inputStream);
                    // Log.e("cheng","************"+response);
                    //用Gson解析数据
                    Gson gson = new Gson();
                    if (action.equals("daily")) {
                        WeatherDaily weatherDaily = gson.fromJson(response, WeatherDaily.class);
                        return weatherDaily;
                    } else if (action.equals("suggestion")) {
                        LifeSuggestion lifeSuggestion = gson.fromJson(response, LifeSuggestion.class);
                        return lifeSuggestion;
                    }
                } else {//连接失败
                    result = "2";
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = "2";
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                result = "3";
            } finally {
                //关闭输入流和连接等资源
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            //失败时返回的是字符串result
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            LifeSuggestion lifeSuggestion = null;
            WeatherDaily weatherDaily = null;
            //关闭进度对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }
            if (result instanceof WeatherDaily) {
                weatherDaily = (WeatherDaily) result;
                if (weatherDaily == null) {
                    Toast.makeText(getActivity(), "没有查询到相关的天气信息", Toast.LENGTH_SHORT).show();
                    //查询到空，将数据清空，更新界面
                } else {
                    tvCity.setText(weatherDaily.getResults().get(0).getLocation().getName());
                    // 往data中填充数据
                    data.clear();
                    for (WeatherDaily.ResultsBean.DailyBean daily : weatherDaily.getResults().get(0).getDaily()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("date", daily.getDate());
                        row.put("codeDay", daily.getCode_day());
                        row.put("temp", daily.getLow() + "-" + daily.getHigh());
                        row.put("textDay", daily.getText_day());
                        row.put("direction", daily.getWind_direction());
                        row.put("windScale", daily.getWind_scale());
                        data.add(row);
                    }
                    String url = "https://api.thinkpage.cn/v3/life/suggestion.json?key=r3b44bu4dzqzadlr&language=zh-Hans" +
                            "&location="+cityName;
                    action = "suggestion";
                    //网络是否可用
                    if(!(NetUtils.check(getActivity()))){
                        Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new WeatherTask().execute(url);

                }
            } else if (result instanceof LifeSuggestion) {
                lifeSuggestion = (LifeSuggestion) result;
                LifeSuggestion.ResultsBean.SuggestionBean suggestionBean = lifeSuggestion.getResults().get(0).getSuggestion();
               suggestion="洗车："+suggestionBean.getCar_washing().getBrief()+"；"
                            +"运动：" +suggestionBean.getSport().getBrief()+"；"
                            +"旅游："+suggestionBean.getTravel().getBrief();

                if (data.size() != 0) {
                        data.get(0).put("uv", "紫外线：" + suggestionBean.getUv().getBrief());
                        //data.get(i).put("suggestion",suggestion);
                }
                Map<String, Object> day1 = data.get(0);
                Map<String, Object> day2 = data.get(1);
                Map<String, Object> day3 = data.get(2);
                int resId = getResource("weather" + day1.get("codeDay"));

                imWeather.setImageResource(resId);
                tvWeather.setText((String) day1.get("textDay"));
                tvTemp.setText(day1.get("temp") + "℃");
                tvWindDirection.setText("风向："+ day1.get("direction"));
                tvDate.setText((String) day1.get("date"));
                tvUv.setText((String) day1.get("uv"));
                tvWindScale.setText("风力等级：" + day1.get("windScale") + "级");
                resId = getResource("s" + day1.get("codeDay"));
                codeDayId = resId;
                imWeatherToday.setImageResource(resId);
                resId = getResource("s" + day2.get("codeDay"));
                imWeatherTomorrow.setImageResource(resId);
                resId = getResource("s" + day3.get("codeDay"));
                imWeaAfterTomo.setImageResource(resId);

                tvTempToday.setText(((String) day1.get("temp")).replace("-", "/"));
                tvTextToday.setText((String) day1.get("textDay"));

                tvTempTomorrow.setText(((String) day2.get("temp")).replace("-", "/"));
                tvTextTomorrow.setText((String) day2.get("textDay"));

                tvTempAfterTomo.setText(((String) day3.get("temp")).replace("-", "/"));
                tvTextAfterTomo.setText((String) day3.get("textDay"));

            } else if (result instanceof String) {
                if ("2".equals(result)) {
                    Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                } else if ("3".equals(result)) {
                    Toast.makeText(getActivity(), "请重新登录", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //根据文件名获取图片id
    public int getResource(String imageName) {
        Context ctx = getActivity().getBaseContext();
        int resId = getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        return resId;
    }
}
