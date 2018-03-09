package com.DeviceTest;
/*
 * author @ mw
 */
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.File;
import java.io.IOException;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class VideoTestActivity extends Activity{
	
    private static final String TAG="VideoTestActiviy";
    private static boolean isVideoFirstTest=true;
    private static final int mRequestCode = 1000;
    private static final int mPass = 10;
    private static final int mFail = 20;
    	
       
	@Override	
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.videotest);
		isVideoFirstTest=true;
		ControlButtonUtil.initControlButtonView(this);
		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
		//ControlButtonUtil.Hide();
	}
	
	@Override	
	public void onRestart() {
		super.onRestart();
		findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
	}
	
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        
		if (paramMotionEvent.getAction() == MotionEvent.ACTION_DOWN){
	        Intent intent = new Intent("android.intent.action.VideoTestDetailActivity");
	        startActivityForResult(intent, mRequestCode);
		}
		return super.onTouchEvent(paramMotionEvent);      
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case mPass:
			findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
			break;
		case mFail:
			findViewById(R.id.btn_Pass).performClick();
			break;
		}
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
	
}
