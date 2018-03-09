package com.DeviceTest.StressTest;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.DeviceTest.StressTest.AntutuLocations;
import com.DeviceTest.StressTest.ApkUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class AntutuLocationService extends Service {

	final static class HandlerSwitch {
		static final int PREPARE = 0;
		static final int CONFIG = 1;
		static final int ENTER_APK = 2;
		static final int TEST_APK = 3;
		static final int QUIT_APK = 4;
		static final int CHECK_ACTIVITY_ALIVE = 5;
	};
	
	private static final String TAG = "AntutuLocationService";
	int mTouchEvent = 0;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG,"onCreate");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		AntutuLocations.init();
		mHandler.sendEmptyMessageAtTime(HandlerSwitch.PREPARE, 1*1000l);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerSwitch.PREPARE:
				mHandler.sendEmptyMessageDelayed(HandlerSwitch.CONFIG, 1000);
				break;
			
				
			case HandlerSwitch.CONFIG:
				
				Log.i(TAG, "Handler CONFIG");

				
				startAPKTest();
				mHandler.sendEmptyMessageDelayed(HandlerSwitch.ENTER_APK,
						25*1000l);
				break;

			case HandlerSwitch.ENTER_APK:

				mTouchEvent = HandlerSwitch.ENTER_APK;
				new Thread(new CpuTestThread()).start();
				mHandler.sendEmptyMessageDelayed(HandlerSwitch.TEST_APK, 10000);
				break;
				
			case HandlerSwitch.TEST_APK:

				mTouchEvent = HandlerSwitch.TEST_APK;

				new Thread(new CpuTestThread()).start();
				mHandler.sendEmptyMessageDelayed(HandlerSwitch.QUIT_APK,
						360 * 1000l);

				break;

			case HandlerSwitch.QUIT_APK:
				mTouchEvent = HandlerSwitch.QUIT_APK;
				new Thread(new CpuTestThread()).start();
				break;

			}
			
		}
		
	};
	
	private class CpuTestThread implements Runnable {
		public void run() {

			switch (mTouchEvent) {
			case HandlerSwitch.ENTER_APK:

				if(AntutuTestActivity.startCount > 1) {
					ApkUtil.touchPoint(AntutuLocations.testAgianButton.x,AntutuLocations.testAgianButton.y);
				}else {
					ApkUtil.touchPoint(AntutuLocations.enterTestBtn.x,AntutuLocations.enterTestBtn.y);
				}
				break;
			case HandlerSwitch.TEST_APK:
				
				ApkUtil.touchPoint(AntutuLocations.startTestBtn.x,AntutuLocations.startTestBtn.y);

				break;
			case HandlerSwitch.QUIT_APK:
				Log.i(TAG, "Touch Back");
				try {
					ApkUtil.touchBack();
					Thread.sleep(3000);
					ApkUtil.touchBack();
					Thread.sleep(1000);
					ApkUtil.touchBack();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}
	
	
	private void startAPKTest(){
		ApkUtil.startApk(this,"com.antutu.ABenchMark",
		"com.antutu.ABenchMark.ABenchMarkStart");
		
	}
}
