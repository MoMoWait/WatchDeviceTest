package com.DeviceTest.StressTest;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.PointF;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class AntutuLocations {
	//基于安兔兔4.01版本
	public static final String PATH_CONFIGS_DIR = "/data/data/com.DeviceTest/data/config/";

	// 第一次启动时 开始按键坐标
	public static PointF enterTestBtn = new PointF();

	// 开始测试按键
	public static PointF startTestBtn = new PointF();

	// 第二次测试按键
	public static PointF testAgianButton = new PointF();

	// 再次测试时的start Test按键
	public static PointF startTestButtonAfterInitial = new PointF();
	
	private static final String TAG = "AntutuLocations";
	
	public static void init() {
		File antutuFile = new File(PATH_CONFIGS_DIR+"antutu_location.xml");
		if(antutuFile.exists()){
			try {
				FileInputStream fin = new FileInputStream(antutuFile);
				readAntutuLocations(fin);
				fin.close();
				
				return;
			} catch (Exception e) {
			}
			
			
		}
		

		if (isSDKof4_1()) {

			enterTestBtn.x = 240f;
			enterTestBtn.y = 248f;

			startTestBtn.x = 305;
			startTestBtn.y = 205f;
			testAgianButton.x = 304f;
			testAgianButton.y = 324f;

			// for android 4.1
			startTestButtonAfterInitial.x = 465f;
			startTestButtonAfterInitial.y = 366f;

		} else {

			enterTestBtn.x = 240f;
			enterTestBtn.y = 248f;

			startTestBtn.x = 305;
			startTestBtn.y = 205f;
			testAgianButton.x = 304f;
			testAgianButton.y = 324f;

			startTestButtonAfterInitial.x = 485f;
			startTestButtonAfterInitial.y = 310f;

		}
	}

	private static boolean isSDKof4_1() {

		return Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN;

	}
	
	
	private static void readAntutuLocations(InputStream inStream) throws Exception{
		SAXParserFactory spf = SAXParserFactory.newInstance(); // 初始化sax解析器  
        SAXParser sp = spf.newSAXParser(); // 创建sax解析器  
        //XMLReader xr = sp.getXMLReader();// 创建xml解析器  
        DefaultHandler handler = new AntutuXMLContentHandler();  
        sp.parse(inStream, handler);  
	}
	
	
	static class AntutuXMLContentHandler extends DefaultHandler {
		
		private static final String ANTUTUSTRING = "Antutu_locations";
		private static final String enterTestBtnSTRING = "enterTestBtnInitial";
		private static final String LEFTTESTBUTTIONTSTRING = "startTestBtn";
		private static final String STARTTESTBUTTONINITEDSTRING = "TestAgainButton";
		private static final String STATRTESTBUTTONSECONDSTRING = "startTestButtonAfterInitial";
		
//		private String tempString;
		
		@Override
		public void startDocument() throws SAXException {
			Log.d(TAG, "======startDocument======");

		}
		
		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {

			Log.d(TAG, "startElement localName:" + localName);
			
			if(ANTUTUSTRING.equals(localName)){
				return;
				
			}

			String x = attributes.getValue("x");
			String y = attributes.getValue("y");
			
			float pointx = Float.valueOf(x);
			float pointy = Float.valueOf(y);
			
			if (enterTestBtnSTRING.equals(localName)) {
				enterTestBtn.x = pointx;
				enterTestBtn.y = pointy;

			}else if(LEFTTESTBUTTIONTSTRING.equals(localName)){
				startTestBtn.x = pointx;
				startTestBtn.y = pointy;
				
			}else if(STARTTESTBUTTONINITEDSTRING.equals(localName)){
				testAgianButton.x = pointx;
				testAgianButton.y = pointy;
				
			}else if(STATRTESTBUTTONSECONDSTRING.equals(localName)){
				startTestButtonAfterInitial.x = pointx;
				startTestButtonAfterInitial.y = pointy;
			}


//			tempString = localName;

		}
		
		
		@Override
		public void endDocument() throws SAXException {

			super.endDocument();
			Log.d(TAG, "====== endDocument()======");
		}
	}

	

}


