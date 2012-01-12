package com.wtalkie;

import java.io.IOException;
import java.net.InetAddress;
import android.app.Activity;
import android.net.DhcpInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class TalkActivity extends Activity {
	
//	static View view;
	 TextView connectedTo;
	 TextView connectedType;
	 Button pushToTalk;
	 static InetAddress broadcast;
	 static int _port = 12345;
	 static boolean recording;
 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.talk);
	    pushToTalk = (Button) findViewById(R.id.pushToTalkButton);	   
	    connectedTo = (TextView) findViewById(R.id.connectedTo);
	    connectedType = (TextView) findViewById(R.id.connectionType);
	    recording = false;
	    try 
	    {
	    	broadcast = getBroadcastAddress();
	    	connectedTo.setText(connectedTo.getText() + " " + WTalkieActivity.wifi.getDhcpInfo().dns1);
		} 
	    catch (IOException e) {
			// TODO Auto-generated catch block
	    	Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
		}
	    
	    pushToTalk.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				String action = "";
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					recording = true;
					action = "down";
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					recording = false;		
					action="up";
				}
				button_action(action);
				return false;
			}
		});

	    // TODO Auto-generated method stub
	}
	
	public void button_action(String s)
	{
		if(s.equals("down"))
		{
			//Toast.makeText(this,"wcisniety",Toast.LENGTH_SHORT).show();
			AudioActivity send = new AudioActivity("send");
		}
		if(s.equals("up"))
		{
			//Toast.makeText(this,"puszczony",Toast.LENGTH_SHORT).show();	
			AudioActivity receive = new AudioActivity("receive");
		}
	}
			
	public InetAddress getBroadcastAddress() throws IOException {
	    DhcpInfo dhcp = WTalkieActivity.wifi.getDhcpInfo();
	    // handle null somehow
	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);    
	}
	
}














