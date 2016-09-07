package com.cheng.weatherschedule.weather.cityList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public abstract class LetterBusinessAdapter extends LetterBaseAdapter{
	public abstract View getTitleView(View view);
	public abstract ArrayList<String> getSortKeys();
	public abstract TextView getTitleTextView(View view);

	public LetterBusinessAdapter(ListAdapter listAdapter) {
		super(listAdapter);
	}
	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = listAdapter.getView(position, view, viewGroup);
		enableFor(position,view);
		return view;
	}
	private void enableFor(int position, View view) {
		if(getTitleTextView(view) == null || getTitleView(view) == null)
			return;
			
		String currentSortKey = getAlpha(getSortKeys().get(position));
		String preSortKey = (position - 1) >= 0 ? getAlpha(getSortKeys().get(position - 1)) : " ";
		
	        if (!preSortKey.equals(currentSortKey)) { 
	        	getTitleView(view).setVisibility(View.VISIBLE);
	        	getTitleTextView(view).setText(currentSortKey);
	        } else {  
	        	getTitleView(view).setVisibility(View.GONE);
	        }  
		
	}
	 private String getAlpha(String str) {  
	        if (str == null) {  
	            return "#";  
	        }  
	        if (str.trim().length() == 0) {  
	            return "#";  
	        }  
	        char c = str.trim().substring(0, 1).charAt(0);  
	        Pattern pattern = Pattern.compile("^[A-Za-z]+$");  
	        if (pattern.matcher(c + "").matches()) {  
	            return (c + "").toUpperCase(Locale.getDefault());  
	        } else {  
	            return "热门";  
	        }  
	    }  
	

	

}
