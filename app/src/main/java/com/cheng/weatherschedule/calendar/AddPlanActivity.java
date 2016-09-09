package com.cheng.weatherschedule.calendar;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.db.PlanHelper;

import java.util.Calendar;

public class AddPlanActivity extends AppCompatActivity implements View.OnClickListener{
private EditText edtTitle,edtPlace;//标题
    private TextView tvTimeShow,tvRemTimShow,tvCancel,tvSave;//时间，提醒时间，取消，保存
    private EditText edtExplain;//详细说明
    private ImageView imSetTime,imSetRemTime;//设置时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        initView();
    }
    private void initView(){
        edtTitle = (EditText)findViewById(R.id.edtTitle);
        edtPlace = (EditText)findViewById(R.id.edtPlace);
        tvTimeShow = (TextView)findViewById(R.id.tvTimeShow);
        tvRemTimShow = (TextView)findViewById(R.id.tvRemTimShow);
        edtExplain = (EditText)findViewById(R.id.edtExplain);
        tvCancel = (TextView)findViewById(R.id.tvCancel);
        tvSave = (TextView)findViewById(R.id.tvSave);
        imSetTime = (ImageView)findViewById(R.id.imSetTime);
        imSetRemTime = (ImageView)findViewById(R.id.imSetRemTime);

        tvSave.setOnClickListener(this);
        imSetTime.setOnClickListener(this);
        imSetRemTime.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.tvCancel:
                finish();
                break;
            case  R.id.imSetTime://选择时间
                Calendar calendar=Calendar.getInstance();
                new TimePickerDialog(AddPlanActivity.this
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tvTimeShow.setText(hourOfDay+":"+minute);
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
                break;
            case  R.id.imSetRemTime://选择提醒时间
                Calendar calendar2=Calendar.getInstance();
                new TimePickerDialog(AddPlanActivity.this
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tvRemTimShow.setText(hourOfDay+":"+minute);
                    }
                },calendar2.get(Calendar.HOUR_OF_DAY),calendar2.get(Calendar.MINUTE),true).show();
                break;
            case R.id.tvSave://保存，将计划保存到数据库
                    String title=edtTitle.getText().toString();
                    PlanHelper helper=new PlanHelper(AddPlanActivity.this);
                    SQLiteDatabase db=helper.getWritableDatabase();
                    ContentValues values=new ContentValues();
                    values.put("title",title);
                    values.put("place",edtPlace.getText().toString());
                    values.put("time",tvTimeShow.getText().toString());
                    values.put("remindTime",tvRemTimShow.getText().toString());
                    values.put("explain",edtExplain.getText().toString());
                    long id=db.insert("plans",null,values);
                    db.close();
                    helper.close();
                    if(id==-1){
                        Toast.makeText(AddPlanActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AddPlanActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                break;
        }
    }
}
