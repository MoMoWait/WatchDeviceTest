package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import android.hardware.Usb;
import android.util.Log;

public class MrioUSBTestActivity extends Activity {

	
	TextView mUsbPluginText1;
	TextView mUsbPluginText2;
//	TextView mUsbPluginText3;
//	TextView mUsbPluginText4;
	boolean pluginPass1 = false;
	boolean pluginPass2 = false;
//	boolean pluginPass3 = false;
//	boolean pluginPass4 = false;
	
	boolean stop = false;
	boolean testNum = false;
	private static final String TAG = "USBTestActivity";
	private static final int BACK_TIME = 1000;
	private static final int R_PASS = 1;
  	private static final int R_FAIL = 2;
  	private BroadcastReceiver mUsbStateReceiver;
 
  	private String usb_path1 = null;   
  	private String usb_path2 = null;
//  	private String usb_path3 = null;   
//  	private String usb_path4 = null;
	   // private StringBuilder sBuilder1;
	   // private StringBuilder sBuilder2;
	 
	private File mFile;
	private String SUCCESS;
	private String FAIL;
	   
	private boolean isfirstTest= false;
	private boolean issencondTest = false;
//	private boolean isthirdTest= false;
//	private boolean isfourthTest = false;
	private StorageManager mStorageManager = null;
	private String filename;
	private static boolean isFirst=true;
	
	private String[] list;
	private int dirNum,passNum = 0;
	private static final int TEST_BEGIN = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFirst = true;
		getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.mriousbtest);
		if (mStorageManager == null) {
           mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		}
		
		mUsbPluginText1 = (TextView) findViewById(R.id.usbplugin1);
		mUsbPluginText2= (TextView) findViewById(R.id.usbplugin2);
//		mUsbPluginText3 = (TextView) findViewById(R.id.usbplugin3);
//		mUsbPluginText4= (TextView) findViewById(R.id.usbplugin4);

       ControlButtonUtil.initControlButtonView(this);
       findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
 //       findViewById(R.id.btn_Fail).setVisibility(View.INVISIBLE);
     	mUsbStateReceiver = new UsbConnectedBroadcastReceiver();
    	mUsbPluginText1.setText(getString(R.string.usbplugin1));
    	mUsbPluginText2.setText(getString(R.string.usbplugin2));
//    	mUsbPluginText3.setText(getString(R.string.usbplugin3));
//    	mUsbPluginText4.setText(getString(R.string.usbplugin4));
	}
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
			intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
			intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
			intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
			intentFilter.addDataScheme("file");
			registerReceiver(mUsbStateReceiver, intentFilter);
	 
	       // mStorageManager.registerListener(mStorageListener);
	       // sBuilder1 = new StringBuilder();
	       // sBuilder2 = new StringBuilder();
			selectTest();
	        	
	    }
	 
//	 private Handler handler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			switch(msg.what){
//			case TEST_BEGIN:
//				selectTest();
//				break;
//			default:
//				break;
//			}
//		} 
//	 };
	 
	 protected void selectTest(){
//		   mStorageManager = StorageManager.from(this);
		    StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
		    Log.v(TAG, "storageVolumes = "+storageVolumes);
		    dirNum = 0;
		    passNum = 0;
		   File dir = new File("/mnt/usb_storage");
		   File file[] = dir.listFiles();
		   if((dirNum=file.length)!=0){
		   Log.v(TAG, file[0].getAbsolutePath());
		   for(int i=0;i<file.length;i++){
			   testUsb(file[passNum++].getAbsolutePath());
		   }
//		   for (int i = 0; i < file.length; i++) {
//				if (file[i].isDirectory()){
//					list[dirNum++]=file[i].getAbsolutePath();
//				}	
//		   }
//		   for (int j = 0;j < dirNum; j++){
//				testUsb(list[j]);
//				passNum++;
//		   }
		   }
//		    for (StorageVolume volume : storageVolumes) {      
//		    	if (!volume.isEmulated()) {	
//		    		String USBstate = mStorageManager.getVolumeState(volume.getPath()); 
//		    		if(Environment.MEDIA_MOUNTED.equals(USBstate)){
//		    		if(volume.getPath().equals("/mnt/usb_storage/USB_DISK0")){
//		    			usb_path1="/mnt/usb_storage/USB_DISK0"; isfirstTest=true;	
//		    			testUsb(usb_path1);
//		    		}  
//		    		if(volume.getPath().equals("/mnt/usb_storage/USB_DISK1")){
//		    			usb_path2="/mnt/usb_storage/USB_DISK1"; issencondTest=true;
//		    			testUsb(usb_path2);
//		    		} 
//		    		if(volume.getPath().equals("/mnt/usb_storage/USB_DISK2")){
//		    			usb_path3="/mnt/usb_storage/USB_DISK2"; isthirdTest=true;	
//		    			testUsb(usb_path3);
//		    		}  
//		    		if(volume.getPath().equals("/mnt/usb_storage/USB_DISK3")){
//		    			usb_path4="/mnt/usb_storage/USB_DISK3"; isfourthTest=true;
//		    			testUsb(usb_path4);
//		    	  }
//		    	}
//		    }
//		    }
		 
	 }

	    @Override
	    protected void onPause() {
	        super.onPause();
	       /// if (mStorageManager != null && mStorageListener != null) {
	       //     mStorageManager.unregisterListener(mStorageListener);
	       // }
	        unregisterReceiver(mUsbStateReceiver);
	    }
	    
	    public void  testUsb(String usbpath){
	    	filename=usbpath+"/usb.flg";
	    	FileOutputStream out;
	    	mFile =new File(filename);
			try {
				if(!mFile.exists())
					mFile.createNewFile();
				out = new FileOutputStream(mFile);
				out.write("test usb".getBytes());
				if(passNum==1)mUsbPluginText1.setText(getString(R.string.usbplugin1)+getString(R.string.usb_write));
				if(passNum==2)mUsbPluginText2.setText(getString(R.string.usbplugin2)+getString(R.string.usb_write));
//				if(passNum==3)mUsbPluginText3.setText(getString(R.string.usbplugin3)+getString(R.string.usb_write));
//				if(passNum==4)mUsbPluginText4.setText(getString(R.string.usbplugin4)+getString(R.string.usb_write));
				Log.v(TAG, "write usb.flg file success");
				out.close();
			} catch (FileNotFoundException e) {
			 set_err();
			 e.printStackTrace();	
			 return;
		 } catch (IOException e) {
			 set_err();
			 e.printStackTrace(); 	
			 return;
		 }
			
			
			try {
	            BufferedReader br = new BufferedReader(new InputStreamReader
	                    (new FileInputStream(filename)));
	            String data = null;
	            StringBuilder temp = new StringBuilder();
	            if(passNum==1) mUsbPluginText1.setText(getString(R.string.usbplugin1)+getString(R.string.usb_write)+";"+getString(R.string.usb_read));
				if(passNum==2) mUsbPluginText2.setText(getString(R.string.usbplugin2)+getString(R.string.usb_write)+";"+getString(R.string.usb_read));
//				if(passNum==3) mUsbPluginText3.setText(getString(R.string.usbplugin3)+getString(R.string.usb_write)+";"+getString(R.string.usb_read));
//				if(passNum==4) mUsbPluginText4.setText(getString(R.string.usbplugin4)+getString(R.string.usb_write)+";"+getString(R.string.usb_read));
	           
	            while ((data = br.readLine()) != null) {
	                temp.append(data);
	            }
	            br.close();
	            mFile.delete();
	            Log.e(TAG, "Readfile " + temp.toString());
	        } catch (Exception e) {
	        	set_err();
	            e.printStackTrace();
	            return;
	           
	        }
			    
		     if(passNum==1) {
		    	 mUsbPluginText1.setText(getString(R.string.usbplugin1)+getString(R.string.usb_write)+";"
		                 +getString(R.string.usb_read)+";"+getString(R.string.usb_test_success)); 
		    	 pluginPass1=true;
		     }
		     if(passNum==2) {
		    	 mUsbPluginText2.setText(getString(R.string.usbplugin2)+getString(R.string.usb_write)+";"
		    		     +getString(R.string.usb_read)+";"+getString(R.string.usb_test_success)); 
		    	 pluginPass2=true;
		    	 }
//		     if(passNum==3) {
//		    	 mUsbPluginText3.setText(getString(R.string.usbplugin3)+getString(R.string.usb_write)+";"
//		                 +getString(R.string.usb_read)+";"+getString(R.string.usb_test_success)); 
//		    	 pluginPass3=true;isthirdTest=false;
//		     }
//		     if(passNum==4) {
//		    	 mUsbPluginText4.setText(getString(R.string.usbplugin4)+getString(R.string.usb_write)+";"
//		    		     +getString(R.string.usb_read)+";"+getString(R.string.usb_test_success)); 
//		    	 pluginPass4=true; isfourthTest=false;
//		    	 }
		     if(pluginPass1&&pluginPass2) {
		    	 ((Button)findViewById(R.id.btn_Retest)).setClickable(false);
					//((Button)findViewById(R.id.btn_Pass)).setClickable(false);
				 ((Button)findViewById(R.id.btn_Fail)).setClickable(false);
		    	 mHandler.sendEmptyMessageDelayed(R_PASS, 1000);
		     }
	    }

	    public void set_err() {
	    	if(passNum==1)pluginPass1=false;
			if(passNum==2)pluginPass2=false;
//			if(passNum==3)pluginPass3=false;
//			if(passNum==4)pluginPass4=false;
        	if(passNum==1)mUsbPluginText1.setText(getString(R.string.usbplugin1)+getString(R.string.usb_test_err));
			if(passNum==2)mUsbPluginText2.setText(getString(R.string.usbplugin2)+getString(R.string.usb_test_err));
//			if(passNum==3)mUsbPluginText3.setText(getString(R.string.usbplugin3)+getString(R.string.usb_test_err));
//			if(passNum==4)mUsbPluginText4.setText(getString(R.string.usbplugin4)+getString(R.string.usb_test_err));
            ((Button)findViewById(R.id.btn_Retest)).setClickable(false);
		    ((Button)findViewById(R.id.btn_Pass)).setClickable(false);
		//	((Button)findViewById(R.id.btn_Fail)).setClickable(false);
			mHandler.sendEmptyMessageDelayed(R_FAIL, 2000);

		}
   
	    class UsbConnectedBroadcastReceiver extends BroadcastReceiver {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "intent.getAction()"+intent.getAction());
				 if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
					Log.v("USB","198 UsbConnectedBroadcastReceiver");
					 selectTest();
					 
		            }else{
			            return;	
		            }
				
			}
		};
	   
	     /*
	    StorageEventListener mStorageListener = new StorageEventListener() {
	        @Override
	        public void onStorageStateChanged(String path, String oldState, String newState) {
	        	if (newState.equals(Intent.ACTION_MEDIA_MOUNTED)) {
	        			Log.v("limiUSB","213 mStorageListener");
	        		selectTest();
	        	}
	        }
	    };
	    */
	   // static boolean isFirst=true;
	    public void TestResult(int result) {
	    	
	        if (result == R_PASS) {
	        	 if(pluginPass1&&pluginPass2&&isFirst){
	        	 	isFirst=false;
	        	  ((Button)findViewById(R.id.btn_Pass)).performClick();
	        	}
	        } else if (result == R_FAIL&&isFirst) {
	        	 	isFirst=false;
	            ((Button) findViewById(R.id.btn_Fail)).performClick();
	        }
	    }

	    Handler mHandler = new Handler() {
	        public void handleMessage(android.os.Message msg) {
	            switch (msg.what) {
	                case R_PASS:
	                	TestResult(R_PASS);
	                    break;
	                case R_FAIL:
	                    TestResult(R_FAIL);
	                    break;
	            }
	        };
	    };
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}
}
