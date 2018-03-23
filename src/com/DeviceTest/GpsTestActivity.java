package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.helper.SystemUtil;
import com.DeviceTest.helper.TestCase;
import com.DeviceTest.helper.TestCase.RESULT;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class GpsTestActivity extends AppBaseActivity {
	private static final String TAG = GpsTestActivity.class.getSimpleName();
	LocationManager mLocatManager;

	private GpsStatus.Listener statusListener = new MystatusListener();
	HashMap<Integer, Integer> passSatellites = new HashMap<Integer, Integer>();
	HashMap<Integer, Integer> Satellites = new HashMap<Integer, Integer>();
	int ttff = 0;
	static final int CN_PASS = 30;
	static final int CN_PASS_NO = 3;
	static final int TTFF_PASS = 100;
	static final int TIMEOUT = 120 * 1000;
	TestCase.RESULT ttffResult = TestCase.RESULT.UNDEF;
	TestCase.RESULT cnResult = TestCase.RESULT.UNDEF;
	static final int MAX_TEST_TIMES = 50;
	boolean stop = false;
	private TextView mTextTime;
	private TextView mTextResult;
	private int mStatelliteCount;
	private String mLocation;
	boolean isTestSuccess = false;
	Handler mHandler = new Handler();
	Runnable timerRunnable = new Runnable() {
		int time = MAX_TEST_TIMES;
		int index = 0;
		@Override
		public void run() {
			if (stop) {
				return;
			}
			
			if(time > 0){
				time--;
				index++;
			}
			else{
				isTestSuccess = false;
				over();
				return;
			}
			mTextTime.setText("" + time);
			int count = index % 3;
			String progressText = "";
			switch (count) {
			case 0:
				progressText += ".";
				break;
			case 1:
				progressText += "..";
				break;
			case 2:
				progressText += "...";
				break;
			default:
				break;
			}
			mTextResult.setText(getString(R.string.search_now) + progressText);
			mHandler.postDelayed(this, 1000);
				
		}
	};

	    private Handler mrequsetHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 1:
                mLocatManager.addGpsStatusListener(statusListener);
				mLocatManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					1000, 0, locationListener);
                break;
            case 2:
            	ControlButtonUtil.mControlButtonView.performPassButtonClick();
            	break;
            case 3:
            	ControlButtonUtil.mControlButtonView.performFailButtonClick();
            	break;
            default:
                break;
            }
        }
    };	
    
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS)
				+ ")");
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.gpstest);

		ControlButtonUtil.initControlButtonView(this);

		findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
		//findViewById(R.id.btn_Fail).setVisibility(View.INVISIBLE);

		this.mLocatManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
		BluetoothAdapter.getDefaultAdapter().disable();
		try {
		//	Settings.Secure.setLocationProviderEnabled(getContentResolver(),
		//			LocationManager.GPS_PROVIDER, true);			old version to open gps
		Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, 1);	//new version to open gps
		} catch (Exception e) {
			// TODO: handle exception
		}

		//mLocatManager.addGpsStatusListener(this.statusListener);
		//mLocatManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		//		1000, 0, locationListener);
		Log.e("Jeffy","gps delay 1 s");
		mTextResult = (TextView)findViewById(R.id.text_result);
		mTextTime = (TextView)findViewById(R.id.text_time);
		
		mTextTime.setText("0");
		mTextResult.setText(getString(R.string.search_now));
		
		mrequsetHandler.sendEmptyMessage(1);
		


	}
    
    public void onStart(){
    	super.onStart();
    	stop = false;
    	mHandler.postDelayed(timerRunnable, 1000);
    }
    

    public void onOverTime() {
    };
    
	LocationListener locationListener = new LocationListener() {

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onLocationChanged(Location location) {
			if(location != null){
				mLocation = "(" + location.getLatitude() + "," + location.getLongitude() + ")";
				stop = true;
				isTestSuccess = true;
				over();
			}
		}

	};

	protected void onStop() {

		super.onStop();
		stop = true;
		this.mLocatManager.removeGpsStatusListener(this.statusListener);
		mLocatManager.removeUpdates(locationListener);
		mrequsetHandler.removeMessages(1);
		mrequsetHandler.removeMessages(2);
		mrequsetHandler.removeMessages(3);
		mHandler.removeCallbacks(timerRunnable);
		
	}

	GpsStatus gpsStatus;

	class MystatusListener implements GpsStatus.Listener {

		public void onGpsStatusChanged(int event) {
			if (stop) {
				return;
			}
			gpsStatus = mLocatManager.getGpsStatus(null);

			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Iterable<GpsSatellite> allSatellites = gpsStatus
						.getSatellites();
				Iterator<GpsSatellite> itStatellites = allSatellites.iterator();
				int gpsStatellitesCount = 0;
				while(itStatellites.hasNext()){
					itStatellites.next();
					++gpsStatellitesCount;
				}
				if(gpsStatellitesCount >= 3){
					mStatelliteCount = gpsStatellitesCount;
					isTestSuccess = true;
					stop = true;
					over();
				}
				
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				/*
				ttffPass = true;
				ttff = gpsStatus.getTimeToFirstFix() / 1000;
				msg = "TTFF:" + ttff;

				Log.e("Jeffy", "Get TTFF, ttffPass:" + ttffPass + ", cnPass:"
						+ cnPass + ", time:" + timerView.getText());

				if (ttff <= TTFF_PASS) {
					msg += "(Pass)";
				} else {
					msg += "(Failed)";
				}

				over();
				ttffView.setText(msg);
				*/
				break;
			default:
				break;
			}
		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	private String getCNs() {
		if (Satellites.size() == 0) {
			return "";
		}
		String msg = "";
		for (int i = 0; i < 255; i++) {
			if (Satellites.get(i) != null) {
				msg += "" + i + "-" + Satellites.get(i) + ",";
			}
		}
		return msg.substring(0, msg.length() - 1);
	}

	public String getResult() {
		return "";
	}

	public void over() {
		if(isTestSuccess){
			if(mStatelliteCount > 0){
				mTextResult.setText(getString(R.string.f_gps_count) + mStatelliteCount);
			}else{
				mTextResult.setText(getString(R.string.gps_pos) + mLocation);
			}
			ControlButtonUtil.mControlButtonView.performPassButtonClick();
		}else{
			mTextResult.setText(getString(R.string.fail));
			ControlButtonUtil.mControlButtonView.performFailButtonClick();
		}
		
	}
	
	@Override
	public int getMaxTime() {
		return 50;
	}
}
