package com.DeviceTest;

import java.io.File;

import com.DeviceTest.HeadsetMicTestActivity.MBroadcastReceiver;
import com.DeviceTest.PhoneMicTestActivityForLenovo.MyHandler;
import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.Recorder;
import com.DeviceTest.helper.VUMeter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.KeyEvent;

public class HeadsetMicTestActivityForLenovo extends Activity implements
		OnClickListener {
	private static final String TAG = HeadsetMicTestActivityForLenovo.class
			.getSimpleName();

	private final static String ERRMSG = "Record error";
	private final static int PLAYBACK_TIME = 5;

	public final static int MSG_TEST_MIC_ING = 0;
	public final static int MSG_TEST_MIC_OVER = 1;
	public final static int MSG_TEST_MIC_START = 2;
	public final static int MSG_TEST_MIC_ABOVE_LEVEL = 3;
	public final static int MSG_TEST_PLAY_BACK = 4;
	public final static int MSG_TEST_PLAY_BACK_STOP = 5;
	public final static int MSG_SHOW_BUTTON = 6;

	private AudioManager mAudioManager;
	private Handler mHandler;
	private Recorder mRecorder;
	private BroadcastReceiver mHeadsetReceiver;

	private TextView mResult;
	private TextView mText;
	private VUMeter mVUMeter;

	private int mOldVolume;
	private int mRecordTimesNow;
	private boolean mSpeakerOn = false;
	private boolean mHeadSetOn = false;

	private TextView prompt;
	private Button btnStart;
	private int test_group= 0;

	/** test need the voice volunm above the Standard value. */
	//public boolean mIsAbove = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().addFlags(1152);
		setContentView(R.layout.headsetmictest);

		mVUMeter = (VUMeter) findViewById(R.id.uvMeter);
		mResult = (TextView) findViewById(R.id.headsetresultText);
		mResult.setVisibility(View.VISIBLE);
		mResult.setGravity(17);

		mText = (TextView) findViewById(R.id.textSubTitle);
		mText.setText(getString(R.string.HeadsetMicSubTitleForLenove));
		prompt = (TextView) findViewById(R.id.headset_prompt);
		btnStart = (Button) findViewById(R.id.headset_begin);
		prompt.setText(R.string.headset_test_prompt1);
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mRecorder = new Recorder();
		mVUMeter.setRecorder(mRecorder);
		mVUMeter.setHandler(mHandler);

		mHandler = new MyHandler();

		ControlButtonUtil.initControlButtonView(this);
		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
		
		btnStart.setOnClickListener(this);
		stopMediaPlayBack();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.headset_begin:
			stopMediaPlayBack();
			mHeadSetOn = true;
			((TextView) findViewById(R.id.headset_prompt)).setVisibility(View.INVISIBLE);
			((Button) findViewById(R.id.headset_begin)).setVisibility(View.INVISIBLE);
			mHandler.sendEmptyMessageDelayed(MSG_TEST_MIC_START,200);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		DeviceTest.isOnTestAll = true;

		stopMediaPlayBack();

		mOldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

		if (!mHeadSetOn) {
			mResult.setText(getString(R.string.HeadsetTips));
			return;
		}

	}

	@Override
	protected void onPause() {
		mHandler.removeMessages(MSG_SHOW_BUTTON);
		mHandler.removeMessages(MSG_TEST_PLAY_BACK_STOP);
		mHandler.removeMessages(MSG_TEST_PLAY_BACK);
		mHandler.removeMessages(MSG_TEST_MIC_ABOVE_LEVEL);
		super.onPause();
		if(null!=mHeadsetReceiver)
			unregisterReceiver(mHeadsetReceiver);
		DeviceTest.isOnTestAll = true;
		switch (mRecorder.state()) {

		case Recorder.IDLE_STATE:
			mRecorder.delete();
			break;
		case Recorder.PLAYING_STATE:
			mRecorder.stop();
			mRecorder.delete();
			break;
		case Recorder.RECORDING_STATE:
			mRecorder.stop();
			mRecorder.clear();
			break;
		}

		mAudioManager.setStreamVolume(3, mOldVolume, 0);

		if (mSpeakerOn) {
			mAudioManager.setSpeakerphoneOn(false);

		}

	}

	public void stopMediaPlayBack() {
		Intent localIntent = new Intent("com.android.music.musicservicecommand");
		localIntent.putExtra("command", "pause");
		sendBroadcast(localIntent);
	}

	public boolean isSDcardHasSpace() {
		File pathFile = android.os.Environment.getExternalStorageDirectory();
		StatFs statfs = new StatFs(pathFile.getPath());
		if (statfs.getAvailableBlocks() > 1) {
			return true;
		}
		return false;
	}
	
	class MyHandler extends Handler {
		MyHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TEST_MIC_START:
				//removeMessages(MSG_TEST_MIC_START);
				mResult.setText(getString(R.string.PhoneMicSubTitle));
				mRecorder.startRecording(MediaRecorder.OutputFormat.AMR_NB, ".amr");
				sendEmptyMessageDelayed(MSG_TEST_MIC_ING,200);
				break;

			case MSG_TEST_MIC_ING:
				mRecordTimesNow++;
				if (mRecordTimesNow > 5) {
					sendEmptyMessage(MSG_TEST_MIC_OVER);
				} else {
					sendEmptyMessageDelayed(MSG_TEST_MIC_ING, 1000);
				}
				break;

			case MSG_TEST_MIC_OVER:
				mRecorder.stopRecording();
				//removeMessages(MSG_TEST_MIC_OVER);
				mResult.setText(getString(R.string.HeadsetRecodrSuccess));
				sendEmptyMessageDelayed(MSG_TEST_PLAY_BACK, 1000L);
				break;

//			case MSG_TEST_MIC_ABOVE_LEVEL:
//				mIsAbove = true;
//				mResult.setText(R.string.PhoneMicEffective);
//				break;

			case MSG_TEST_PLAY_BACK:
				if (mRecorder.sampleLength() > 0) {
					mResult.setText(R.string.HeadsetRecodrSuccess);
					mRecorder.startPlayback();
				} else {
					mResult.setText(R.string.RecordError);
				}
				sendEmptyMessageDelayed(MSG_TEST_PLAY_BACK_STOP, 5000L);
				break;

			case MSG_TEST_PLAY_BACK_STOP:
				mRecorder.stopPlayback();
				test_group++;
				mHeadSetOn = false;
				mRecordTimesNow = 0;
				if(test_group<2){
					mResult.setText(getString(R.string.HeadsetTips));
					prompt.setText(R.string.headset_test_prompt2);
					((TextView) findViewById(R.id.headset_prompt)).setVisibility(View.VISIBLE);
					((Button) findViewById(R.id.headset_begin)).setVisibility(View.VISIBLE);
				}
				else {
					sendEmptyMessage(MSG_SHOW_BUTTON);
				}
				break;
			case MSG_SHOW_BUTTON:
				prompt.setText(R.string.headset_result);
				((Button)findViewById(R.id.headset_begin)).setVisibility(View.INVISIBLE);
				((Button) findViewById(R.id.btn_Pass)).setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}

			mVUMeter.invalidate();
		}

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

}
