package com.fruitmill.grapes.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.fruitmill.grapes.Grapes;
import com.fruitmill.grapes.MainActivity;
import com.fruitmill.grapes.R;
import com.fruitmill.grapes.adapter.VideoItem;

public class Utils {
	
	private static boolean isVideoSaved = true;
	
    public static String imageFileToString(String imgFilePath) throws IOException {
    	Bitmap bm = BitmapFactory.decodeFile(imgFilePath);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    	bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
    	byte[] b = baos.toByteArray();
    	
    	return Base64.encodeToString(b, Base64.DEFAULT);
    }
    
    public static Bitmap stringToImageFile(String b64ImageString) {
    	byte[] decodedString = Base64.decode(b64ImageString, Base64.DEFAULT);
    	Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    	return decodedImage; 
    }

	public static boolean isOnline(Activity act) {
    	
        ConnectivityManager conMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }
    
	public static List<NameValuePair> attachDeviceId(List<NameValuePair> params) {
		params.add(new BasicNameValuePair("device_id", MainActivity.deviceId));
		return params;
	}
	
    public static List<VideoItem> doGrapesQuery(List<NameValuePair> params) {
    	List<VideoItem> localVideoList = null;
    	params = attachDeviceId(params);
    	String url = Grapes.backendUrl;
    	HttpClient httpClient = new DefaultHttpClient();
		String paramsString = URLEncodedUtils.format(params, "UTF-8");
		
		HttpGet httpGet = new HttpGet(url + "?" + paramsString);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());
			//Log.v("response_fetch2",responseString);
			
			try {
				JSONArray videoObjects = new JSONArray(responseString);
				//JSONArray videoObjects = new JSONArray("[{\"rating\": 0.0, \"distance\": 0.0, \"lon\": 1.0, \"link\": \"http://im.not.ready\", \"lat\": 1.0, \"thumbnail\": \""+Grapes.sampleImageB64+"\"}]");//, {"rating": 0.0, "distance": 142198.891532703, "lon": -0.0002, "link": "http://i.m.not.ready", "lat": 0.2, "thumbnail": "ABCDG"}, {"rating": 0.0, "distance": 142207.60692903, "lon": -0.0003, "link": "http://i.m.n.ot.ready", "lat": 0.2, "thumbnail": "ABCD"}, {"rating": 0.0, "distance": 156899.568281358, "lon": 0.0, "link": "http://im.ready", "lat": 0.0, "thumbnail": "ABCDEF"}]');
				JSONObject jsonVideoItem = new JSONObject();
				VideoItem vItem;
				localVideoList = new ArrayList<VideoItem>();
				for(int i=0; i<videoObjects.length(); i++)
				{
					jsonVideoItem = videoObjects.getJSONObject(i);
					vItem = new VideoItem();
					vItem.setVideoURI(Uri.parse(jsonVideoItem.getString("link")));
					vItem.setRating(jsonVideoItem.getInt("rating"));
					vItem.setvLat(jsonVideoItem.getDouble("lat"));
					vItem.setvLon(jsonVideoItem.getDouble("lon"));
					vItem.setDisFromCurrentLocation(jsonVideoItem.getDouble("distance"));
					vItem.setvThumbnail(Utils.stringToImageFile(jsonVideoItem.getString("thumbnail")));
					vItem.setVideoID(jsonVideoItem.getString("video_id"));
					vItem.setDisplayAddress(jsonVideoItem.getString("address"));
					
					localVideoList.add(vItem);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
    	
    	return localVideoList;
    }
    
    public static String generateVideoID(String path) {
    	String videoId = addPadding(MainActivity.deviceId)+addPadding(Integer.toString(path.hashCode()));
    	return videoId;
    }
    
    public static String addPadding(String str) {
    	String paddedString = "";
    	int paddingLimit = 20 - str.length();
    	for(int i=0; i < paddingLimit; i++)
    	{
    		paddedString += "0";
    	}
    	return paddedString+str;
    }
    
    public static void videoAction(final VideoItem vItem, final String action) {
    	Thread t = new Thread() {
    		public void run() {

    			String url = Grapes.backendUrl;
    			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    			nameValuePairs.add(new BasicNameValuePair("action", action));
    			nameValuePairs.add(new BasicNameValuePair("video_id", vItem.getVideoID()));
    			nameValuePairs = Utils.attachDeviceId(nameValuePairs);
    			
    			String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
    			
    			HttpClient httpClient = new DefaultHttpClient();
    			
    			HttpGet httpGet = new HttpGet(url + "?" + paramsString);
    			
    			try {
    				HttpResponse response = httpClient.execute(httpGet);
    				Log.v("INFO: Report video",response.toString());
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	};
    	t.start();
    	return;
    }
    
    public static void reportVideo(VideoItem vItem) 
    {
    	videoAction(vItem, "report");
    }
    
    public static void rateVideo(VideoItem vItem, String rateType)
    {
    	videoAction(vItem, rateType);
    }
    
    public static void saveVideoFromFeed(final VideoItem vItem, ImageButton vDown) {
    	vDown.setImageResource(R.id.progress_circular);
    	isVideoSaved = true;
    	Thread t = new Thread() {
    		public void run()
    		{
    			isVideoSaved = saveOneVideo(vItem.getVideoURI().toString(), vItem.getvLat(), vItem.getvLon(), vItem.getvThumbnail());
    		}
    	};
    	t.start();
    	try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			isVideoSaved = false;
			e.printStackTrace();
		}
    	if(isVideoSaved)
    	{
    		vDown.setImageResource(R.drawable.ic_action_delete);
    		// Add listener for deleting cached video
    	}
    	else
    	{
    		vDown.setImageResource(R.drawable.ic_action_download);
    	}
    	//return isVideoSaved;
    }
    
    
	// Assuming it is not running on UI thread
    public static boolean saveOneVideo(final String link, final double lat, final double lon, final Bitmap thumb) {
    	//File videoData = new File(Uri.parse(link));
    	boolean hasDownloaded = true;
    	try {
    		URL url = new URL(link);
    		URLConnection connection = url.openConnection();
    		InputStream in = connection.getInputStream();
    		File cachedVideoFile = new File(Grapes.appCachedVideoDir, Integer.toString(link.hashCode()) + ".mp4");
    		FileOutputStream fos = new FileOutputStream(cachedVideoFile);
    		byte[] buf = new byte[512];
    		while (true) {
    			int len = in.read(buf);
    			if (len == -1) {
    				break;
    			}
    			fos.write(buf, 0, len);
    		}
    		in.close();
    		fos.flush();
    		fos.close();

    		File cachedThumbFile = new File(Grapes.appCachedThumbsDir, Integer.toString(link.hashCode()) + ".png");
    		try {
    			FileOutputStream fOut = new FileOutputStream(cachedThumbFile);

    			thumb.compress(Bitmap.CompressFormat.PNG, 85, fOut);
    			fOut.flush();
    			fOut.close();
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		
    		Grapes.appContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(cachedVideoFile)));
    		Grapes.appContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(cachedThumbFile)));

    		ContentValues values = new ContentValues(2);
    		values.put(MediaStore.Video.VideoColumns.LATITUDE, lat);
    		values.put(MediaStore.Video.VideoColumns.LONGITUDE, lon);

    		geoLocUpdate(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 
    				values, 
    				MediaStore.Video.VideoColumns.DATA + " LIKE ?", new String[] { cachedVideoFile.getAbsolutePath() }, 
    				cachedVideoFile.getAbsolutePath(), cachedThumbFile.getAbsolutePath());

    	} catch (MalformedURLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		hasDownloaded = false;
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		hasDownloaded = false;
    	}
		    
    	return hasDownloaded;
    }
    
    public static boolean isFeedUpdateNecessary() {
    	if(MainActivity.prevLocation == null)
    	{
    		return true;
    	}
    	if(MainActivity.location.distanceTo(MainActivity.prevLocation) > Grapes.feedUpdateThresholdDistance)
    	{
    		return true;
    	}
    	return false;
    }
    
	public static void geoLocUpdate(Uri uri, ContentValues values, String where, String[] selectionArgs, String vFilePath, String tFilePath)
	{
		Bundle args = new Bundle();
		args.putParcelable  ("URI", uri);
		args.putParcelable  ("VALUES", values);
		args.putString      ("WHERE", where);
		args.putStringArray ("SELECTION_ARGS", selectionArgs);
		args.putStringArray	("FILE_PATHS", new String[] { vFilePath, tFilePath });
		
		new Utils().new AsyncUpdate().execute(args);
		
	}

	final class AsyncUpdate extends AsyncTask<Bundle, Void, Integer>
	{
		@Override
		protected Integer doInBackground(Bundle... params) {
			Bundle args = params[0];
			Uri             uri             = args.getParcelable("URI");
			ContentValues   values          = args.getParcelable("VALUES");
			String          where           = args.getString("WHERE");
			String[]        selectionArgs   = args.getStringArray("SELECTION_ARGS");
			String[]		fPaths			= args.getStringArray("FILE_PATHS");
			
			int rowsUpdated = 0;
			while (rowsUpdated != 1)
			{
				rowsUpdated = Grapes.appContext.getContentResolver().update(uri, values, where, selectionArgs);
			}
			
			return 1;
		}

		@Override
		protected void onPostExecute(Integer flag)
		{
			
		}
		
	}
	
	public static void toggleCameraIcon(boolean toShow) {
		int visibility = toShow ? View.VISIBLE : View.INVISIBLE;
//		Animation animFadeOut = AnimationUtils.loadAnimation(Grapes.appContext, android.R.anim.fade_out);
//		Animation animFadeIn = AnimationUtils.loadAnimation(Grapes.appContext, android.R.anim.fade_in);
//		Animation toPlay = toShow ? animFadeIn : animFadeOut;
//		MainActivity.cameraIcon.setAnimation(toPlay);
		MainActivity.cameraIcon.setVisibility(visibility);
	}
	
	public static void setAppStatusLabel(int resId) {
		MainActivity.appStatus.setText(resId);
	}
	
	public static void updateLastUpdatedTime() {
		Date cDate = new Date();
		MainActivity.lastLocUpdated = cDate;
	}
	
	public static String getTimeSinceLastUpdate(String action) {
		Date cDate = new Date();
		long ms = (cDate.getTime() - MainActivity.lastLocUpdated.getTime());
		
		int SECOND = 1000;
		int MINUTE = 60 * SECOND;
		int HOUR = 60 * MINUTE;
		int DAY = 24 * HOUR;
		
		StringBuffer text = new StringBuffer("");
		String timeText = "";
		if (ms > DAY) {
			timeText = (ms / DAY) > 1 ? " days " : " day ";
			text.append(ms / DAY).append(timeText);
			ms %= DAY;
		}
		else if (ms > HOUR) {
			timeText = (ms / HOUR) > 1 ? " hours " : " hour ";
			text.append(ms / HOUR).append(timeText);
			ms %= HOUR;
		}
		else if (ms > MINUTE) {
			timeText = (ms / MINUTE) > 1 ? " minutes " : " minute ";
			text.append(ms / MINUTE).append(timeText);
			ms %= MINUTE;
		}
		else if (ms > SECOND) {
			timeText = (ms / SECOND) > 1 ? " seconds " : " second ";
			text.append(ms / SECOND).append(timeText);
			ms %= SECOND;
		}
		
		return "Last "+action+" "+text+ "ago.";
	}
	
	
}
