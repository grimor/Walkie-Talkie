package com.wtalkie;

import java.util.ArrayList;
import java.util.EventListener;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;


public class WTalkieActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    
	private static final String TAG = "wifi";
	Button buttonSettings;
	ToggleButton buttonStartStop;
	ListView wifiList;
	public WifiManager wifi;
	private BroadcastReceiver receiver;
	ArrayList<String> wifiItemList = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	WifiScanner wifiScanner;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonStartStop = (ToggleButton) findViewById(R.id.startstopScanButton);
        buttonStartStop.setOnClickListener(this);
        buttonSettings = (Button) findViewById(R.id.settingsButton);
        buttonSettings.setOnClickListener(this);
        wifiList = (ListView) findViewById(R.id.wifiList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wifiItemList);
        wifiList.setAdapter(adapter);
        wifiList.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Toast.makeText(getApplicationContext(), "You clicked " + wifiItemList.get(position) ,Toast.LENGTH_LONG).show();
        	}
		});
        
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (receiver == null)
        	receiver = new WifiScanner(this);
        
    }
	
	@Override
    public void onStop()
    {
		unregisterReceiver(receiver);
    	super.onStop(); 
    }
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId() == R.id.settingsButton)
		{
			printResults("Item");
		}
		if(view.getId() == R.id.startstopScanButton)
		{
			if(buttonStartStop.isChecked()) {
				Log.d(TAG, "onClick() wifi.startscan()");
				registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				wifi.startScan();
			} else {
				onStop();
			}
		}
	}
	
	
	public void printResults(String object) {
		wifiItemList.add(object);
		adapter.notifyDataSetChanged();
	}
	public void clearList (ArrayList<String> list){
		list.clear();
		adapter.notifyDataSetChanged();
	}
}