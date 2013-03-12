package com.ordio.player;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EqualizerTabActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
	private static boolean enabled = false;
	private static int[] bands32 = {20,25,31,40,50,63,80,63,80,100,125,170,200,250,315,400,500,630,800,1000,1250,1600,2000,2500,3150,4000,5000,6300,8000,10000,12500,16500,20000};
	private static int[] bands10 = {31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000};
	private static int[][] lookup = {{20,25,31,40}, 		// 31
									{50,63,80},  		// 62
									{100,125,170}, 		// 125
									{200,250,315}, 		// 250
									{400,500,630}, 		// 500
									{800,1000,1250},	// 1000
									{1600,2000,2500},	// 2000
									{3150,4000,5000}, 	// 4000
									{6300,8000,12500}, 	// 8000
									{16500,20000}};		// 16000
	private static int[] bandVol;
	private static int bassVol = 12;
	private static int midVol = 12;
	private static int trebleVol = 12;
	
	private static SeekBar[] seekBarBands;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equalizertab);
        /**
         * Enable/Disable equalizer
         */
        final CheckBox checkBoxEnable = (CheckBox) findViewById(R.id.EqualizerCheckBoxEnable);
        checkBoxEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				enabled = isChecked;
				
				if(isChecked)
				{
					try 
					{
						double dVol = bassVol;
						dVol /= 12;
						
						((GlobalState) getApplicationContext()).setEQ(0, dVol);
						
						dVol = midVol;
						dVol /= 12;
						
						((GlobalState) getApplicationContext()).setEQ(1, dVol);
						
						dVol = trebleVol;
						dVol /= 12;
						
						for(int i = 2; i < 32; i++)
							((GlobalState) getApplicationContext()).setEQ(i, dVol);
						
					} catch (RemoteException re) {
						// TODO:
					}
				}
				else
				{ 
					try 
					{
						((GlobalState) getApplicationContext()).resetEQ();
					} catch (RemoteException re) {
						// TODO:
					}
				}
			}
        });
        final TextView text31hz = (TextView) findViewById(R.id.freq31);
        text31hz.setText("31Hz");
		final SeekBar seekbar30hz = (SeekBar) findViewById(R.id.seek31);
		seekbar30hz.setOnSeekBarChangeListener(this);
		final TextView text62hz = (TextView) findViewById(R.id.freq62);
		text62hz.setText("62Hz");
		final SeekBar seekbar62hz = (SeekBar) findViewById(R.id.seek62);
		seekbar62hz.setOnSeekBarChangeListener(this);
		final TextView text125hz = (TextView) findViewById(R.id.freq125);
		text125hz.setText("125Hz");
		final SeekBar seekbar125hz = (SeekBar) findViewById(R.id.seek125);
		seekbar125hz.setOnSeekBarChangeListener(this);
		final TextView text250hz = (TextView) findViewById(R.id.freq250);
		text250hz.setText("250Hz");
		final SeekBar seekbar250hz = (SeekBar) findViewById(R.id.seek250);
		seekbar250hz.setOnSeekBarChangeListener(this);
//        
//        /**
//         * Set current equalizer
//         */
//        final TextView textBassSt = (TextView) findViewById(R.id.EqualizerTextView1);
//		textBassSt.setText(Integer.valueOf(bassVol).toString());
//		
//		final TextView textMidSt = (TextView) findViewById(R.id.EqualizerTextView2);
//		textMidSt.setText(Integer.valueOf(midVol).toString());
//		
//		final TextView textTrebleSt = (TextView) findViewById(R.id.EqualizerTextView3);
//		textTrebleSt.setText(Integer.valueOf(trebleVol).toString());
//		
//        /**
//		 * Bass
//		 */
//		final Button buttonBassUp = (Button) findViewById(R.id.EqualizerButtonUp1);
//		buttonBassUp.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(bassVol < 24)
//				{
//					bassVol += 1;
//					try 
//					{
//						double dVol = bassVol;
//						dVol /= 12;
//						
//						if(enabled)
//							((GlobalState) getApplicationContext()).setEQ(0, dVol);
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				final TextView textBass = (TextView) findViewById(R.id.EqualizerTextView1);
//				textBass.setText(Integer.valueOf(bassVol).toString());
//			}
//        });
//		
//		final Button buttonBassDown = (Button) findViewById(R.id.EqualizerButtonDown1);
//		buttonBassDown.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(bassVol > 0)
//				{
//					bassVol -= 1;
//					try 
//					{
//						double dVol = bassVol;
//						dVol /= 12;
//						
//						if(enabled)
//							((GlobalState) getApplicationContext()).setEQ(0, dVol);
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				final TextView textBass = (TextView) findViewById(R.id.EqualizerTextView1);
//				textBass.setText(Integer.valueOf(bassVol).toString());
//			}
//        });
//		
//		/**
//		 * Mid
//		 */
//		final Button buttonMidUp = (Button) findViewById(R.id.EqualizerButtonUp2);
//		buttonMidUp.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(midVol < 24)
//				{
//					midVol += 1;
//					try 
//					{
//						double dVol = midVol;
//						dVol /= 12;
//						
//						if(enabled)
//							((GlobalState) getApplicationContext()).setEQ(1, dVol);
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				final TextView textMid = (TextView) findViewById(R.id.EqualizerTextView2);
//				textMid.setText(Integer.valueOf(midVol).toString());
//			}
//        });
//		
//		final Button buttonMidDown = (Button) findViewById(R.id.EqualizerButtonDown2);
//		buttonMidDown.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(midVol > 0)
//				{
//					midVol -= 1;
//					try 
//					{
//						double dVol = midVol;
//						dVol /= 12;
//						
//						if(enabled)
//							((GlobalState) getApplicationContext()).setEQ(1, dVol);
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				final TextView textMid = (TextView) findViewById(R.id.EqualizerTextView2);
//				textMid.setText(Integer.valueOf(midVol).toString());
//			}
//        });
//		
//		/**
//		 * Treble
//		 */
//		final Button buttonTrebleUp = (Button) findViewById(R.id.EqualizerButtonUp3);
//		buttonTrebleUp.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(trebleVol < 24)
//				{
//					trebleVol += 1;
//					
//					double dVol = trebleVol;
//					dVol /= 12;
//					
//					if(enabled)
//					{
//						for(int i = 2; i < 32; i++)
//							try 
//							{
//								((GlobalState) getApplicationContext()).setEQ(i, dVol);
//							} catch (RemoteException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//					}
//				}
//				
//				final TextView textTreble = (TextView) findViewById(R.id.EqualizerTextView3);
//				textTreble.setText(Integer.valueOf(trebleVol).toString());
//			}
//        });
//		
//		final Button buttonTrebleDown = (Button) findViewById(R.id.EqualizerButtonDown3);
//		buttonTrebleDown.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(trebleVol > 0)
//				{
//					trebleVol -= 1;
//					
//					double dVol = trebleVol;
//					dVol /= 12;
//					
//					if(enabled)
//					{
//						for(int i = 2; i < 32; i++)
//							try 
//							{
//								((GlobalState) getApplicationContext()).setEQ(i, dVol);
//							} catch (RemoteException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//					}
//				}
//				
//				final TextView textTreble = (TextView) findViewById(R.id.EqualizerTextView3);
//				textTreble.setText(Integer.valueOf(trebleVol).toString());
//			}
//        });
	}
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        //mProgressText.setText(progress + " " + 
        //        getString(R.string.seekbar_from_touch) + "=" + fromTouch);
    }
	
    public void onStartTrackingTouch(SeekBar seekBar) {
        //mTrackingText.setText(getString(R.string.seekbar_tracking_on));
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        //mTrackingText.setText(getString(R.string.seekbar_tracking_off));
    }
}
