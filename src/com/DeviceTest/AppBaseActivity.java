package com.DeviceTest;

import android.app.Activity;
import android.os.Handler;
import android.widget.TextView;

import com.DeviceTest.data.Configs;

public abstract class AppBaseActivity extends Activity{
	
	private TextView mTextTime;
	private Handler mTimeHandler = new Handler();
	private boolean mIsStop = false;
	Runnable timerRunnable = new Runnable() {
		int time = getMaxTime();
		@Override
		public void run() {
			if(mIsStop)
				return;
			if(time > 0){
				time--;
			}
			else{
				onOverTime();
				return;
			}
			mTextTime.setText("" + time);
			mTimeHandler.postDelayed(this, 1000);
				
		}
	};
	public void startTimer(){
		mTextTime = (TextView)findViewById(R.id.text_time);
		mTextTime.setText("" + Configs.MAX_OVER_TIME);
		mTimeHandler.postDelayed(timerRunnable, 1000);
	}
	public void endTimer(){
		mTimeHandler.removeCallbacks(timerRunnable);
	}
	
	public abstract void onOverTime();
	
	public void stopTimer(){
		mIsStop = true;
	}
	
	public int getMaxTime(){
		return Configs.MAX_OVER_TIME;
	}
}
