package com.cheng.weatherschedule.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.LifeSuggestion;
import com.cheng.weatherschedule.bean.WeatherDaily;
import com.cheng.weatherschedule.bean.WeatherViewHolder;
import com.cheng.weatherschedule.utils.URLConnManager;
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
    private ListView lvWeather;
    private Adapter adapter;
    private List<Map<String,Object>> data=null;
    private ProgressDialog pDialog;
    private String action;
    private TextView tvCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }
private void initView(){
    lvWeather = (ListView) getActivity().findViewById(R.id.lvWeather);
    tvCity = (TextView) getActivity().findViewById(R.id.tvCity);
    data=new ArrayList<>();
    adapter=new Adapter(data);
    lvWeather.setAdapter(adapter);
    String url="https://api.thinkpage.cn/v3/weather/daily.json?key=r3b44bu4dzqzadlr&location=beijing&language=zh-Hans&unit=c&start=0&days=5";
    action="daily";
    new WeatherTask().execute(url);

}
    private class WeatherTask extends AsyncTask<String,Void,Object>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //显示进度对话框
            pDialog = ProgressDialog.show(getActivity(), null, "正从服务器获取数据，请稍候", false, true);
        }

        @Override
        protected Object doInBackground(String... params) {
            String result=null;
            InputStream inputStream=null;
            HttpURLConnection conn=null;
            String url=params[0];
            try {
                //获取连接
                conn = URLConnManager.getHttpURLConnection(url);
                //连接
                conn.connect();
                //获得响应码
                int code=conn.getResponseCode();
                //Log.e("cheng","***********"+code);
                if(code==200){//连接成功
                    //获取服务器返回的数据
                    inputStream=conn.getInputStream();
                    //将输入流转成字符串
                    String response=URLConnManager.converStreamToString(inputStream);
                   // Log.e("cheng","************"+response);
                    //用Gson解析数据
                    Gson gson=new Gson();
                    if(action.equals("daily")){
                        WeatherDaily weatherDaily=gson.fromJson(response,WeatherDaily.class);
                        return weatherDaily;
                    }else if(action.equals("suggestion")){
                        LifeSuggestion lifeSuggestion=gson.fromJson(response,LifeSuggestion.class);
                        return lifeSuggestion;
                    }
                }else{//连接失败
                    result="2";
                }

            } catch (IOException e) {
                e.printStackTrace();
                result = "2";
            }catch (JsonSyntaxException e){
                e.printStackTrace();
                result="3";
            }
            finally {
                //关闭输入流和连接等资源
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(conn!=null){
                    conn.disconnect();
                }
            }
            //失败时返回的是字符串result
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            LifeSuggestion lifeSuggestion=null;
            WeatherDaily weatherDaily=null;
            //关闭进度对话框
            if(pDialog!=null){
                pDialog.dismiss();

            }

            if(result instanceof WeatherDaily){
                weatherDaily=(WeatherDaily)result;
                if(weatherDaily==null){
                    Toast.makeText(getActivity(), "没有查询到相关的天气信息", Toast.LENGTH_SHORT).show();
                    //查询到空，将数据清空，更新界面
                    data.clear();
                    adapter.notifyDataSetChanged();
                }else{
                    tvCity.setText(weatherDaily.getResults().get(0).getLocation().getName());
                    // 往data中填充数据
                    for(WeatherDaily.ResultsBean.DailyBean daily:weatherDaily.getResults().get(0).getDaily()){
                        Map<String,Object> row=new HashMap<>();
                        row.put("date",daily.getDate());
                        row.put("codeDay",daily.getCode_day());
                        row.put("temp",daily.getLow()+"-"+daily.getHigh());
                        row.put("textDay",daily.getText_day());
                        row.put("direction",daily.getWind_direction());
                        data.add(row);
                    }
                   String url="https://api.thinkpage.cn/v3/life/suggestion.json?key=r3b44bu4dzqzadlr&location=beijing&language=zh-Hans";
                    action="suggestion";
                    new WeatherTask().execute(url);

                }
            }else if(result instanceof LifeSuggestion){
                lifeSuggestion=(LifeSuggestion)result;
                LifeSuggestion.ResultsBean.SuggestionBean suggestionBean=lifeSuggestion.getResults().get(0).getSuggestion();
                String suggestion=suggestionBean.getCar_washing().getBrief()+"洗车;"
                                    +suggestionBean.getSport().getBrief()+"运动;"
                                    +suggestionBean.getTravel().getBrief()+"旅游;"
                                    +suggestionBean.getFlu().getBrief()+"感冒.";
                if(data.size()!=0){
                    for(int i=0;i<data.size();i++){
                         data.get(i).put("uv","紫外线："+suggestionBean.getUv().getBrief());
                        data.get(i).put("suggestion",suggestion);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            else if(result instanceof String){
                if("2".equals(result)){
                    Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                }else if("3".equals(result)){
                    Toast.makeText(getActivity(), "请重新登录", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 自定义适配器
     */
    private class Adapter extends BaseAdapter{
        List<Map<String,Object>> data;
        public Adapter(List<Map<String,Object>> data){
            this.data=data;

        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //声明一个ViewHolder
            WeatherViewHolder viewHolder;
            if(convertView==null){
                viewHolder=new WeatherViewHolder();
                //将转换的View对象存入缓存中
                convertView=View.inflate(getActivity(),R.layout.item_weather,null);
                //通过convertView对象来获得控件并将其保存到viewHolder中
               viewHolder.imWeather= (ImageView) convertView.findViewById(R.id.imWeather);
               viewHolder.tvDay= (TextView) convertView.findViewById(R.id.tvDay);
                viewHolder.tvDate= (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvTemperature= (TextView) convertView.findViewById(R.id.tvTemperature);
                viewHolder.tvTextDay= (TextView) convertView.findViewById(R.id.tvTextDay);
                viewHolder.tvUv= (TextView) convertView.findViewById(R.id.tvUv);
                viewHolder.tvWindDirection= (TextView) convertView.findViewById(R.id.tvWindDirection);
                viewHolder.tvSuggestion= (TextView) convertView.findViewById(R.id.tvSuggestion);

                convertView.setTag(viewHolder);
            }else{
                viewHolder=(WeatherViewHolder)convertView.getTag();
            }
            //为控件绑定值

            viewHolder.tvDay.setText((String) data.get(position).get("day"));
            viewHolder.tvDate.setText((String)data.get(position).get("date"));
            String s="weather"+data.get(position).get("codeDay");
            viewHolder.imWeather.setImageResource(getResource(s));
            viewHolder.tvTemperature.setText((String)data.get(position).get("temp"));
            viewHolder.tvTextDay.setText((String)data.get(position).get("textDay"));
            viewHolder.tvWindDirection.setText((String)data.get(position).get("direction"));
            viewHolder.tvUv.setText((String)data.get(position).get("uv"));
            viewHolder.tvSuggestion.setText((String)data.get(position).get("suggestion"));
            switch (position) {
                case  0:
                    viewHolder.tvDay.setText("今天");
                    break;
                case  1:
                    viewHolder.tvDay.setText("明天");
                    break;
                case  2:
                    viewHolder.tvDay.setText("后天");
                    break;
            }

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
    public int  getResource(String imageName) {
        Context ctx = getActivity().getBaseContext();
        int resId = getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        return resId;
    }
}
