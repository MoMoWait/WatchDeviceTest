package com.DeviceTest;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import com.DeviceTest.helper.ControlButtonUtil;
import com.DeviceTest.view.LcdTestView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
/*
 * author @ mw
 */

public class EthernetTestActivity extends Activity {

	private String TAG="网络状态监测";
	private ConnectivityManager cm;
	
	/*IP*/
	private String localIpAddress;
	private State ethernetState; 
    private TextView mResult;
    
    private final static int DISPLAY = 1;
    private final static int SUCCESS = 2;
    private final static int FAIL = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(FLAG_FULLSCREEN | FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ethernettest);
        mResult = (TextView) findViewById(R.id.result);
        ControlButtonUtil.initControlButtonView(this);
        ((Button) findViewById(R.id.btn_Pass)).setVisibility(View.INVISIBLE);
        
        //close wifi
        WifiManager wifimanager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifimanager.setWifiEnabled(false);
        
		cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        mHandler.sendEmptyMessageDelayed(DISPLAY, 1500);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DISPLAY:
                	//String ip = getLocalIpAddress();
                	boolean conn = isethernetConnected();
                	if(conn){
                		mResult.setText(R.string.EthernetTestsuccess);
                    	sendEmptyMessageDelayed(SUCCESS, 1500);
                	} else {
                		mResult.setText(R.string.EthernetTestFail);
                		sendEmptyMessageDelayed(FAIL, 1500);
                	}
                    break;
                case SUCCESS:
                	this.removeMessages(DISPLAY);
                	((Button) findViewById(R.id.btn_Pass)).setVisibility(View.INVISIBLE);
                	((Button) findViewById(R.id.btn_Pass)).performClick();
                    break;
                case FAIL:
                	((Button) findViewById(R.id.btn_Fail)).performClick();
                	break;
                default:
                    break;
            }
        }
    };
    
    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(DISPLAY);
        mHandler.removeMessages(SUCCESS);
    }
	
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						localIpAddress = inetAddress.getHostAddress()
								.toString();
						return localIpAddress;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}
	
	@SuppressLint("InlinedApi")
	public boolean isethernetConnected() {  
        Log.e(TAG,"以太网连接");
	    boolean result = false; 
	    NetworkInfo ni=cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        ethernetState = ni.getState();  
        if(ethernetState == State.CONNECTED){
            result=true;
        }  
        return result;  
    }

    public boolean checkNetworkState() {
		return (isethernetConnected());
	}
    
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
    
}
