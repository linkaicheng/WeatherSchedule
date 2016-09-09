package com.cheng.weatherschedule.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.DayPlanViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DayPlanActivity extends AppCompatActivity implements View.OnClickListener{
    //日期title
    private TextView tvDate;
    //计划列表
    private ListView lvPlan;
    //添加计划
    private TextView tvPlus;
    //删除计划
    private ImageView imDelete;
    //数据源
     private List<Map<String,Object>> data=null;
    private Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_plan);
        initView();
    }
    private void initView(){
        tvDate = (TextView)findViewById(R.id.tvDate);
        lvPlan = (ListView)findViewById(R.id.lvPlan);
        tvPlus = (TextView)findViewById(R.id.tvPlus);
        imDelete = (ImageView)findViewById(R.id.imDelete);

        Intent intent =getIntent();
        String date=intent.getStringExtra("date");
        tvDate.setText("计划列表("+date+")");
        data=getDate();
        adapter=new Adapter(data);
        lvPlan.setAdapter(adapter);
        //添加计划设置监听
        tvPlus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.tvPlus://添加计划
                Intent intent=new Intent(DayPlanActivity.this,AddPlanActivity.class);
                startActivity(intent);
                break;
        }
    }

    //自定义适配器
    private class Adapter extends BaseAdapter{
        List<Map<String,Object>> data;
        public Adapter( List<Map<String,Object>> data){
            this.data=data;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DayPlanViewHolder viewHolder;
            if(convertView==null){
                viewHolder=new DayPlanViewHolder();
                convertView=View.inflate(DayPlanActivity.this,R.layout.item_day_plan,null);
                viewHolder.tvTime= (TextView) convertView.findViewById(R.id.tvTime);
                viewHolder.imDelete= (ImageView) convertView.findViewById(R.id.imDelete);
                viewHolder.tvPlanTitle= (TextView) convertView.findViewById(R.id.tvPlanTitle);
                viewHolder.tvPlanId= (TextView) convertView.findViewById(R.id.tvPlanId);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (DayPlanViewHolder) convertView.getTag();
            }
            viewHolder.tvTime.setText((String)data.get(position).get("time"));
            viewHolder.tvPlanTitle.setText((String)data.get(position).get("title"));
            viewHolder.imDelete.setImageResource(R.mipmap.delete3);
            viewHolder.tvPlanId.setText((String)data.get(position).get("id"));
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
    //从数据库获取数据
    private List<Map<String,Object>> getDate(){
        List<Map<String,Object>> data=new ArrayList<>();
        Map<String ,Object> row;

        return data;
    }
}
