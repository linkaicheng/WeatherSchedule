package com.cheng.weatherschedule.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.weather.cityList.LetterListView;
import com.cheng.weatherschedule.weather.cityList.MyLetterAdapter;
import com.cheng.weatherschedule.weather.cityList.TestData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class ChangeCityActivity extends Activity {
private EditText input_search_query;
    private ImageButton button_search;
    private TextView tvCity;
    private ImageView imWeather;
    private TextView tvWeather;
    private TextView tvTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_city);

        imWeather = (ImageView)findViewById(R.id.imWeather);
        tvCity = (TextView)findViewById(R.id.tvCity);
        tvWeather = (TextView)findViewById(R.id.tvWeather);
        tvTemp = (TextView)findViewById(R.id.tvTemp);

        LetterListView letterListView = (LetterListView) findViewById(R.id.letterListView);
        input_search_query = (EditText)findViewById(R.id.input_search_query);
        button_search = (ImageButton)findViewById(R.id.button_search);

        ArrayList<String> sortKeys = new ArrayList<String>();
        for(TestData item : getTestDatas()){
            sortKeys.add(item.getSortKey());
        }
        letterListView.setLetter(R.id.alpha, R.id.alpha, sortKeys );
        MyLetterAdapter adapter = new MyLetterAdapter(this, getTestDatas());
        letterListView.setAdapter(adapter);

        letterListView.setOnItemClickListener(new LetterListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //TextView tvname=(TextView)arg0.getChildAt(arg2).findViewById(R.id.name);//获取点击城市的名字（会报错）
                TextView tvname=(TextView)arg1.findViewById(R.id.name);//获取点击城市的名字（可行）
                input_search_query.setText(tvname.getText().toString());
            }
        });

        //默认天气情况
        Intent intent=getIntent();
        tvCity.setText(intent.getStringExtra("city")+"(默认)");
        imWeather.setImageResource(intent.getIntExtra("codeDay",100));
        tvWeather.setText(intent.getStringExtra("weather"));
        tvTemp.setText(intent.getStringExtra("temp"));

        button_search.setOnClickListener(new BtnSearchOnCkListener());
    }
    //点击查询按钮，将城市名回传
    private class BtnSearchOnCkListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent=getIntent();
            intent.putExtra("city",input_search_query.getText().toString());
            setResult(RESULT_OK,intent);
            finish();
        }
    }
    public List<TestData> getTestDatas() {
        List<TestData> datas = new ArrayList<TestData>();
        String[] strkeys=new String[]{"热门","A","B","C","D","E","F","G","H","J","K","L","M","N","P","Q","R","S","T","W","X","Y","Z"};
        try
        {
		/*A*/
            //将json文件读取到buffer数组中
            InputStream is = this.getResources().openRawResource(R.raw.allcity);
            byte[] buffer = new byte[is.available()];is.read(buffer);
            //将字节数组转换为以UTF-8编码的字符串
            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
        	//解析info数组，解析中括号括起来的内容就表示一个数组，使用JSONArray对象解析
            JSONArray array = jsonObject.getJSONArray("City");

            for(int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String strname="";
                String strIteam="";
                String strkey = "";
                for (int j = 0; j < strkeys.length; j++) {
                    JSONArray	arrayItem = item.getJSONArray(strkeys[j]);
                    for(int count=0;count<arrayItem.length();count++)
                    {	TestData data=new TestData();
                        JSONObject jsonItem=arrayItem.getJSONObject(count);

                        strname=jsonItem.getString("name");

                        strkey=jsonItem.getString("key");

                        data.setName(strname);


                        //自行设置
                        data.setKey(strkey);
                        datas.add(data);

                    }
                }
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return datas;
    }



}
