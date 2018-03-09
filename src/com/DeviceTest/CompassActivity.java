package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.SystemUtil;
import com.DeviceTest.view.CompassView;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class CompassActivity extends Activity {

	private CompassView compassView;
	TextView compassText;

	boolean start = false;
	boolean stop = false;
	private SensorManager mSensorManager = null;
	private Sensor aSensor = null;
	private Sensor mSensor = null;
	private SensorEventListener mListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			if (stop) {
				mSensorManager.unregisterListener(this);
			}
			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				accelerometerValues = event.values;
			}
			else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
				magneticFieldValues = event.values;
			}
			Log.d("lcf", "CompassActivity - before getRotationMatrix values[0]: " + values[0] + 
				", values[1]: " + values[1] + 
				", values[2]: " + values[2]);
			SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticFieldValues);
			Log.d("lcf", "CompassActivity - after getRotationMatrix r[0]: " + r[0] + 
				", r[1]: " + r[1] + 
				", r[2]: " + r[2]);
			Log.d("lcf", "CompassActivity - after getRotationMatrix values[0]: " + values[0] + 
				", values[1]: " + values[1] + 
				", values[2]: " + values[2]);
			float[] values2 = new float[3];
			values2 = SensorManager.getOrientation(r, values);
			Log.d("lcf", "CompassActivity - onSensorChanged() values[0]: " + values[0] + 
				", values[1]: " + values[1] + 
				", values[2]: " + values[2]);
			Log.d("lcf", "CompassActivity - onSensorChanged() values2[0]: " + values2[0] + 
				", values2[1]: " + values2[1] + 
				", values2[2]: " + values2[2]);
			values[0] = (float) Math.toDegrees(values[0]);
			if ((int) values[0] == 0 || values[0] == 360) {
				return;
			}
			compassView.update(values[0]);
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	float[] accelerometerValues = new float[3];
	float[] magneticFieldValues = new float[3];
	float[] values = new float[3];
	float[] r = new float[9];
	Handler mHandler = new Handler();
	Runnable mFailedRunnable = new Runnable() {
		public void run() {
			if (stop) {
				return;
			}
			mHandler.removeCallbacks(mFailedRunnable);
			findViewById(R.id.btn_Fail).performClick();
		}
	};
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS)
				+ ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.compasstest);
		compassView = (CompassView) findViewById(R.id.compasstestview);
		compassText = (TextView) findViewById(R.id.compassText);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.registerListener(mListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
		ControlButtonUtil.initControlButtonView(this);
	}

	protected void onStop() {
		super.onStop();
		stop = true;
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}