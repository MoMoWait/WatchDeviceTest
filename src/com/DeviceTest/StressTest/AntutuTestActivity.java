package com.DeviceTest.StressTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.DeviceTest.R;
import com.DeviceTest.R.layout;
import com.DeviceTest.helper.LogFileHelper;
import com.DeviceTest.helper.SystemUtil;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AntutuTestActivity extends Activity implements OnClickListener {
	private final static String TAG = "AntutuTestActivity";
	private final static String APKPATH = "/mnt/sdcard/demo/antutu-benchmark.apk";
	private Button btn_start;
	public static int startCount = 0;
	public static int maxCount = 10;
	public static int changeLowCpuFreqCount = 4;
	private WakeLock mWakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antutu_test);
		
		btn_start = (Button)findViewById(R.id.start_btn);
		btn_start.setOnClickListener(this);
		installAntutu();
		
		mWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "videotest");
		mWakeLock.acquire();
		
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "on Destory, release wakelock!");
		if(mWakeLock.isHeld()) {
			mWakeLock.release();
		}
		
		try {
			ArmFreqUtils.setGovernorMode(ArmFreqUtils.INTERACTIVE_MODE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		if(startCount < maxCount) {
			if(startCount == changeLowCpuFreqCount) {
				try {
					LogFileHelper.writeLogWithoutClose("["+SystemUtil.getSystemTime()+"]"+" :AnTuTu TEST START.\n");
					ArmFreqUtils.setGovernorMode(ArmFreqUtils.USERSPACE_MODE);
					ArmFreqUtils.setSpeedFreq(312000);
					Log.d(TAG, "set speedfreq " + String.valueOf(312000));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			btn_start.performClick();
		}else {
			LogFileHelper.writeLogWithoutClose("["+SystemUtil.getSystemTime()+"]"+" :AnTuTu TEST FINISH.\n");
			finish();
		}
		
		super.onResume();
	}

	private int installAntutu() {
		String[] install_cmd = {"pm", "install", "-r", APKPATH};
		ShellExecute exeCute = new ShellExecute();
		String result = exeCute.execute(install_cmd, "/");
		Log.d(TAG, "install result is " + result);
		
		return 0;
	}
	
	private int uninstallAntutu() {
		String[] uninstall_cmd = {"pm", "uninstall", "com.antutu.ABenchMark"};
		ShellExecute exeCute = new ShellExecute();
		String result = exeCute.execute(uninstall_cmd, "/");
		Log.d(TAG, "install result is " + result);
		
		return 0;
	}

	private class ShellExecute {
		/*
		 * args[0] : shell command like "ls" or "ls -1"; args[1] : the command execute directory "/" ;
		 */
		public String execute(String[] cmmand, String directory) {
			String result = "";
			try {
				ProcessBuilder builder = new ProcessBuilder(cmmand);

				if (directory != null)
					builder.directory(new File(directory));
				builder.redirectErrorStream(true);
				Process process = builder.start();

				InputStream is = process.getInputStream();
				byte[] buffer = new byte[1024];
				while (is.read(buffer) != -1) {
					result = result + new String(buffer);
				}
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.start_btn) {
			startCount++;
			startService(new Intent(this, AntutuLocationService.class));
			
		}
	}
}
