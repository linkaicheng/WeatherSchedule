package com.cheng.weatherschedule.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.WeatherDaily;
import com.cheng.weatherschedule.bean.WeatherViewHolder;
import com.cheng.weatherschedule.utils.URLConnManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
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
    data=new ArrayList<>();
    adapter=new Adapter(data);
    lvWeather.setAdapter(adapter);
    String weatherDailyUrl="https://api.thinkpage.cn/v3/weather/daily.json?key=r3b44bu4dzqzadlr&location=beijing&language=zh-Hans&unit=c&start=0&days=5";
    new WeatherTask().execute(weatherDailyUrl);
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
                Log.e("cheng","***********"+code);
                if(code==200){//连接成功
                    //获取服务器返回的数据
                    inputStream=conn.getInputStream();
                    //将输入流转成字符串
                    String reponse=URLConnManager.converStreamToString(inputStream);
                    Log.e("cheng","************"+reponse);
                    //用Gson解析数据
                    Gson gson=new Gson();
                    WeatherDaily weatherDaily=gson.fromJson(reponse,WeatherDaily.class);
                    //成功时返回的是List<Train>对象
                    return weatherDaily;
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
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            //每次查询后清空以前查到的数据
//            data.clear();
//            List<Train> trains=null;
//            //关闭进度对话框
//            if(pDialog!=null){
//                pDialog.dismiss();
//            }
//
//            if(o instanceof List<?>){
//                trains=(List<Train>)o;
//                if(trains.size()==0){
//                    Toast.makeText(TicketResultStep1Activity.this, "没有查询到相关的车次", Toast.LENGTH_SHORT).show();
//                    //查询到空，将数据清空，更新界面
//                    data.clear();
//                    adapter.notifyDataSetChanged();
//                }else{
//                    // 往data中填充数据
//                    for(Train train:trains){
//                        Map<String,Object> row=new HashMap<>();
//                        row.put("trainNo",train.getTrainNo());
//                        if(train.getStartStationName().equals(train.getFromStationName())){
//                            row.put("flg1",R.mipmap.flg_shi);
//                        }else{
//                            row.put("flg1",R.mipmap.flg_guo);
//                        }
//                        if(train.getEndStationName().equals(train.getToStationName())){
//                            row.put("flg2",R.mipmap.flg_zhong);
//                        }else{
//                            row.put("flg2",R.mipmap.flg_guo);
//                        }
//                        row.put("timeFrom",train.getStartTime());
//                        row.put("timeTo",train.getArriveTime());
//                        Map<String,Seat> seats=train.getSeats();
//                        String[] seatKey=new String[]{"seat1", "seat2", "seat3", "seat4"};
//                        int i=0;
//                        for(String key:seats.keySet()){
//                            Seat seat=seats.get(key);
//                            row.put(seatKey[i++],seat.getSeatName()+":"+seat.getSeatNum());
//                        }
//                        data.add(row);
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }else if(o instanceof String){
//                if("2".equals(o)){
//                    Toast.makeText(TicketResultStep1Activity.this, "服务器错误", Toast.LENGTH_SHORT).show();
//                }else if("3".equals(o)){
//                    Toast.makeText(TicketResultStep1Activity.this, "请重新登录", Toast.LENGTH_SHORT).show();
//                }
//
//            }else{
//                Toast.makeText(TicketResultStep1Activity.this, "服务器错误", Toast.LENGTH_SHORT).show();
//            }
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
                viewHolder=new WeatherViewHolder();;
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


                convertView.setTag(viewHolder);
            }else{
                viewHolder=(WeatherViewHolder)convertView.getTag();
            }
            //为控件绑定值

            viewHolder.tvDay.setText((String) data.get(position).get("day"));
            viewHolder.tvDate.setText((String)data.get(position).get("date"));
            viewHolder.imWeather.setImageResource((Integer) data.get(position).get("imWeather"));
            viewHolder.tvTemperature.setText((String)data.get(position).get("temp"));
            viewHolder.tvTextDay.setText((String)data.get(position).get("textDay"));
            viewHolder.tvWindDirection.setText((String)data.get(position).get("direction"));
            viewHolder.tvUv.setText((String)data.get(position).get("uv"));
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
}
