package com.DeviceTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private SharedPreferences mSharedPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		mSharedPreferences = context.getSharedPreferences("devicetest", 0);
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.d("DeviceTest", "BootReceiver, set 3g_tx_power_init to 1");
			SharedPreferences.Editor editor = mSharedPreferences.edit();
			editor.putInt("3g_tx_power_init", 1);
			editor.commit();
		}
	}
}
