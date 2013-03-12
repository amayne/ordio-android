package com.ordio.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.content.ContentResolver;
import android.widget.AdapterView.OnItemClickListener;
import com.ordio.player.backend.BPMCounter;

public class PlayerTabActivity extends Activity{
	BPMCounter bpmCounter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playertab);
		bpmCounter = BPMCounter.getInstance();
		OrdioActivity.databaseHelper.getExistingPlaylists();

		/*
		 * Query our database for all song assigned to playing playlist.
		 * Furthermore identify, if exists, currently played song.
		 */
		ContentResolver resolver = getBaseContext().getContentResolver();
		final Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] {
						MediaStore.MediaColumns._ID,
						MediaStore.Audio.AudioColumns.ALBUM_ID,
						MediaStore.Audio.AudioColumns.ARTIST,
						MediaStore.Audio.AudioColumns.TITLE,
						MediaStore.Audio.AudioColumns.ALBUM,
						MediaStore.Audio.Media.DATA },
				MediaStore.Audio.AudioColumns.IS_MUSIC + "=" + 1, null, null);

		// String[] PROJECTION = new String[] { DatabaseColumns._ID,
		// DatabaseColumns.SONG_PLAYING, DatabaseColumns.SONG_ARTIST,
		// DatabaseColumns.SONG_ALBUM, DatabaseColumns.SONG_TITLE,
		// DatabaseColumns.SONG_PATH };

		// final Cursor c = OrdioActivity.databaseHelper
		// .queryAllPlayingSongs(PROJECTION);
		final ListView listSongs = (ListView) findViewById(R.id.ListViewPlayingSongs);
		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.list_item_album_track, 
				cursor, new String[] { MediaStore.Audio.AudioColumns.ARTIST, MediaStore.Audio.AudioColumns.TITLE }, 
				new int[] { R.id.artist, R.id.track });

		listSongs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		        Uri selectedUri = ContentUris.withAppendedId(uri, id);
		        System.out.println(id);
		        String[] songProjection = new String[] { MediaStore.Audio.Media.ARTIST, 
		        		MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
		        		MediaStore.Audio.Media.DATA };
		        
		        Cursor cFile = managedQuery(selectedUri, songProjection, null, null, null);
		        if(cFile != null && cFile.moveToFirst() )
		        {
		        	AlertDialog.Builder dialog = new AlertDialog.Builder(PlayerTabActivity.this);
		        	//dialog.setMessage();
		        	
		        	try 
		        	{
		        		// Play this song
		        		((GlobalState) getApplicationContext()).PlaySong(cFile.getString(3));
					
		        		
		        	}catch(RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
			}
		});
		listSongs.setAdapter(listAdapter);
		/**
		 * Start playback
		 */
		final ImageButton buttonStart = (ImageButton) findViewById(R.id.ButtonPlay);
		buttonStart.setOnClickListener(new OnClickListener() {
			private boolean play = false;
			@Override
			public void onClick(View v) {
				try {
					if(play) {
						// IS PLAYING?
						buttonStart.setImageResource(R.drawable.playback_pause);
						((GlobalState) getApplicationContext()).PlayPlayback();
					} else {
						buttonStart.setImageResource(R.drawable.playback_play);
						((GlobalState) getApplicationContext()).PausePlayback();
					}
					play = !play;
				} catch (RemoteException re) {
					Log.e("Error", re.toString());
				}
			}
		});

		/**
		 * Stop playback
		 */
//		final ImageButton buttonPause = (ImageButton) findViewById(R.id.ButtonPause);
//		buttonPause.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				try {
//					((GlobalState) getApplicationContext()).PausePlayback();
//				} catch (RemoteException re) {
//					Log.e("Error", re.toString());
//				}
//			}
//		});
		final Button buttonTap = (Button) findViewById(R.id.ButtonTap);
		/*
		 * Typeface tf = Typeface.createFromAsset(getAssets(),
		 * "fonts/rockwellextrabold.ttf");
		 */
		if (bpmCounter.getBPMAverage() > 0) {
			buttonTap.setText(Integer.toString(bpmCounter.getBPMAverage()));
		}
		buttonTap.setTypeface(Typeface.DEFAULT_BOLD);
		buttonTap.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
		// buttonTap.setTypeface(tf);
		buttonTap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				System.out.println(bpmCounter.tap());
				buttonTap.setText(Integer.toString(bpmCounter.getBPMAverage()));
			}
		});
		
//		final ImageButton buttonLoop = (ImageButton) findViewById(R.id.ButtonLoop);
//		buttonLoop.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				try {
//					((GlobalState) getApplicationContext())
//							.LoopPlayback(bpmCounter.getMilliseconds(), false);
//				} catch (RemoteException re) {
//					Log.e("Error", re.toString());
//				}
//			}
//		});
	}
}
