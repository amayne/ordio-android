package com.ordio.player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.ordio.player.backend.AudioEffect;
import com.ordio.player.backend.BPMCounter;

public class EffectsTabActivity extends GraphicsActivity {
    /** Used as a pulse to gradually fade the contents of the window. */
    private static final int FADE_MSG = 1;
    
    /** Menu ID for the command to clear the window. */
    private static final int EFFECT_ID = Menu.FIRST;
    /** Menu ID for the command to toggle fading. */
    private static final int HOLD_ID = Menu.FIRST+1;
    private static final int CONTINUOUS_PLAY_ID = Menu.FIRST+2;
    
    /** How often to fade the contents of the window (in ms). */
    private static final int FADE_DELAY = 25;
    
    /** The view responsible for drawing the window. */
    MyView mView;
    /** Is fading mode enabled? */
    boolean mFading;
    boolean bpmCounting = false;
    
    private AudioEffect mAudioEffect;
    final String[] effectStrings = {"Loop & Pan", "Loop & High Pass Filter", "Loop & Low Pass Filter", "Loop & Speed", "BPM Counter"};
    AudioEffect[] effects;
    int currentEffect = 0;
    private BPMCounter mBpmCounter;
    
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create and attach the view that is responsible for painting.
        effects = new AudioEffect[] {
        		new AudioEffect(getApplicationContext(), AudioEffect.EffectType.pan, AudioEffect.EffectType.loop, 8, 7),
        		new AudioEffect(getApplicationContext(), AudioEffect.EffectType.highpassfilter, AudioEffect.EffectType.loop, 14, 8),
        		new AudioEffect(getApplicationContext(), AudioEffect.EffectType.lowpassfilter, AudioEffect.EffectType.loop, 14, 8),
        		new AudioEffect(getApplicationContext(), AudioEffect.EffectType.speed, AudioEffect.EffectType.loop, 4, 8)};
        mBpmCounter = BPMCounter.getInstance();
        mView = new MyView(this);
        mAudioEffect = effects[currentEffect];
        setContentView(mView);
        mView.requestFocus();
        //mView.drawEffectText("Filter", "Loop");
        mAudioEffect.resetEffects(false);
        //drawEffectText();
        // Restore the fading option if we are being thawed from a
        // previously saved state.  Note that we are not currently remembering
        // the contents of the bitmap.
        mFading = savedInstanceState != null ? savedInstanceState.getBoolean("fading", true) : true;
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, EFFECT_ID, 0, "Effect");
        menu.add(0, HOLD_ID, 0, "Hold").setCheckable(true);
        menu.add(0, CONTINUOUS_PLAY_ID, 0, "Continuous").setCheckable(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(HOLD_ID).setChecked(mFading);
        menu.findItem(CONTINUOUS_PLAY_ID).setChecked(mAudioEffect.isContinuousPlay());
    	mView.mFadeSteps = 0;
        return super.onPrepareOptionsMenu(menu);
    }
    
    private void drawEffectText() {
    	String[] str = effectStrings[currentEffect].split(" & ");
    	if(str.length == 2)
    		mView.drawEffectText(str[1], str[0]);
    	else
    		mView.drawEffectText(str[0], "");
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case EFFECT_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Effect");
                builder.setSingleChoiceItems(effectStrings, currentEffect, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int dialogItem) {
                        //Toast.makeText(getApplicationContext(), effectStrings[item], Toast.LENGTH_SHORT).show();
                    	//String[] str = effectStrings[dialogItem].split(" & ");
                    	currentEffect = dialogItem;
                    	drawEffectText();
                    	
//                    	if(str.length == 2)
//                    		mView.drawEffectText(str[1], str[0]);
//                    	else
//                    		mView.drawEffectText(str[0], "");
                    	if(dialogItem < effects.length) {
                        	mAudioEffect = effects[dialogItem];
                        	bpmCounting = false;
                        } else {
                        	bpmCounting = true;
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case HOLD_ID:
                mFading = !mFading;
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                if (mFading) {
                    startFading();
                } else {
                    stopFading();
                }
                return true;
            case CONTINUOUS_PLAY_ID:
            	if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
            	mAudioEffect.setContinuousPlay(!mAudioEffect.isContinuousPlay());
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    

    @Override protected void onResume() {
        super.onResume();
        mAudioEffect.resetEffects(false);
        //drawEffectText();
        // If fading mode is enabled, then as long as we are resumed we want
        // to run pulse to fade the contents.
        if (mFading) {
            startFading();
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save away the fading state to restore if needed later.  Note that
        // we do not currently save the contents of the display.
        outState.putBoolean("fading", mFading);
    }

    @Override protected void onPause() {
        super.onPause();
        mAudioEffect.resetEffects(false);
        // Make sure to never run the fading pulse while we are paused or
        // stopped.
        stopFading();
    }

    /**
     * Start up the pulse to fade the screen, clearing any existing pulse to
     * ensure that we don't have multiple pulses running at a time.
     */
    void startFading() {
        mHandler.removeMessages(FADE_MSG);
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(FADE_MSG), FADE_DELAY);
    }
    
    /**
     * Stop the pulse to fade the screen.
     */
    void stopFading() {
        mHandler.removeMessages(FADE_MSG);
    }
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                // Upon receiving the fade pulse, we have the view perform a
                // fade and then enqueue a new message to pulse at the desired
                // next time.
                case FADE_MSG: {
                    mView.fade();
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(FADE_MSG), FADE_DELAY);
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    };
    
    public class MyView extends View {
        protected static final int FADE_ALPHA = 0x06;
        protected static final int MAX_FADE_STEPS = 256/FADE_ALPHA + 4;
        protected int r = 10;
        protected int g = 128;
        protected int b = 238;
        protected Bitmap mBitmap;
        protected Canvas mCanvas;
        protected final Rect mRect = new Rect();
        protected final Paint mPaint;
        protected final Paint mWhitePaint;
        protected final Paint mEffectPaint;
        protected final Paint mFadePaint;
        protected final Path mVerticalPath;
        protected Path mCirclePath;
        protected boolean mCurDown;
        protected int mCurX;
        protected int mCurY;
        protected float mCurPressure;
        protected float mCurSize;
        protected int mCurWidth;
        protected int mFadeSteps = MAX_FADE_STEPS;
        protected boolean isTouching = false;
        protected int drawingCounter = 0;
        
        public MyView(Context c) {
            super(c);
            mVerticalPath = new Path();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setARGB(255, r, g, b);
            mEffectPaint = new Paint();
            mEffectPaint.setColor(Color.WHITE);
            mEffectPaint.setAlpha(255);
            mEffectPaint.setTextSize(42f);
            mEffectPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mEffectPaint.setAntiAlias(true);
            mEffectPaint.setTextAlign(Paint.Align.CENTER);
            mWhitePaint = new Paint();
            mWhitePaint.setColor(Color.WHITE);
            mWhitePaint.setAlpha(255);
            mWhitePaint.setTextSize(60f);
            mWhitePaint.setTypeface(Typeface.DEFAULT_BOLD);
            mWhitePaint.setAntiAlias(true);
            mWhitePaint.setTextAlign(Paint.Align.CENTER);
            mFadePaint = new Paint();
            mFadePaint.setDither(true);
            mFadePaint.setARGB(FADE_ALPHA, 0, 0, 0);
        }

        public void clear() {
            if (mCanvas != null) {
                mPaint.setARGB(0xff, 0, 0, 0);
                mCanvas.drawPaint(mPaint);
                invalidate();
                mFadeSteps = MAX_FADE_STEPS;
            }
        }
        
        public void fade() {
            if (mCanvas != null && mFadeSteps < MAX_FADE_STEPS) {
                mCanvas.drawPaint(mFadePaint);
                invalidate();
                mFadeSteps++;
            } 
            if(!isTouching) {
            	mAudioEffect.resetEffects(true);
            }
        }
        
        @Override protected void onSizeChanged(int w, int h, int oldw,
                int oldh) {
            int curW = mBitmap != null ? mBitmap.getWidth() : 0;
            int curH = mBitmap != null ? mBitmap.getHeight() : 0;
            if (curW >= w && curH >= h) {
                return;
            }

            //TrackInformation trackinfo = new TrackInformation("/sdcard/MP3/LanzamientosMp3.es/VA.-.Original.Dutch.House.Sound/16 16 - Quintin vs. DJ Jean - Original Dutch (Original Mix).mp3");
//            ContentResolver resolver = getBaseContext().getContentResolver();
//            String where = MediaStore.Audio.Media.ARTIST_ID + "=" + 26 + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
//    		Cursor cursor = resolver.query(
//    				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//    				new String[] { MediaStore.Audio.AudioColumns.ALBUM_ID, MediaStore.MediaColumns._ID }, MediaStore.MediaColumns._ID + "=26", null, null);

            if (curW < w) curW = w;
            if (curH < h) curH = h;
            Bitmap newBitmap = Bitmap.createBitmap(curW, curH,Bitmap.Config.RGB_565);
            Canvas newCanvas = new Canvas();
            newCanvas.setBitmap(newBitmap);
            if (mBitmap != null) {
                newCanvas.drawBitmap(mBitmap, 0, 0, null);
            }
            mBitmap = newBitmap;
            mCanvas = newCanvas;
            mVerticalPath.lineTo(0, this.getHeight());
            //mCanvas.drawLine(0, 100, curW, 100, mWhitePaint);
            mFadeSteps = MAX_FADE_STEPS;
        }
        
        @Override protected void onDraw(Canvas canvas) {
            if (mBitmap != null) {
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        }

        @Override public boolean onTrackballEvent(MotionEvent event) {
            boolean oldDown = mCurDown;
            mCurDown = true;
            int N = event.getHistorySize();
            int baseX = mCurX;
            int baseY = mCurY;
            final float scaleX = event.getXPrecision();
            final float scaleY = event.getYPrecision();
            for (int i=0; i<N; i++) {
                drawPoint(baseX+event.getHistoricalX(i)*scaleX,
                        baseY+event.getHistoricalY(i)*scaleY,
                        event.getHistoricalPressure(i),
                        event.getHistoricalSize(i));
            }
            drawPoint(baseX+event.getX()*scaleX, baseY+event.getY()*scaleY,
                    event.getPressure(), event.getSize());
            mCurDown = oldDown;
            return true;
        }
        
        @Override public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if(action == MotionEvent.ACTION_UP) {
            	isTouching = false;
            	if(bpmCounting) {
            		Integer bpm = mBpmCounter.tap();
            		mCanvas.drawText(bpm.toString(), mBitmap.getWidth()/2, mBitmap.getHeight()/4, mWhitePaint);//drawCircle(mCurX, mCurY, mCurWidth, mPaint);
            		drawPoint(event.getX(), event.getY(), event.getPressure(),
                            event.getSize()*8);
            	}
            } else {
            	mCurDown = action == MotionEvent.ACTION_DOWN
                    	|| action == MotionEvent.ACTION_MOVE;
            	isTouching = true;
            	if(!bpmCounting)
                    drawPoint(event.getX(), event.getY(), event.getPressure(),
                            event.getSize());
            }
            /*int N = event.getHistorySize();
            for (int i=0; i<N; i++) {
                //Log.i("TouchPaint", "Intermediate pointer #" + i);
                drawPoint(event.getHistoricalX(i), event.getHistoricalY(i),
                        event.getHistoricalPressure(i),
                        event.getHistoricalSize(i));
            }*/
            return true;
        }
        
        
        private void drawPoint(float x, float y, float pressure, float size) {
            mCurX = (int)x;
            mCurY = (int)y;
            if(!bpmCounting) {
            	mAudioEffect.doEffect(mCurX, mCurY, mBitmap.getWidth(), mBitmap.getHeight());
                size = 0.23f;
            }
            System.out.println("MCURX" + x + " MCURY " + y);
            mCurPressure = pressure;
            mCurSize = size;
            mCurWidth = (int)(mCurSize*(getWidth()/3));
            if (mCurWidth < 1) mCurWidth = 1;
            if (mCurDown && mBitmap != null) {
                int pressureLevel = (int)(mCurPressure*255);
                mPaint.setARGB(pressureLevel, r, g, b);
                mCanvas.drawCircle(mCurX, mCurY, mCurWidth, mPaint);
                mPaint.setARGB(pressureLevel, 255, 255, 255);
                mCanvas.drawCircle(mCurX, mCurY, mCurWidth/2, mPaint);
                mRect.set(mCurX-mCurWidth-2, mCurY-mCurWidth-2,
                        mCurX+mCurWidth+2, mCurY+mCurWidth+2);
                invalidate(mRect);
            }
            mFadeSteps = 0;
        }
        private void drawEffectText(String effectX, String effectY) {
        	mCanvas.drawTextOnPath(effectY, mVerticalPath, 0f, -10f, mEffectPaint);
        	mCanvas.drawText(effectX, mBitmap.getWidth()/2, mBitmap.getHeight()*15f/16f, mEffectPaint);
        	mFadeSteps = MAX_FADE_STEPS-1;
        }
    }
}
