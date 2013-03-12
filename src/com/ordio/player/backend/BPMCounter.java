package com.ordio.player.backend;


public final class BPMCounter {
	
	private int mcount = 0;
	private long msecsFirst;
	private long msecsPrevious;
	private long trackSamplesFirst = 0;
	private long averageSampleDelay = 0;
	private int[] pauses = {1,2,3,4,5};
	private int interval = 1000;
	private float mbpmAvg = 0f;
	private int roundedBpmAvg = 0;
	private static BPMCounter instance = null;
	private int multiple;
	
	private BPMCounter() {
	}
	public static synchronized BPMCounter getInstance() {
	      if(instance == null) {
	         instance = new BPMCounter();
	      }
	      return instance;
	   }
	public void reset() {
		mcount = 0;
	}
	public int tap() {
		long msecs = System.currentTimeMillis();
		if ((msecs - msecsPrevious) > interval * pauses[0]) {
			mcount = 0;
		}
		
		msecsPrevious = msecs;
		if (mcount == 0) {
			trackSamplesFirst = MP3Wrapper.tell();
			msecsFirst = msecs;
			mcount = 1;
			return 0;
		} else {
			mbpmAvg = (float) (60000f * mcount / (msecs - msecsFirst));
			roundedBpmAvg = (Math.round(mbpmAvg * 100)) / 100;
			System.out.println("tell: " + MP3Wrapper.tell() + " samplesfirst: " + trackSamplesFirst);
			averageSampleDelay = Math.round((MP3Wrapper.tell() - trackSamplesFirst)/mcount);
			System.out.println("delay: " + averageSampleDelay);
			mcount++;
			return roundedBpmAvg;
		}
	}
	public int getBPMAverage() {
		return roundedBpmAvg;
	}
	
	public int getMilliseconds() {
		if(mbpmAvg == 0) {
			mbpmAvg = 128;
		}
		switch(multiple){
			case(0):
				return Math.round((60000/mbpmAvg)*4*1000)/1000;
			case(2):
				return Math.round((60000/mbpmAvg)*2*1000)/1000;
			case(4):
				return Math.round((60000/mbpmAvg)*1000)/1000;
			case(8):
				return Math.round((60000/mbpmAvg)/2*1000)/1000;
			case(16):
				return Math.round((60000/mbpmAvg)/4*1000)/1000;
			case(32):
				return Math.round((60000/mbpmAvg)/8*1000)/1000;
			case(64):
				return Math.round((60000/mbpmAvg)/16*1000)/1000;
			default:
				return 0;
		}
	}
	public void setMultiple(int m) {
		multiple = m;
	}
}
