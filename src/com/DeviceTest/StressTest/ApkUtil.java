package com.DeviceTest.StressTest;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ApkUtil {
	private static final String TAG= "ApkUtil";
	public static void startApk(Context mContext,String mPKGName, String mPKGMainActivity){
		Intent mIntent = new Intent("android.intent.action.MAIN");
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ComponentName comp = new ComponentName(mPKGName, mPKGMainActivity);
		mIntent.setComponent(comp);
		mIntent.setAction("android.intent.action.VIEW");
		mContext.startActivity(mIntent);
		
	}
	
	public static boolean isPkgInstalled(Context mContext, String packageName){
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }
	
	public static void touchPoint(float x, float y){
		Instrumentation inst = new Instrumentation();
		inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));

		inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
		Log.d(TAG, "touchPoint x:"+x+" y:"+y);
	}
	
	public static void touchBack(){
		new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
	}

}
