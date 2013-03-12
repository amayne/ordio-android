package com.ordio.player.backend;

import com.ordio.player.backend.PlaybackHandler.PlaybackEvent;
import com.ordio.player.database.DatabaseHelper;
//import com.ordio.backend.IPlaybackService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class PlaybackService extends Service {
	public enum PlaybackState {
		none,
		play,
		pause,
		stop,
		loop,
		end,
		volume,
		speed
	};
	
	public static DatabaseHelper databaseHelper = null;
	private PlaybackHandler playback_handler = null;
	
	
	private final IPlaybackService.Stub myPlaybackServiceStub = new IPlaybackService.Stub() {
		@Override
		public void PlayPlayback() throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.playSong, null);
		}
		
		@Override
		public void PausePlayback() throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.pauseSong, null);
		}
		
		@Override
		public void LoopPlayback(int ms, boolean continuousPlay) throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.loopSong, ms, continuousPlay);
		}

		@Override
		public void StopPlayback() throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.stopSong, null);
		}

		@Override
		public void PlaySong(String filename) throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.startSong, filename);
		}
		
		@Override
		public void setEQ(int channel, double vol) throws RemoteException {
			MP3Wrapper.setEQ(channel, vol);
		}
		
		@Override
		public void resetEQ() throws RemoteException {
			MP3Wrapper.resetEQ();
		}
		
		public int getPositionInMs() throws RemoteException {
			return (int) Math.round(MP3Wrapper.tell()/(2f*44.1));
		}
		
		public void setPlaybackRate(int speed) throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.speed, speed);
		}
		
		public void setVolume(double leftVol, double rightVol) throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.volume, leftVol, rightVol);
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return myPlaybackServiceStub;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		 // Initialize our MPG123 native library (once per process)
		MP3Wrapper.initLib();
		
		playback_handler = new PlaybackHandler();
		databaseHelper = new DatabaseHelper(this);
		
		// Start our controlling thread
        final Thread playbackHandlerThread = new Thread(playback_handler);
        playbackHandlerThread.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Stop our controlling thread
		// TODO:
		
		databaseHelper.Finish();
		
		// Finalize MPG123 native library (once per process)
		MP3Wrapper.cleanupLib();
	}
}
