package com.DeviceTest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.DeviceTest.helper.ControlButtonUtil;

public class ChargeTestActivity extends AppBaseActivity {
  private TextView mChargeInfoTV,battery;
  private ImageView chargingImage;
  private ImageView notChargingImage;
  private final static String path="/sys/class/power_supply/battery/current_now";
  private boolean mIsPowerConnected;
  private boolean mIsPowerDisConnected;
  
  @Override
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.chargetest);
    ControlButtonUtil.initControlButtonView(this);
    battery = (TextView) findViewById(R.id.battery_info);
    mChargeInfoTV = (TextView) findViewById(R.id.charge_info);
    chargingImage = (ImageView) findViewById(R.id.charge_image);
    notChargingImage = (ImageView) findViewById(R.id.not_charge_image);
    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
    intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
    registerReceiver(mBatteryStateReceiver, intentFilter);
	
  }

  @Override
	protected void onResume() {
		super.onResume();
		 startTimer();
	}
  
  
  @Override
	protected void onPause() {
		super.onPause();
		endTimer();
	}
  
  
  @Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBatteryStateReceiver);
		
	}
  private BroadcastReceiver mBatteryStateReceiver = new BroadcastReceiver(){
    @Override
    public void onReceive(Context context, Intent intent){
    	
     if(Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())){
    		mIsPowerConnected = true;
      }else if(Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())){
    	  mIsPowerDisConnected = true;
      }
     if(mIsPowerConnected && mIsPowerDisConnected){
    	 ControlButtonUtil.mControlButtonView.performPassButtonClick();
     }
      if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
        String batteryInfo = "";
        String val="";
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
        try {
                FileReader fread = new FileReader(path);
	        BufferedReader buffer = new BufferedReader(fread);
	        String str = null;
	        while ((str = buffer.readLine()) != null) {
	        	val = str;
	        	break;
	        }
	        buffer.close();
	        fread.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
	float vol=voltage/1000;
	int val_ch=Integer.parseInt(val);
        val_ch=val_ch/1000;
      	battery.setText("电量:"+level+"% \n电压:"+vol+"v \n电流:"+val_ch+"mA");
	 switch(status){
          case BatteryManager.BATTERY_STATUS_CHARGING://The device is being charged
//          int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,BatteryManager.BATTERY_PLUGGED_AC);
//	    if(plugged == BatteryManager.BATTERY_PLUGGED_AC){
               batteryInfo = getString(R.string.on_charge);
//	    }
//          else if(plugged == BatteryManager.BATTERY_PLUGGED_USB){
//             batteryInfo = getString(R.string.on_charge, "USB");
//          }
//          else{
//            batteryInfo = getString(R.string.on_charge, "wireless");
//          }
            chargingImage.setVisibility(View.VISIBLE);
            notChargingImage.setVisibility(View.GONE);
            break;
          case BatteryManager.BATTERY_STATUS_FULL://The battery is full
            chargingImage.setVisibility(View.GONE);
            notChargingImage.setVisibility(View.VISIBLE);
            batteryInfo = getString(R.string.battery_full);
            break;
          default://The device is not charged
            chargingImage.setVisibility(View.GONE);
            notChargingImage.setVisibility(View.VISIBLE);
            batteryInfo = getString(R.string.not_on_charge);
            break;
        }
        mChargeInfoTV.setText(batteryInfo);
      }
    }
  };

  public void onOverTime() {
	  ControlButtonUtil.mControlButtonView.performFailButtonClick();
  };
}
