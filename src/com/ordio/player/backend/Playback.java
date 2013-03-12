package com.ordio.player.backend;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.ordio.player.backend.PlaybackHandler.PlaybackEvent;
import com.ordio.player.backend.PlaybackService.PlaybackState;

class Playback implements Runnable {
	private WaitNotify msignalControl = new WaitNotify();
	private WaitNotify msignalThread = null;
	private PlaybackHandler mplaybackHandler = null;

	private AudioTrack maudioTrack = null;
	private AudioFileInformations maudioInfo = null;
	private boolean mtrackPaused = true;
	private boolean mtrackEnded = false;
	private boolean mlooping = false;

	private String mfilename = null;
	private short[] mbuffer = null;

	private final static int MPG123_DONE = -12;
	private final static int MPG123_NEW_FORMAT = -11;
	private final static int MPG123_OK = 0;

	private int merr = 0;
	private int minBufferSize;
	private long mloopStart = 0;
	private long mloopEnd = 0;
	private long mloopInMs = 0;
	private boolean msmoothLooping = true;
	private long msamplesPlayedWhileLooping = 0;
	private boolean mcontinuousPlay = false;
	private int mplaybackSpeed = 1;

	public Playback(PlaybackHandler playbackHandler, String inputFilename,
			WaitNotify signal_thread) {
		mplaybackHandler = playbackHandler;
		mfilename = inputFilename;
		msignalThread = signal_thread;
	}

	@Override
	public void run() {
		int currentPlaybackSpeed = 1;
		long currentLoopInMs = 0;
		// We're important
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		merr = MP3Wrapper.initMP3(mfilename);
		if (merr != MPG123_OK) {
			// TODO: Show error
		}

		maudioInfo = MP3Wrapper.getAudioInformations();
		if (!maudioInfo.success) {
			// TODO: Show error
		} else {
			minBufferSize = AudioTrack.getMinBufferSize((int) maudioInfo.rate,
					AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_16BIT);
			maudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					(int) maudioInfo.rate,
					AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2,
					AudioTrack.MODE_STREAM);
			System.out.println(mfilename);
			mbuffer = new short[minBufferSize * 2];
			short[] tempBuffer = new short[minBufferSize * 4];
			boolean first = true;
			msignalThread.doNotify();
			do {
				if (mtrackPaused && !first)
					msignalControl.doWait();
				if (mtrackEnded)
					break;
				if (!mlooping)
					merr = MP3Wrapper.decodeMP3(minBufferSize * 2, mbuffer);
				if (merr == MPG123_OK || merr == MPG123_NEW_FORMAT) {
					first = false;
					if(!msmoothLooping && currentLoopInMs != mloopInMs) {
						MP3Wrapper.seekTo((int) mloopStart);
						currentLoopInMs = mloopInMs;
					}
					if(currentPlaybackSpeed != mplaybackSpeed) {
						currentPlaybackSpeed = mplaybackSpeed;
						MP3Wrapper.setPlaybackRate(currentPlaybackSpeed);
					}
					if (mlooping) {
						if (MP3Wrapper.tell() < (mloopEnd - minBufferSize)) {
							merr = MP3Wrapper.decodeMP3(minBufferSize * 2,
									mbuffer);
							msamplesPlayedWhileLooping += minBufferSize;
							maudioTrack.write(mbuffer, 0, minBufferSize);
						} else {
							int leftover = (int) (mloopEnd - MP3Wrapper.tell()) * 2;
							msamplesPlayedWhileLooping += leftover;
							merr = MP3Wrapper.decodeMP3((leftover) * 2,
									tempBuffer);
							maudioTrack.write(tempBuffer, 0, leftover);
							if (mlooping)
								MP3Wrapper.seekTo((int) mloopStart);
							else if(mcontinuousPlay) {
								maudioTrack.flush();
								MP3Wrapper.seekTo((int) msamplesPlayedWhileLooping);
							}
						}
					} else {
						maudioTrack.write(mbuffer, 0, minBufferSize);
					}
				} else
					break;
			} while (!mtrackEnded);
			if (merr == MPG123_DONE) {
				maudioTrack.flush();
				maudioTrack.stop();
				mplaybackHandler.SetEvent(PlaybackEvent.songStopped, mfilename);
			}
			maudioTrack.release();
			MP3Wrapper.cleanupMP3();
			if (mtrackEnded)
				msignalThread.doNotify();
		}
	}

	public void Control(PlaybackState control) {
		if (mtrackEnded)
			return;
		switch (control) {
		case play:
			mtrackPaused = false;
			mlooping = false;
			maudioTrack.play();
			msignalControl.doNotify();
			break;

		case pause:
			mtrackPaused = true;
			mlooping = false;
			maudioTrack.pause();
			break;

		case stop:
			mtrackPaused = true;
			mlooping = false;
			MP3Wrapper.seekTo(0);
			maudioTrack.flush();
			maudioTrack.stop();
			break;

		case end:
			mtrackEnded = true;
			mlooping = false;
			maudioTrack.stop();
			msignalControl.doNotify();
			break;
		}
	}

	public void Control(PlaybackState control, int playbackSpeed) {
		switch (control) {
		case speed:
			mtrackPaused = false;
			maudioTrack.play();
			mplaybackSpeed = playbackSpeed;
			msignalControl.doNotify();
			break;
		}
	}

	
	public void Control(PlaybackState control, int loopInMs, boolean continuousPlay) {
		switch (control) {
		case loop:
			this.mcontinuousPlay = continuousPlay;
			if (!mlooping) {
				mlooping = true;
				maudioTrack.flush();
				System.out.println("LOOP TIME: " + loopInMs);
				if(loopInMs < 200) {
					mloopEnd = MP3Wrapper.tell() - minBufferSize/8;
				} else {
					mloopEnd = MP3Wrapper.tell();
				}
				msamplesPlayedWhileLooping = mloopEnd;
				loopInMs = validateLoop(loopInMs);
				MP3Wrapper.seekTo((int) mloopStart);
			}
			if (loopInMs != this.mloopInMs) {
				loopInMs = validateLoop(loopInMs);
				maudioTrack.flush();
			}
			maudioTrack.play();
			msignalControl.doNotify();
			break;
		}
	}

	public void Control(PlaybackState control, double leftVol, double rightVol) {
		if (mtrackEnded)
			return;

		switch (control) {
		case volume:
			maudioTrack.setStereoVolume((float) leftVol, (float) rightVol);
			break;
		}
	}
	
	private int validateLoop(int loopInMs) {
		mloopStart = mloopEnd - Math.round(2f * loopInMs * 44.1f);
		if (mloopStart < 0)
			mloopStart = 0;
		loopInMs = Math.round((mloopEnd - mloopStart) / (2f * 44.1f));
		this.mloopInMs = loopInMs;
		return loopInMs;
	}
}
