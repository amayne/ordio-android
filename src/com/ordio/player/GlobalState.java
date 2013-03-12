package com.ordio.player;

import java.util.List;

import com.ordio.player.backend.PlaybackService;
import com.ordio.player.backend.PlaybackService.PlaybackState;
import com.ordio.player.backend.IPlaybackService;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class GlobalState extends Application {
	private Intent serviceIntent = null;
	private ServiceConnection serviceConnection = null;
	private IPlaybackService myPlaybackService = null;
	private boolean serviceRunning = false;
	
	private PlaybackState state = PlaybackState.none;
	
	public synchronized boolean PlaySong(String filepath) throws RemoteException {
		if(filepath == null || filepath.equals("") || myPlaybackService == null)
			return false;
		myPlaybackService.StopPlayback();
		state = PlaybackState.play;
		myPlaybackService.PlaySong(filepath);
		return true;
	}

	public synchronized boolean PlayPlayback() throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		state = PlaybackState.play;
		myPlaybackService.PlayPlayback();
		return true;
	}
	
	public synchronized boolean PausePlayback() throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		state = PlaybackState.pause;
		myPlaybackService.PausePlayback();
		return true;
	}
	
	public synchronized boolean LoopPlayback(int ms, boolean continuousPlay) throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		state = PlaybackState.loop;
		myPlaybackService.LoopPlayback(ms, continuousPlay);
		return true;
	}
	
	public synchronized boolean StopPlayback() throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		state = PlaybackState.stop;
		myPlaybackService.StopPlayback();
		return true;
	}
	
	public synchronized boolean setEQ(int channel, double vol) throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		myPlaybackService.setEQ(channel, vol);
		return true;
	}
	
	public synchronized boolean setPlaybackRate(int speed) throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		myPlaybackService.setPlaybackRate(speed);
		return true;
	}
	
	public synchronized boolean resetEQ() throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		myPlaybackService.resetEQ();
		return true;
	}
	
	public synchronized boolean setVolume(double leftVol, double rightVol) throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		myPlaybackService.setVolume(leftVol, rightVol);
		return true;
	}
	
	public synchronized int getPositionInMs() throws RemoteException {
		if(myPlaybackService == null)
			return 0;
		
		return myPlaybackService.getPositionInMs();
	}
	
	public synchronized void newServiceConnection() {
		serviceIntent = new Intent(this, PlaybackService.class);
		 
		// Check if service is already running
		if(!serviceRunning)
		{
			// Service is not running, start and bind it
			startService(serviceIntent);
			
			serviceConnection = new ServiceConnection() {
	        	@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					myPlaybackService = IPlaybackService.Stub.asInterface((IBinder) service);
				}
	
				@Override
				public void onServiceDisconnected(ComponentName name) {
					myPlaybackService = null;
				}
			};
			
			bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
			serviceRunning = true;
		}
	}
	
	/**
	 * Indicates whether the specified service is already started. This 
	 * method queries the activity manager for launched services that can
	 * respond to an binding with an specific service name.
	 * If no existed service is found, this method returns null.
	 * 
	 * @param context The context of the activity
	 * @param className The service full name to check for availability.
	 * 
	 * @return ComponentName if the service is already existed, NULL otherwise.
	 */
	public static ComponentName isServiceExisted(Context context, String className)
	{
		ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		
		List<ActivityManager.RunningServiceInfo> serviceList = 
			activityManager.getRunningServices(Integer.MAX_VALUE);
		
		if(!(serviceList.size() > 0))
		{
			return null;
		}
		
		for(int i = 0; i < serviceList.size(); i++)
		{
			RunningServiceInfo serviceInfo = serviceList.get(i);
			ComponentName serviceName = serviceInfo.service;
			
			if(serviceName.getClassName().equals(className))
			{
				return serviceName;
			}
		}
		return null;
	}
}
