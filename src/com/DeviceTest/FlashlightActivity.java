package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.File;
import java.math.BigInteger;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.util.Log;
import android.widget.ImageView;

public class FlashlightActivity extends Activity {
        private static final String TAG = "FlashlightActivity";

	private FlashlightSurface mSurface;

        protected void onCreate(Bundle savedInstanceState) {

                super.onCreate(savedInstanceState);


                setTitle(getTitle() + "----("
                                + getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
                //requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

                setContentView(R.layout.flashlighttest);
                ControlButtonUtil.initControlButtonView(this);
		mSurface = (FlashlightSurface) findViewById(R.id.surfaceview);
        }


        protected void onResume() {
                super.onResume();
		mSurface.setFlashlightSwitch(true);
        }

	protected void onPause() {
                super.onPause();
                mSurface.setFlashlightSwitch(false);
        }

	protected void onDestroy() {
                super.onDestroy();
                
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        return false;
                }
                return super.dispatchKeyEvent(event);
        }
}
