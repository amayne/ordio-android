package com.ordio.player;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.ordio.player.database.DatabaseHelper;

public class OrdioActivity extends TabActivity {
	public static DatabaseHelper databaseHelper = null;
	 
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        // Create or open our database backend
        databaseHelper = new DatabaseHelper(this);
        
        
        // Create our service if it doesn't exist already
        ((GlobalState) getApplicationContext()).newServiceConnection();
        
        
        // Build our tabs
        TabHost tabs = getTabHost();
        
        // Player tab
        TabHost.TabSpec playerTab = tabs.newTabSpec("tab_player");
        //playerTab.setIndicator("Player", getResources().getDrawable(R.drawable.));
        playerTab.setIndicator("Player");
        playerTab.setContent(new Intent(this, PlayerTabActivity.class));
        tabs.addTab(playerTab);
       
        // Equalizer tab
//        TabHost.TabSpec equalizerTab = tabs.newTabSpec("tab_equalizer");
//        equalizerTab.setIndicator("Equalizer");
//        equalizerTab.setContent(new Intent(this, EqualizerTabActivity.class));
//        tabs.addTab(equalizerTab);
        
        // Loop and Volume tab
        TabHost.TabSpec effectsTab = tabs.newTabSpec("tab_effects");
        effectsTab.setIndicator("Effects");
        effectsTab.setContent(new Intent(this, EffectsTabActivity.class));
        tabs.addTab(effectsTab);
/*        
        // Playlist tab
        TabHost.TabSpec playlistTab = tabs.newTabSpec("tab_playlist");
        playlistTab.setIndicator("Playlists");
        playlistTab.setContent(new Intent(this, PlaylistsTabActivity.class));
        tabs.addTab(playlistTab);
*/        
        
        // Audio library
        TabHost.TabSpec playerLibrary = tabs.newTabSpec("tab_player");
        playerLibrary.setIndicator("Library");
        playerLibrary.setContent(new Intent(this, LibraryTabActivity.class));
        tabs.addTab(playerLibrary);
        
        
        tabs.setCurrentTab(0);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
//    	try {
//			((GlobalState) getApplicationContext()).PausePlayback();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	databaseHelper.Finish();
    }
    @Override
    public void onDestroy() {
    	
    }
}
