package com.DeviceTest.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.DeviceTest.DeviceTest;

import android.content.Context;

public class LogFileHelper {
	public final static String TAG = "LogFileHelper";

	public final static String DEFAULT_LOG_FILE_PATH = "/data/data/com.DeviceTest/";
	public final static String DEFAULT_LOG_FILE_NAME = "devicetest.log";

	public static String adjustTimeFromServerString = null;
	public static FileWriter mFileWriter = null;
	public static String LogFile = null;

	public LogFileHelper(String logFilePath) {
		try {
			if (logFilePath != null && !logFilePath.trim().equals("")) {
				mFileWriter = new FileWriter(logFilePath, true);
			} else {
				mFileWriter = new FileWriter(DEFAULT_LOG_FILE_PATH
						+ DEFAULT_LOG_FILE_NAME, true);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized public static void writeLog(String log) {
		try {
			if (LogFileHelper.LogFile == null) {
				LogFile = DEFAULT_LOG_FILE_PATH + DEFAULT_LOG_FILE_NAME;
			}
			FileWriter mFW = new FileWriter(LogFile, true);
			mFW.write(log);
			mFW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized public static void writeLogWithoutClose(String log) {
		try {	
			if (LogFileHelper.LogFile == null) {
				LogFile = DEFAULT_LOG_FILE_PATH + DEFAULT_LOG_FILE_NAME;
			}
			mFileWriter = new FileWriter(LogFile, true);
			mFileWriter.write(log);
			mFileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Close the File.
	 * 
	 * if you call writeLogWithoutClose ,you need call writeLogClose in pairs.
	 */
	
	synchronized public static void writeLogClose() {
		try {
			if (mFileWriter != null) 
				mFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Func: Generate Log header.
	 */
	public static String getLogHeader(Context c) {
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		//Date date = new Date(System.currentTimeMillis());
		//DeviceTest.TIME = sdf.format(date);
		StringBuilder sb = new StringBuilder();
		sb.append("[SYSTEM]").append("\n");
		sb.append("SPN=").append(SystemInfoUtil.getProductName()).append("\n");
		sb.append("SN=").append(DeviceTest.SN).append("\n");
		sb.append("MBSN=").append(android.os.Build.SERIAL).append("\n");
		sb.append("BTMAC=").append(SystemInfoUtil.getBTMAC()).append("\n");
		//sb.append("WMAC=").append(SystemInfoUtil.getLocalMacAddress(c)).append("\n");
		sb.append("WMAC=").append(SystemInfoUtil.getWlanId(c)).append("\n");
		sb.append("\n");
		
		sb.append("[TEST]").append("\n");
		sb.append("SYSTIME=").append(DeviceTest.TIME).append("\n");
		sb.append("CPU=").append(SystemInfoUtil.getCpuName()).append("\n");
		sb.append("EMMC=").append(SystemInfoUtil.getEMMC(c)).append("\n");
		//sb.append("RAM=").append(SystemInfoUtil.GetMemInfo1(c)).append("\n");
		//sb.append("CAMERA_1=").append("").append("\n");
		//sb.append("CAMERA_2=").append("").append("\n");
		sb.append("\n");
		sb.append("[RESULT]").append("\n");
		//sb.append("BoardID=").append(nativeManger.getBoardId()).append("\n");
		//sb.append(adjustTimeFromServerString).append("\n");
		//sb.append("Build_number=").append(SystemInfoUtil.getBuildNumber()).append("\n");
		return sb.toString();
	}
	
	public static boolean rmLogFileIfExist() {
		File file = new File(LogFile);
		if (file != null && file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}
	
}
