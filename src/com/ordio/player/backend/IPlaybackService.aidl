package com.ordio.player.backend;

interface IPlaybackService {
	   void PlayPlayback();
	   void PausePlayback();
	   void StopPlayback();
	   void LoopPlayback(int ms, boolean continuousPlay);
	   void PlaySong(String filename);
	   void setEQ(int channel, double vol);
	   void setVolume(double left, double right);
	   void setPlaybackRate(int speed);
	   int getPositionInMs();
	   void resetEQ();
}