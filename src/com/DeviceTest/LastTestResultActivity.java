package com.DeviceTest;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.R.integer;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlSerializer;

import com.DeviceTest.StressTest.RunInTest;
import com.DeviceTest.helper.DeviceInfo;
import com.DeviceTest.helper.GetServerTimeHelper;
import com.DeviceTest.helper.LogFileHelper;
import com.DeviceTest.helper.LogSenderHelper;
import com.DeviceTest.helper.LogSenderHelperByCISF;
import com.DeviceTest.helper.NativeManger;
import com.DeviceTest.helper.SystemInfoUtil;
import com.DeviceTest.helper.SystemUtil;
import com.DeviceTest.helper.TestCase;
import com.DeviceTest.helper.XmlDeal;
import com.DeviceTest.helper.TestCase.RESULT;
import com.DeviceTest.view.MyGridView;
import com.DeviceTest.view.MyItemView;
import com.android.internal.os.storage.ExternalStorageFormatter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.view.View;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
//import android.os.IECService;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.widget.Toast;
import android.widget.TableLayout;
import android.widget.TextView;
import android.os.Message;

public class LastTestResultActivity extends Activity {

	public static final String EXTRA_PATH = "/data/";
	private static final String CONFIG_FILE_NAME = "PcbaDeviceTestConfig.xml";
	private static final String CONFIG_FILE_NAME_ZH = "PcbaDeviceTestConfig.xml";
	private static final String CONFIG_FILE_NAME_EN = "PcbaDeviceTestConfig_en.xml";
	private static final String EXTRA_CONFIG_FILE_NAME = EXTRA_PATH
			+ CONFIG_FILE_NAME;
	private static final String EXTRA_CONFIG_FILE_NAME_EN = EXTRA_PATH
			+ CONFIG_FILE_NAME_EN;
			
	public static final String DATA_PATH = "/data/data/com.DeviceTest/";
	private static final String SAVE_FILE_PATH = EXTRA_PATH
			+ "DeviceTestResult";
	private static final String TAG = "DeviceTest";
	private static final String SAVE_DATA_PATH = DATA_PATH + "DeviceTest.tmp";

	private static final int RESULT_NOT_TEST = 0;
	private static final int RESULT_SUCCESS = 1;
	private static final int RESULT_FAIL = 2;
	
	private XmlDeal xmldoc = null;
	private Spinner mGroupTestSpinner;
	private Button returnButton;
	
	MyGridView myGridView;

	private List<TestCase> mTestCases;
	Object[] mTestGroupNames;
	private WakeLock mWakeLock;
	
	private SharedPreferences mSharedPreferences;
	
	public static String SN;
	public static String TIME;
	 private static boolean shouldReset=false;
	/**
	 * mAllfinish: when reboot test finish , it will start the actvity and 
	 * send log to server then recovery.
	 */
	private int mAllFinish = 0;
	private String extra_config_file_name = EXTRA_CONFIG_FILE_NAME;
	private String config_file_name = CONFIG_FILE_NAME;

	/** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_activity);
		String country = getResources().getConfiguration().locale.getCountry();
		if(!country.equals("CN") && ! country.equals("TW")){
			config_file_name = CONFIG_FILE_NAME_EN;
			extra_config_file_name = EXTRA_CONFIG_FILE_NAME_EN;
		}
		else{
			extra_config_file_name = EXTRA_CONFIG_FILE_NAME;
			config_file_name = CONFIG_FILE_NAME;
		}
		
		if (!InitTestData()) {
			System.exit(-1);
		}
		
		this.setTitle("DeviceTest");
		mTestCases = xmldoc.mTestCases;
		try {
			loadData();
		} catch (Exception e) {
			Log.e(TAG, "load data error.");
			e.printStackTrace();
		}
		myGridView = (MyGridView) findViewById(R.id.myGridView);
		myGridView.setColumnCount(2);
		for (TestCase testCase : mTestCases) {
			MyItemView itemView = new MyItemView(this);
			itemView.setText(testCase.getTestName());
			itemView.setTag(testCase.getTestNo());
			itemView.setCheck(testCase.getneedtest());
			itemView.setShowChecked(false);
			if (testCase.isShowResult()) {
				RESULT result = testCase.getResult();
				itemView.setResult(result);
			}
			myGridView.addView(itemView);
		}
		mGroupTestSpinner = (Spinner) findViewById(R.id.GroupTestSpinner);
		mTestGroupNames = xmldoc.mCaseGroups.keySet().toArray();
		String[] testGroupTexts = new String[mTestGroupNames.length + 1];
		for (int i = 1; i < testGroupTexts.length; i++) {
			testGroupTexts[i] = "Group: " + mTestGroupNames[i - 1].toString();
		}
		testGroupTexts[0] = "CaseGroups";
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, testGroupTexts);
		mGroupTestSpinner.setAdapter(adapter);
		mGroupTestSpinner.setSelection(0, false);
		mWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "videotest");
		mWakeLock.acquire();
		//in common use
		if(null == TIME){
			TIME = SystemUtil.getSystemTime();
		}
		returnButton = (Button) findViewById(R.id.btn_return);
		returnButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				LastTestResultActivity.this.finish();
			}
		});
	}


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, menu.FIRST+1, Menu.NONE, "Exit");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == Menu.FIRST+1) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
	}

	private void loadData() throws Exception {
		SharedPreferences resultPreferences = getSharedPreferences("test_result", Activity.MODE_PRIVATE);
		for (TestCase testCase : mTestCases) {
			RESULT result = RESULT.valueOf(resultPreferences.getString(testCase.getTestName(), "UNDEF"));
			Log.e("lcf", "loaded data: case: " + testCase.getTestName() +  " result: " + result);
			testCase.setResult(result);
		}
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				SAVE_DATA_PATH));
		List<TestCase> savedData = (List<TestCase>) ois.readObject();
		for (TestCase savedCase : savedData) {
			for (TestCase testCase : mTestCases) {
				if (testCase.getClassName().equals(savedCase.getClassName())) {
					//testCase.setResult(savedCase.getResult());
					testCase.setDetail(savedCase.getDetail());
					testCase.setShowResult(savedCase.isShowResult());
				}
			}
		}
		ois.close();
	}
	
	private boolean InitTestData(InputStream is) {
		if (is == null) {
			return false;
		}
		try {
			xmldoc = new XmlDeal(is);
		} catch (Exception e) {
			Log.e(TAG, "parse the xmlfile is fail");
			return false;
		}
		return true;

	}

	private boolean InitTestData() {
		InputStream is = null;
		try {
			File configFile = new File(extra_config_file_name);
			if (configFile.exists()) {
				Log.i(TAG, "Use extra config file:"
						+ extra_config_file_name);
				if (InitTestData(new FileInputStream(configFile))) {
					return true;
				}
			}

			// is = this.openFileInput(strXmlPath);
			is = getAssets().open(config_file_name);

			try {
				xmldoc = new XmlDeal(is);
			} catch (Exception e) {
				Log.e(TAG, "parse the xmlfile is fail");
				return false;
			}
		} catch (IOException e) {

			e.printStackTrace();
			Log.e(TAG, "read the xmlfile is fail" + e.getMessage());
			// ForwardErrorActive();
			return false;
		}

		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
