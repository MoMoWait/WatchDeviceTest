package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.File;
import java.math.BigInteger;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import java.util.List;
import android.widget.EditText;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.text.InputType;

//import android.telephony.TelephonyManager;
import android.util.Log;

public class SmsSendTestActivity extends Activity {
	private static final String TAG = "SmsSendTestActivity";

	TextView mResult;
	private Button   mSend;
	private EditText mPhoneNum;
	private EditText mMsgContent;
	private TextView mSendStatus;
	private final String action="com.rk.sms";
	private final String DEFAULT_NUM = "10086";
	private final String DEFAULT_CONTENT = "hi 10086";
	private int SEND_SUCCESS_NUM=0;
	private int SEND_FAIL_NUM=0;
	
	private SmsManager smsManager;
	private PendingIntent pi;
	private sendReceiver receiver;
	private String sendStatus;
	Handler mHandler = new Handler();
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		
		setTitle(getTitle() + "----("
				+ getIntent().getStringExtra(DeviceTest.EXTRA_TEST_PROGRESS) + ")");
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.smssendtest);
		smsManager = SmsManager.getDefault();
		receiver=new sendReceiver();
        IntentFilter filter=new IntentFilter();
		filter.addAction(action);
		registerReceiver(receiver,filter);

		Intent intent = new Intent(action);
    	pi = PendingIntent.getBroadcast(this, 0, intent, 0);



		

		ControlButtonUtil.initControlButtonView(this);
		initRes();
	}

	
	protected void onResume() {
		super.onResume();
		/*TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();

		if (imsi == null) {
			mResult.setText("Cann't get IMSI!");
		} else {
			mResult.setText("IMSI:" + imsi);
		}*/
		SEND_SUCCESS_NUM=0;
		SEND_FAIL_NUM=0;
		mSendStatus.setText("");
		
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
	private void initRes() {
		/*this.mResult = (TextView) findViewById(R.id.text);
		this.mResult.setVisibility(View.VISIBLE);
		this.mResult.setGravity(17);*/
		sendStatus=getResources().getString(R.string.send_status);
		mPhoneNum = (EditText) findViewById(R.id.phone_num);
		mPhoneNum.setText(DEFAULT_NUM);
		mPhoneNum.setInputType(InputType.TYPE_CLASS_PHONE);
		mMsgContent = (EditText) findViewById(R.id.msg_content);
		mMsgContent.setText(DEFAULT_CONTENT);
		mSendStatus = (TextView)findViewById(R.id.send_status);

		mSend = (Button) findViewById(R.id.send_btn);
		mSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SendSms();
			}
		});
		
		//mSendStatus.setText(R.string.send_status);
		
	}
		
	private void SendSms()
	{
		mSend.setEnabled(false);
		String mobile = mPhoneNum.getText().toString();
		String content = mMsgContent.getText().toString();
		
		if (PhoneNumberUtils.isGlobalPhoneNumber(mobile)) 
		{
			try {
				/*
				 * 两个条件都检查通过的情况下,发送简讯 *
				 * 先建构一PendingIntent对象并使用getBroadcast
				 * ()方法进行Broadcast *
				 * 将PendingIntent,电话,简讯文字等参数传入sendTextMessage
				 * ()方法发送简讯
				 */
				// SmsManager manager =
				// SmsManager.getDefault();
				// smsManager.sendTextMessage("10086",null,"hi,this is sms",null,null);
				/*PendingIntent mPI = PendingIntent.getBroadcast(
						SmsSendTestActivity.this, 0, new Intent(), 0);*/

				if (content.length() >= 70 || content.length() == 0) {
					if (content.length() >= 70) {

						// 短信字数大于70，自动分条
						List<String> ms = smsManager
								.divideMessage(content);

						for (String str : ms) {
							// 短信发送

							smsManager.sendTextMessage(mobile,
									null, content, pi, null);
						}
					}
					if (content.length() == 0) {
						//Toast.makeText(SmsSendTestActivity.this, "请输入发送内容",
						//		Toast.LENGTH_LONG).show();
					}
				} else {
					smsManager.sendTextMessage(mobile, null,
							content, pi, null);
				}

				//Toast.makeText(SmsSendTestActivity.this, "发送成功！",
				//		Toast.LENGTH_LONG).show();

			} catch (Exception e) {
							e.printStackTrace();
						}
						//Toast.makeText(SmsSendTestActivity.this, "短信成功!!",
						//		Toast.LENGTH_SHORT).show();
						// mEditText1.setText("");
						// mEditText2.setText("");
					}
	}

	class sendReceiver extends BroadcastReceiver{
	//д¸�ֆ�@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int resultCode = getResultCode();
		if(resultCode==Activity.RESULT_OK){
			SEND_SUCCESS_NUM++;
			Log.d(TAG,"send success."+SEND_SUCCESS_NUM);			
		}else{
			SEND_FAIL_NUM++;
			Log.d(TAG,"send failed."+SEND_FAIL_NUM);

		}
		mSend.setEnabled(true);
		mSendStatus.setText(String.format(sendStatus, SEND_SUCCESS_NUM,SEND_FAIL_NUM));
		mHandler.postDelayed(mResultRunnable, 2 * 1000);

		}
	}

		Runnable mResultRunnable = new Runnable() {

		public void run() {
			if(SEND_SUCCESS_NUM>0)
				findViewById(R.id.btn_Pass).performClick();
			else
				findViewById(R.id.btn_Fail).performClick();
		}
	};
		
}
