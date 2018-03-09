package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.LcdTestView;

import android.R.color;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.util.Log;

public class LcdTestActivity extends AppBaseActivity {
	private static final String TAG = "LcdTestActivity";
	public static final int MSG_LCD_TESTING = 2;
	public static final int MSG_LCD_TEST_START = 1;
	public static final int MSG_LCD_TES_END = 3;

	LcdTestView mLcdView;
	boolean mTestOn;
	TextView mText;
	TextView mTitle;
	private TextView mTextTime;
	int mTestCount = 0;
	private static final int TEST_COLOR_COUNT = 5;
	private static final int TEST_COLORS[] = new int[TEST_COLOR_COUNT];

	private Canvas mCanvas = new Canvas();
	private Paint mPaint = new Paint();
	private Handler mChangeColorHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				mTextTime.setVisibility(View.GONE);
				mTitle.setVisibility(View.GONE);
				mText.setVisibility(View.GONE);
				mLcdView.setBackgroundColor(TEST_COLORS[mTestCount - 1]);
				break;
			case 6:
				mTextTime.setVisibility(View.VISIBLE);
				mLcdView.setVisibility(View.GONE);
				mLcdView.clearFocus();
				mTitle.setVisibility(View.VISIBLE);
				findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
			    findViewById(R.id.btn_Fail).setVisibility(View.VISIBLE);
			    findViewById(R.id.btn_Retest).setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		};
	};
	
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		TEST_COLORS[0] = Color.WHITE;
		TEST_COLORS[1] = Color.BLACK;
		TEST_COLORS[2] = Color.RED;
		TEST_COLORS[3] = Color.GREEN;
		TEST_COLORS[4] = Color.BLUE;

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.lcdtest);
		mTitle = (TextView) findViewById(R.id.lcdtextTitle);
		mText = (TextView) findViewById(R.id.lcdtestresult);
		mLcdView = (LcdTestView)findViewById(R.id.lcdtestview);
		ControlButtonUtil.initControlButtonView(this);
		ControlButtonUtil.Hide();
		mLcdView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mTestCount++;
				Log.i(TAG, "mTestCount:" + mTestCount);
				mChangeColorHandler.sendEmptyMessage(mTestCount);
				
			}
		});
		mTextTime = (TextView)findViewById(R.id.text_time);
		/*mLcdView.requestFocus();
		mLcdView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					
				}
				return true;
			}
		});*/
	}

	
	protected void onPause() {
		super.onPause();
		endTimer();
		for(int i = 1; i <= 6; ++i)
			mChangeColorHandler.removeMessages(i);
	}

	
	protected void onResume() {
		super.onResume();
		startTimer();
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public void onOverTime() {
		ControlButtonUtil.mControlButtonView.performFailButtonClick();
	}
}