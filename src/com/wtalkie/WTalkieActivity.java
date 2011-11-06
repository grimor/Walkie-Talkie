package com.wtalkie;

import java.util.ArrayList;
import java.util.EventListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.AlteredCharSequence;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;


public class WTalkieActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    
	private static final String TAG = "wifi";
	Button buttonSettings;
	Button buttonStartAp;
	ToggleButton buttonStartStop;
	ListView wifiList;
	public WifiManager wifi;
	private BroadcastReceiver receiver;
	ArrayList<String> wifiItemList = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	WifiScanner wifiScanner;
	private boolean launch_control = false; //uzywane przy wyszukiwaniu sieci
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonStartStop = (ToggleButton) findViewById(R.id.startstopScanButton);
        buttonStartStop.setOnClickListener(this);
        buttonSettings = (Button) findViewById(R.id.settingsButton);
        buttonSettings.setOnClickListener(this);
        buttonStartAp = (Button) findViewById(R.id.startApButton);
        buttonStartAp.setOnClickListener(this);
        wifiList = (ListView) findViewById(R.id.wifiList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wifiItemList);
        wifiList.setAdapter(adapter);
        wifiList.setOnItemClickListener(new OnItemClickListener() {
        	//@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//Toast.makeText(getApplicationContext(), "You clicked " + wifiItemList.get(position) ,Toast.LENGTH_LONG).show();
        		//sprawdzenie czy haslo do sieci jest w zapamietane czy nie
        		
        		dialogHasloDoSieci(wifiItemList.get(position).toString());
        		
        	}
		});
        
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (receiver == null)
        	receiver = new WifiScanner(this);
        
        isWifiEnable();
    }
	
	@Override
    public void onStop()
    {
		//aby nie bylo bledow przy zamykaniu aplikacji 
		if(launch_control)
		{
			launch_control = false;
			unregisterReceiver(receiver); 
		}
    	super.onStop(); 
    }
	
	//@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId() == R.id.startApButton)
		{
			Intent myIntent = new Intent(view.getContext(), SettingsActivity.class);
	        startActivityForResult(myIntent, 0);
		}
		if(view.getId() == R.id.settingsButton)
		{
			printResults("Item");
		}
		if(view.getId() == R.id.startstopScanButton)
		{
			isWifiEnable(); //sprawdzanie czy modul sieci wifi jest wlaczony
			if(buttonStartStop.isChecked()) {
				Log.d(TAG, "onClick() wifi.startscan()");
				registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				launch_control = true; //potrzebny przy rejestowania / wyrejestrowania
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
	//laczenie z siecia
	public void connectNewtwork (String ssid, String pass)
	{
		Log.d(TAG, "laczenie z siecia");
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\""+ssid+"\"";
		config.preSharedKey = "\""+pass+"\"";
		
		int id = wifi.addNetwork(config);
		wifi.enableNetwork(id, true);
		
	}
	
	public void dialogHasloDoSieci (final String ssid) 
	{
		Log.d(TAG, "klikniecie w siec");
		final EditText input = new EditText(this);
		input.setText("123123123");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(input);
		builder.setMessage("Wpisz has³o do sieci" +  ssid)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   connectNewtwork(ssid,input.getText().toString());
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		
		alert.show();
	}
	
	
	private void isWifiEnable ()
	{
		Log.d(TAG, "sprawdzanie czy modul sieci wlaczony");
		
		if(!wifi.isWifiEnabled())
		{
			Log.d(TAG, "modul wifi wylaczony");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Modu³ WiFi jest wy³¹czony\nW³¹czyæ ?")
					.setCancelable(false)
					.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							wifi.setWifiEnabled(true);
							
						}
					})
					.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							
							//komunikat o uniemozliwieniu dzialania bez wifi
							
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
		}

		
	}
	//przycisk ZAMKNIJ aplikacje
	private void wifiOff ()
	{
		//pytanie czy wylaczyc wifi
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Modu³ WiFi jest w³¹czony!\nWy³¹czyæ ?");
		builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				wifi.setWifiEnabled(false);
				
			}
		});
		builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				//wychodzenie z palikacji, pozostawianie wifi wlaczonego
				
			}
		});
	}

}