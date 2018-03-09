package com.DeviceTest.StressTest;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.DeviceTest.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayFromSDActivity extends Activity {

private static final String TAG = "VideoPlayActivity";
	
	private VideoView mVideoView;
	private String mPath = null;
	private int mTime; //hours
	private int mAutoTestFlag = 0;
	private WakeLock mWakeLock;
	private final static int MSG_SHOW_DIALOG = 0;
	private BroadcastReceiver mUsbStateReceiver;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		mWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "videotest");
		mWakeLock.acquire();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		setContentView(R.layout.activity_video_play);
		
		init();
//		LogFileHelper.writeLogWithoutClose("["+SystemUtil.getSystemTime()+"]"+" :VIDEO TEST START.\n");
	
		//playVideo();
/*		
		registerReceiver(ACDCDetectedReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
*/		
	};
	
	@Override
	protected void onResume() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addDataScheme("file");
		registerReceiver(mUsbStateReceiver, intentFilter);
 
       // mStorageManager.registerListener(mStorageListener);
       // sBuilder1 = new StringBuilder();
       // sBuilder2 = new StringBuilder();
		findVideo();
		super.onResume();
	}
	
	protected void findVideo(){
		LinkedList list = new LinkedList();
		File dir = new File("/mnt/usb_storage");
		File file[] = dir.listFiles();
		if(file.length==0){
			Toast.makeText(this, "请插入包含测试视频video.mp4的SD卡!", 1).show();
		}
		else{
			for (int i = 0; i < file.length; i++) {
				if (file[i].isDirectory()) 
					list.add(file[i]);
				else{
					String path = file[i].getAbsolutePath();
					if(checkFile(path)){
						mPath = path;
						playVideo(mPath);
						return;
					}
				}		
			}
			File tmp;
			while (!list.isEmpty()) {
				tmp = (File) list.removeFirst();
				if (tmp.isDirectory()) {
					file = tmp.listFiles();
					if (file == null) continue;
					for (int i = 0; i < file.length; i++) {
						if (file[i].isDirectory()) 
							list.add(file[i]);
						else{
							String path = file[i].getAbsolutePath();
							if(checkFile(path)){
								mPath = path;
								playVideo(mPath);
								return;
							}
						}
					}
				}
			}
		}
	}
	
	private boolean checkFile(String path) {
		Pattern pattern = Pattern.compile(".*(video.mp4$)");
		Matcher matcher = pattern.matcher(path);
		if(matcher.find())
			return true;
		else 
			Toast.makeText(this, "请插入包含测试视频video.mp4的SD卡!", 1).show();
		return false;
	}

	/*	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(ACDCDetectedReceiver);
	}
*/	
	private void init() {
		//mPath = getIntent().getStringExtra("path");
		mAutoTestFlag = getIntent().getIntExtra("auto", 0);
		if (mAutoTestFlag != 0) {
			mTime = getIntent().getIntExtra("time", 0);
			mHandler.sendEmptyMessageDelayed(1, mTime*60*60*1000);
		}
		
		mVideoView = (VideoView) this.findViewById(R.id.video_view);
		Button stopBtn = (Button) findViewById(R.id.stop_btn);
		stopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stopVideo();
			}
		});
		
	}
	
	private void playVideo(String path) {
		if (null == (mPath = path)) {
			Toast.makeText(this, R.string.error_video , Toast.LENGTH_LONG).show();
			return;
		}
//		LogFileHelper.writeLogWithoutClose("["+SystemUtil.getSystemTime()+"]"+" :PLAY VIDEO ="+mPath+".\n");
        MediaController mc = new MediaController(this);
        mVideoView.setMediaController(mc);
        //videoView.setVideoURI(Uri.parse(""));
        mVideoView.setVideoPath(mPath);
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				mVideoView.start();
			}
		});
        mVideoView.requestFocus();
        mVideoView.start();
	}
	
	private void stopVideo() {
		mVideoView.stopPlayback();
		mWakeLock.release();
//		LogFileHelper.writeLogWithoutClose("["+SystemUtil.getSystemTime()+"]"+" :VIDEO TEST FINISH.\n");
		finish();
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SHOW_DIALOG:
//				ShowACDialog();
				break;
			case 1:
				if(mAutoTestFlag == 1)
					stopVideo();
				break;

			default:
				break;
			}
		};
	};
	
	
/*	
	private void ShowACDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.AC_NO_IN_MSG)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
							}
						})
				
				;
		mDialog = builder.create();
		mDialog.show();
		
	}
*/	
	class UsbConnectedBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "intent.getAction()"+intent.getAction());
			 if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
				Log.v("USB","198 UsbConnectedBroadcastReceiver");
				 findVideo();
				 
	            }else{
		            return;	
	            }
		}
	};
	
/*	
	private BroadcastReceiver ACDCDetectedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				int plugType = intent.getIntExtra("plugged", 0);
				if (plugType > 0) {
					if (mDialog != null && mDialog.isShowing()) {
						mDialog.dismiss();
					}
				} else {
					//AC NOT IN
					//topVideo(); //LIMI 8.15
					mHandler.sendEmptyMessage(MSG_SHOW_DIALOG);	
				}
			}
		}
	};
*/
	
}
