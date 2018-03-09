package com.DeviceTest.StressTest;

import java.util.ArrayList;

import com.DeviceTest.CompareServerInfoActivity;
import com.DeviceTest.R;
import com.DeviceTest.helper.LogFileHelper;
import com.DeviceTest.helper.NativeManger;
import com.DeviceTest.helper.TestCase.RESULT;
import com.DeviceTest.view.MyItemView;

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.os.storage.ExternalStorageFormatter;


public class RunInTest extends Activity implements OnClickListener {

	private final static int MSG_SHOW_DIALOG = 0;
	private Button mStartBtn;
	private Button mRecoveryBtn;
	private Button mExitBtn;
	private TextView mResultTv;
	private AlertDialog mDialog;
	private int  runinTime;
	private SharedPreferences mSharedPreferences;
	
	private StringBuilder mSB = new StringBuilder();

	private ArrayList<Intent> mTestIntents = new ArrayList<Intent>();
	private int mPos = 0;
	private boolean mAC_IN = false;
	private boolean mIsTesting = false;
//	private String  mSN;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runin_test);
		
		mSharedPreferences = getSharedPreferences("state", 0);
//		mSN = mSharedPreferences.getString("SN", null);
//		
//		if (mSN != null && !mSN.equals("")) {
//					LogFileHelper.LogFile = LogFileHelper.DEFAULT_LOG_FILE_PATH+"/"+mSN+".log";
//					
//				}	
		
		initIntentData();
		initRes();
		int isFinish = getIntent().getIntExtra("finish", 0);
		if (isFinish == 1) {
			mSB.append(getString(R.string.test_sleep_result)).append("\n");
			mSB.append(getString(R.string.test_video_result)).append("\n");
			mSB.append(getString(R.string.test_reboot_result)).append("\n");
			mResultTv.setText(mSB.toString());
			mStartBtn.setEnabled(false);
			mExitBtn.setEnabled(false);
		}
		
		if (isFinish == 0) { startActivityForResult(mTestIntents.get(mPos),mPos); }
		 
/*		registerReceiver(ACDCDetectedReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
*/
	};

	private void initRes() {
		mStartBtn = (Button) findViewById(R.id.start_btn);
		mStartBtn.setOnClickListener(this);
		mRecoveryBtn = (Button) findViewById(R.id.recovery_btn);
		mRecoveryBtn.setOnClickListener(this);
		mRecoveryBtn.setVisibility(View.INVISIBLE);
		mExitBtn = (Button) findViewById(R.id.exit_btn);
		mExitBtn.setOnClickListener(this);
		mExitBtn.setVisibility(View.INVISIBLE);
		mResultTv = (TextView) findViewById(R.id.result);

	}
/*	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(ACDCDetectedReceiver);
	}
*/	
	
	private void initIntentData() {
	
		Intent intent1 = new Intent(this, SleepTest.class);
        intent1.putExtra("max", 5);
		intent1.putExtra("auto", 1);
		mTestIntents.add(intent1);
/*		
        Intent intent2 = new Intent(this, VideoPlayFromSDActivity.class);
        //intent2.putExtra("path", "/mnt/sdcard/demo/video.mp4");
        intent2.putExtra("auto", 1);
        intent2.putExtra("time", 2);// hour
        mTestIntents.add(intent2);
*/
		Intent intentEnd = new Intent(this, RebootTest.class);
		intentEnd.putExtra("auto", 1);
		intentEnd.putExtra("max", 5);
		mTestIntents.add(intentEnd);
		
		SharedPreferences.Editor edit = mSharedPreferences.edit();
		edit.putInt("auto", 1);
		edit.putInt("reboot_max", 5);
		edit.commit();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (!mAC_IN) {
//			mSB.append(getString(R.string.AC_NO_IN_TIP)).append("\n");
//			mResultTv.setText(mSB.toString());
//			mIsTesting = false;
//			//mPos = 0;
//			return;
//		}
		
		
		switch (requestCode) {
		case 0:
			
			mSB.append(getString(R.string.test_video_result)).append("\n");
			mResultTv.setText(mSB.toString());
			break;
		case 1:
			
			
			mSB.append(getString(R.string.test_sleep_result)).append("\n");
			mResultTv.setText(mSB.toString());
			break;
		case 2:
			
			break;
		default:
			break;
		}
		
		if (mPos < mTestIntents.size() - 1) {
			mPos++;
			startActivityForResult(mTestIntents.get(mPos), mPos);
			Log.v("limi runin","mpos= "+mPos);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_btn:
			startActivityForResult(mTestIntents.get(mPos), mPos);
			break;
		case R.id.recovery_btn:
			Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
            intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
            startService(intent);
			break;
		case R.id.exit_btn:
			finish();
			break;
		default:
			break;
		}
	}
/*	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SHOW_DIALOG:
				ShowACDialog();
				break;

			default:
				break;
			}
		};
	};
*/
/*	
	private void ShowACDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.AC_NO_IN_MSG)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
							}
						})
				;
		mDialog = builder.create();
		mDialog.show();
		
	}
*/
/*	
	private BroadcastReceiver ACDCDetectedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				int plugType = intent.getIntExtra("plugged", 0);
				if (plugType > 0) {
					if (mDialog != null && mDialog.isShowing())
						mDialog.cancel();
					mAC_IN = true;
					if (!mIsTesting) {
						mStartBtn.performClick();
						mIsTesting = true;
					}
				} else {
					//AC NOT IN
					mAC_IN = false;
					mHandler.sendEmptyMessage(MSG_SHOW_DIALOG);	
				}
			}
		}
	};
*/
}
