package com.wtalkie;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.net.wifi.*;


public class AccessPoint {

	String ssid = "Walkie-Talkie";
	private boolean apstatus;
	@SuppressWarnings("unused")
	private Method apMethod;
	private WifiConfiguration netConfig;
	/**
	 * Uruchomienie Access Pointa
	 * @param WifiManager Instance
	 */
	@SuppressWarnings("unused")
	public void createWifiAccessPoint(WifiManager wifiManager) {
		if(wifiManager.isWifiEnabled())
	    {
	        wifiManager.setWifiEnabled(false);          
	    }       
	    Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();   //Get all declared methods in WifiManager class     
	    boolean methodFound=false;
	    for(Method method: wmMethods){
	        if(method.getName().equals("setWifiApEnabled")){
	            methodFound=true;
	            apMethod = method;
	            netConfig = new WifiConfiguration();
	            netConfig.SSID = ""+ssid+"";
	            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
	            //netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
	            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
	            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
	            netConfig.preSharedKey="123123123";
	            //netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	            //netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
	            //netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	            //netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	            try {
	                apstatus=(Boolean) method.invoke(wifiManager, netConfig,true);          
	                for (Method isWifiApEnabledmethod: wmMethods)
	                {
	                    if(isWifiApEnabledmethod.getName().equals("isWifiApEnabled")){
	                        while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){
	                        };
	                        for(Method method1: wmMethods){
	                            if(method1.getName().equals("getWifiApState")){
	                                int apstate;
	                                apstate=(Integer)method1.invoke(wifiManager);
	                            }
	                        }
	                    }
	                }
	                if(apstatus)
	                {
	                    System.out.println("SUCCESS");  
	                }else
	                {
	                    System.out.println("FAILED");
	                }

	            } catch (IllegalArgumentException e) {
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                e.printStackTrace();
	            } catch (InvocationTargetException e) {
	                e.printStackTrace();
	            }
	        }      
	    }
	    if(!methodFound){
	        //statusView.setText("Your phone's API does not contain setWifiApEnabled method to configure an access point");
	    }
	}
	public boolean getApStatus() {
		return apstatus;
	}
	public void stopAp(WifiManager wifiManager) {
		Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
		for (Method setWifiApEnabledmethod: wmMethods) {
			if(setWifiApEnabledmethod.getName().equals("setWifiApEnabled")){
				try {
					setWifiApEnabledmethod.invoke(wifiManager, netConfig, false);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
