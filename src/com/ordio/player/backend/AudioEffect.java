package com.ordio.player.backend;

import android.content.Context;
import android.os.RemoteException;

import com.ordio.player.GlobalState;

public class AudioEffect {
	public static enum EffectType {
		loop, lowpassfilter, highpassfilter, rangefilter, pan, speed
	};

	private int sizeX;
	private int sizeY;
	private EffectType typeX;
	private EffectType typeY;
	private BPMCounter mBpmCounter;
	private Context mContext;
	private boolean continuousPlay = false;
	private int currentSpeed = 1;
	private double[][] lpfValues = {{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 0.5	},
									{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0	},
									{ 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 1.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 1.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 1.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  },
									{ 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0  }};
	
	private double[][] hpfValues = {{ 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0	},
									{ 0.6, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0  },
									{ 0.4, 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0  },
									{ 0.2, 0.6, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0  },
									{ 0.0, 0.4, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0  },
									{ 0.0, 0.2, 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0   },
									{ 0.0, 0.0, 0.6, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0   },
									{ 0.0, 0.0, 0.4, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0   },
									{ 0.0, 0.0, 0.2, 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0   },
									{ 0.0, 0.0, 0.0, 0.6, 0.8, 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0   },
									{ 0.0, 0.0, 0.0, 0.4, 0.6,	0.6, 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0   },
									{ 0.0, 0.0, 0.0, 0.2, 0.4, 0.4, 0.4, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0   },
									{ 0.0, 0.0, 0.0, 0.0, 0.2, 0.2, 0.2, 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0   },
									{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0  }};
	public AudioEffect(Context context, EffectType typeX, EffectType typeY,
			int sizeX, int sizeY) {
		this.typeX = typeX;
		this.typeY = typeY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		mContext = context;
		mBpmCounter = BPMCounter.getInstance();
		// this.floatParams = floatParams;
	}

	public boolean isContinuousPlay() {
		return continuousPlay;
	}

	public void setContinuousPlay(boolean continuousPlay) {
		this.continuousPlay = continuousPlay;
	}

	public void doEffect(int x, int y, int width, int height) {
		// System.out.println("Width: " + mBitmap.getWidth() + " Width/8: " +
		// mBitmap.getWidth()/8);
		int spacingY = height / sizeY;
		double spaceY = y / spacingY;
		int spacingX = width / sizeX;
		double spaceX = x / spacingX;
		findEffect(typeX, (int) spaceX);
		findEffect(typeY, (int) spaceY);
	}

	private void findEffect(EffectType type, int space) {
		switch (type) {
		case loop:
			setLoop(space);
			break;
		case highpassfilter:
			setHighPassFilter(space);
			break;
		case lowpassfilter:
			setLowPassFilter(space);
			break;
		case pan:
			setPan(space);
			break;
		case speed:
			setPlaybackRate(space);
			break;
		}
	}

	private void setLoop(int space) {
		boolean loop = true;
		if (space < 1) {
			mBpmCounter.setMultiple(64);
		} else if (space < 2) {
			mBpmCounter.setMultiple(32);
		} else if (space < 3) {
			mBpmCounter.setMultiple(16);
		} else if (space < 4) {
			mBpmCounter.setMultiple(8);
		} else if (space < 5) {
			mBpmCounter.setMultiple(4);
		} else if (space < 6) {
			mBpmCounter.setMultiple(2);
		} else {
			loop = false;
		}
		try {
			if (loop)
				((GlobalState) mContext).LoopPlayback(mBpmCounter
						.getMilliseconds(), continuousPlay);
			else
				((GlobalState) mContext).PlayPlayback();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setPan(int space) {
		double[] volume = null;
//		System.out.println((double) (space / 8.0));
//		System.out.println((double) (8 - space) / 8.0);
		double leftVol = (double) (space / 8.0);
		double rightVol = (double) ((8 - space) / 8.0);
		volume = new double[] { leftVol, rightVol };
		try {
			((GlobalState) mContext).setVolume(volume[0], volume[1]);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setPlaybackRate(int space) {
		int speed = 1;
		if(space < 1) {
			speed = 1;
		} else if(space < 2) {
			speed = 2;
		} else if(space < 3) {
			speed = 3;
		} else {
			speed = 4;
		} 
		if(currentSpeed != speed) {
			currentSpeed = speed;
			try {
				((GlobalState) mContext).setPlaybackRate(speed);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private void setHighPassFilter(int space) {
		boolean resetEq = false;
		if (space < 1) {
			resetEq = true;
		}
		try {
			if (resetEq) {
				((GlobalState) mContext).resetEQ();
			} else {
				for (int i = 0; i < hpfValues[space-1].length; i++) {
					((GlobalState) mContext).setEQ(i, hpfValues[space-1][i]);
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setLowPassFilter(int space) {
		boolean resetEq = false;
		if (space < 1) {
			resetEq = true;
		}
		try {
			if (resetEq) {
				((GlobalState) mContext).resetEQ();
			} else {
				for (int i = 0; i < lpfValues[space-1].length; i++) {
					((GlobalState) mContext).setEQ(i, lpfValues[space-1][i]);
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resetEffects(boolean play) {
		try {
			((GlobalState) mContext).resetEQ();
			((GlobalState) mContext).setVolume(1.0, 1.0);
			currentSpeed = 1;
			((GlobalState) mContext).setPlaybackRate(currentSpeed);
			if(play)
				((GlobalState) mContext).PlayPlayback();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
