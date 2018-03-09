package com.DeviceTest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.NativeManger;

public class CouplingTestActivity extends Activity {
  public static final int MSG_STOP_COUPLING_TEST = 0;
  final static int[] BAND_ID = {1, 2, 8, 8};
  static final int[] CHANNEL_ID = {9750, 9400, 2800, 2800};
  private NativeManger mNM;
  EditText couplingPower;
  RadioGroup chooseGroup;
  Button startButton;
  Button stopButton;
  AlertDialog couplingTestDialog;
  private String tool_enable = "-1";
	
  @Override
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.couplingtest);
    mNM = new NativeManger();
    couplingPower = (EditText) findViewById(R.id.oupling_power);
    chooseGroup = (RadioGroup) findViewById(R.id.choose_group);
    startButton = (Button) findViewById(R.id.btn_start_coupling_test);
    startButton.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v){
        int index = 0;
        switch(chooseGroup.getCheckedRadioButtonId()){
          case R.id.band1:
            index = 0;
            break;
          case R.id.band2:
            index = 1;
            break;
          case R.id.band5:
            index = 2;
            break;
          case R.id.band8:
            index = 3;
            break;
          default:
            index = 0;
            break;
        }
        Log.d("PcbaDeviceTest", "startButton onclick, index: " + index);
        SharedPreferences sp = getSharedPreferences("devicetest", 0);
        int power = 432;
        if(!couplingPower.getText().toString().equals("")){
          power = Integer.parseInt(couplingPower.getText().toString()) * 16;
        }
        CouplingTestThread mCouplingTestThread = new CouplingTestThread(power, index);
        mCouplingTestThread.start();
        startButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
      }
    });
    stopButton = (Button) findViewById(R.id.btn_stop_coupling_test);
    stopButton.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v){
        handler.removeMessages(MSG_STOP_COUPLING_TEST);
        handler.sendEmptyMessage(MSG_STOP_COUPLING_TEST);
      }
    });
    ControlButtonUtil.initControlButtonView(this);
  }

  private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
      case MSG_STOP_COUPLING_TEST:
        SystemProperties.set("persist.tool_enable","1");
        try{
          Thread.sleep(500);
        }
        catch(InterruptedException e){
          e.printStackTrace();
        }
        tool_enable = SystemProperties.get("persist.tool_enable", "-1");
        if(tool_enable.equals("1")){
          mNM.stopMax3GTxPower();
        }
        else{
          Log.e("PcbaDeviceTest", "Setting tool_enable fails! Now tool_enable is: " + tool_enable);
        }
        SystemProperties.set("persist.tool_enable","0");
        tool_enable = SystemProperties.get("persist.tool_enable", "-1");
        Log.e("PcbaDeviceTest", "Now tool_enable is: " + tool_enable);
        tool_enable = "-1";
        //if(isAirplaneModeOn()){
          setAirplaneModeOn(false);
        //}
        stopButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
			}
		}
	};
  private class CouplingTestThread extends Thread{
    int power;
    int index;
    CouplingTestThread(int power, int index){
      this.power = power;
      this.index = index;
    }
    public void run(){
      //enter fly mode
      //if(!isAirplaneModeOn()){
        setAirplaneModeOn(true);
        try{
          Thread.sleep(3000);
        }
        catch(Exception e){
          e.printStackTrace();
        }
      //}
    SystemProperties.set("persist.tool_enable","1");
    try{
      Thread.sleep(500);
    }
    catch(InterruptedException e){
      e.printStackTrace();
    }
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    if(tool_enable.equals("1")){
      mNM.max3GTxPower(power, BAND_ID[index], CHANNEL_ID[index]);
    }
    else{
      Log.e("PcbaDeviceTest", "Setting tool_enable fails! Now tool_enable is: " + tool_enable);
    }
    SystemProperties.set("persist.tool_enable","0");
    tool_enable = SystemProperties.get("persist.tool_enable", "-1");
    Log.e("PcbaDeviceTest", "Now tool_enable is: " + tool_enable);
    tool_enable = "-1";
      handler.sendEmptyMessageDelayed(MSG_STOP_COUPLING_TEST, 3 * 60 * 1000);
    }
  }
    /*public boolean isAirplaneModeOn() {
        return Settings.Global.getInt(getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }*/

    private void setAirplaneModeOn(boolean enabled) {
      Log.d("lib_rkinfo", "setAirplaneModeOn() - enabled: " + enabled);
        final ConnectivityManager mgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mgr.setAirplaneMode(enabled);
    }


  public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
      return false;
    }
    return super.dispatchKeyEvent(event);
  }
  
}
