package com.DeviceTest.helper;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract.Data;
import android.util.Log;

import com.DeviceTest.DeviceTest;
import com.DeviceTest.ForScanningActivity;

public class LogSenderHelper {
	private final static String TAG = "LogSenderHelper";
	
	private final String FTP_CONFIG_PATH = "ftp.config";

	FTPClient mFTPClient;
	
	private String mFTPHost;// = "172.16.9.86";
	private int mFTPPort;// = 21;
	private String mFTPUser;// = "test";
	private String mFTPPassword;// = "1";
	private String mFTPPath;// = "ARIlog";
	
	private Handler mHandler;
	
	public static Date md=null;
	
	private boolean istest = false;
	
	private SharedPreferences mSharedPreferences;
	
	public LogSenderHelper(Context context ,Handler handler) {
		mFTPClient = new FTPClient();
		mHandler = handler;
		//getFtpConfig(context);
		getConfig(context);
	}
	
	private void getConfig(Context context) {
		mSharedPreferences = context.getSharedPreferences("FTP", 0);
		mFTPHost = mSharedPreferences.getString("host", "10.10.10.55");
		mFTPPath = mSharedPreferences.getString("path", null);
		mFTPPort = mSharedPreferences.getInt("port", 21);
		mFTPUser = mSharedPreferences.getString("user", "ecsaioadmin");
		mFTPPassword = mSharedPreferences.getString("pwd", "ecsaioadmin");
		
	}

	public void getFtpConfig(Context c) {
		String temp;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(c.getAssets().open(FTP_CONFIG_PATH)));

			while ((temp = br.readLine()) != null) {
				String[] data = temp.split(":");
				if (data[0].trim().equals("host")) {
					mFTPHost = data[1].trim();
				} else if (data[0].trim().equals("port")) {
					mFTPPort = Integer.valueOf(data[1].trim());
				} 
				else if (data[0].trim().equals("user")) {
					mFTPUser = data[1].trim();
				} else if (data[0].trim().equals("pwd")) {
					mFTPPassword = data[1].trim();
				}
			}
			
			if (br != null) {
				br.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connectFTP() {
		Thread thread = new Thread(new CmdConnect());
		thread.start();
	}
	
	public void sendLogToServer(String path,boolean b) {
		if (mFTPClient != null && mFTPClient.isConnected()) {
			new CmdUpload().execute(path);
			istest = b;
		}
	}
	
	public void getLogFromServer(String path) {
		if (mFTPClient != null && mFTPClient.isConnected()) {
			new CmdGetFile().execute(path);
			istest = false;
		}
	}

	public void disconnectFTP() {
		Thread thread = new Thread(new CmdDisConnect());
		thread.start();
	}
	
	/*
	 * Function ： 用于FTP 连接登陆
	 */
	
	public class CmdConnect extends FtpCmd {
		@Override
		public void run() {
			try {
				String[] welcome = mFTPClient.connect(mFTPHost, mFTPPort);
				if (welcome != null) {
					for (String value : welcome) {
						Log.e(TAG, "connect " + value);
					}
				}
				mFTPClient.login(mFTPUser, mFTPPassword);
				Log.e(TAG, "Login Sucess!");
				if(!("").equals(mFTPPath) && null != mFTPPath)
					mFTPClient.changeDirectory(mFTPPath);
				mHandler.sendEmptyMessage(DeviceTest.MSG_LOGIN_SUCESS);
			} catch (IllegalStateException illegalEx) {
				mHandler.sendEmptyMessage(DeviceTest.MSG_LOGIN_FAIL);
				illegalEx.printStackTrace();
			} catch (IOException ex) {
				mHandler.sendEmptyMessage(DeviceTest.MSG_LOGIN_FAIL);
				ex.printStackTrace();
			} catch (FTPIllegalReplyException e) {
				mHandler.sendEmptyMessage(DeviceTest.MSG_LOGIN_FAIL);
				e.printStackTrace();
			} catch (FTPException e) {
				mHandler.sendEmptyMessage(DeviceTest.MSG_LOGIN_FAIL);
				e.printStackTrace();
			}
		}
	}
	
	
	/*
	 * Function: 上传指定文件
	 */
	public class CmdUpload extends AsyncTask<String, Integer, Boolean> {
		String path;

		public CmdUpload() {

		}

		@Override
		protected Boolean doInBackground(String... params) {
			path = params[0];
			try {
				File file = new File(path);
				mFTPClient.upload(file, new DownloadFTPDataTransferListener(
						file.length()));
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

			return true;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Boolean result) {
			Log.e(TAG, "" + (result ? path + "上传成功" : "上传失败"));
			mHandler.sendEmptyMessage(result ? ForScanningActivity.MSG_SEND_LOG_SUCESS
					: DeviceTest.MSG_SEND_LOG_FAIL);
			//上传完毕， 断开链接
			if(istest = false)
				disconnectFTP();
			
		}
	}
	
	public class CmdGetFile extends AsyncTask<String, Integer, Boolean> {
		String path;

		public CmdGetFile() {

		}

		@Override
		protected Boolean doInBackground(String... params) {
			path = params[0];
			try {
				File file = new File(path);
				md = mFTPClient.modifiedDate("ForSyncTime.txt");
				md.setHours(md.getHours()+8);
				mFTPClient.deleteFile("ForSyncTime.txt");
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

			return true;
		}
		
		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Boolean result) {
			Log.e(TAG, "" + (result ? path + "获取成功" : "获取失败"));
			mHandler.sendEmptyMessage(result ? ForScanningActivity.MSG_GET_LOG_SUCCESS
					: DeviceTest.MSG_SEND_LOG_FAIL);
			//获取完毕， 断开链接
			disconnectFTP();
			
		}
	}

	/*
	 * Function: 断开连接
	 */
	public class CmdDisConnect extends FtpCmd {

		@Override
		public void run() {
			if (mFTPClient != null) {
				try {
					mFTPClient.disconnect(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private class DownloadFTPDataTransferListener implements
			FTPDataTransferListener {

		private int totolTransferred = 0;
		private long fileSize = -1;

		public DownloadFTPDataTransferListener(long fileSize) {
			if (fileSize <= 0) {
				throw new RuntimeException(
						"the size of file muset be larger than zero.");
			}
			this.fileSize = fileSize;
		}

		@Override
		public void aborted() {
		}

		@Override
		public void completed() {
		}

		@Override
		public void failed() {
		}

		@Override
		public void started() {
		}

		@Override
		public void transferred(int length) {
			// totolTransferred += length;
			// float percent = (float) totolTransferred / this.fileSize;
			// setLoadProgress((int) (percent * mPbLoad.getMax()));
		}
	}
	
	public abstract class FtpCmd implements Runnable {
		public abstract void run();

	}


}
