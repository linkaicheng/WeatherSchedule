package com.cheng.weatherschedule.weather.cityList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class LetterListView extends FrameLayout implements MyLetterListView.OnTouchingLetterChangedListener {
    private MyListView listView;
    private MyLetterListView myLetterListView;
    private Context mContext;
    HashMap<String, Integer> titleIndexer;
    public LetterListView(Context context) {
        super(context);
        init(context);
    }

    public LetterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LetterListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    public void setLetter(int titleViewId, int titleViewTextViewId,ArrayList<String> sortKeys){
        listView = new MyListView(mContext, titleViewId, titleViewTextViewId, sortKeys);
        initTitleIndexer(sortKeys);
        initView(mContext);
    }
    private void initTitleIndexer(ArrayList<String> sortKeys) {
        titleIndexer = new HashMap<String, Integer>();
        int len = sortKeys.size();
        for(int i = 0; i < len; i ++){
            String currentStr = sortKeys.get(i);
            String previewStr = (i - 1) >= 0 ? sortKeys.get(i -1 ) : " ";
            if (!previewStr.equals(currentStr)) {
                titleIndexer.put(currentStr, i);
            }
        }

    }

    public void init(Context context){
        this.mContext = context;
        LayoutParams listViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLayoutParams(listViewParams);

    }
    private void initView(Context context) {

        RelativeLayout.LayoutParams listViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutParams(listViewParams);

        LayoutParams letterParams = new LayoutParams(40, LayoutParams.WRAP_CONTENT,Gravity.RIGHT);
        myLetterListView = new MyLetterListView(context);
        myLetterListView.setOnTouchingLetterChangedListener(this);
        myLetterListView.setLayoutParams(letterParams);

        addView(listView);
        addView(myLetterListView);

    }
    public interface OnItemClickListener{
        void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3);
    }
    public void setOnItemClickListener(final OnItemClickListener onItemClickListener){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                onItemClickListener.onItemClick(arg0, arg1, arg2, arg3);
            }
        });
    }
    public void setAdapter(BaseAdapter adapter){
        listView.setAdapter(adapter);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        if(titleIndexer.get(s) != null) {
            int position = titleIndexer.get(s);
            listView.setSelection(position);
            LetterToast.getInstance(mContext).showToast(s);//弹出悬浮框显示你选的字母导航

        }

    }


}