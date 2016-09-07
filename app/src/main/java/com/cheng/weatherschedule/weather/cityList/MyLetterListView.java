package com.cheng.weatherschedule.weather.cityList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



public class MyLetterListView extends View {

    public final static String[] INDEX_KEY = {"热门","A","B","C","D","E","F","G","H","I","J","K","L"
            ,"M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = true;

    public MyLetterListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyLetterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLetterListView(Context context) {
        super(context);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(showBkg){
            canvas.drawColor(Color.WHITE);

        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / INDEX_KEY.length;
        for(int i=0;i<INDEX_KEY.length;i++){

            paint.setTextSize(20);//设置导航栏文字大小
            paint.setColor(Color.parseColor("#62AC3C"));
            //英文字体加粗
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            if(i == choose){

                paint.setColor(Color.BLACK);
                //中文字体加粗
                paint.setFakeBoldText(true);
            }
            float xPos = width/2  - paint.measureText(INDEX_KEY[i])/2;
            float yPos = singleHeight * i+ singleHeight;
            canvas.drawText(INDEX_KEY[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y/getHeight()*INDEX_KEY.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if(oldChoose != c && listener != null){
                    if(c >= 0 && c< INDEX_KEY.length){
                        listener.onTouchingLetterChanged(INDEX_KEY[c]);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(oldChoose != c && listener != null){
                    if(c >= 0 && c< INDEX_KEY.length){
                        listener.onTouchingLetterChanged(INDEX_KEY[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener{
        public void onTouchingLetterChanged(String s);
    }

}