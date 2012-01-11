package com.wtalkie;

import java.beans.IndexedPropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.InvalidParameterException;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Address;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.DhcpInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TalkActivity extends Activity {
	
//	static View view;
	 TextView connectedTo;
	 TextView connectedType;
	 Button pushToTalk;
	 InetAddress broadcast;
	 boolean recording;
	 String mFileName = null;
	 MediaRecorder mRecorder = new MediaRecorder();
	 MediaPlayer  mPlayer = new MediaPlayer();
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
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					recording = true;
					button_action("down");
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					recording = false;
					button_action("up");
				}
				return false;
			}
		});


	    // TODO Auto-generated method stub
	}
	
	public void button_action(String s)
	{
		if(s.equals("down"))
		{
			Toast.makeText(this,"wcisniety",Toast.LENGTH_SHORT).show();
			startRecording();
			//nowy watek - nagrywania i wysylanie tak d³ugo jak recording = true;
			
			
		}
		if(s.equals("up"))
		{
			Toast.makeText(this,"puszczony",Toast.LENGTH_SHORT).show();
			stopRecording();
			startPlaying();
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
	
	public void send_data_to_broadcast()
	{
		 try{
	            String messageStr="Hello Android!";
	            int _port = 12345;
	            DatagramSocket s = new DatagramSocket();
	            s.setBroadcast(true);
	            int msg_length=messageStr.length();
	            byte[] message = messageStr.getBytes();
	            DatagramPacket p = new DatagramPacket(message, msg_length,broadcast,_port);
	            s.send(p);  
			 }
			 catch(Exception e)
			 {

			 }
	}
	

		    private void startRecording() {
		   	 mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		     mFileName += "/audiorecordtest.3gp";
		     mRecorder = new MediaRecorder();
		     mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		     mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		     mRecorder.setOutputFile(mFileName);
		     mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		     try 
		     {
		            mRecorder.prepare();
		     } 
		     catch (IOException e) 
		     {  
		     }
		        mRecorder.start();
		    }

		    private void stopRecording() {
		        mRecorder.stop();
		        mRecorder.release();
		        mRecorder = null;
		    }
		    
		    private void startPlaying() {
		        mPlayer = new MediaPlayer();
		        try {
		            mPlayer.setDataSource(mFileName);
		            mPlayer.prepare();
		            mPlayer.start();
		        } catch (IOException e) {
		            
		        }
		    }


	    
	    
}
