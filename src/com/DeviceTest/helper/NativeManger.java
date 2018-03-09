package com.DeviceTest.helper;

import android.R.integer;


public class NativeManger {

	public NativeManger() {
		init();
	}
	
	static {
		try {
			System.loadLibrary("rkinfoDeviceTest");
		} catch (Exception e) {

		}
	}
	
	public native void init();
	public native String getMAC();
	public native String getSN();
	public native String getBoardId();
	public native String getIMEI();
	public native String getUID();
	public native String getBT();
	
	public native String getLcdId();
	
	public native int gSensorStore(int x, int y, int z);
	public native int[] gSensorLoad();
	public native int gSensorCabiration(int[] array);
	
	public native int setIntelChipBootMode();
	public native int setDevicetestStatus(int status);	
	
	public native int getWifiResult();
	public native int getRfResult();
	public native int getPcbaResult();
	public native int enterPtest();
	public native int getResult();

	public native int gSensorCalibration(int value);

	public native int saveGsensorX(int value);
	public native int saveGsensorY(int value);
	public native int saveGsensorZ(int value);
	public native int saveGsensor(int x, int y, int z);
	public native int savePsensor(int value);

	public native int[] readPsData();
	public native void max3GTxPower(int power, int band, int channel);
	public native void stopMax3GTxPower();
}
