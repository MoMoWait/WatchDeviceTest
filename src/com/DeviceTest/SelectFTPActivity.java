package com.DeviceTest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import com.DeviceTest.StressTest.RunInTest;
import com.DeviceTest.helper.LogFileHelper;
import com.DeviceTest.helper.LogSenderHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SelectFTPActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "SelectFTP";
	
	private TextView host,path,port,user,pwd;
	private EditText mHost,mPath,mPort,mUser,mPwd;
	private Button mBtnSend,mBtnCancle; 
	private ProgressDialog mProgressDialog;
	
	private SharedPreferences mSharedPreferences;
	private LogSenderHelper mSenderHelper;
	
	private boolean mIsSendingLog = false;
	
	public static final int MSG_SEND_LOG_SUCESS = 0;
	public static final int MSG_SEND_LOG_FAIL = 1;
	public static final int MSG_LOGIN_SUCESS= 2;
	public static final int MSG_LOGIN_FAIL = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectftp);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		init();
		
		mSharedPreferences = getSharedPreferences("FTP", 0);
		
	}
	
	private void init() {
		host = (TextView)findViewById(R.id.ftp_host);
		path = (TextView)findViewById(R.id.ftp_path);
		port = (TextView)findViewById(R.id.ftp_port);
		user = (TextView)findViewById(R.id.ftp_user);
		pwd = (TextView)findViewById(R.id.ftp_pwd);
		mHost = (EditText)findViewById(R.id.ftp_host_text);
		mPath = (EditText)findViewById(R.id.ftp_path_text);
		mPort = (EditText)findViewById(R.id.ftp_port_text);
		mUser = (EditText)findViewById(R.id.ftp_user_text);
		mPwd = (EditText)findViewById(R.id.ftp_pwd_text);
		mBtnSend = (Button)findViewById(R.id.ftp_sure);
		mBtnCancle = (Button)findViewById(R.id.ftp_cancle);
		
		mBtnSend.setOnClickListener(this);
		mBtnCancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ftp_sure:
			
			if (mIsSendingLog) {
				Log.e(TAG, "sendLog called , but is sending log, so return !");
				return;
			}
			
			try {
				SharedPreferences.Editor edit = mSharedPreferences.edit();
				edit.putString("host", mHost.getText().toString().trim());
				edit.putString("path", mPath.getText().toString().trim());
				edit.putInt("port", Integer.parseInt(mPort.getText().toString().trim()));
				edit.putString("user", mUser.getText().toString().trim());
				edit.putString("pwd", mPwd.getText().toString().trim());
				edit.commit();
				
				if (mSenderHelper == null) {
					mSenderHelper = new LogSenderHelper(this, mHandler);
				}
				mSenderHelper.connectFTP();
				mIsSendingLog = true;
				
				showSendLogDialog();
			}catch(Exception e){
				//} else {
					e.printStackTrace();
				}
			break;
		case R.id.ftp_cancle:
			this.finish();
			break;
		}
	}
	
	private void showSendLogDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(getString(R.string.sending_log));
		mProgressDialog.show();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SEND_LOG_SUCESS:
				mIsSendingLog = false;
				//mSendLogFinish = true;
				if (mProgressDialog!=null && mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				Toast.makeText(getApplicationContext(), R.string.send_sucess_and_recovery, Toast.LENGTH_LONG).show();
				finish();
				break;
			case MSG_SEND_LOG_FAIL:
				if (mProgressDialog!=null && mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				mIsSendingLog = false;
				showSendLogFailDialog();
				Toast.makeText(getApplicationContext(), R.string.send_fail, Toast.LENGTH_LONG).show();
				break;
			case MSG_LOGIN_SUCESS:
				if (mProgressDialog!=null && mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				mSenderHelper.sendLogToServer(LogFileHelper.LogFile,false);
				break;
			case MSG_LOGIN_FAIL: 
				if (mProgressDialog!=null && mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};
	
	private void showSendLogFailDialog() {
		new AlertDialog.Builder(this)
	    .setMessage(getString(R.string.send_fail))
        .setPositiveButton(R.string.resend_log, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
        }).show();
//        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        }).show();
	}
	
}
