package com.cheng.weatherschedule.weather.cityList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cheng.weatherschedule.R;

import java.util.List;

public class MyLetterAdapter extends BaseAdapter {
	private Context mContext;
	private List<TestData> datas;
	
	public  MyLetterAdapter(Context context, List<TestData> datas){
		this.mContext = context;
		this.datas = datas;
		
	}
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {  
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
            holder = new ViewHolder();  
            holder.name = (TextView) convertView.findViewById(R.id.name);  
          
            convertView.setTag(holder);  
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }  
        TestData data = datas.get(position);  
        holder.name.setText(data.getName());
        
        return convertView;  
	}
	private class ViewHolder {
        TextView name;  
       
	}

}
