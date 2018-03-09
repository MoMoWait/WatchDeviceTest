package com.DeviceTest.helper;

import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;

import com.DeviceTest.BluetoothTestActivity;
import com.DeviceTest.BrightnessTestActivity;
import com.DeviceTest.CallNumTestActivity;
import com.DeviceTest.CameraWithFlashTestActivity;
import com.DeviceTest.ChargeTestActivity;
import com.DeviceTest.DeviceTest;
import com.DeviceTest.GpsTestActivity;
import com.DeviceTest.GsensorTestActivity;
import com.DeviceTest.InfomationActivity;
import com.DeviceTest.LcdTestActivity;
import com.DeviceTest.PcbaDeviceTest;
import com.DeviceTest.PhoneMicTestActivity;
import com.DeviceTest.R;
import com.DeviceTest.R.id;
import com.DeviceTest.SimCardTestActivity;
import com.DeviceTest.SpeakerTestActivity;
import com.DeviceTest.TouchTestActivity;
import com.DeviceTest.VersionTestActivity;
import com.DeviceTest.VibrationTestActivity;
import com.DeviceTest.WifiTestActivity;
import com.DeviceTest.data.ConstData;
import com.DeviceTest.helper.TestCase.RESULT;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ControlButtonUtil {
    static Activity mActivity;
    public static ControlButtonUtil mControlButtonView;
    static NotificationManager mNotificationManager;
    static Intent resultIntent = new Intent();

    public ControlButtonUtil(Activity paramActivity) {
        resultIntent.removeExtra(DeviceTest.EXTRA_TEST_RESULT_INFO);
        resultIntent.removeExtra(DeviceTest.EXTRA_TEST_FINISH_TIME);
        resultIntent.removeExtra(DeviceTest.EXTRA_TEST_START_TIME);

        String startTime = DateFormat.format("yyyy-MM-dd kk-mm-ss",
                System.currentTimeMillis()).toString();
        resultIntent.putExtra(DeviceTest.EXTRA_TEST_START_TIME, startTime);

        mActivity = paramActivity;
        final Button passButton = (Button) mActivity.findViewById(R.id.btn_Pass);
        if (mActivity != null) {
            passButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                	performPassButtonClick();
                }
            });
        }
        final Button failedButton = (Button) mActivity.findViewById(R.id.btn_Fail);

        if (mActivity != null) {
            failedButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                   performFailButtonClick();
                }
            });
        }

        Button skipButton = (Button) mActivity.findViewById(R.id.btn_Skip);
        skipButton.setVisibility(View.GONE);
        skipButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // ControlButtonUtil.mActivity.setResult(RESULT.SKIP.ordinal(),
                // resultIntent);
                // if (ControlButtonUtil.mNotificationManager != null)
                // ControlButtonUtil.mNotificationManager.cancelAll();
                // ControlButtonUtil.mActivity.finish();
                String finishTime = DateFormat.format("yyyy-MM-dd kk-mm-ss",
                        System.currentTimeMillis()).toString();
                resultIntent.putExtra(DeviceTest.EXTRA_TEST_FINISH_TIME,
                        finishTime);

                ControlButtonUtil.mActivity.setResult(RESULT.UNDEF.ordinal(),
                        resultIntent);
                if (ControlButtonUtil.mNotificationManager != null)
                    ControlButtonUtil.mNotificationManager.cancelAll();
                ControlButtonUtil.mActivity.finish();
            }
        });


        final Button retestButton = (Button) mActivity.findViewById(R.id.btn_Retest);
        if (mActivity != null) {
            retestButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    // ControlButtonUtil.mActivity.setResult(RESULT.SKIP.ordinal(),
                    // resultIntent);
                    // if (ControlButtonUtil.mNotificationManager != null)
                    // ControlButtonUtil.mNotificationManager.cancelAll();
                    // ControlButtonUtil.mActivity.finish();
                    retestButton.setClickable(false);
                    String finishTime = DateFormat.format("yyyy-MM-dd kk-mm-ss",
                            System.currentTimeMillis()).toString();
                    resultIntent.putExtra(DeviceTest.EXTRA_TEST_FINISH_TIME,
                            finishTime);

                    ControlButtonUtil.mActivity.setResult(RESULT.RETEST.ordinal(),
                            resultIntent);
                    if (ControlButtonUtil.mNotificationManager != null)
                        ControlButtonUtil.mNotificationManager.cancelAll();
                    ControlButtonUtil.mActivity.finish();
                }
            });
        }

    }

    //
    // public static void back() {
    // }

    public static void setResult(String result) {
        resultIntent.putExtra(DeviceTest.EXTRA_TEST_RESULT_INFO, result);
    }

    public static void Hide() {
        mActivity.findViewById(R.id.btn_Pass).setVisibility(View.GONE);
        mActivity.findViewById(R.id.btn_Fail).setVisibility(View.GONE);
        mActivity.findViewById(R.id.btn_Skip).setVisibility(View.GONE);
        mActivity.findViewById(R.id.btn_Retest).setVisibility(View.GONE);
    }

    public static void Show() {
        mActivity.findViewById(R.id.btn_Pass).setVisibility(View.VISIBLE);
       // mActivity.findViewById(R.id.btn_Pass).requestFocus();
        mActivity.findViewById(R.id.btn_Fail).setVisibility(View.VISIBLE);
       // mActivity.findViewById(R.id.btn_Fail).requestFocus();
//		mActivity.findViewById(R.id.btn_Skip).setVisibility(View.VISIBLE);
//		mActivity.findViewById(R.id.btn_Skip).requestFocus();
        mActivity.findViewById(R.id.btn_Retest).setVisibility(View.VISIBLE);
       // mActivity.findViewById(R.id.btn_Retest).requestFocus();
    }

    public static void initControlButtonView(Activity paramActivity) {
        mControlButtonView = new ControlButtonUtil(paramActivity);
    }

    static void setNotification(NotificationManager paramNotificationManager) {
        mNotificationManager = paramNotificationManager;
    }
    
    public void performPassButtonClick(){
    	Button passButton = (Button)mActivity.findViewById(R.id.btn_Pass);
    	passButton.setClickable(false);
    	StorageUtils.saveDataToSharedPreference(mActivity.getClass().getName(), "" + ConstData.TestResult.SUCCESS);
         String finishTime = DateFormat.format("yyyy-MM-dd kk-mm-ss",
                 System.currentTimeMillis()).toString();
         resultIntent.putExtra(DeviceTest.EXTRA_TEST_FINISH_TIME,
                 finishTime);

         ControlButtonUtil.mActivity.setResult(RESULT.OK.ordinal(),
                 resultIntent);
         if (ControlButtonUtil.mNotificationManager != null)
             ControlButtonUtil.mNotificationManager.cancelAll();
         ControlButtonUtil.mActivity.finish();
    }
    
    
    public void performFailButtonClick(){
    	Button failedButton = (Button) mActivity.findViewById(R.id.btn_Fail);
    	failedButton.setClickable(false);
    	StorageUtils.saveDataToSharedPreference(mActivity.getClass().getName(), "" + ConstData.TestResult.FAILED);
         String finishTime = DateFormat.format("yyyy-MM-dd kk-mm-ss",
                 System.currentTimeMillis()).toString();
         resultIntent.putExtra(DeviceTest.EXTRA_TEST_FINISH_TIME,
                 finishTime);

         ControlButtonUtil.mActivity.setResult(RESULT.FAIL.ordinal(),
                 resultIntent);
         if (ControlButtonUtil.mNotificationManager != null)
             ControlButtonUtil.mNotificationManager.cancelAll();
         ControlButtonUtil.mActivity.finish();
    }

}
