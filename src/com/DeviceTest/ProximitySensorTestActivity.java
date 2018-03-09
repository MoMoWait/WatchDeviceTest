package com.DeviceTest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.DeviceTest.helper.ControlButtonUtil;

import jp.megachips.frizzservice.Frizz;
import jp.megachips.frizzservice.FrizzEvent;
import jp.megachips.frizzservice.FrizzListener;
import jp.megachips.frizzservice.FrizzManager;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;


/**
 * @author caijq
 * @date 2015-01-27
 */

public class ProximitySensorTestActivity extends Activity implements FrizzListener {
    /**
     * Called when the activity is first created.
     */
    private static final String TAG = "ProximitySensorTestActivity";
//    private SensorManager sensorManager;

    private static final int MSG_STATUS_COME = 1;

    private static final int MSG_STATUS_AWAY = 2;

    /*
    private SensorManager mgr;
    private Sensor proximity;
    */
    private FrizzManager mFrizzManager;

    private PowerManager localPowerManager = null;//电源管理对象
    private PowerManager.WakeLock localWakeLock = null;//电源锁
    private PowerManager.WakeLock mWakeLock = null;//电源锁

    private ImageView imageView1;
    boolean isHasCome = false;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        setTitle(getTitle() + "----("
                + getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.proximitysensortest);
        ControlButtonUtil.initControlButtonView(this);

        /*
        this.mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        this.proximity = this.mgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        */
        mFrizzManager = FrizzManager.getFrizzService(this);
        mFrizzManager.debug(this, false);

        imageView1 = (ImageView) findViewById(R.id.imageView1);

        localPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag 
        localWakeLock = this.localPowerManager.newWakeLock(32, "MyPower");//第一个参数为电源锁级别，第二个是日志tag
        localWakeLock.setReferenceCounted(false);

        mWakeLock = localPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                getClass().getCanonicalName());
        mWakeLock.acquire();
        Log.d(TAG, "onCreate.........");

    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    protected void onResume() {
        super.onResume();
        findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);

        /*
        this.mgr.registerListener(mSensorListener, this.proximity,
                SensorManager.SENSOR_DELAY_NORMAL);
                */
        mFrizzManager.registerListener(this, Frizz.Type.SENSOR_TYPE_PROXIMITY, 10000);
        Log.d(TAG, "onResume.........");
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        /*if (!localWakeLock.isHeld()) {
            localWakeLock.acquire();// 申请设备电源锁
        }*/
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d(TAG, "onPause.........");
//        this.mgr.unregisterListener(mSensorListener, this.proximity);
        mFrizzManager.unregisterListener(this, Frizz.Type.SENSOR_TYPE_PROXIMITY);
        localWakeLock.release();
        releaseWakeLock();
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STATUS_COME://onNear
                    isHasCome = true;
                    imageView1.setImageResource(R.drawable.proximity_on);
                /*if (localWakeLock.isHeld()) {
                    return;
                } else{
                    localWakeLock.acquire();// 申请设备电源锁
                }*/
                    break;
                case MSG_STATUS_AWAY://onAway
                    imageView1.setImageResource(R.drawable.proximity_off);
                    if (isHasCome) {
                        findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
                        //((Button) findViewById(R.id.btn_Pass)).performClick();
                    }
                /*if (localWakeLock.isHeld()) {
                    return;
                } else{
                    localWakeLock.release(); // 释放设备电源锁
                }*/
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /*
    private SensorEventListener mSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "onSensorChanged...: event.values[0]="
                    + event.values[0] + ",event.values[1]=" + event.values[1]
                    + ",event.values[2]=" + event.values[2]);
            if (event.values[0] > 2.5f)
                myHandler.sendEmptyMessage(MSG_STATUS_AWAY);
            else
                myHandler.sendEmptyMessage(MSG_STATUS_COME);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // ignore
        }
    };
    */
    @Override
    public void onFrizzChanged(FrizzEvent event) {
        Frizz.Type frizzType = event.sensor.getType();
        if (Frizz.Type.SENSOR_TYPE_PROXIMITY == frizzType) {
            if (event.values[0] > 6F) //2.5f)
                myHandler.sendEmptyMessage(MSG_STATUS_AWAY);
            else
                myHandler.sendEmptyMessage(MSG_STATUS_COME);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}
