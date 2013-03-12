package com.ordio.player.database;

import android.provider.BaseColumns;

public class DatabaseColumns implements BaseColumns {
	/** This class cannot be instantiated. */
    private DatabaseColumns() {
    }
    
    public static final String PLAYLIST_NR = "PlaylistNr";
    
    public static final String PLAYLIST_PLAY = "PlaylistPlay";
    
    public static final String SONG_PLAYING = "SongPlaying";
    
    public static final String SONG_ARTIST = "SongArtist";
    
    public static final String SONG_ALBUM = "SongAlbum";
    
    public static final String SONG_TITLE = "SongTitle";
    
    // TODO: Add genre?
    
    public static final String SONG_PATH = "SongPath";
}
