package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.KeyTestView;

/*
 * author @ mw
 */

public class KeyboardActivity extends Activity{
	
	private KeyTestView mKeyTestView;
	private static final String TAG = "KeyboardActivity";
	private final static int OK = 0;
	private final static int CHECK = 1;
	private boolean pb0, pb1, pb2, pb3=false;
	
	private String mKeyNames[] = {	"VOLUME+", 
									"VOLUME-",
									/*"HOME", */
									"MENU", 
									"BACK",
									/*"SEARCH"*/};
	private int mKeyCodes[] = {		KeyEvent.KEYCODE_VOLUME_UP,
									KeyEvent.KEYCODE_VOLUME_DOWN,	
									/*KeyEvent.KEYCODE_HOME,*/
									KeyEvent.KEYCODE_MENU,
									KeyEvent.KEYCODE_BACK,
									/*KeyEvent.KEYCODE_SEARCH*/};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.keyboard);
		mKeyTestView = (KeyTestView)findViewById(R.id.keytestview);
		if(mKeyNames.length == mKeyCodes.length){
			for(int i = 0; i < mKeyNames.length; i ++){
				mKeyTestView.addKey(mKeyNames[i], mKeyCodes[i]);
			}
		}
		ControlButtonUtil.initControlButtonView(this);
		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(TAG, " _____________---- dispatchKeyEvent(),   " + event.getKeyCode());
		switch(event.getAction()){
		case KeyEvent.ACTION_DOWN:
			mKeyTestView.setKeyDown(event.getKeyCode());
			break;
		case KeyEvent.ACTION_UP:
			mKeyTestView.setKeyUp(event.getKeyCode());
			if(KeyEvent.KEYCODE_VOLUME_DOWN==event.getKeyCode())
				pb0=true;
			if(KeyEvent.KEYCODE_VOLUME_UP==event.getKeyCode())
				pb1=true;
			if(KeyEvent.KEYCODE_MENU==event.getKeyCode())
				pb2=true;
			if(KeyEvent.KEYCODE_BACK==event.getKeyCode())
				pb3=true;
			handler.sendEmptyMessage(CHECK);
			break;
		case KeyEvent.KEYCODE_BACK:
			return false;
		}
		return true;
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK:
				check_status();
				break;
			case OK:
				findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
				((Button)findViewById(R.id.btn_Pass)).performClick();
				break;
			}
		}
	};

	private void check_status() {
		if(pb0 && pb1 && pb2 && pb3){
			handler.sendEmptyMessage(OK);
		}
	}
	
}
