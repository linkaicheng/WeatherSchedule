package com.cheng.weatherschedule.weather.cityList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MyListView extends ListView{
    private int TITLE_VIEW_ID;
    private int TITLE_VIEW_TEXTVIEW_ID;
    private ArrayList<String> sortKeys;
    private LetterAdapter adapter;
    public MyListView(Context context) {
        super(context);
    }
    public MyListView(Context context, int titleViewId, int titleViewTextViewId,ArrayList<String> sortKeys) {
        super(context);
        this.TITLE_VIEW_ID = titleViewId;
        this.TITLE_VIEW_TEXTVIEW_ID = titleViewTextViewId;
        this.sortKeys = sortKeys;
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void setAdapter(ListAdapter adapter) {

        this.adapter = new LetterAdapter(adapter, TITLE_VIEW_ID, TITLE_VIEW_TEXTVIEW_ID, sortKeys);
        super.setAdapter(this.adapter);
    }




}