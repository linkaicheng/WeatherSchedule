package com.cheng.weatherschedule.calendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.DayPlanViewHolder;
import com.cheng.weatherschedule.dao.PlanDao;
import com.cheng.weatherschedule.daoImpl.PlanDaoImpl;
import com.cheng.weatherschedule.db.PlanHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayPlanActivity extends AppCompatActivity implements View.OnClickListener {
    //日期title
    private TextView tvDate;
    //计划列表
    private ListView lvPlan;
    //添加计划
    private TextView tvPlus;
    //删除计划
    private ImageView imDelete;
    //数据源
    private List<Map<String, Object>> data = null;
    private Adapter adapter;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_plan);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        data = getDate();
        adapter = new Adapter(data);
        lvPlan.setAdapter(adapter);
    }

    private void initView() {
        tvDate = (TextView) findViewById(R.id.tvDate);
        lvPlan = (ListView) findViewById(R.id.lvPlan);
        tvPlus = (TextView) findViewById(R.id.tvPlus);
        imDelete = (ImageView) findViewById(R.id.imDelete);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        tvDate.setText("计划列表(" + date + ")");
        //添加计划设置监听
        tvPlus.setOnClickListener(this);
        imDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPlus://添加计划
                Intent intent = new Intent(DayPlanActivity.this, AddPlanActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
                break;
            case R.id.imDelete://删除当天所有计划
               deleteDayPlan();
                break;
        }
    }
    //删除当天所有计划
    private void deleteDayPlan(){
        AlertDialog.Builder builder=new AlertDialog.Builder(DayPlanActivity.this);
        if(data.size()==0){
            builder.setTitle("确认删除");
            builder.setIcon(R.mipmap.warn2);
            builder.setMessage("计划为空");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else{
            builder.setTitle("确认删除");
            builder.setIcon(R.mipmap.warn2);
            builder.setMessage("确定要删除当天所有计划吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PlanDao planDao=new PlanDaoImpl(DayPlanActivity.this);
                    planDao.deletePlanByDate(date);
                    data.clear();
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        builder.show();

    }


    //自定义适配器
    private class Adapter extends BaseAdapter {
        List<Map<String, Object>> data;

        public Adapter(List<Map<String, Object>> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DayPlanViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new DayPlanViewHolder();
                convertView = View.inflate(DayPlanActivity.this, R.layout.item_day_plan, null);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                viewHolder.imDelete = (ImageView) convertView.findViewById(R.id.imDelete);
                viewHolder.tvPlanTitle = (TextView) convertView.findViewById(R.id.tvPlanTitle);
                viewHolder.tvPlanId = (TextView) convertView.findViewById(R.id.tvPlanId);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (DayPlanViewHolder) convertView.getTag();
            }
            viewHolder.tvTime.setText((String) data.get(position).get("time"));
            viewHolder.tvPlanTitle.setText((String) data.get(position).get("title"));
            viewHolder.imDelete.setImageResource(R.mipmap.delete3);
            viewHolder.tvPlanId.setText(String.valueOf(data.get(position).get("id")));

            viewHolder.imDelete.setOnClickListener(new ImDeleteOnCkListener((int) data.get(position).get("id"), position));
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

    //删除一条计划
    private class ImDeleteOnCkListener implements View.OnClickListener {
        int id;
        int position;
        PlanDao planDao = new PlanDaoImpl(DayPlanActivity.this);

        public ImDeleteOnCkListener(int id, int position) {
            this.id = id;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            int count = planDao.deletePlan(id);
            data.remove(position);
            adapter.notifyDataSetChanged();
            if (count != 0) {
                Toast.makeText(DayPlanActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //从数据库获取数据
    private List<Map<String, Object>> getDate() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row;
        PlanHelper helper = new PlanHelper(DayPlanActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("plans", null, "date=?", new String[]{date}, null, null, null);
        while (cursor.moveToNext()) {
            row = new HashMap<>();
            row.put("time", cursor.getString(cursor.getColumnIndex("time")));
            row.put("title", cursor.getString(cursor.getColumnIndex("title")));
            row.put("id", cursor.getInt(cursor.getColumnIndex("id")));
            data.add(row);
        }
        return data;
    }
}
