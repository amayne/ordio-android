package com.ordio.player.backend;

import android.util.Log;

class MonitorObject {
}

class WaitNotify
{
	private MonitorObject myMonitorObject = new MonitorObject();
	private boolean wasSignalled = false;
	private boolean isWaiting = false;
	private boolean isGoingWait = false;
	
	public void goWait() {
		isGoingWait = true;
	}
	
	public boolean isGoWait() {
		return isGoingWait;
	}
	
	public boolean isWaiting() {
		return isWaiting;
	}
	
	public void doWait() {
		synchronized(myMonitorObject) {
			while(!wasSignalled) 
			{
				try 
				{
					isWaiting = true;
					isGoingWait = false;
					myMonitorObject.wait();
		        } 
		        catch(InterruptedException e)
		        {
		        	Log.e("Fehler", e.toString());
		        }
			}
			// Clear signals and continue running.
			wasSignalled = false;
			isWaiting = false;
		}
	}

	public void doNotify(){
		synchronized(myMonitorObject) {
			wasSignalled = true;
			myMonitorObject.notify();
		}
	 }
}

