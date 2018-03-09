package com.DeviceTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.DeviceTest.R;
import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayActivity extends Activity {
	
	private static final String TAG = "VideoPlayActivity";
	
	private final String VIDEO_PATH_CONFIG = "video.path";
	
	private VideoView mVideoView;
	private String mPath = null;
	private WakeLock mWakeLock;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		mWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "videotest");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		setContentView(R.layout.activity_video_play);
		
		ControlButtonUtil.initControlButtonView(this);
		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
		findViewById(R.id.btn_Fail).setVisibility(View.INVISIBLE);
		findViewById(R.id.btn_Retest).setVisibility(View.INVISIBLE);
		
		getVideoPathConfig(getApplicationContext());
		init();
		playVideo();
	};

	protected void onResume(){
		super.onResume();
		if (mWakeLock.isHeld() == false){
			mWakeLock.setReferenceCounted(false);
			mWakeLock.acquire();
		}
	}

	protected void onPause(){
		super.onPause();
		if(mWakeLock.isHeld()){
			mWakeLock.release();
		}
	}

	private void init() {
		mVideoView = (VideoView) this.findViewById(R.id.video_view);
		Button stopBtn = (Button) findViewById(R.id.stop_btn);
		stopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stopVideo();
			}
		});
	}
	
	private void playVideo() {
		if (mPath == null) {
			Toast.makeText(this, R.string.error_video, Toast.LENGTH_LONG).show();
			findViewById(R.id.btn_Fail).performClick();
			return;
		} 
		File file = new File(mPath);
		if(!file.exists()) {
			Toast.makeText(this, R.string.video_not_exist, Toast.LENGTH_LONG).show();
			findViewById(R.id.btn_Fail).performClick();
			return;
		}
		try {
			MediaController mc = new MediaController(this);
			mVideoView.setMediaController(mc);
			mVideoView.setVideoPath(mPath);
			mVideoView.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mVideoView.start();
				}
			});
			mVideoView.requestFocus();
			mVideoView.start();
		} catch(Exception e) {
			e.printStackTrace();
			findViewById(R.id.btn_Fail).performClick();
		}
	}
	
	private void stopVideo() {
		try {
			mVideoView.stopPlayback();
			findViewById(R.id.btn_Pass).performClick();
		} catch(Exception e) {
			e.printStackTrace();
			findViewById(R.id.btn_Fail).performClick();
		}
	}
	
	public void getVideoPathConfig(Context c) {
		String temp;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(c.getAssets().open(VIDEO_PATH_CONFIG)));

			while ((temp = br.readLine()) != null) {
				String[] path = temp.split(":");
				mPath = path[1];
			}
			
			if (br != null) {
				br.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
