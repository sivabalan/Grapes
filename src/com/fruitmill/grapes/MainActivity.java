package com.fruitmill.grapes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.fruitmill.grapes.adapter.TabsPagerAdapter;
import com.fruitmill.grapes.adapter.VideoItem;
import com.fruitmill.grapes.utils.Utils;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private Tab prevTab;
	// Tab titles
	private static final int FEED_VIEW = 0;
	private static final int MAP_VIEW = 1;
	private static final int MY_VIDEOS_VIEW = 2;
	private String[] tabs = { "Feed", "Map", "My Videos" };
	private static final int ACTION_TAKE_VIDEO = 1;
	private Grapes config;
	private Uri mVideoUri;
	private File capturedVideoFile, capturedThumbFile;
	private boolean mLoggedIn;
	DropboxAPI<AndroidAuthSession> mApi;
	public static String[] remoteVideoList;
	private Context mainApp;
	
	private LocationManager locationManager;
	private Criteria criteria;
	private String provider;
	private MyLocationListener locationListener;
	public static Location location;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
		
        setContentView(R.layout.activity_main);
        config = (Grapes)getApplication();
        mainApp = getApplicationContext();
        
//        String deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
//        Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();
        
        // Create app directory
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(getString(R.string.app_name), "Storage not mounted");
	    } else {
	        File directory = new File(Environment.getExternalStorageDirectory()+File.separator+getString(R.string.app_name)+File.separator+Grapes.appVideoDirName);
	        directory.mkdirs();
	        File thumbDir = new File(Environment.getExternalStorageDirectory()+File.separator+getString(R.string.app_name)+File.separator+Grapes.appThumbsDirName);
	        thumbDir.mkdirs();
	        if(directory.isDirectory() && thumbDir.isDirectory())
	        {
	        	Grapes.appThumbsDir = thumbDir;
	        	Grapes.appVideoDir = directory;
	        	Grapes.appRootDir = directory.getParentFile();
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
		
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Define the criteria how to select the location provider
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);	

		criteria.setCostAllowed(false); 

		// get the best provider depending on the criteria
		provider = locationManager.getBestProvider(criteria, false);

		// the last known location of this provider
		Location location = locationManager.getLastKnownLocation(provider);

		locationListener = new MyLocationListener();

		if (location != null) {
			locationListener.onLocationChanged(location);
		} else {
			// leads to the settings because there is no last known location
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
		// location updates: at least 1 meter and 200millsecs change
		locationManager.requestLocationUpdates(provider, Grapes.locationUpdateInterval, 1, locationListener);
		
		
    }

    private class MyLocationListener extends FragmentActivity implements LocationListener  {

    	@Override
    	public void onLocationChanged(Location loc) {
    		// Initialize the location fields
    		String op = "Latitude: "+String.valueOf(loc.getLatitude()) +
    				"Longitude: "+String.valueOf(loc.getLongitude());

    		location = loc;

//    		Fragment feeds = getSupportFragmentManager().findFragmentById(R.id.feedListView);
//    		
//    		try {
//				((FeedFragment) feeds).fetchVideos();
//			} catch (NullPointerException e) {
//				// TODO Auto-generated catch block
//				Log.v("hi","bye");
//				e.printStackTrace();
//			}
    		
    		Toast.makeText(mainApp,  "Location changed : "+op, Toast.LENGTH_SHORT).show();
    	}

    	@Override
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    		Toast.makeText(mainApp, provider + "'s status changed to "+status +"!",
    				Toast.LENGTH_SHORT).show();
    	}

    	@Override
    	public void onProviderEnabled(String provider) {
    		Toast.makeText(mainApp, "Provider " + provider + " enabled!",
    				Toast.LENGTH_SHORT).show();

    	}

    	@Override
    	public void onProviderDisabled(String provider) {
    		Toast.makeText(mainApp, "Provider " + provider + " disabled!",
    				Toast.LENGTH_SHORT).show();
    	}
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
		switch (tab.getPosition()) {
		case FEED_VIEW:
			
			break;
		case MAP_VIEW:
			
			break;
		case MY_VIDEOS_VIEW:
			
			break;
		default:
			return;
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
				v.requestFocus();
			}
		});
		
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				TabsPagerAdapter adapter = ((TabsPagerAdapter)viewPager.getAdapter());
				MapFragment fragment = (MapFragment)adapter.getFragment(1);
				fragment.findPlaces(query);
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
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
			case R.id.dropbox_login:
				if(!mApi.getSession().isLinked()) {
					mApi.getSession().startOAuth2Authentication(MainActivity.this);
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void dispatchTakeVideoIntent() {
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		capturedVideoFile = new File(Grapes.appVideoDir.getAbsolutePath(), System.currentTimeMillis()+".mp4");
		takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturedVideoFile));
		takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.videoDuration);
		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
	}
	
	private void handleCameraVideo(Intent intent) {
				
		Bitmap tempThumbnail = ThumbnailUtils.createVideoThumbnail(capturedVideoFile.getAbsolutePath(), Thumbnails.FULL_SCREEN_KIND);
		
		String fileName = capturedVideoFile.getName();
		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
		    fileName = fileName.substring(0, pos);
		}
		
		capturedThumbFile = new File(Grapes.appThumbsDir, fileName+".png");
	    try {
			FileOutputStream fOut = new FileOutputStream(capturedThumbFile);

			tempThumbnail.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(capturedVideoFile)));
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(capturedThumbFile)));
		
		ContentValues values = new ContentValues(2);
		values.put(MediaStore.Video.VideoColumns.LATITUDE, location.getLatitude());
		values.put(MediaStore.Video.VideoColumns.LONGITUDE, location.getLongitude());
		
		
		
		geoLocUpdate(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 
					values, 
					MediaStore.Video.VideoColumns.DATA + " LIKE ?", new String[] { capturedVideoFile.getAbsolutePath() }, 
					capturedVideoFile.getAbsolutePath(), capturedThumbFile.getAbsolutePath());

	}
	
	public void geoLocUpdate(Uri uri, ContentValues values, String where, String[] selectionArgs, String vFilePath, String tFilePath)
	{
		Bundle args = new Bundle();
		args.putParcelable  ("URI", uri);
		args.putParcelable  ("VALUES", values);
		args.putString      ("WHERE", where);
		args.putStringArray ("SELECTION_ARGS", selectionArgs);
		args.putStringArray	("FILE_PATHS", new String[] { vFilePath, tFilePath });
		
		new AsyncUpdate().execute(args);
		
	}

	final class AsyncUpdate extends AsyncTask<Bundle, Void, VideoItem>
	{
		@Override
		protected VideoItem doInBackground(Bundle... params) {
			Bundle args = params[0];
			Uri             uri             = args.getParcelable("URI");
			ContentValues   values          = args.getParcelable("VALUES");
			String          where           = args.getString("WHERE");
			String[]        selectionArgs   = args.getStringArray("SELECTION_ARGS");
			String[]		fPaths			= args.getStringArray("FILE_PATHS");
			
			int rowsUpdated = 0;
			while (rowsUpdated != 1)
			{
				rowsUpdated = getContentResolver().update(uri, values, where, selectionArgs);
			}
			
			VideoItem tempVideoItem = new VideoItem();
			tempVideoItem.setVideoPath(fPaths[0]);
			tempVideoItem.setThumbPath(fPaths[1]);
			tempVideoItem.setvLat(values.getAsDouble(MediaStore.Video.VideoColumns.LATITUDE));
			tempVideoItem.setvLon(values.getAsDouble(MediaStore.Video.VideoColumns.LONGITUDE));
			String dropBoxUrl = DropBox.tryUploadSingleFile(tempVideoItem.getVideoPath(), mLoggedIn, mApi);
			if(dropBoxUrl != null)
			{
				tempVideoItem.setVideoURI(Uri.parse(dropBoxUrl));
			}
			else
			{
				return tempVideoItem;
			}
			try {
				tempVideoItem.setThumbBase64(Utils.imageFileToString(tempVideoItem.getThumbPath()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			updateGrapesServer(tempVideoItem);
			
			return tempVideoItem;
		}

		@Override
		protected void onPostExecute(VideoItem vItem)
		{
			
			Toast.makeText(MainActivity.this, "Video saved @ "+location.getLatitude()+ " : " +location.getLongitude(), Toast.LENGTH_SHORT).show();
			
		}
		
		private void updateGrapesServer(VideoItem vItem)
		{			
			String url = Grapes.backendUrl;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("action", "save"));
			nameValuePairs.add(new BasicNameValuePair("thumbnail", vItem.getThumbBase64()));
			nameValuePairs.add(new BasicNameValuePair("link", vItem.getVideoURI().toString()));
			nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(vItem.getvLat())));
			nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(vItem.getvLon())));

			HttpClient httpClient = new DefaultHttpClient();
			String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
			HttpGet httpGet = new HttpGet(url + "?" + paramsString);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				Log.v("response1",response.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpGet = new HttpGet(url + "?" + paramsString);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				Log.v("response2",response.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	
	@Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);
                String selection = MediaStore.Video.Media.DATA +" like ?";
		        String[] selectionArgs = new String[]{"%"+getString(R.string.app_name)+"%"+Grapes.appVideoDirName+"%"};
		        String[] projection = new String[]{
		        		MediaStore.Video.VideoColumns.DATA,
		        		MediaStore.Video.VideoColumns.LATITUDE,
		        		MediaStore.Video.VideoColumns.LONGITUDE
				};
		        
		        Cursor videoCursor =  MainActivity.this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
		                projection, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
		        
                DropBox.setLoggedIn(true, mApi, videoCursor);
                this.mLoggedIn = true;
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(DropBox.TAG, "Error authenticating", e);
            }
        }
    }
	
	private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

	public static void sendShareToServer(VideoItem video){
		
	}
	
    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(DropBox.ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(DropBox.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(DropBox.ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(DropBox.ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(DropBox.ACCESS_KEY_NAME, "oauth2:");
            edit.putString(DropBox.ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        
    }

	private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(DropBox.ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(DropBox.APP_KEY, DropBox.APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }
}