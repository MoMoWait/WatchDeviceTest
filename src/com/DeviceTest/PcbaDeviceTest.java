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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;
import android.view.Gravity;

import java.io.Console;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

import org.xmlpull.v1.XmlSerializer;

import com.DeviceTest.StressTest.RunInTest;
import com.DeviceTest.data.ConstData;
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
import android.os.SystemProperties;
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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.view.View;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
//import android.os.IECService;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.os.Message;
import android.os.SystemProperties;

import java.lang.Thread;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class PcbaDeviceTest extends Activity {
    private static final boolean GTI_ENABLE = false;

    public static final int DEVICE_TEST_MAX_NUM = 1000;
    public static final int TEST_FAILED_DELAY = 5000;
    public static final String EXTRA_TEST_PROGRESS = "test progress";
    public static final String EXTRA_TEST_RESULT_INFO = "test result info";
    public static final String EXTRA_TEST_START_TIME = "test start";
    public static final String EXTRA_TEST_FINISH_TIME = "test finish";

    public static final String RESULT_INFO_HEAD = ";";
    public static final String RESULT_INFO_HEAD_JUST_INFO = "just info;";

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
    public static final String TEMP_FILE_PATH = DeviceTest.DATA_PATH + "test";

    public static final int MSG_SEND_LOG_SUCESS = 0;
    public static final int MSG_SEND_LOG_FAIL = 1;
    public static final int MSG_LOGIN_SUCESS = 2;
    public static final int MSG_LOGIN_FAIL = 3;
    public static final int MSG_RECOVERY = 4;
    public static final int MSG_RUNIN = 5;
    public static final int MSG_ENABLE_CALI = 6;


    public static final int SEND_LOG_BY_FTP = 0;
    public static final int SEND_LOG_BY_CIFS = 1;
    private static int SEND_LOG_METHOD = SEND_LOG_BY_FTP;
    private static int SEND_CHECK_ALLPASS = 6;

    private LogSenderHelper mSenderHelper;
    private LogSenderHelperByCISF mLogSenderHelperByCISF;

    private XmlDeal xmldoc = null;
    private Spinner mGroupTestSpinner;
    private Button mButtonCancel;
    private Button mTestChecked;
    private Button mSendLogButton;
    private Button mClearButton;
    private Button mUninstallButton;
    private Button mRecoveryButton;
    private Button mCalibrationResultButton;
    private Button mPtestButton;
    private Button mResultButton;
    private ToggleButton mGtiToggle;

    private ProgressDialog mProgressDialog;
    private ProgressDialog mwifiProgressDialog;
    private AlertDialog mNetworkCheckDialog;
    private AlertDialog mWIFIOpenFailDialog;
    private boolean isNetworkConnect;

    private List<TestCase> mTestCases;
    Object[] mTestGroupNames;

    private NativeManger mNM;
    private WakeLock mWakeLock;
    private boolean mIsTestAll = false;
    private boolean mIsSendingLog = false;
    private int mTestPos = 0;

    private boolean mAC_IN = false;
    private boolean mSendLogFinish = false;
    private boolean mCanRecovery = false;
    private int mBatteryLevel = 0;
    private int image_flag = -1;
    private int check_image_cmd = 0x1020;
    private SharedPreferences mSharedPreferences;
    public static boolean isOnTestAll = false;

    public static String SN;
    public static String TIME;
    private int close = 0;
    private static HomeWatcherReceiver mHomeKeyReceiver = null;
    private static boolean shouldReset = false;
    private Context mContext;
    private String tool_enable = "-1";

    /**
     * mAllfinish: when reboot test finish , it will start the actvity and
     * send log to server then recovery.
     */
    private int mAllFinish = 0;
    private String extra_config_file_name = EXTRA_CONFIG_FILE_NAME;
    private String config_file_name = CONFIG_FILE_NAME;
    private boolean enableCalibrationButton = true;

    /**
     * Called when the activity is first created.
     */

    private static int ENTER_PTEST_MODE_CLICK_COUNT = 8;
    private static long sPreTime = 0;
    private static int sCount = 0;
    private Map<String, String> mTestMap = new LinkedHashMap<String, String>();
    private LinearLayout mLayoutTestResContainer;
    private Button mBtnTestAll;
    private Button mBtnTestFaildUnknow;
    private boolean mIsTestFaildOrUnknow;
    private List<String> mFaiedUnknowTestClassNames = new ArrayList<String>();
    private Dialog mAllSuccessDialog;
    public void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String country = getResources().getConfiguration().locale.getCountry();
        if (!country.equals("CN") && !country.equals("TW")) {
            config_file_name = CONFIG_FILE_NAME_EN;
            extra_config_file_name = EXTRA_CONFIG_FILE_NAME_EN;
        } else {
            extra_config_file_name = EXTRA_CONFIG_FILE_NAME;
            config_file_name = CONFIG_FILE_NAME;
        }
        if (!InitTestData()) {
            System.exit(-1);
        }

        this.setTitle("DeviceTest");
        mTestCases = xmldoc.mTestCases;
        Iterator<TestCase> iterator = mTestCases.iterator();
        while(iterator.hasNext()){
        	TestCase itemCase = iterator.next();
        	if(itemCase.getClassName().equals("ProximitySensorTestActivity") && "false".equals(SystemProperties.get("ro.product.proximity", "false")))
        		iterator.remove();
        	else
        		mTestMap.put("com.DeviceTest." + itemCase.getClassName(), itemCase.getTestName());
        		
        }
        isOnTestAll = true;
        mLayoutTestResContainer = (LinearLayout)findViewById(R.id.layout_res_container);
        mBtnTestAll = (Button)findViewById(R.id.btn_test_all);
        mBtnTestFaildUnknow = (Button)findViewById(R.id.btn_retry_failed_unknow);
        mBtnTestAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mIsTestFaildOrUnknow = false;
				gotoActivity(0);
			}
		});
        
        mBtnTestFaildUnknow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.i(TAG, "PcbaDeviceTest->mBtnTestFaildUnknow->onClick");
				mIsTestFaildOrUnknow = true;
				 List<String> failedTestClassList = new ArrayList<String>();
			     List<String> successTestClassList = new ArrayList<String>();
			     List<String> unknowTestClassList = new ArrayList<String>();
			     for(Map.Entry<String, String> testItem : mTestMap.entrySet()){
					String testResult = StorageUtils.getDataFromSharedPreference(testItem.getKey());
					if (TextUtils.isEmpty(testResult))
						unknowTestClassList.add(testItem.getKey());
					else if (String.valueOf(ConstData.TestResult.SUCCESS).equals(testResult))
						successTestClassList.add(testItem.getKey());
					else
						failedTestClassList.add(testItem.getKey());
			     }
			     mFaiedUnknowTestClassNames.clear();
			     mFaiedUnknowTestClassNames.addAll(failedTestClassList);
			     mFaiedUnknowTestClassNames.addAll(unknowTestClassList);
			     if(mFaiedUnknowTestClassNames.size() > 0)
			    	 gotoActivity(findCasePositionByClassName(mFaiedUnknowTestClassNames.get(0)));
			     else
			    	 mIsTestFaildOrUnknow = false;
			}
		});


        mAllFinish = getIntent().getIntExtra("finish", 0);

        mWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "DeviceTest");

        mNM = new NativeManger();
        mPtestButton = (Button) findViewById(R.id.btn_ptest);
        mPtestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                if (0 == sCount || time - sPreTime < 1000) {
                    sPreTime = time;
                    sCount++;
                    if (sCount >= ENTER_PTEST_MODE_CLICK_COUNT) {
                        sPreTime = 0;
                        sCount = 0;

                        mNM.enterPtest();
                    }
                } else {
                    sPreTime = 0;
                    sCount = 0;
                }
            }
        });

        if(checkAllPass()){
        	showTestSuccessDialog();
        }else{
        	//gotoActivity(0);
        	mBtnTestFaildUnknow.performClick();
        }
        
    }

    final static int FLAG_RF_CALIBRATED = 1;
    final static int FLAG_WIFI_CALIBRATED = 2;
    final static int FLAG_PCBA_PASS = 4;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, menu.FIRST + 1, Menu.NONE, "Exit");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == Menu.FIRST + 1) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean enableitemclick = true;

    @Override
    protected void onResume() {
    	//ToastUtils.showToast("onResume");
        enableitemclick = true;
        super.onResume();
        List<String> failedTestClassList = new ArrayList<String>();
        List<String> successTestClassList = new ArrayList<String>();
        List<String> unknowTestClassList = new ArrayList<String>();
        for(Map.Entry<String, String> testItem : mTestMap.entrySet()){
        	String testResult = StorageUtils.getDataFromSharedPreference(testItem.getKey());
        	if(TextUtils.isEmpty(testResult))
        		unknowTestClassList.add(testItem.getKey());
        	else if(String.valueOf(ConstData.TestResult.SUCCESS).equals(testResult))
        		successTestClassList.add(testItem.getKey());
        	else
        		failedTestClassList.add(testItem.getKey());
        }
        mLayoutTestResContainer.removeAllViews();
        for(String className : failedTestClassList){
        	TextView testView = new TextView(this);
        	testView.setTextColor(getResources().getColor(R.color.red));
        	testView.setText(mTestMap.get(className));
        	mLayoutTestResContainer.addView(testView);
        	testView.setTextSize(20);
        	testView.setGravity(Gravity.CENTER);
        	LinearLayout.LayoutParams testParams = (LinearLayout.LayoutParams)testView.getLayoutParams();
        	testParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        for(String className : successTestClassList){
        	TextView testView = new TextView(this);
        	testView.setTextColor(Color.parseColor("#00ff00"));
        	testView.setText(mTestMap.get(className));
        	mLayoutTestResContainer.addView(testView);
        	testView.setTextSize(20);
        	testView.setGravity(Gravity.CENTER);
        	LinearLayout.LayoutParams testParams = (LinearLayout.LayoutParams)testView.getLayoutParams();
        	testParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        for(String className : unknowTestClassList){
        	TextView testView = new TextView(this);
        	testView.setTextColor(getResources().getColor(R.color.yellow));
        	testView.setText(mTestMap.get(className));
        	mLayoutTestResContainer.addView(testView);
        	testView.setTextSize(20);
        	testView.setGravity(Gravity.CENTER);
        	LinearLayout.LayoutParams testParams = (LinearLayout.LayoutParams)testView.getLayoutParams();
        	testParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        enableCalibrationButton = true;
        if (mWakeLock.isHeld() == false) {
            mWakeLock.setReferenceCounted(false);
            mWakeLock.acquire();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //unregisterHomeKeyReceiver(PcbaDeviceTest.this);//for home key test
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mNetworkReceiver);
    }

    /**
     * send log to server.  please config the param{SEND_LOG_METHOD}.
     */
    private void sendLog() {
        if (mIsSendingLog) {
            Log.e(TAG, "sendLog called , but is sending log, so return !");
            return;
        }

        if (SEND_LOG_METHOD == SEND_LOG_BY_FTP) {
            if (mSenderHelper == null) {
                mSenderHelper = new LogSenderHelper(PcbaDeviceTest.this, mHandler);
            }
            mSenderHelper.connectFTP();
            mIsSendingLog = true;
        } else if (SEND_LOG_METHOD == SEND_LOG_BY_CIFS) {
            if (mLogSenderHelperByCISF == null)
                mLogSenderHelperByCISF = new LogSenderHelperByCISF(PcbaDeviceTest.this, mHandler);
            mLogSenderHelperByCISF.sendLog();
            mIsSendingLog = true;
        }

        showSendLogDialog();
    }

    private void createAssetFile(String name, String destPath) {

        InputStream is = null;
        OutputStream os = null;
        try {
            is = getAssets().open(name);
            os = new FileOutputStream(destPath);
            int data = 0;
            while (true) {
                data = is.read();
                if (data < 0) {
                    break;
                }
                os.write(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
            SystemUtil.execRootCmd("chmod 777 " + destPath);
        }
    }

    public final static String MEMTESTER_PATH = DeviceTest.DATA_PATH
            + "memtester";
    public final static String GPS_COLD_START_PATH = DeviceTest.DATA_PATH
            + "gps_coldstart";
    static final int DIALOG_CLEAR_ID = 10;

    private void startRunin() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_prompt)
                .setMessage(R.string.dialog_message_for_runin)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent shutdownIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                        shutdownIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        shutdownIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(shutdownIntent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent("android.intent.action.SelectFTPActivity");
//                startActivity(intent);
                    }
                }).show();
    }

    private void loadData() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                SAVE_DATA_PATH));
        List<TestCase> savedData = (List<TestCase>) ois.readObject();
        for (TestCase savedCase : savedData) {
            for (TestCase testCase : mTestCases) {
                if (testCase.getClassName().equals(savedCase.getClassName())) {
                    testCase.setResult(savedCase.getResult());
                    testCase.setDetail(savedCase.getDetail());
                    testCase.setShowResult(savedCase.isShowResult());
                }
            }
        }
        ois.close();
    }

    public static String formatResult(String testName, RESULT result,
                                      String detail) {
        if (detail == null) {
            return "[" + testName + "]\n" + result.name();
        }
        if (detail.startsWith(RESULT_INFO_HEAD_JUST_INFO)) {
            return detail.substring(RESULT_INFO_HEAD_JUST_INFO.length());
        }
        return "[" + testName + "]\n" + result.name() + detail;
    }

    private void saveResult2SP(TestCase testCase) {
        Log.e("lcf", "save test resules to SharedPreferences!");
        SharedPreferences resultPreferences = getSharedPreferences("test_result", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = resultPreferences.edit();
        if (testCase.getResult() != RESULT.UNDEF) {
            editor.putString(testCase.getTestName(), testCase.getResult().name());
        }
        editor.commit();
    }

    //log add start time and finish time
    public String formatResultByTestcast(TestCase testcast) {
        saveResult2SP(testcast); //added by lcf, for saving test result to SharedPreferences.
        StringBuilder resultStr = new StringBuilder();
        resultStr.append(testcast.getTestName()).append(" = ");
//        if(testcast.getDetail()!=null && testcast.getDetail().startsWith(RESULT_INFO_HEAD_JUST_INFO)) {
//            resultStr.append(testcast.getDetail().substring(RESULT_INFO_HEAD_JUST_INFO.length()));
//        }
        resultStr.append(testcast.getResult().name()).append("\n");

        return resultStr.toString();
    }
    
    synchronized private void save(String saveFilePath) throws IOException {
        for (TestCase testCase : mTestCases) {
        /*    if (testCase.getClassName().equals(
                    RuninTestActivity.class.getSimpleName())) {
                if (testCase.getDetail() == null) {
                    testCase.setDetail(new RuninTestActivity().getResult());
                }
            } else*/
            //if (testCase.getClassName().equals(GpsTestActivity.class.getSimpleName())) {
          //      if (testCase.getDetail() == null) {
          //          testCase.setDetail(new GpsTestActivity().getResult());
          //      }
          //  }
//            fw.write(formatResult(testCase.getTestName(), testCase.getResult(),
//                    testCase.getDetail()) + "\n");
            LogFileHelper.writeLogWithoutClose(formatResultByTestcast(testCase));
        }
        LogFileHelper.writeLogClose();

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                SAVE_DATA_PATH));
        oos.writeObject(mTestCases);
        oos.close();
    }



    private void uninstallPackage(String packageName) {
        String cmd = "mount -o remount,rw /system /system\n"
                + "rm -r /data/data/*DeviceTest*\n"
                + "rm /data/app/*DeviceTest*\n"
                + "rm /system/app/*DeviceTest*\n";
        SystemUtil.execScriptCmd(cmd, TEMP_FILE_PATH, true);

        Uri uninstallUri = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, uninstallUri);
        startActivity(uninstallIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent paramIntent) {
        super.onActivityResult(requestCode, resultCode, paramIntent);
        Log.i(TAG, " -------------------- onResult---request:" + requestCode + ",result:" + resultCode);
        int pos = requestCode;
        if (resultCode == RESULT.RETEST.ordinal()) {
            String strClsPath = null;
            strClsPath = "com.DeviceTest."+ mTestCases.get(pos).getClassName();
            try {
                Intent retestIntent = new Intent();
                retestIntent.setClassName(PcbaDeviceTest.this, strClsPath);
                startActivityForResult(retestIntent, pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (pos < mTestCases.size()) {
            RESULT result = RESULT.values()[resultCode];
            mTestCases.get(pos).setResult(result);
            mTestCases.get(pos).setShowResult(true);
            try {
                String detail = paramIntent
                        .getStringExtra(EXTRA_TEST_RESULT_INFO);
                mTestCases.get(pos).setDetail(detail);
                mTestCases.get(pos).startTime = paramIntent
                        .getStringExtra(EXTRA_TEST_START_TIME);
                mTestCases.get(pos).finishTime = paramIntent
                        .getStringExtra(EXTRA_TEST_FINISH_TIME);
                if(RESULT.OK == result || RESULT.FAIL == result){
                    if(!mIsTestFaildOrUnknow && pos < mTestCases.size() -1){
                    		gotoActivity(pos + 1);
                    }else if(mIsTestFaildOrUnknow){
                		int failedUnknowIndex = mFaiedUnknowTestClassNames.indexOf("com.DeviceTest."+ mTestCases.get(pos).getClassName());
                		if(failedUnknowIndex == mFaiedUnknowTestClassNames.size() - 1)
                			mIsTestFaildOrUnknow = false;
                		else
                			gotoActivity(findCasePositionByClassName(mFaiedUnknowTestClassNames.get(failedUnknowIndex + 1)));
                	}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (checkAllPass()) {
            SharedPreferences mSharedPreferences = getSharedPreferences("state", 0);
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putInt("first_finish", 1);
            edit.commit();
            showTestSuccessDialog();
            // startRunin();
            mAllFinish = 1;
            Log.e(TAG, "all test case pass!");
        } else {
            Log.e(TAG, "NO all test case pass!");
            closeTestSuccessDialog();
        }
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

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_SEND_LOG_SUCESS:
                    mIsSendingLog = false;
                    mSendLogFinish = true;
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Toast.makeText(PcbaDeviceTest.this, R.string.send_sucess_and_recovery, Toast.LENGTH_LONG).show();
                    mButtonCancel.performClick();
                /*
                if (mAllFinish == 1) {
                    if (mBatteryLevel > 75 && !mAC_IN) {
                        //LIMI 8.14
                        int state_flag=-1;
                          IECService  ecService = IECService.Stub.asInterface(ServiceManager.getService("ECServiceinfo"));
                          try{
                               state_flag=ecService.writeVendorFlags(0x1010,1);
                             } catch (RemoteException e) {
                               e.printStackTrace();
                             }
                          if(state_flag==0){
                              mCanRecovery = true;
                              mHandler.sendEmptyMessageDelayed(MSG_RECOVERY, 5000);
                          }
                    }
                    else {
                         if(mAC_IN)showshipmodeACDialog();
                         if(mBatteryLevel <=75)showshipmodeBatteryDialog();
                    }
                } 
                */
                    break;
                case MSG_SEND_LOG_FAIL:
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    mIsSendingLog = false;
                    showSendLogFailDialog();
//                Toast.makeText(DeviceTest.this, R.string.send_fail, Toast.LENGTH_LONG).show();
                    break;
                case MSG_LOGIN_SUCESS:
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    SharedPreferences sp = getSharedPreferences("SN", 0);
                    SN = sp.getString("SN", "devicetest");
                    LogFileHelper.LogFile = LogFileHelper.DEFAULT_LOG_FILE_PATH + SN + ".log";
                    mSenderHelper.sendLogToServer(LogFileHelper.LogFile, false);
                    break;
                case MSG_LOGIN_FAIL:
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Toast.makeText(PcbaDeviceTest.this, R.string.login_fail, Toast.LENGTH_LONG).show();
                    break;
                case MSG_RECOVERY:
                    Log.d(TAG, "MSG_RECOVERY--------");
                    Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
                    intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
                    startService(intent);
                    break;
                case MSG_RUNIN:
                    //test all selectd items then test runInTest.
                    if (mIsTestAll) {
                        Intent RunInIntent = new Intent(PcbaDeviceTest.this, RunInTest.class);
                        RunInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(RunInIntent);
                    }
                    break;
                /*
                case :SEND_CHECK_ALLPASS
                showShutdownDialog(getString(R.string.Run_runin));
                break;
                */
                case MSG_ENABLE_CALI:
                    enableCalibrationButton = true;
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void showSendLogDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getString(R.string.sending_log));
        mProgressDialog.show();
    }

    private void showShutdownDialog(String shutmsg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.Shutdown)
                .setMessage(getString(R.string.Shutdown_msg) + shutmsg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        Intent shutdownIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                        shutdownIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        shutdownIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(shutdownIntent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void showRecoveryDialog() {
        final CheckBox cb = new CheckBox(this);
        cb.setText(R.string.Erase_sd);
        Log.d(TAG, "showRecoveryDialog:");
        new AlertDialog.Builder(this)
                .setTitle(R.string.Recovery)
                .setMessage(R.string.Recovery_msg)
                .setView(cb)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SystemProperties.set("persist.tool_enable", "1");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tool_enable = SystemProperties.get("persist.tool_enable", "-1");
                        if (tool_enable.equals("1")) {
                            mNM.setDevicetestStatus(1);
                            mNM.setIntelChipBootMode();
                        } else {
                            Log.e("PcbaDeviceTest", "Setting tool_enable fails! Now tool_enable is: " + tool_enable);
                        }
                        SystemProperties.set("persist.tool_enable", "0");
                        tool_enable = SystemProperties.get("persist.tool_enable", "-1");
                        Log.e("PcbaDeviceTest", "Now tool_enable is: " + tool_enable);
                        tool_enable = "-1";
                        if (cb.isChecked()) {
                            Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
                            intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
                            startService(intent);
                        } else {
                            sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
                            try {
                                bootCommand(getApplicationContext(), "--wipe_data");
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void showSendLogFailDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.send_fail))
                .setPositiveButton(R.string.resend_log, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mSendLogButton.performClick();
                        dialog.dismiss();
                    }
                }).show();
//        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        }).show();
    }

    private void showNetworkCheckDialog() {
        if (mNetworkCheckDialog != null && mNetworkCheckDialog.isShowing())
            return;

        mNetworkCheckDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.log_send_dailog1))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }


    private void hideButton() {
        mButtonCancel.setVisibility(View.GONE);
        mClearButton.setVisibility(View.GONE);
        mRecoveryButton.setVisibility(View.GONE);
        mSendLogButton.setVisibility(View.GONE);
        mUninstallButton.setVisibility(View.GONE);
    }

    private boolean checkAllPass() {
    	Set<Entry<String, String>> allTestItems = mTestMap.entrySet();
    	for(Entry<String, String> item : allTestItems){
    		if(!String.valueOf(ConstData.TestResult.SUCCESS).equals(StorageUtils.getDataFromSharedPreference(item.getKey())))
    			return false;
    		
    	}
    	return true;
    	
    }

    /*
        //use for listen the network state change.
        private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        NetworkInfo networkInfo =
                                (NetworkInfo)extras.get(ConnectivityManager.EXTRA_NETWORK_INFO);

                        if (networkInfo == null) return;

                        State state = networkInfo.getState();
                        int networkType = networkInfo.getType();
                        //also can use TYPE_WIFI
                        if (networkType == ConnectivityManager.TYPE_ETHERNET) {
                            if (state == State.CONNECTED) {
                                Log.e(TAG, "ETHERNET ... CONNECTED");
                                if (mNetworkCheckDialog != null && mNetworkCheckDialog.isShowing())
                                    mNetworkCheckDialog.cancel();
                                if (mAllFinish == 1) {
                                    sendLog();
                                }
                            } else if (state == State.DISCONNECTED) {
                                Log.e(TAG, "ETHERNET ... DISCONNECTED");
                            }
                        }
                    }
                }
            }
        };


        private BroadcastReceiver mBatteryCheckReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int plugType = intent.getIntExtra("plugged", 0);
                    mBatteryLevel = intent.getIntExtra("level", 0);
                      if (plugType > 0) {
                          //AC IN
                          mAC_IN = true;
                      } else {
                            //AC NOT IN
                            mAC_IN = false;
                             //

                     }
               }
               if (mAllFinish == 1 && mSendLogFinish) {
                    if (mBatteryLevel > 75 && !mAC_IN) {
                        int state_flag=-1;
                                 int read_flag=-1;
                         int[] w_array=new int[]{-1,-1,-1,-1,-1};
                         int[] r_array=new int[]{-1,-1,-1,-1,-1};
    */
/*                    IECService  ecService = IECService.Stub.asInterface(ServiceManager.getService("ECServiceinfo"));    
                    try{                              
                         for(int i=0;i<5;i++){
                             w_array[i]=ecService.writeVendorFlags(0x1010,1); 
                             Thread.sleep(100);
                             r_array[i]=ecService.readVendorFlags(); 
                             Thread.sleep(100);
                             }
                         if(w_array[0]==0&&w_array[1]==0&&w_array[2]==0&&w_array[3]==0&&w_array[4]==0)state_flag=0;
                         if(r_array[0]==1&&r_array[1]==1&&r_array[2]==1&&r_array[3]==1&&r_array[4]==1)read_flag=1;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                            } catch (Exception e1) {
                                        e1.printStackTrace();
                                }
*/                    
/*                    if(image_flag==0){
                        if(state_flag==0&&read_flag==1) mHandler.sendEmptyMessageDelayed(MSG_RECOVERY, 5000); 
                    }else{
                        DissImageLogDialog();
                    }
                    //if(image_flag!=0)DissImageLogDialog();
                    //if(image_flag==0&&state_flag==0) mHandler.sendEmptyMessageDelayed(MSG_RECOVERY, 5000);     
                }
              else // Toast.makeText(getApplicationContext(), getString(R.string.shippingMode), 3000).show();
              {
                  if(mAC_IN)showshipmodeACDialog(); 
                  if(mBatteryLevel <=75)showshipmodeBatteryDialog();
              }
          }
      }
    };
    
    private void showshipmodeACDialog() {
        AlertDialog    mshipmodeACDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.shipmode_AC_Dialog))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }
*/
/*    
    private void DissImageLogDialog() {
        AlertDialog    mDissImageLogDialog = new AlertDialog.Builder(this)
        .setMessage(getString(R.string.diss_image_log))
        .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int which) {
*/
/*                        IECService  ecService = IECService.Stub.asInterface(ServiceManager.getService("ECServiceinfo"));    
                    try{                              
                        image_flag=ecService.resetImageCheckFlags(check_image_cmd);
                        Log.v(TAG, "image_flag= "+image_flag);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                   }
*/
/*                   }
                }).show();
}
*/
/*    
    private void showshipmodeBatteryDialog() {
        AlertDialog    mshipmodeBatteryDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.shipmode_Battery_Dialog))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
*/
    private static File RECOVERY_DIR = new File("/cache/recovery");
    private static File COMMAND_FILE = new File(RECOVERY_DIR, "command");

    private static void bootCommand(Context context, String arg) throws IOException {
        RECOVERY_DIR.mkdirs();  // In case we need it
        COMMAND_FILE.delete();  // In case it's not writable

        FileWriter command = new FileWriter(COMMAND_FILE);
        try {
            command.write(arg);
            command.write("\n");
        } finally {
            command.close();
        }

        // Having written the command file, go ahead and reboot
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");

        throw new IOException("Reboot failed (no permissions?)");
    }

    private void showTestSuccessDialog() {
    	if(mAllSuccessDialog == null){
    		AlertDialog.Builder builder = new AlertDialog.Builder(PcbaDeviceTest.this);
            builder.setMessage(R.string.test_success_info);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    shouldReset = true;
                }
            });
            mAllSuccessDialog = builder.create();
    	}
        mAllSuccessDialog.show();
    }
    
    private void closeTestSuccessDialog(){
    	if(mAllSuccessDialog != null && mAllSuccessDialog.isShowing())
    		mAllSuccessDialog.dismiss();
    }

    private void registerHomeKeyReceiver(Context context) {
        Log.i(TAG, "registerHomeKeyReceiver ++++++++++++++++++++++++");
        mHomeKeyReceiver = new HomeWatcherReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private void unregisterHomeKeyReceiver(Context context) {
        if (null != mHomeKeyReceiver) {
            Log.i(TAG, "unregisterHomeKeyReceiver ----------------------");
            context.unregisterReceiver(mHomeKeyReceiver);
            mHomeKeyReceiver = null;
        }
    }

    private class HomeWatcherReceiver extends BroadcastReceiver {

        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: action: " + action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                Log.i(TAG, "reason: " + reason);

                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // ׌дHomeݼ
                    Log.w(TAG, "homekey============================");
                    if (shouldReset && mAllFinish == 1) {
                        if (checkAllPass()) {
                            mRecoveryButton.setVisibility(View.VISIBLE);
                            mNM.setDevicetestStatus(1);
                            try {
                                bootCommand(getApplicationContext(), "--wipe_data");
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // ӤдHomeݼ ܲ֟ activityȐۻݼ
                    Log.i(TAG, "long press home key or activity switch");

                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    // ̸ǁ
                    Log.i(TAG, "lock");
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung ӤдHomeݼ
                    Log.i(TAG, "assist");
                }

            }
        }

    }

    private void gotoActivity(int position){
        Intent intent = new Intent();
        try {
            if (mTestCases.get(position) != null) {
                String strClsPath = "com.DeviceTest."
                        + mTestCases.get(position).getClassName();
                intent.setClass(PcbaDeviceTest.this,
                        Class.forName(strClsPath).newInstance()
                                .getClass());
                intent.putExtra(EXTRA_TEST_PROGRESS, "0/1");
                startActivityForResult(intent, position);
                //Toast.makeText(this,mTestCases.get(position).getTestName(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int findCasePositionByClassName(String className){
    	for(int i = 0; i < mTestCases.size(); ++i){
    		if(("com.DeviceTest." + mTestCases.get(i).getClassName()).equals( className))
    			return i;
    	}
    	return -1;
    }
}
