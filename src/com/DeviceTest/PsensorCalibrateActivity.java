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

public class PsensorCalibrateActivity extends Activity {
  final static private int MSG_HIDE_DIALOG = 0;
  final static private int MSG_ENABLE_UPDATE_SENSOR = 1;
  final static private int MSG_UPDATE_TXT = 2;

  private AlertDialog progressDialog;
  private boolean start = false;
  private boolean stop = false;
  private boolean hasCalibrated = false;
  private boolean updateSensorEnable = true;

  private TextView preCaliValueTxt;
  private TextView postCaliValueTxt;
  private Button caliBtn;
  private NativeManger mNM;
  private String tool_enable = "-1";

  private int thresholdValue = 0;
  private int psData = 0;
  private int maxPsData = 0;
  private SensorManager mgr;
  private Sensor proximity;
  private SensorEventListener lsn;


  private Handler mHandler;

  @Override
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.psensor_calibrate);
    preCaliValueTxt = (TextView) findViewById(R.id.pensor_precali_value);
    postCaliValueTxt = (TextView) findViewById(R.id.pensor_postcali_value);
    caliBtn = (Button) findViewById(R.id.psensor_calibrate_btn);
    caliBtn.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v){
        /*if(!(diffX < 3.0f && diffY < 3.0f && diffZ < 3.0f)){
          Toast.makeText(PensorCalibrateActivity.this, R.string.not_horizontal_info, Toast.LENGTH_LONG).show();
          return;
        }*/
        if(hasCalibrated){
          Toast.makeText(PsensorCalibrateActivity.this, R.string.has_calibrated, Toast.LENGTH_LONG).show();
          return;
        }
        //================start calibration=====================
        showProgressDialog();
        new Thread(new Runnable(){
          @Override
          public void run(){
            resetPsensor();
            rebootPsensor();
            start = true;
            try{
              Thread.sleep(2000);
            }
            catch(Exception e){
              e.printStackTrace();
            }
            start = false;
            //readPsData();
            if(maxPsData > thresholdValue){
              Toast.makeText(PsensorCalibrateActivity.this, R.string.calibration_fails, Toast.LENGTH_LONG).show();
              return;
            }
            hasCalibrated = true;
            writeToNvm();
            rebootPsensor();
            mHandler.sendEmptyMessage(MSG_HIDE_DIALOG);
          }
        }).start();
      }
    });
    getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
    mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
    proximity = this.mgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    ControlButtonUtil.initControlButtonView(this);
    mNM = new NativeManger();
    mHandler = new Handler() {
      public void handleMessage(android.os.Message msg) {
        switch(msg.what){
          case MSG_HIDE_DIALOG:
            hideProgressDialog();
            Toast.makeText(PsensorCalibrateActivity.this, R.string.calibration_succeed, Toast.LENGTH_LONG).show();
            break;
          case MSG_ENABLE_UPDATE_SENSOR:
            updateSensorEnable = true;
            break;
          case MSG_UPDATE_TXT:
            if(!hasCalibrated){
              preCaliValueTxt.setText("" + psData);
            }
            else{
              postCaliValueTxt.setText("" + psData);
            }
            break;
        }
      }
    };
  }

  @Override
  protected void onResume(){
    super.onResume();
    start = false;
    stop = false;
    lsn = new SensorEventListener() {
      public void onAccuracyChanged(Sensor sensor, int accuracy) {
      }
      public void onSensorChanged(SensorEvent e) {
      }
    };
    mgr.registerListener(lsn, proximity,
        SensorManager.SENSOR_DELAY_NORMAL);
    new Thread(new Runnable(){
      @Override
      public void run(){
        while(!stop){
          readPsData();
          mHandler.sendEmptyMessage(MSG_UPDATE_TXT);
          try{
            Thread.sleep(200);

          }
          catch(Exception e){
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  @Override
  protected void onPause(){
    super.onPause();
    stop = true;
    mgr.unregisterListener(lsn);
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

  private void rebootPsensor(){
    try{
      mgr.unregisterListener(lsn, this.proximity);
      //Thread.sleep(1000);

      mgr.registerListener(lsn, this.proximity,
          SensorManager.SENSOR_DELAY_NORMAL);
      Thread.sleep(1000);
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

  private void resetPsensor(){
    SystemProperties.set("persist.tool_enable","1");
    try{
      Thread.sleep(500);
    }
    catch(InterruptedException e){
      e.printStackTrace();
    }
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    if(tool_enable.equals("1")){
      mNM.savePsensor(0);
    }
    else{
      Log.e("PcbaDeviceTest", "Setting tool_enable fails! Now tool_enable is: " + tool_enable);
    }
    SystemProperties.set("persist.tool_enable","0");
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    Log.e("PcbaDeviceTest", "Now tool_enable is: " + tool_enable);
    tool_enable = "-1";
  }

  private void readPsData(){
    int[] result = null;
    SystemProperties.set("persist.tool_enable","1");
    try{
      Thread.sleep(500);
    }
    catch(InterruptedException e){
      e.printStackTrace();
    }
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    if(tool_enable.equals("1")){
      result = mNM.readPsData();
    }
    else{
      Log.e("PcbaDeviceTest", "Setting tool_enable fails! Now tool_enable is: " + tool_enable);
    }
    SystemProperties.set("persist.tool_enable","0");
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    Log.e("PcbaDeviceTest", "Now tool_enable is: " + tool_enable);
    tool_enable = "-1";
    if(result != null){
      psData = result[0];
      if(start){
        Log.e("PsensorCalibrateActivity", "has started calibration, maxPsData: " + maxPsData + ", psData: " + psData);
        if(psData > maxPsData){
          maxPsData = psData;
        }
      }
      thresholdValue = result[1];
      Log.e("PsensorCalibrateActivity", "readPsData - result[0]: " + result[0] + ", result[1]: " + result[1]);
    }
  }

  private void writeToNvm(){
    Log.e("PsensorCalibrateActivity", "write to NVM, maxPsData: " + maxPsData);
    SystemProperties.set("persist.tool_enable","1");
    try{
      Thread.sleep(500);
    }
    catch(InterruptedException e){
      e.printStackTrace();
    }
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    if(tool_enable.equals("1")){
      mNM.savePsensor(maxPsData);
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
