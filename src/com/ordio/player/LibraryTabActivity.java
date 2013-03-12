package com.ordio.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class LibraryTabActivity extends Activity {
	// private static AlbumArtUtils albumUtils = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.librarytab);

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
		final ListView listSongs = (ListView) findViewById(R.id.ListViewLibrarySongs);//ListViewPlayingSongs);
		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this,
				R.layout.list_item_album_track, cursor, new String[] {
						MediaStore.Audio.AudioColumns.ARTIST,
						MediaStore.Audio.AudioColumns.TITLE }, new int[] {
						R.id.artist, R.id.track });

		listSongs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				Uri selectedUri = ContentUris.withAppendedId(uri, id);
				System.out.println(id);
				String[] songProjection = new String[] {
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DATA };

				Cursor cFile = managedQuery(selectedUri, songProjection, null,
						null, null);
				if (cFile != null && cFile.moveToFirst()) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							LibraryTabActivity.this);
					// dialog.setMessage();

					try {
						// Play this song
						((GlobalState) getApplicationContext()).PlaySong(cFile
								.getString(3));

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		listSongs.setAdapter(listAdapter);
	}
}
