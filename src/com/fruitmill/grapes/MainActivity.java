package com.fruitmill.grapes;

import java.io.File;

import com.fruitmill.grapes.adapter.TabsPagerAdapter;
import com.fruitmill.grapes.adapter.VideoListAdapter;
import com.fruitmill.grapes.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.SearchView.OnCloseListener;
import android.app.ActionBar;
import android.app.SearchManager;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private Tab prevTab;
	// Tab titles
	private String[] tabs = { "Feed", "Map", "My Videos" };
	private static final int ACTION_TAKE_VIDEO = 1;
	private Grapes config;
	private Uri mVideoUri;
	private File capturedVideoFile;
	private Cursor videoCursor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = (Grapes)getApplication();
        
//        String deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
//        Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();
        
        // Create app directory
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(getString(R.string.app_name), "Storage not mounted");
	    } else {
	        File directory = new File(Environment.getExternalStorageDirectory()+File.separator+getString(R.string.app_name)+File.separator+config.appVideoDirName);
	        directory.mkdirs();
	        if(directory.isDirectory())
	        {
	        	config.appVideoDir = directory;
	        	config.appRootDir = directory.getParentFile();
	        }    	
	    }
        
        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
	    for(String tab_name : tabs) {
	        actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
	    }
	    
	    /**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		// Open camera intent when camera icon is pressed
		ImageView cameraIcon = (ImageView) findViewById(R.id.cameraView);
		
		cameraIcon.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        dispatchTakeVideoIntent();
		    }
		});
    }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
		if(tab.getPosition() == 2)
		{
			String selection = MediaStore.Video.Media.DATA +" like ?";
	        String[] selectionArgs = new String[]{"%"+getString(R.string.app_name)+"%"+config.appVideoDirName+"%"};
	        String[] projection = new String[]{
	        		MediaStore.Video.Media.SIZE,
	        		MediaStore.Video.VideoColumns.DURATION,
	        		MediaStore.Video.VideoColumns.DATE_TAKEN,
	        		MediaStore.Video.VideoColumns.RESOLUTION,
	        		MediaStore.Video.VideoColumns.DISPLAY_NAME,
	        		MediaStore.Video.VideoColumns.DATA
    		};
	        videoCursor = getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
	                projection, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
	        int count = videoCursor.getCount();
	        ListView videoListView = (ListView) findViewById(R.id.myVideosListView);
	        videoListView.setAdapter(new VideoListAdapter(count, getApplicationContext(), videoCursor));
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		prevTab = tab;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		searchView.setOnSearchClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				actionBar.setSelectedNavigationItem(1); // 1 - Map fragment
				viewPager.setCurrentItem(actionBar.getSelectedNavigationIndex());
			}
		});
			
		searchView.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public boolean onClose() {
				actionBar.setSelectedNavigationItem(prevTab.getPosition()); 
				viewPager.setCurrentItem(actionBar.getSelectedNavigationIndex());
				return false;
			}
		});	
		
		return super.onCreateOptionsMenu(menu);
		//return true;
	}
	
	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void dispatchTakeVideoIntent() {
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		capturedVideoFile = new File(config.appVideoDir.getAbsolutePath(), System.currentTimeMillis()+".mp4");
		takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturedVideoFile));
		takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.videoDuration);
		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
	}
	
	private void handleCameraVideo(Intent intent) {
		mVideoUri = intent.getData();
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(capturedVideoFile)));

		Toast.makeText(this, "Video saved", Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		
		case ACTION_TAKE_VIDEO: {
			if (resultCode == RESULT_OK) {
				handleCameraVideo(data);
			}
			break;
		} 
		} 
	}
	
	/**
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	 
	    // save file url in bundle as it will be null on scren orientation
	    // changes
	    outState.putParcelable("captured_video_uri", mVideoUri);
	}
	 
	/*
	 * Here we restore the fileUri again
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	 
	    // get the file url
	    mVideoUri = savedInstanceState.getParcelable("captured_video_uri");
	}
}