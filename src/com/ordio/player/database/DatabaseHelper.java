package com.ordio.player.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;


public class DatabaseHelper {
	private static final String DATABASE_NAME = "Ordio.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String PLAYLISTS_TABLE_NAME = "Playlists";
	
	private SQLiteOpenHelper sqliteHelper;
	
	// TODO: Make all database queries via "whereString = ?" and whereArgs to avoid 
	// escaping special characters and making the application more stable!
	public DatabaseHelper(Context context) {
		this.sqliteHelper = new SQLiteHelper(context);
	}
	
	public void Finish()
	{
		sqliteHelper.close();
	}
	
	/**
	 * 
	 * @return cursor holding informations about all playing songs
	 */
	public Cursor queryAllPlayingSongs(String[] projection) {
		String[] PROJECTION = new String[] { BaseColumns._ID, DatabaseColumns.SONG_PLAYING, 
				DatabaseColumns.SONG_ARTIST, DatabaseColumns.SONG_ALBUM, DatabaseColumns.SONG_TITLE, 
				DatabaseColumns.SONG_PATH };
		
		Cursor c = sqliteHelper.getReadableDatabase().query(PLAYLISTS_TABLE_NAME, PROJECTION, 
				DatabaseColumns.PLAYLIST_PLAY + " = '1'", null, null, null, null);
		return c;
	}
	
	/**
	 * TODO: Make more dynamic
	 * @param artist
	 * @param album
	 * @param title
	 * @param path
	 * @return
	 */
	public synchronized boolean insertSong(String artist, String album, String title, String path) {
		String[] PROJECTION = new String[] { BaseColumns._ID };
		
		// Need to escape all special characters!
		StringBuilder s = new StringBuilder();
		s.append("(" + DatabaseColumns.SONG_PATH + " = ");
		s.append(DatabaseUtils.sqlEscapeString(path));
		s.append(") AND (" + DatabaseColumns.PLAYLIST_PLAY + " = '1')");
		
		Cursor c = sqliteHelper.getReadableDatabase().query(PLAYLISTS_TABLE_NAME, PROJECTION, 
				s.toString(), null, null, null, null);
		if(c != null) 
		{
			if(c.moveToFirst())
			{
				// Song is already in playing playlist, just mark as currently played
				c.close();
				return markSongCurrPlayed(true, path);
			}
			else
			{
				// Song isn't is playing playlist, assign it to it
				c.close();
				ContentValues initialValues = new ContentValues();
				initialValues.put(DatabaseColumns.PLAYLIST_NR, 0);
				initialValues.put(DatabaseColumns.PLAYLIST_PLAY, 1);
				initialValues.put(DatabaseColumns.SONG_PLAYING, 1);
				initialValues.put(DatabaseColumns.SONG_ARTIST, artist);
				initialValues.put(DatabaseColumns.SONG_ALBUM, album);
				initialValues.put(DatabaseColumns.SONG_TITLE, title);
				initialValues.put(DatabaseColumns.SONG_PATH, path);
				
				if (sqliteHelper.getWritableDatabase().insert(PLAYLISTS_TABLE_NAME, null, initialValues) != -1)
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	public synchronized boolean markSongCurrPlayed(boolean playing, String path) {
		if(path != null && !path.equals(""))
		{
			// Need to escape all special characters!
			StringBuilder s = new StringBuilder();
			s.append("(" + DatabaseColumns.SONG_PATH + " = ");
			s.append(DatabaseUtils.sqlEscapeString(path));
			s.append(") AND (" + DatabaseColumns.PLAYLIST_PLAY + " = '1')");
			
			ContentValues updateValues = new ContentValues();
			if(playing)
				updateValues.put(DatabaseColumns.SONG_PLAYING, 1);
			else
				updateValues.put(DatabaseColumns.SONG_PLAYING, 0);

			// Update row, should affect just one!
			if(sqliteHelper.getWritableDatabase().update(PLAYLISTS_TABLE_NAME, updateValues, s.toString(), 
					null) > 0)
				return true;
		}
		return false;
	}
	
	public synchronized Cursor getExistingPlaylists() {
		Cursor c = sqliteHelper.getReadableDatabase().rawQuery("SELECT name FROM sqlite_master " +
				"WHERE type = 'table'", null);
		return c;
	}
	
	/**
	 * Database open and create helper class
	 * @author Sebastian
	 *
	 */
	private static class SQLiteHelper extends SQLiteOpenHelper {
		SQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
		
		@Override
        public void onCreate(SQLiteDatabase db) 
        {
			// Log.v(TAG, "Creating database for interface statistics.");
			db.execSQL("CREATE TABLE " + PLAYLISTS_TABLE_NAME + " ("
					+ DatabaseColumns._ID + " INTEGER PRIMARY KEY,"
					+ DatabaseColumns.PLAYLIST_NR + " INTEGER DEFAULT 0,"
					+ DatabaseColumns.PLAYLIST_PLAY + " INTEGER DEFAULT 0,"
					+ DatabaseColumns.SONG_PLAYING + " INTEGER DEFAULT 0," 
					+ DatabaseColumns.SONG_ARTIST + " TEXT,"
					+ DatabaseColumns.SONG_ALBUM + " TEXT," 
					+ DatabaseColumns.SONG_TITLE + " TEXT,"
					+ DatabaseColumns.SONG_PATH + " TEXT" + ");");
        }
		
		@Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
			// Log.w(TAG, "Upgrading database from version " + oldVersion +
            // " to " + newVersion
            // + ", which will destroy all old data");
        	db.execSQL("DROP TABLE IF EXISTS " + PLAYLISTS_TABLE_NAME);
            onCreate(db);
        }
	}
}
