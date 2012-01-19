package com.wtalkie;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.util.Log;

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
	
		List<ScanResult> results = WTalkieActivity.wifi.getScanResults();
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
