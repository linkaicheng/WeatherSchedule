package com.cheng.weatherschedule.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.LifeSuggestion;
import com.cheng.weatherschedule.bean.WeatherDaily;
import com.cheng.weatherschedule.utils.URLConnManager;
import com.cheng.weatherschedule.weather.ChangeCityActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
    private TextView tvWeather, tvTemp, tvWindDirection, tvUv, tvWindScale
            , tvTempToday, tvTextToday, tvTempTomorrow, tvTextTomorrow
            , tvTempAfterTomo, tvTextAfterTomo,tvDate;
    //添加城市
    private TextView tvPlus;


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

    private void initView() {
        tvCity = (TextView) getActivity().findViewById(R.id.tvCity);
        imWeather = (ImageView) getActivity().findViewById(R.id.imWeather);
        imWeatherToday = (ImageView) getActivity().findViewById(R.id.imWeatherToday);
        imWeatherTomorrow = (ImageView) getActivity().findViewById(R.id.imWeatherTomorrow);
        imWeaAfterTomo = (ImageView) getActivity().findViewById(R.id.imWeaAfterTomo);

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

        tvPlus = (TextView) getActivity().findViewById(R.id.tvPlus);
        tvPlus.setOnClickListener(new tvPlusOnCkListerner());

        data = new ArrayList<>();
        String url = "https://api.thinkpage.cn/v3/weather/daily.json?key=r3b44bu4dzqzadlr&location=beijing&language=zh-Hans&unit=c&start=0&days=5";
        action = "daily";
        new WeatherTask().execute(url);

    }
    //点击加号，到添加城市界面
    private class tvPlusOnCkListerner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(getActivity(), ChangeCityActivity.class);
            startActivityForResult(intent,1);
        }
    }

    /**
     * 处理选择城市界面回传的城市名
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&requestCode==getActivity().RESULT_OK){
            if(data!=null){

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
                    String url = "https://api.thinkpage.cn/v3/life/suggestion.json?key=r3b44bu4dzqzadlr&location=beijing&language=zh-Hans";
                    action = "suggestion";
                    new WeatherTask().execute(url);

                }
            } else if (result instanceof LifeSuggestion) {
                lifeSuggestion = (LifeSuggestion) result;
                LifeSuggestion.ResultsBean.SuggestionBean suggestionBean = lifeSuggestion.getResults().get(0).getSuggestion();
//                String suggestion=suggestionBean.getCar_washing().getBrief()+"洗车;"
//                                    +suggestionBean.getSport().getBrief()+"运动;"
//                                    +suggestionBean.getTravel().getBrief()+"旅游;"
//                                    +suggestionBean.getFlu().getBrief()+"感冒.";
                if (data.size() != 0) {
                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).put("uv", "紫外线：" + suggestionBean.getUv().getBrief());
                        //data.get(i).put("suggestion",suggestion);
                    }
                }
                Map<String, Object> day1 = data.get(0);
                Map<String, Object> day2 = data.get(1);
                Map<String, Object> day3 = data.get(2);
                int resId=getResource("weather"+day1.get("codeDay"));
                imWeather.setImageResource(resId);
                tvWeather.setText((String)day1.get("textDay"));
                tvTemp.setText(day1.get("temp")+"℃");
                tvWindDirection.setText((String)day1.get("direction"));
                tvDate.setText((String)day1.get("date"));
                tvUv.setText((String)day1.get("uv"));
                tvWindScale.setText("风力等级："+day1.get("windScale")+"级");
                resId=getResource("s"+day1.get("codeDay"));
                imWeatherToday.setImageResource(resId);
                resId=getResource("s"+day2.get("codeDay"));
                imWeatherTomorrow.setImageResource(resId);
                resId=getResource("s"+day3.get("codeDay"));
                imWeaAfterTomo.setImageResource(resId);

                tvTempToday.setText(((String)day1.get("temp")).replace("-","/"));
                tvTextToday.setText((String)day1.get("textDay"));

                tvTempTomorrow.setText(((String)day2.get("temp")).replace("-","/"));
                tvTextTomorrow.setText((String)day2.get("textDay"));

                tvTempAfterTomo.setText(((String)day3.get("temp")).replace("-","/"));
                tvTextAfterTomo.setText((String)day3.get("textDay"));

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
