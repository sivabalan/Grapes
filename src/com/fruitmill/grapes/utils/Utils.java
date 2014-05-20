package com.fruitmill.grapes.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.fruitmill.grapes.Grapes;
import com.fruitmill.grapes.MainActivity;
import com.fruitmill.grapes.adapter.VideoItem;

public class Utils {
    
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
    
    public static List<VideoItem> doGrapesQuery(List<NameValuePair> params) {
    	List<VideoItem> localVideoList = null;
    	
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
}
