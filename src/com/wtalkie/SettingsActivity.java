package com.wtalkie;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	WifiManager wifi;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.settings);
	    // TODO Auto-generated method stub
	}

}
