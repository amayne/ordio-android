package com.ordio.player.backend;

import com.ordio.player.backend.PlaybackService.PlaybackState;


class PlaybackHandler implements Runnable {
	public enum PlaybackEvent {
		none,
		playSong,
		pauseSong,
		loopSong,
		stopSong,
		startSong,
		endSong,
		songStopped,
		volume,
		speed
	};
	
	private PlaybackEvent event = PlaybackEvent.none;
	private String event_parameter = null;
	private int loopInMs = 0;
	private boolean continuousPlay = false;
	private double leftVol = 1;
	private double rightVol = 1;
	private int playbackSpeed = 1;
	
	private WaitNotify signalHandler = new WaitNotify();
	private WaitNotify signalEvent = new WaitNotify();
	private WaitNotify signalThread = new WaitNotify();
	
	private Playback play = null;
	private PlaybackState playback = PlaybackState.stop;
	
	
	private void StartSong(String filename)
	{
		// End possibly existing old song
		EndSong();
		
		// Create new song
		play = new Playback(this, filename, signalThread);
		
		// Create new song thread
        final Thread playThread = new Thread(play);
        playThread.start();
        
        // Wait for thread to finish initializing
        signalThread.doWait();
        
        // Start it
        play.Control(PlaybackState.play);
        setPlaybackState(PlaybackState.play);
	}
	
	private void EndSong()
	{
		if((getPlaybackState() != PlaybackState.none) && (play != null))
		{
			// Make thread finish
			play.Control(PlaybackState.end);
			
			// Wait for thread to finish
			signalThread.doWait();
			setPlaybackState(PlaybackState.none);
		}
	}
	
	public synchronized void setPlaybackState(PlaybackState state) {
		playback = state;
	}
	
	public PlaybackState getPlaybackState() {
		return playback;
	}
	
	/**
	 * 
	 * @param input_event
	 */
	public synchronized void SetEvent(PlaybackEvent input_event, String filename) {
		// Wait if playback handler is working
		if(!signalHandler.isWaiting())
			signalEvent.doWait();
		
		// Playback handler is ready to work
		event = input_event;
		event_parameter = filename;
		signalHandler.doNotify();
	}
	
	public synchronized void SetEvent(PlaybackEvent input_event, int playbackSpeed) {
		// Wait if playback handler is working
		if(!signalHandler.isWaiting())
			signalEvent.doWait();
		
		// Playback handler is ready to work
		event = input_event;
		this.playbackSpeed = playbackSpeed;
		//event_parameter = filename;
		signalHandler.doNotify();
	}
	
	public synchronized void SetEvent(PlaybackEvent input_event, int loopInMs, boolean continuousPlay) {
		// Wait if playback handler is working
		if(!signalHandler.isWaiting())
			signalEvent.doWait();
		
		// Playback handler is ready to work
		event = input_event;
		this.loopInMs = loopInMs;
		this.continuousPlay = continuousPlay;
		//event_parameter = filename;
		signalHandler.doNotify();
	}
	
	public synchronized void SetEvent(PlaybackEvent input_event, double leftVol, double rightVol) {
		// Wait if playback handler is working
		if(!signalHandler.isWaiting())
			signalEvent.doWait();
		
		// Playback handler is ready to work
		event = input_event;
		this.leftVol = leftVol;
		this.rightVol = rightVol;
		//event_parameter = filename;
		signalHandler.doNotify();
	}
	
	@Override
	public void run() {
		// We're important
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO); 
		
		do
		{
			// Wait for new work
			signalHandler.doWait();
			
			switch(event)
			{
				case playSong:
					if((getPlaybackState() != PlaybackState.play) && (play != null))
					{
						play.Control(PlaybackState.play);
						setPlaybackState(PlaybackState.play);
					}
				break;
				
				case pauseSong:
					if((getPlaybackState() != PlaybackState.pause) && (play != null))
					{
						play.Control(PlaybackState.pause);
						setPlaybackState(PlaybackState.pause);
					}
				break;
				
				case loopSong:
					if((getPlaybackState() != PlaybackState.pause) && (play != null))
					{
						play.Control(PlaybackState.loop, loopInMs, continuousPlay);
						setPlaybackState(PlaybackState.loop);
					}
				break;
					
				case stopSong:
					if((getPlaybackState() != PlaybackState.stop) && (play != null))
					{
						play.Control(PlaybackState.stop);
						setPlaybackState(PlaybackState.stop);
					}
				break;
					
				case startSong:
					StartSong(event_parameter);
				break;
				
				case endSong:
					EndSong();
				break;
				
				case songStopped:
					setPlaybackState(PlaybackState.none);
					play = null;
					PlaybackService.databaseHelper.markSongCurrPlayed(false, event_parameter);
				break;
				
				case volume:
					if((getPlaybackState() != PlaybackState.pause) && (play != null))
					{
						play.Control(PlaybackState.volume, leftVol, rightVol);
						setPlaybackState(PlaybackState.volume);
					}
				break;
				case speed:
					if((getPlaybackState() != PlaybackState.pause) && (play != null))
					{
						play.Control(PlaybackState.speed, playbackSpeed);
						setPlaybackState(PlaybackState.speed);
					}
				break;
			}
			
			signalEvent.doNotify();
		}while(true);
	}
}
