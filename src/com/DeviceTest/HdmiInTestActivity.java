package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.LcdTestView;
import android.os.UEventObserver;

public class HdmiInTestActivity extends Activity {
    private final static String TAG = "HDMIINTEST";
    
    private final static int HDMI_IN_START = 1;
    private final static int HDMI_IN_FINISH = 2;
//    private LcdTestView mTestView;
    private TextView mTitle;
    private TextView mResult;
    private int key = 0;
//    private TextView mShowTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.hdmiintest);

//       mTestView = (LcdTestView) findViewById(R.id.lcdtestview);
        mResult = (TextView) findViewById(R.id.result);
//        mShowTime = (TextView) findViewById(R.id.TimeShow);
        ControlButtonUtil.initControlButtonView(this);
        ((Button) findViewById(R.id.btn_Pass)).setVisibility(View.INVISIBLE);
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        if(key == 2){
        	ControlButtonUtil.Show();
        	mResult.setText(R.string.HdmiInResult);
        }
//        mResult.setText(R.string.HdmiInNoInsert);
//        Log.i(TAG, "Hdmi no insert");
    }
*/
    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(HDMI_IN_START);
        mHandler.removeMessages(HDMI_IN_FINISH);
    }
    
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HDMI_IN_START:
                	key = 1;
                    sendEmptyMessageDelayed(HDMI_IN_FINISH, 500);
                    break;
                case HDMI_IN_FINISH:
                	this.removeMessages(HDMI_IN_START);
                	key = 2;
                	finishHdmiInTest();
                    break;
                default:
                    break;
            }
        }
    };
    
    public void finishHdmiInTest() {
        //((Button) findViewById(R.id.btn_Pass)).setVisibility(View.VISIBLE);
        ControlButtonUtil.Show();
//        mShowTime.setVisibility(View.GONE);
//        mTestView.setVisibility(View.GONE);
        mResult.setText(R.string.HdmiInResult);
    }
    
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        //WAIT FOR MODIFYING
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
        	mHandler.sendEmptyMessageDelayed(HDMI_IN_START, 500);
        	return true;
        }
        return super.dispatchKeyEvent(event);
    }
   
}
