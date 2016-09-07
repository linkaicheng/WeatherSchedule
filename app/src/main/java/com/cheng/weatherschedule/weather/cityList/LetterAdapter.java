package com.cheng.weatherschedule.weather.cityList;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LetterAdapter extends LetterBusinessAdapter{
	private int TITLE_VIEW_ID;
	private int TITLE_VIEW_TEXTVIEW_ID;
	private ArrayList<String> sortKeys;
	public LetterAdapter(ListAdapter listAdapter, int titleViewId, int titleViewTextViewId,ArrayList<String> sortKeys){
		super(listAdapter);
		this.TITLE_VIEW_ID = titleViewId;
		this.TITLE_VIEW_TEXTVIEW_ID = titleViewTextViewId;
		this.sortKeys = sortKeys;
	}

	public LetterAdapter(ListAdapter listAdapter) {
		super(listAdapter);
	}

	@Override
	public View getTitleView(View view) {
		return view.findViewById(TITLE_VIEW_ID);
	}

	@Override
	public ArrayList<String> getSortKeys() {
		return sortKeys;
	}

	@Override
	public TextView getTitleTextView(View view) {
		return (TextView) view.findViewById(TITLE_VIEW_TEXTVIEW_ID);
	}

}
