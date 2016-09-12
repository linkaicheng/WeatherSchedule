package com.cheng.weatherschedule.calendar;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.Plan;
import com.cheng.weatherschedule.dao.PlanDao;
import com.cheng.weatherschedule.daoImpl.PlanDaoImpl;
import com.cheng.weatherschedule.remind.LongRunningService;

import java.util.Calendar;

public class EditPlanActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edtEdtitle, edtEdPlace, edtEdExplain;
    private TextView tvEdTimeShow, tvEdRemTimShow, tvEdCancel
            ,tvEdSetTime, tvEdSave,tvEdSetRemTime;

    private int planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plan);
        initView();
    }

    private void initView() {
        edtEdtitle = (EditText) findViewById(R.id.edtEdtitle);
        edtEdPlace = (EditText) findViewById(R.id.edtEdPlace);
        edtEdExplain = (EditText) findViewById(R.id.edtEdExplain);
        tvEdTimeShow = (TextView) findViewById(R.id.tvEdTimeShow);
        tvEdRemTimShow = (TextView) findViewById(R.id.tvEdRemTimShow);
        tvEdCancel = (TextView) findViewById(R.id.tvEdCancel);
        tvEdSave = (TextView) findViewById(R.id.tvEdSave);
        tvEdSetTime = (TextView) findViewById(R.id.tvEdSetTime);
        tvEdSetRemTime = (TextView) findViewById(R.id.tvEdSetRemTime);
        Intent intent=getIntent();
      planId=intent.getIntExtra("id",-1);
        PlanDao planDao=new PlanDaoImpl(EditPlanActivity.this);
        Plan plan=planDao.findById(planId);
        if(plan!=null){
            edtEdtitle.setText(plan.getTitle());
            edtEdPlace.setText(plan.getPlace());
            edtEdExplain.setText(plan.getExplain());
            tvEdTimeShow.setText(plan.getTime());
            tvEdRemTimShow.setText(plan.getRemindTime());
        }
        //设置监听
        tvEdSave.setOnClickListener(this);
        tvEdCancel.setOnClickListener(this);
         tvEdSetTime.setOnClickListener(this);
        tvEdSetRemTime.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.tvEdCancel:
                finish();
                break;
            case  R.id.tvEdSave:
                int count=saveChange();
                if(count!=0){
                    Toast.makeText(EditPlanActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    //再次开启LongRunningService这个服务，以修改提醒
                    Intent serviceIntent = new Intent(this, LongRunningService.class);
                    //关闭后重新开启Service
                    stopService(serviceIntent);
                    startService(serviceIntent);
                    finish();
                }

                break;
            case  R.id.tvEdSetTime:
                selectTime(tvEdTimeShow);
               break;
            case  R.id.tvEdSetRemTime:
                selectTime(tvEdRemTimShow);
               break;
        }
    }
    //弹出选择时间对话框
    private void selectTime(final TextView tvTimeSet){
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(EditPlanActivity.this
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tvTimeSet.setText(hourOfDay + ":" + minute);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
    //保存修改
    private int saveChange(){
        Plan plan=new Plan();
        plan.setTime(tvEdTimeShow.getText().toString());
        plan.setTitle(edtEdtitle.getText().toString());
        plan.setRemindTime(tvEdRemTimShow.getText().toString());
        plan.setPlace(edtEdPlace.getText().toString());
        Intent intent=getIntent();
        plan.setDate(intent.getStringExtra("date"));
        plan.setExplain(edtEdExplain.getText().toString());
        plan.setId(planId);
        PlanDao planDao=new PlanDaoImpl(EditPlanActivity.this);
       int count= planDao.updatePlan(plan);
        return count;
    }
}
