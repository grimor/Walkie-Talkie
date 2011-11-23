package com.wtalkie;

import java.util.ArrayList;
import java.util.EventListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.text.method.PasswordTransformationMethod;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
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
	AccessPoint ap;
	private boolean launch_control = false; //uzywane przy wyszukiwaniu sieci
	private static Context context;
	private ProgressDialog progDialog; //dialog "prosze czekac..."
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        WTalkieActivity.context = getApplicationContext();
        
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
	public void onDestroy() {
		if(launch_control)
		{
			launch_control = false;
			unregisterReceiver(receiver); 
		}
		ap.stopAp(wifi);
		super.onDestroy();
	}
	
	@Override
    public void onStop()
    {
    	super.onStop(); 
    }
	
	//@Override
	public void onClick(final View view) {
		// TODO Auto-generated method stub
		if(view.getId() == R.id.startApButton)
		{
			progDialog = ProgressDialog.show(this, "Access Point", "Proszę czekać...");
			new Thread () {
				public void run() {
					ap = new AccessPoint();
					ap.createWifiAccessPoint(wifi);
					if (ap.getApStatus() == true) {
						Intent myIntent = new Intent(view.getContext(), TalkActivity.class);
						progDialog.dismiss();
				        startActivityForResult(myIntent, 0);
					} else {
						Toast.makeText(view.getContext(), "Cannot run Access Point", 2000);
					}
				}}.start();
			
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
	
	public static Context getAppContext() {
		return WTalkieActivity.context;
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
	public void connectNetwork (String ssid, String pass)
	{
		Log.d(TAG, "laczenie z siecia");
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\""+ssid+"\"";
		if(pass != "")
			config.preSharedKey = "\""+pass+"\"";
		
		int id = wifi.addNetwork(config);
		wifi.enableNetwork(id, true);
		
	}
	public void dialogHasloDoSieci (final String ssid) 
	{
		/*
		Log.d(TAG, "klikniecie w siec");
		final EditText input = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Wpisz hasło do sieci " +  ssid)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   connectNetwork(ssid,input.getText().toString());
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		
		alert.show();
		*/
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_pass);
		dialog.setTitle("Wpisz hasło do sieci " + ssid);
		final EditText input = (EditText) dialog.findViewById(R.id.pass);
		final CheckBox checkb = (CheckBox) dialog.findViewById(R.id.check_pass);
		checkb.setChecked(true);
		
		checkb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				if(checkb.isChecked())
					input.setTransformationMethod(new PasswordTransformationMethod());
				else
					input.setTransformationMethod(null);
			}
		});
		
		Button button_ok = (Button) dialog.findViewById(R.id.yesButton);
		button_ok.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				connectNetwork(ssid,input.getText().toString());
				dialog.cancel();
			}
		});
		Button button_no = (Button) dialog.findViewById(R.id.noButton);
		button_no.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dialog.cancel();
				
			}
		});
		
		dialog.show();
	}
	
	
	private void isWifiEnable ()
	{
		Log.d(TAG, "sprawdzanie czy modul sieci wlaczony");
		if(!wifi.isWifiEnabled())
		{
			Log.d(TAG, "modul wifi wylaczony");
			final ProgressDialog progressdialog = new ProgressDialog(this);
			progressdialog.setMessage("Proszę czekać...");
			progressdialog.setTitle("WiFi");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Moduł WiFi jest wyłączony.\nWłączyć ?")
					.setCancelable(false)
					.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							wifi.setWifiEnabled(true);
							progressdialog.show();
							new Thread() {
								public void run() {
									try{
										while (!wifi.isWifiEnabled()) {sleep(100);} 
										progressdialog.dismiss();
									} catch (Exception e) {} 
									
								}
							}.start();
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
	private void wifiOff()
	{
		//pytanie czy wylaczyc wifi
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Modu¸ WiFi jest w¸�czony!\nWy¸�czyŤ ?");
		builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				wifi.setWifiEnabled(false);
			}
		});
		builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				//wychodzenie z aplikacji, pozostawianie wifi wlaczonego
				
			}
		});
	}

}