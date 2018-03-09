package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoTestDetailActivity extends Activity  implements SurfaceHolder.Callback{

	private File myRecAudioFile = new File("/data/data/com.DeviceTest/VedioTest.mpg");
	private MediaRecorder recorder;
	private SurfaceView surfaceView1 ;
	private SurfaceHolder surfaceHolder;
	private boolean isStopRecord;
	private VideoView mVideoView;
	
	private final static int RECORD_BEGIN = 0;
	private final static int RECORD_END = 1;
	private final static int PLAYBACK_BEGIN = 2;
	private final static int PLAYBACK_END = 3;
	private static final int mPass = 10;
    private static final int mFail = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);	
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.videotestdetail);
		
		surfaceView1 = (SurfaceView) findViewById(R.id.SurfaceView);
		mVideoView = (VideoView) this.findViewById(R.id.video_test_view);
		mVideoView.setVisibility(View.GONE);
		surfaceHolder = surfaceView1.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		handler.sendEmptyMessageDelayed(RECORD_BEGIN, 500);
	}
	
	private void recorder(){
		try {
			recorder = new MediaRecorder(); 
			recorder.setPreviewDisplay(surfaceHolder.getSurface());
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); 
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  
			recorder.setVideoSize(352,288);
			recorder.setVideoFrameRate(15);   
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); 
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);          
			recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
			recorder.prepare();
			recorder.start();
			isStopRecord = false;
		} catch (IOException e) {
			e.printStackTrace();
			setResult(mFail);
			finish();
		}
	}
	
	private void stop(){
		try {
		if (recorder == null){
			return;
		}
		recorder.stop();
		recorder.release();
		recorder = null;
		isStopRecord = true;
		} catch (Exception e){
			e.printStackTrace();
			setResult(mFail);
			finish();
		}
	}
	
	private void playback(){
		try {
		MediaController mc = new MediaController(this);
        mVideoView.setMediaController(mc);
        mVideoView.setVideoPath(myRecAudioFile.toString());
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				handler.sendEmptyMessageDelayed(PLAYBACK_END,500);
			}
		});
        mVideoView.requestFocus();
        mVideoView.start();
		} catch (Exception e){
			e.printStackTrace();
			setResult(mFail);
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (recorder != null && !isStopRecord){
	  		  recorder.stop();
	  		  recorder.release();
		}
	}
/*	
	private void back(){
		if (recorder != null && !isStopRecord){
  		  recorder.stop();
  		  recorder.release();
		}
  	  	finish();
	}
*/	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECORD_BEGIN:
				recorder();
				handler.sendEmptyMessageDelayed(RECORD_END, 5000);
				break;
			case RECORD_END:
				stop();
				surfaceView1.setVisibility(View.GONE);
				mVideoView.setVisibility(View.VISIBLE);
				handler.sendEmptyMessageDelayed(PLAYBACK_BEGIN, 500);
				break;
			case PLAYBACK_BEGIN:
				playback();
				break;
			case PLAYBACK_END:
				setResult(mPass);
				myRecAudioFile.delete();
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
	        
	}
	public void surfaceCreated(SurfaceHolder holder) {
	        
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
	        
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
	
}
