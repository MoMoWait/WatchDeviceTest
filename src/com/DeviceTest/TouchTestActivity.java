package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.*;

import java.io.File;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.PointerLocationView;
import com.DeviceTest.view.PointerLocationView.OnPointCountChangeListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

public class TouchTestActivity extends Activity {
	TextView mText;
	TextView mTitle;
	PointerLocationView mPointerView;
	private Button passButton;
	private final static int TEST_PASS = 0;
	boolean showButtons = false;
	private GestureDetector mGestureDetector;
	
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.touchtest);

		mPointerView = (PointerLocationView) findViewById(R.id.pointerview);
		mPointerView.setBackgroundColor(Color.TRANSPARENT);

		/*mPointerView.setOnPointCountChangeListener(new OnPointCountChangeListener() {
					public void onPointCountChange(int newPointCount) {
						Log.i("Jeffy", "Count:" + newPointCount);
						if (newPointCount >= 10) {
							handler.sendEmptyMessageDelayed(TEST_PASS, 500);
						}
					}
				});*/
		ControlButtonUtil.initControlButtonView(this);
		passButton = (Button) findViewById(R.id.btn_Pass);
		passButton.setVisibility(View.VISIBLE);

		showButtons = false;
		//ControlButtonUtil.Hide();
		
		mGestureDetector = new GestureDetector(new GestureListener());
	}
	
	/*Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == TEST_PASS){
				passButton.setVisibility(View.VISIBLE);
				passButton.performClick();
			}
		}
	};*/

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	public void toggleButtonShow(){
		showButtons = !showButtons;
		if(showButtons){
			ControlButtonUtil.Show();
		}
		else{
			ControlButtonUtil.Hide();
		}
	}
	
	private class GestureListener implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener{
		public boolean onDown(MotionEvent e){
			return false;
		}
		public void onShowPress(MotionEvent e){

		}
		public boolean onSingleTapUp(MotionEvent e){
			return false;
		}
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
			return false;
		}
		public void onLongPress(MotionEvent e){
		}
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
			return false;
		}
		public boolean onSingleTapConfirmed(MotionEvent e){
			return false;
		}
		public boolean onDoubleTap(MotionEvent e){
			//toggleButtonShow();
			return false;
		}
		public boolean onDoubleTapEvent(MotionEvent e){
			//double click, hide pass/retest/fail button.
			return false;
		}
	}
}