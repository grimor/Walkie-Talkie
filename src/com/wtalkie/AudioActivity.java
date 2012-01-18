package com.wtalkie;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;

public class AudioActivity extends Thread {
	public AudioActivity(String action)
	{
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		_action=action;
		start();
	}
	private String _action;
	private AudioRecord recorder = null;
	private byte [] receiveData;
	private DatagramSocket clientSocket;
	private int maxSize;
	private byte[] buffer;

	
	public void run() { 
		if(_action.equals("send"))
		{
			record();
		}
		if(_action.equals("receive"))
		{
			received_data();
		}
	}
	
	public void record() 
	{
		try
		{
			maxSize = AudioRecord.getMinBufferSize(8000, 
					AudioFormat.CHANNEL_CONFIGURATION_MONO, 
					AudioFormat.ENCODING_PCM_16BIT);
			maxSize=10240;
			buffer = new byte[maxSize]; 
			recorder = new AudioRecord(AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, maxSize);
		
			recorder.startRecording(); 
			while(TalkActivity.recording)  
			{
				recorder.read(buffer, 0, maxSize);
				send_data_to_broadcast();
			}
			recorder.stop();
			recorder.release();
		}
		catch(Exception e)
		{
		}
	}
	
	private void send_data_to_broadcast()
	{
		 try
		 {      	 
	            DatagramSocket s = new DatagramSocket();
	            s.setBroadcast(true);
	            TalkActivity.broadcast = InetAddress.getByName("192.168.43.255");
	            DatagramPacket p = new DatagramPacket(buffer, buffer.length,TalkActivity.broadcast,TalkActivity._port);
	            s.send(p);
		}
		catch(Exception e)
		{		 
		}
	}	
	
	private void received_data() 
	{
		try
		{
			while(true)
			{
				receiveData = new byte[10240];
				clientSocket = new DatagramSocket(TalkActivity._port);
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				playMp3(receiveData);
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	private void playMp3(byte[] mp3SoundByteArray) {
	    try {
	        // create temp file that will hold byte array
	        File tempMp3 = File.createTempFile("temp", ".mp3",Environment.getExternalStorageDirectory().getAbsoluteFile());
	        //tempMp3.deleteOnExit();
	        FileOutputStream fos = new FileOutputStream(tempMp3);
	        fos.write(mp3SoundByteArray);
	        fos.close();
	        // Tried reusing instance of media player
	        // but that resulted in system crashes...  
	        MediaPlayer mediaPlayer = new MediaPlayer();
	        // Tried passing path directly, but kept getting 
	        // "Prepare failed.: status=0x1"
	        // so using file descriptor instead
	        FileInputStream fis = new FileInputStream(tempMp3);
	        mediaPlayer.setDataSource(fis.getFD());
	        mediaPlayer.prepare();
	        mediaPlayer.start();
	        while(mediaPlayer.isPlaying())
	        {
	        	
	        }
	        mediaPlayer.stop();
	    } 
	    catch (IOException ex) 
	    {

	    }
	}
}




