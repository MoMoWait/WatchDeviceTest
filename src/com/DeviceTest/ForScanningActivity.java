package com.DeviceTest;
/*
 * author @ mw
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.DeviceTest.helper.LogSenderHelper;
import com.DeviceTest.helper.SystemUtil;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.IActivityManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForScanningActivity extends Activity implements OnClickListener{

	private EditText scan;
	private TextView sync_prompt;
	private Button btnOK,btnOKWhatever;
	private Button btnClear;
	private ProgressBar progressBar;
	//public static String SN;
	private WindowManager wm;
	//public static GetServerTimeHelper GSTH;
	private LogSenderHelper mSenderHelper;
	
	public static final int MSG_SEND_LOG_SUCESS = 0;
	public static final int MSG_SEND_LOG_FAIL = 1;
	public static final int MSG_LOGIN_SUCESS= 2;
	public static final int MSG_LOGIN_FAIL = 3;
	public static final int MSG_GET_LOG_SUCCESS=4;
	public static final int MSG_CONNECT_FTP=5;
	
	private boolean mSendLogFinish = false;
	
	private File file = null;
	private boolean sync_success = false;
	private boolean key = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forscanning);
		scan = (EditText)findViewById(R.id.text_scan);
		sync_prompt = (TextView)findViewById(R.id.sync_prompt);
		btnOK = (Button)findViewById(R.id.first_btn_OK);
		btnOKWhatever = (Button)findViewById(R.id.first_btn_OK_whatever);
		btnClear = (Button)findViewById(R.id.first_btn_clear);
		progressBar = (ProgressBar) findViewById(R.id.sync_time);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		scan.setWidth(wm.getDefaultDisplay().getWidth() / 3);
		
		btnOK.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnOKWhatever.setOnClickListener(this);
		btnOK.setVisibility(View.INVISIBLE);
		btnClear.setVisibility(View.INVISIBLE);
		btnOKWhatever.setVisibility(View.INVISIBLE);
		
		scan.addTextChangedListener(watcher);
		sync_prompt.setText(R.string.sync_server_time_ing);
		progressBar.setVisibility(View.VISIBLE);
		
		//GSTH = new GetServerTimeHelper(this);
		
		//open wifi
		WifiManager wifimanager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifimanager.setWifiEnabled(true);
		
		//change language to Chinese		
/*		IActivityManager iActMag = ActivityManagerNative.getDefault();
		try {
			Configuration config = iActMag.getConfiguration();
			config.locale = Locale.SIMPLIFIED_CHINESE;
			iActMag.updateConfiguration(config);
		} catch(RemoteException e) {
			e.printStackTrace();
		}
*/		
		//open bluetooth
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		bluetooth.enable();
		
		//change brightness to max
		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL );
		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 255 );
		LayoutParams lp=getWindow().getAttributes();  
        lp.screenBrightness=1;  
        getWindow().setAttributes(lp);
		
		//30 minites lock
		android.provider.Settings.System.putInt(getContentResolver(),android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 1800000 );
		
		//security
//		android.provider.Settings.Secure.putInt(getContentResolver(), android.provider.Settings.Secure.INSTALL_NON_MARKET_APPS, 1 );
		
		//USB debug
//		android.provider.Settings.Secure.putInt(getContentResolver(), android.provider.Settings.Secure.ADB_ENABLED, 1 );
		
		//location
//		android.provider.Settings.Secure.putInt(getContentResolver(), android.provider.Settings.Secure.ALLOW_MOCK_LOCATION, 1 );
		
		//time
		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.TIME_12_24, 24);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(MSG_CONNECT_FTP);
	}
	
	private void showSyncTimeDialog() {
			new AlertDialog.Builder(this)
			.setTitle(R.string.sync_server_time_title)
		    .setMessage(getString(R.string.log_send_dailog1))
	        .setPositiveButton(R.string.retry_sync, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	mHandler.sendEmptyMessage(MSG_CONNECT_FTP);
	            	dialog.dismiss();
	            }
	        })
	        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	dialog.cancel();
	                Intent intent=new Intent(getApplicationContext(),DeviceTest.class);
	    			startActivity(intent);
	    			finish();
	            }
	        }).show();
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_CONNECT_FTP:
				if (mSenderHelper == null) {
					mSenderHelper = new LogSenderHelper(getApplicationContext(), mHandler);
				}
					mSenderHelper.connectFTP();
				break;
			case MSG_SEND_LOG_SUCESS:
				mSenderHelper.getLogFromServer(file.getAbsolutePath());
				break;
			case MSG_GET_LOG_SUCCESS:
				mSendLogFinish = true;
				if (mSendLogFinish) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					DeviceTest.TIME = sdf.format(LogSenderHelper.md);
					progressBar.setVisibility(View.INVISIBLE);
					sync_prompt.setText(R.string.sync_server_time_success);
					sync_success = true;
					if(key && null!=DeviceTest.TIME){
						AlarmManager timeZone = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
						timeZone.setTimeZone("Asia/Shanghai");
						SystemUtil.SetSystemDateAndTime(DeviceTest.TIME);
						findViewById(R.id.first_btn_OK).performClick();
					}
				}
				break;
			case MSG_LOGIN_SUCESS:
				file = new File("/data/data/com.DeviceTest/ForSyncTime.txt");
				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter(file));
					bw.write("test");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mSenderHelper.sendLogToServer(file.getAbsolutePath(),true);		
				break;
			case MSG_LOGIN_FAIL:
			case MSG_SEND_LOG_FAIL:
				if(!key) {
					progressBar.setVisibility(View.INVISIBLE);
					sync_prompt.setVisibility(View.INVISIBLE);
				} else {
					showSyncTimeDialog();
				}
				break;
			default:
				break;
			}
		};
	};

	TextWatcher watcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if(null!=scan.getText().toString().trim() && !scan.getText().toString().trim().equals("")){
				DeviceTest.SN = scan.getText().toString();
				if(DeviceTest.SN.substring(DeviceTest.SN.length()-1).equals("\n")){
					DeviceTest.SN = DeviceTest.SN.substring(0, DeviceTest.SN.length()-1).trim();
					key = true;
					if(sync_success){
						AlarmManager timeZone = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
						timeZone.setTimeZone("Asia/Shanghai");
						SystemUtil.SetSystemDateAndTime(DeviceTest.TIME);
						findViewById(R.id.first_btn_OK).performClick();
					}
					else 
						showSyncTimeDialog();
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.first_btn_OK:
			Intent intent=new Intent(this,DeviceTest.class);
			this.startActivity(intent);
			this.finish();
			break;
		case R.id.first_btn_OK_whatever:
			Intent intent2=new Intent(this,DeviceTest.class);
			this.startActivity(intent2);
			this.finish();
			break;
		case R.id.first_btn_clear:
			scan.setText("");
			break;
		}
		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

}
