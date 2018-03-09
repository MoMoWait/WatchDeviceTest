package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.Recorder;
import com.DeviceTest.helper.VUMeter;

public class HeadsetMicTestActivity extends Activity {

	private static final String TAG = HeadsetMicTestActivity.class
			.getSimpleName();

	//Use this to set Mic key should test or not.
	private final boolean needTestMicKey=true;
	private static final int MSG_TEST_MIC_ING = 8738;
	private static final int MSG_TEST_MIC_OVER = 13107;
	private static final int MSG_TEST_MIC_INTERRUPT = 1310;
	private static final int MSG_TEST_MIC_START = 4369;
	private boolean isSDcardTestOk = false;
	//private boolean isTestStart = false;
	private AudioManager mAudioManager;
	private Handler mHandler;
	private boolean mHeadSetOn = false;
	private BroadcastReceiver mHeadsetReceiver;
	private boolean mIsRecording = false;
	private int mOldVolume;
	private Recorder mRecorder;
	private TextView mResult;
	private TextView mText;
	private int mTimes = 0;
	private RelativeLayout ll;
	
	private Button btn_useless;
	private VUMeter mVUMeter;

	public HeadsetMicTestActivity() {

		this.mHeadsetReceiver = new MBroadcastReceiver();

		this.mHandler = new MyHandler();
	}

	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.headsetmictest);
		
		ll = (RelativeLayout) findViewById(R.id.relativeLayout_prompt);
		ll.setVisibility(View.GONE);
		
		this.mResult = (TextView) findViewById(R.id.headsetresultText);

		this.mResult.setVisibility(View.VISIBLE);
		this.mResult.setGravity(17);
		ControlButtonUtil.initControlButtonView(this);
		
	
		this.mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

	}

	
	protected void onResume() {

		super.onResume();
//		this.isSDcardTestOk = false;
//		if (!Environment.getExternalStorageState().equals(
//				Environment.MEDIA_MOUNTED)) {
//			this.mResult.setText("Please insert sdcard");
//			return;
//		}
//
//		if (!isSDcardHasSpace()) {
//
//			this.mResult.setText("sdcard has no space");
//			stopMediaPlayBack();
//			return;
//
//		}
		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);

		IntentFilter localIntentFilter = new IntentFilter(
				"android.intent.action.HEADSET_PLUG");
		registerReceiver(this.mHeadsetReceiver, localIntentFilter);
		
		stopMediaPlayBack();
		this.isSDcardTestOk = true;
		this.mOldVolume = this.mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		this.mHeadSetOn = this.mAudioManager.isWiredHeadsetOn();
		if (!mHeadSetOn) {
			mResult.setText(getString(R.string.HeadsetTips));
			return;
		}
	//	mIsRecording = true;
		mHandler.sendEmptyMessage(MSG_TEST_MIC_START);

	}
	
	
	protected void onPause() {
		 super.onPause();
	

		if (!mHeadSetOn&& mRecorder==null ) {
			return;
			
		}
		
	   
	    if (this.isSDcardTestOk)
	    {
	     
	      switch (this.mRecorder.state()) {

			case Recorder.IDLE_STATE:
				this.mRecorder.delete();
				break;
			case Recorder.PLAYING_STATE:
				this.mRecorder.stop();
				this.mRecorder.delete();
				break;
			case Recorder.RECORDING_STATE:
				this.mRecorder.stop();
				this.mRecorder.clear();
				break;
			}
	      
	      unregisterReceiver(mHeadsetReceiver);
	      mAudioManager.setStreamVolume(3, mOldVolume, 0);
	    }
	    
	}

	public boolean isSDcardHasSpace() {
		File pathFile = android.os.Environment.getExternalStorageDirectory();
		StatFs statfs = new StatFs(pathFile.getPath());
		if (statfs.getAvailableBlocks() > 1) {
			return true;
		}
		return false;
	}

	public void stopMediaPlayBack() {
		Intent localIntent = new Intent("com.android.music.musicservicecommand");
		localIntent.putExtra("command", "pause");
		sendBroadcast(localIntent);
	}

	class MyHandler extends Handler {
		MyHandler() {

		}

		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			switch (msg.what) {

			case MSG_TEST_MIC_START:
				Log.i(TAG, "MSG_TEST_MIC_START");
				HeadsetMicTestActivity.this.mRecorder = new Recorder();
				
				mVUMeter = (VUMeter) findViewById(R.id.uvMeter);
				mVUMeter.setRecorder(mRecorder);
				mVUMeter.setHandler(mHandler);
				
				mIsRecording=true;
				
				int maxVolume = HeadsetMicTestActivity.this.mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				HeadsetMicTestActivity.this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						maxVolume, 0);
				
				
				//isTestStart = true;
				removeMessages(MSG_TEST_MIC_START);
				mTimes = 3;
				mResult.setText(" " + mTimes + " ");
				mRecorder.startRecording(3, ".amr");
				sendEmptyMessageDelayed(MSG_TEST_MIC_ING, 1000L);
				break;
			case MSG_TEST_MIC_ING:
				removeMessages(MSG_TEST_MIC_ING);
				Log.i(TAG, "MSG_TEST_MIC_ING");
				if (mTimes > 0) {
					if(mIsRecording)
						{
					mTimes--;
					mResult.setText(" " + mTimes + " ");
					Log.i(TAG, "mTimes=" + mTimes);

					sendEmptyMessageDelayed(MSG_TEST_MIC_ING, 1000L);
					}
				} else {
					mRecorder.stopRecording();
					mIsRecording=false;
					if(!needTestMicKey)
						sendEmptyMessage(MSG_TEST_MIC_OVER);
					else
					{
						mResult.setText(R.string.HeadsetMicPlay);
					}
				}
				break;
			case MSG_TEST_MIC_OVER:
				Log.i(TAG, "MSG_TEST_MIC_OVER");
				removeMessages(MSG_TEST_MIC_OVER);
				if (null !=mRecorder ) {
					
					if (mRecorder.sampleLength() > 0) {
						
						mRecorder.startPlayback();
						if(!needTestMicKey)
							findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
					} else {
						mResult.setText(R.string.record_error);
					}

				}

				break;

			case MSG_TEST_MIC_INTERRUPT:
				removeMessages(MSG_TEST_MIC_INTERRUPT);
				Log.i(TAG, "MSG_TEST_MIC_INTERRUPT");
				mRecorder.stopRecording();
				mRecorder.clear();
				mIsRecording=false;
				mResult.setText(R.string.record_error);
				break;
			}

		}

	}

	class MBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context paramContext, Intent paramIntent) {

			String action = paramIntent.getAction();
			Log.i(TAG, "action");
			if ("android.intent.action.HEADSET_PLUG".equals(action)) {
				if (paramIntent.getIntExtra("state", 0) != 1) {

					Log.i(TAG, "HEADSET has bean removed");
					
					if (mIsRecording)
						mHandler.sendEmptyMessage(MSG_TEST_MIC_INTERRUPT);
					//mIsRecording = false;
					//return;
				}
				else
				{
					Log.i(TAG, "HEADSET has bean inserted");
					if (!mIsRecording) {
						mHandler.sendEmptyMessage(MSG_TEST_MIC_START);
					}
				}
			}

		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.i(TAG, " dispatchKeyEvent:event.getKeyCode()="+(event.getKeyCode()));
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
			if(event.getAction()==KeyEvent.ACTION_UP)
			{
			
				Log.i(TAG, " KeyEvent.KEYCODE_HEADSETHOOK");
						
				if (needTestMicKey && !mIsRecording)
				{
					findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
					mHandler.sendEmptyMessage(MSG_TEST_MIC_OVER);
					//mIsRecording = false;
					
				}
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
