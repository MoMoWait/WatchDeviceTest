package com.DeviceTest;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.widget.TextView;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.GsensorBall;

import jp.megachips.frizzservice.Frizz;
import jp.megachips.frizzservice.FrizzEvent;
import jp.megachips.frizzservice.FrizzListener;
import jp.megachips.frizzservice.FrizzManager;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

/**
 * @author LanBinYuan
 * @date 2011-06-11
 * @date 2015-02-03 caijq
 */

public class GsensorTestActivity extends AppBaseActivity implements FrizzListener {
	/** Called when the activity is first created. */
	private static final String TAG = "GsensorTestActivity";
	private final static int MAX_NUM = 5;
	private final static int MIN_NUM =- 5;
	/*
	private SensorManager sensorManager;
	private SensorEventListener lsn = null;
	*/
	private FrizzManager mFrizzManager;
	boolean stop = false;
	private static enum TEST_AXIS {
		X_UP, Y_UP, Z_UP,X_DOWN,Y_DOWN,Z_DOWN, D
	};
	private TextView Xup_textView, Yup_textView, Zup_textView;
	private TextView Xdown_textView, Ydown_textView, Zdown_textView;
	private  TextView subTitle;
	private TEST_AXIS testAxis;
	private GsensorBall mGsensorBall;
	private boolean isPhone = true;
	private Handler mHandler;
	private SensorManager mSensorManager;
	private Sensor mGSensor;
	private SensorEventListener mGsenorEventListener;
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.gsensortest);
		mHandler = new Handler(Looper.getMainLooper());
		stop = false;
		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		ControlButtonUtil.initControlButtonView(this);

		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		if(mSensorManager == null)
			ControlButtonUtil.mControlButtonView.performFailButtonClick();
		List<Sensor> gravitySensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(gravitySensors == null || gravitySensors.size() == 0)
			ControlButtonUtil.mControlButtonView.performPassButtonClick();
		mGSensor = gravitySensors.get(0);
		subTitle = (TextView) findViewById(R.id.Accelerometer);
		subTitle.setTextColor(Color.rgb(255, 0, 0));
		subTitle.setText("x:" +0+ ", y:"+0+ ", z:" + 0);
		startTimer();
//		Xup_textView = (TextView) findViewById(R.id.gsensorTestXup);
//		Xup_textView.setTextColor(android.graphics.Color.GREEN);
//
//		Yup_textView = (TextView) findViewById(R.id.gsensorTestYup);
//		Yup_textView.setTextColor(android.graphics.Color.GREEN);
//
//		Zup_textView = (TextView) findViewById(R.id.gsensorTestZup);
//		Zup_textView.setTextColor(android.graphics.Color.GREEN);
//
//		Xdown_textView = (TextView) findViewById(R.id.gsensorTestXdown);
//		Xdown_textView.setTextColor(android.graphics.Color.GREEN);
//
//		Ydown_textView = (TextView) findViewById(R.id.gsensorTestYdown);
//		Ydown_textView.setTextColor(android.graphics.Color.GREEN);
//
//		Zdown_textView = (TextView) findViewById(R.id.gsensorTestZdown);
//		Zdown_textView.setTextColor(android.graphics.Color.GREEN);

		
//		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		mFrizzManager = FrizzManager.getFrizzService(this);
		mFrizzManager.debug(this, false);
		mGsensorBall = (GsensorBall)findViewById(R.id.gsensorball);

		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		isPhone = rotation == Surface.ROTATION_90;
		Log.i(TAG, "onCreate->x:" +0+ ", y:"+0+ ", z:" + 0);
		Log.i(TAG, "onCreate->isPhone:" + isPhone);
//		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
	}

	

	
	protected void onResume() {
		super.onResume();
		
		// findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);	
		stop = false;
		mGsenorEventListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				Log.i(TAG, "onSensorChanged->x:" + event.values[0]);
				Log.i(TAG, "onSensorChanged->y:" + event.values[1]);
				Log.i(TAG, "onSensorChanged->z:" + event.values[2]);
				
				subTitle.setText("x:" + (int) event.values[0] + ", y:" + (int) event.values[1] + ", z:" + (int) event.values[2]);
				mGsensorBall.setXYZ(event.values[0], event.values[1], event.values[2]);
				if(Math.abs(event.values[0]) >= 9.0f || Math.abs(event.values[1]) >= 9.0f || Math.abs(event.values[2]) >= 9.0f){
					ControlButtonUtil.mControlButtonView.performPassButtonClick();
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				
			}
		};
		if(mGSensor != null)
			mSensorManager.registerListener(mGsenorEventListener, mGSensor, SensorManager.SENSOR_DELAY_FASTEST, null);
	//	setTestAxis(TEST_AXIS.X_DOWN);

		/*
		lsn = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}

			public void onSensorChanged(SensorEvent e) {
				if(stop) {
					return;
				}
				//subTitle.setText("x:" + (int) e.values[0] + ", y:"
				//		+ (int) e.values[1] + ", z:" + (int) e.values[2]);
				float[] values = (isPhone ? e.values : switchXY(e.values));
				
//				if(Math.abs(values[0])>Math.abs(values[1]) && (int)values[0]>=0){
//					 setTestAxis(TEST_AXIS.X_UP);
//                                }
//                                if(Math.abs(values[0])>Math.abs(values[1]) && (int)values[0]<0){
//					setTestAxis(TEST_AXIS.X_DOWN);
//                                }
//                                if(Math.abs(values[0])<Math.abs(values[1]) && (int)values[1]>=0){
//					setTestAxis(TEST_AXIS.Y_UP);
//                                }
//                                if(Math.abs(values[0])<Math.abs(values[1]) && (int)values[1]<0){
//					setTestAxis(TEST_AXIS.Y_DOWN);
//                                }
				subTitle.setText("x:" + (int) values[0] + ", y:"
						+ (int) values[1] + ", z:" + (int) values[2]);
			//	doTest(values);
//				findViewById(R.id.screen_up_prompt).setVisibility(View.VISIBLE);
				mGsensorBall.setXYZ(values[0], values[1], values[2]);
			}

		};

		Sensor sensors = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sensorManager.registerListener(lsn, sensors,
				SensorManager.SENSOR_DELAY_NORMAL);
				*/
		try{
			mFrizzManager.registerListener(this, Frizz.Type.SENSOR_TYPE_ACCELEROMETER, 10000);
		}catch(Exception e){
			//no handle
		}
		
	}

	private float[] switchXY(float[] values){
		float[] result = new float[3];
		result[0] = values[1];
		result[1] = values[0] * -1;
		result[2] = values[2];
		return result;
	}

	private float[] scale(float[] values) {
		float[] result = new float[3];
		result[0] = values[0] * 10;
		result[1] = values[1] * 10;
		result[2] = values[2] * 10;
		return result;
	}

	private void doTest(float[] values) {
		switch (testAxis) {
		case X_DOWN:
			if ((int) values[0] <= MIN_NUM/* && (int) e.values[1] == 0
					&& (int) e.values[2] == 0*/) {
//				setTestAxis(TEST_AXIS.X_UP);
			//	Xdown_textView.setText(Xdown_textView.getText() + ":Pass");
			}
			break;
		case X_UP:
			if ((int) values[0] >= MAX_NUM /*&& (int) e.values[1] == 0
					&& (int) e.values[2] == 0*/) {
//				setTestAxis(TEST_AXIS.Y_DOWN);
			//	Xup_textView.setText(Xup_textView.getText() +":Pass ");
			}
			break;
		case Y_DOWN:
			if (/*(int) e.values[0] == 0 && */(int) values[1] <= MIN_NUM
					/*&& (int) e.values[2] == 0*/) {
//				setTestAxis(TEST_AXIS.Y_UP);
			//	Ydown_textView.setText(Ydown_textView.getText() + ":Pass  ");
			}
			break;
		case Y_UP:
			if (/*(int) e.values[0] == 0 && */(int) values[1] >= MAX_NUM
					/*&& (int) e.values[2] == 0*/) {
				//setTestAxis(TEST_AXIS.Z_DOWN);
//				setTestAxis(TEST_AXIS.Z_UP);
			//	Yup_textView.setText(Yup_textView.getText() + ":Pass  ");
			}
			break;
		/*case Z_DOWN:
			if ((int) e.values[0] == 0 && (int) e.values[1] == 0
					&& (int) e.values[2] <= MIN_NUM) {
				setTestAxis(TEST_AXIS.Z_UP);
				Zdown_textView.setText(Zdown_textView.getText() + ":Pass");
	//			findViewById(R.id.btn_Pass).performClick();
			}
						
			break;*/
		case Z_UP:
			if ((int) values[0] == 0 && (int) values[1] == 0
					&& (int) values[2] >= MAX_NUM) {
//				setTestAxis(TEST_AXIS.D);
	//			Zup_textView.setText(Zup_textView.getText() + ":Pass");
	//			findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE); 
			}
			break;

		default:
			break;
		}
	}

/*	private void setTestAxis(TEST_AXIS testAxis) {
		this.testAxis = testAxis;
		switch (testAxis) {
		case X_UP:
	//		findViewById(R.id.gsensorTestXup).setVisibility(View.VISIBLE);
			//setGraphicGone();
			findViewById(R.id.arrow).setVisibility(View.VISIBLE);
			((ImageView) findViewById(R.id.arrow)).setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
			break;
		case X_DOWN:
	//		findViewById(R.id.gsensorTestXdown).setVisibility(View.VISIBLE);
			findViewById(R.id.arrow).setVisibility(View.VISIBLE);
			((ImageView) findViewById(R.id.arrow)).setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
			break;
		case Y_UP:
	//		findViewById(R.id.gsensorTestYup).setVisibility(View.VISIBLE);
			findViewById(R.id.arrow).setVisibility(View.VISIBLE);
			((ImageView) findViewById(R.id.arrow)).setImageDrawable(getResources().getDrawable(R.drawable.arrow_left));
			break;
		case Y_DOWN:
	//		findViewById(R.id.gsensorTestYdown).setVisibility(View.VISIBLE);
			findViewById(R.id.arrow).setVisibility(View.VISIBLE);
			((ImageView) findViewById(R.id.arrow)).setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
			break;
		case Z_UP:
	//		findViewById(R.id.gsensorTestZup).setVisibility(View.VISIBLE);
	//		setGraphicGone();
			findViewById(R.id.screen_up_prompt).setVisibility(View.VISIBLE);
			break;
		case Z_DOWN:
			Log.e("lcf", "Z_DOWN");
	//		findViewById(R.id.gsensorTestZdown).setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}*/
//	private void setGraphicGone(){
//		findViewById(R.id.arrow).setVisibility(View.GONE);
//		findViewById(R.id.screen_up_prompt).setVisibility(View.GONE);
//
//	}

	//
	
	protected void onPause() {
		super.onPause();
		if(mGSensor != null)
			mSensorManager.unregisterListener(mGsenorEventListener);
//		sensorManager.unregisterListener(lsn);
		try{
			mFrizzManager.unregisterListener(this, Frizz.Type.SENSOR_TYPE_ACCELEROMETER);
		}catch(Exception e){
			//no handle
		}
		
		stop = true;
	}

	
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onFrizzChanged(FrizzEvent event) {
		Frizz.Type frizzType = event.sensor.getType();
		if (Frizz.Type.SENSOR_TYPE_ACCELEROMETER == frizzType) {
			if(stop) {
				return;
			}
			final float[] values = scale(isPhone ? event.values : switchXY(event.values));
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, "onFrizzChanged->x:" +0+ ", y:"+0+ ", z:" + 0);
					subTitle.setText("x:" + (int) values[0] + ", y:" + (int) values[1] + ", z:" + (int) values[2]);
					mGsensorBall.setXYZ(values[0], values[1], values[2]);
					if(Math.abs(values[0]) >= 9.0f || Math.abs(values[1]) >= 9.0f || Math.abs(values[2]) >= 9.0f){
						ControlButtonUtil.mControlButtonView.performPassButtonClick();
					}
				}
			});
		}
	}
	
	@Override
	public void onOverTime() {
		//超时
		ControlButtonUtil.mControlButtonView.performFailButtonClick();
	}
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
		endTimer();
	}
}
