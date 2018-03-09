package com.DeviceTest.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ForInstall {
	private String ApkName;
	private String PackageApkName;
	private String ApkPath;
	private int ApkVersionCode;
	private Context Fcontext;
	
	public ForInstall(Context context,String ApkName,String FilePath)
	{
		this.Fcontext = context;
		this.ApkName = ApkName;
		ApkPath = FilePath + "/" + ApkName;
		PackageManager ApkPackage = Fcontext.getPackageManager();
		PackageInfo ApkInfo = ApkPackage.getPackageArchiveInfo(ApkPath, 0);
		this.ApkVersionCode = ApkInfo.versionCode;
		this.PackageApkName = ApkInfo.packageName;
	}
	
	public String GetApkName()
	{
		return ApkName;
	}
	
	public String GetPackageApkName()
	{
		return PackageApkName;
	}
	
	public String GetApkPath()
	{
		return ApkPath;
	}
	
	public int GetApkVersionCode()
	{
		return ApkVersionCode;
	}

}
