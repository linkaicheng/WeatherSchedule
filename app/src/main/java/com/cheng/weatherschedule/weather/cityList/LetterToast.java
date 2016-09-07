package com.cheng.weatherschedule.weather.cityList;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class LetterToast {
	private TextView txtToast;
	private ToastThread toastThread;
	private Handler mHandler;
	public static LetterToast letterToast;

	public static LetterToast getInstance(Context context) {
		if (letterToast == null)
			letterToast = new LetterToast(context);
		return letterToast;

	}

	private LetterToast(Context context) {
		mHandler = new Handler();
		toastThread = new ToastThread();
		txtToast = new TextView(context);
		txtToast.setTextSize(70);
		txtToast.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(txtToast, lp);

	}

	public void showToast(String msg) {
		txtToast.setVisibility(View.VISIBLE);
		txtToast.setText(msg);

		mHandler.removeCallbacks(toastThread);
		mHandler.postDelayed(toastThread, 500);

	}

	class ToastThread implements Runnable {
		@Override
		public void run() {
			txtToast.setVisibility(View.GONE);
		}

	}

}
