package com.wtalkie;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ListView;

public class WifiScanner extends BroadcastReceiver{

	private static final String TAG = "WIFiScanReceiver";
    WTalkieActivity wTalkie;
    
	public WifiScanner(WTalkieActivity wTalkie)
	{
		super();
		this.wTalkie = wTalkie;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
	
		List<ScanResult> results = wTalkie.wifi.getScanResults();
		wTalkie.clearList(wTalkie.wifiItemList);
		for(ScanResult result : results)
		{
			wTalkie.printResults(result.SSID);
			Log.d(TAG, "Result: " + result.toString());
		}
		//wTalkie.onStop();
		Log.d(TAG, "onReceive()");
	}
	
}
