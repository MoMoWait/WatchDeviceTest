package com.DeviceTest;


import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Surface;
import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.NativeManger;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.os.SystemProperties;
import java.lang.Thread;

public class GsensorCalibrateActivity extends Activity {
  final static private float STANDARD_X = 0.0f;
  final static private float STANDARD_Y = 0.0f;
  final static private float STANDARD_Z = 9.8f;
  final static private int MSG_HIDE_DIALOG = 0;
  final static private int MSG_ENABLE_UPDATE_SENSOR = 1;

  private SensorManager sensorManager;
  private SensorEventListener lsn = null;
  private Sensor sensors;
  private AlertDialog progressDialog;
  private boolean stop = false;
  private boolean hasCalibrated = false;
  private boolean updateSensorEnable = true;

  private TextView preCaliValueTxt;
  private TextView postCaliValueTxt;
  private TextView diffValueTxt;
  private Button caliBtn;
  private float curX;
  private float curY;
  private float curZ;
  private float diffX;
  private float diffY;
  private float diffZ;
  private NativeManger mNM;
  private String tool_enable = "-1";

  private Handler mHandler = new Handler() {
    public void handleMessage(android.os.Message msg) {
      switch(msg.what){
        case MSG_HIDE_DIALOG:
          hideProgressDialog();
          Toast.makeText(GsensorCalibrateActivity.this, R.string.calibration_succeed, Toast.LENGTH_LONG).show();
          break;
        case MSG_ENABLE_UPDATE_SENSOR:
          updateSensorEnable = true;
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.gsensor_calibrate);
    preCaliValueTxt = (TextView) findViewById(R.id.gensor_precali_value);
    postCaliValueTxt = (TextView) findViewById(R.id.gensor_postcali_value);
    diffValueTxt = (TextView) findViewById(R.id.gsensor_diff_value);
    caliBtn = (Button) findViewById(R.id.gsensor_calibrate_btn);
    caliBtn.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v){
        if(!(diffX < 3.0f && diffY < 3.0f && diffZ < 3.0f)){
          Toast.makeText(GsensorCalibrateActivity.this, R.string.not_horizontal_info, Toast.LENGTH_LONG).show();
          return;
        }
        if(hasCalibrated){
          Toast.makeText(GsensorCalibrateActivity.this, R.string.has_calibrated, Toast.LENGTH_LONG).show();
          return;
        }
        showProgressDialog();
        new Thread(new Runnable(){
          @Override
          public void run(){
            resetGsensor();
            rebootGsensor();
            saveDiff();
            rebootGsensor();
            mHandler.sendEmptyMessage(MSG_HIDE_DIALOG);
          }
        }).start();
        hasCalibrated = true;
      }
    });
    getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

    ControlButtonUtil.initControlButtonView(this);
    sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
    mNM = new NativeManger();
  }

  @Override
  protected void onResume(){
    super.onResume();
    lsn = new SensorEventListener() {
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }

      public void onSensorChanged(SensorEvent e) {
        if(stop) {
          return;
        }
        if(!updateSensorEnable){
          return;
        }
        updateSensorEnable = false;
        mHandler.sendEmptyMessageDelayed(MSG_ENABLE_UPDATE_SENSOR, 500);
        float[] values = e.values;
        curX = values[0];
        curY = values[1];
        curZ = values[2];
        diffX = curX - STANDARD_X;
        diffY = curY - STANDARD_Y;
        diffZ = curZ - STANDARD_Z;
        if(!hasCalibrated){
          preCaliValueTxt.setText("x: " + curX + ", y: "
              + curY + ", z: " + curZ);
        }
        else{
          postCaliValueTxt.setText("x: " + curX + ", y: "
              + curY + ", z: " + curZ);
        }
        diffValueTxt.setText("x: " + diffX + ", y: " + diffY + ", z: " + diffZ);
      }
    };
    sensors = sensorManager
        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(lsn, sensors,
        SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onPause(){
    super.onPause();
    stop = true;
    sensorManager.unregisterListener(lsn);
  }

  private void showProgressDialog(){
    if(progressDialog == null){
      progressDialog = new ProgressDialog(this);
    }
    progressDialog.show();
  }

  private void hideProgressDialog(){
    if(progressDialog != null){
      progressDialog.hide();
    }
  }

  private void rebootGsensor(){
    try{
      sensorManager.unregisterListener(lsn);
      Thread.sleep(500);
      sensorManager.registerListener(lsn, sensors,
          SensorManager.SENSOR_DELAY_NORMAL);
      Thread.sleep(500);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
      return false;
    }
    return super.dispatchKeyEvent(event);
  }

  private void resetGsensor(){
    SystemProperties.set("persist.tool_enable","1");
    try{
      Thread.sleep(500);
    }
    catch(InterruptedException e){
      e.printStackTrace();
    }
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    if(tool_enable.equals("1")){
      mNM.saveGsensor(0, 0, 0);
    }
    else{
      Log.e("PcbaDeviceTest", "Setting tool_enable fails! Now tool_enable is: " + tool_enable);
    }
    SystemProperties.set("persist.tool_enable","0");
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    Log.e("PcbaDeviceTest", "Now tool_enable is: " + tool_enable);
    tool_enable = "-1";
  }

  private void saveDiff(){
    int x = (int) (diffX * 10);
    int y = (int) (diffY * 10);
    int z = (int) (diffZ * 10);
    int value = x >> 16 + y >> 8 + z;
    SystemProperties.set("persist.tool_enable","1");
    try{
      Thread.sleep(500);
    }
    catch(InterruptedException e){
      e.printStackTrace();
    }
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    if(tool_enable.equals("1")){
      mNM.saveGsensor((int) (diffX * 10), (int) (diffY * 10), (int) (diffZ * 10));
    }
    else{
      Log.e("PcbaDeviceTest", "Setting tool_enable fails! Now tool_enable is: " + tool_enable);
    }
    SystemProperties.set("persist.tool_enable","0");
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    Log.e("PcbaDeviceTest", "Now tool_enable is: " + tool_enable);
    tool_enable = "-1";
  }
}
