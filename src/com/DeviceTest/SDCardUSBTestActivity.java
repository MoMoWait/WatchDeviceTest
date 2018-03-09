package com.DeviceTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import com.DeviceTest.helper.ControlButtonUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SDCardUSBTestActivity extends Activity{
	
	private static final String TAG = "SdCardTestActivity"; 
    private static final int R_PASS = 1;
    private static final int R_FAIL = 2;
    public String SUCCESS;
    public String FAIL;
    private StorageManager mStorageManager = null;
    TextView mResult;
    private BroadcastReceiver mSDCardStateReceiver;
    
    private String filename;
    private File mFile;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().addFlags(1152);
        setContentView(R.layout.sdcardtest);
		if (mStorageManager == null) {
            mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		}
        this.mResult = (TextView) findViewById(R.id.sdresultText);
        this.mResult.setVisibility(View.VISIBLE);
        this.mResult.setGravity(17);

        ControlButtonUtil.initControlButtonView(this);
        findViewById(R.id.btn_Pass).setVisibility(View.INVISIBLE);
        mSDCardStateReceiver = new SDCardConnectedBroadcastReceiver();
        SUCCESS = getString(R.string.success);
        FAIL = getString(R.string.fail);
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
		registerReceiver(mSDCardStateReceiver, intentFilter);
        	
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSDCardStateReceiver);
    }
    
    protected void selectTest(){
    	File dir = new File("/mnt/usb_storage");
		File file[] = dir.listFiles();
		Log.v(TAG, file[file.length-1].getAbsolutePath());
		testSDCard(file[file.length-1].getAbsolutePath());	   
	 }
    
    public void testSDCard(String sdpath){
    	filename=sdpath+"/sdcard.flg";
    	FileOutputStream out;
    	mFile =new File(filename);
		try {
			if(!mFile.exists())
				mFile.createNewFile();
			out = new FileOutputStream(mFile);
			out.write("test sdcard".getBytes());
			Log.v(TAG, "write usb.flg file success");
			out.close();
		} catch (Exception e) {
			e.printStackTrace(); 
			mHandler.sendEmptyMessageDelayed(R_FAIL, 500);
			return;
		}
		
		try {
            BufferedReader br = new BufferedReader(new InputStreamReader
                    (new FileInputStream(filename)));
            String data = null;
            StringBuilder temp = new StringBuilder();
            while ((data = br.readLine()) != null) {
                temp.append(data);
            }
            br.close();
            mFile.delete();
            Log.e(TAG, "Readfile " + temp.toString());
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessageDelayed(R_FAIL, 500);
            return;
        }
        mResult.setText(getString(R.string.sdcard_usb_wr_ok));
        mHandler.sendEmptyMessageDelayed(R_PASS, 500);
    }
    
    class SDCardConnectedBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "intent.getAction()"+intent.getAction());
			 if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
				Log.v("SDCard"," SDCardConnectedBroadcastReceiver");
				mResult.setText(getString(R.string.resume_findSD));
				selectTest();
	            }else{
		            return;	
	            }
			
		}
	};
	
	Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case R_PASS:
                	((Button)findViewById(R.id.btn_Pass)).performClick();
                    break;
                case R_FAIL:
                	//((Button) findViewById(R.id.btn_Fail)).performClick();
                    break;
            }
        };
    };
    
}
