package com.fruitmill.grapes;

import java.io.IOException;
import java.util.ArrayList;
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

import android.content.Context;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.VideoView;

import com.fruitmill.grapes.adapter.VideoItem;
import com.fruitmill.grapes.adapter.VideoListAdapter;
import com.fruitmill.grapes.utils.Utils;

public class FeedFragment extends Fragment implements OnRefreshListener {

	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	private List<VideoItem> videoList;
	private SwipeRefreshLayout swipeRefreshLayout;
	private BaseAdapter feedListAdapter;
	private View rootView;
	private ListView videoListView;
	
	private String[] remoteVideoList = {
			"https://www.dropbox.com/s/ny13me38k5ym2ay/955847023.mp4",
			"http://dl.dropboxusercontent.com/1/view/ny13me38k5ym2ay/grapes-public/955847023.mp4",
			"https://db.tt/fGGgbXK4",
			"https://db.tt/aQtJ5RME",
			"https://db.tt/7i9BkY6e",
			"http://dl.dropboxusercontent.com/1/view/vrchc1ywv40lvbr/grapes-public/961572535",
			"http://dl.dropboxusercontent.com/1/view/qobsyrdhj9inssd/grapes-public/-1387871915",
			"http://dl.dropboxusercontent.com/1/view/v9salzyvfhqp1zt/grapes-public/1558049880",
			"http://dl.dropboxusercontent.com/1/view/g8uicrz2jy9pmv6/grapes-public/1978224610",
			"http://dl.dropboxusercontent.com/1/view/vkys6rlj7xjdfzl/grapes-public/133635181"
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_feed, container, false);
		
		vpVideoView = (VideoView) rootView.findViewById(R.id.vp_video_view);
		vpVideoFrame = (FrameLayout) rootView.findViewById(R.id.vp_video_frame);
		vpVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				vpVideoFrame.setVisibility(View.INVISIBLE);
			}
		});

		vpVideoView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(vpVideoView.isPlaying())
					vpVideoView.pause();
				else
					vpVideoView.start();
				return false;
			}
		});
		
		swipeRefreshLayout = new SwipeRefreshLayout(rootView.getContext());
        swipeRefreshLayout.addView(rootView);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_light, 
        		android.R.color.holo_red_light, 
        		android.R.color.holo_green_light, 
        		android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(this);
		
		videoList = new ArrayList<VideoItem>();
		
		feedListAdapter = new VideoListAdapter(videoList.size(), rootView.getContext(), videoList, rootView);

		videoListView = (ListView) rootView.findViewById(R.id.feedListView);
        
        videoListView.setAdapter(feedListAdapter);
        
		fetchVideos();
        
		return swipeRefreshLayout;
	}
	
	public void fetchVideos() {
		swipeRefreshLayout.setRefreshing(true);
		new Thread() {
			public void run() {
				String url = Grapes.backendUrl;
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "query"));
				nameValuePairs.add(new BasicNameValuePair("maxd", Integer.toString(Grapes.videoRadius)));
				nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(MainActivity.location.getLatitude())));
				nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(MainActivity.location.getLongitude())));
				nameValuePairs.add(new BasicNameValuePair("vc", Integer.toString(Grapes.videoFetchCount)));

				HttpClient httpClient = new DefaultHttpClient();
				String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
				HttpGet httpGet = new HttpGet(url + "?" + paramsString);
				try {
					HttpResponse response = httpClient.execute(httpGet);
					Log.v("response_fetch1",response.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				httpGet = new HttpGet(url + "?" + paramsString);
				try {
					HttpResponse response = httpClient.execute(httpGet);
					Log.v("response_fetch2",response.toString());
					
					try {
						//JSONArray videoObjects = new JSONArray(EntityUtils.toString(response.getEntity()));
						JSONArray videoObjects = new JSONArray("[{\"rating\": 0.0, \"distance\": 0.0, \"lon\": 1.0, \"link\": \"http://im.not.ready\", \"lat\": 1.0, \"thumbnail\": \""+Grapes.sampleImageB64+"\"}]");//, {"rating": 0.0, "distance": 142198.891532703, "lon": -0.0002, "link": "http://i.m.not.ready", "lat": 0.2, "thumbnail": "ABCDG"}, {"rating": 0.0, "distance": 142207.60692903, "lon": -0.0003, "link": "http://i.m.n.ot.ready", "lat": 0.2, "thumbnail": "ABCD"}, {"rating": 0.0, "distance": 156899.568281358, "lon": 0.0, "link": "http://im.ready", "lat": 0.0, "thumbnail": "ABCDEF"}]');
						JSONObject jsonVideoItem = new JSONObject();
						VideoItem vItem = new VideoItem();
						videoList = new ArrayList<VideoItem>();
						for(int i=0; i<videoObjects.length(); i++)
						{
							jsonVideoItem = videoObjects.getJSONObject(i);
							vItem.setVideoURI(Uri.parse(jsonVideoItem.getString("link")));
							vItem.setRating(jsonVideoItem.getInt("rating"));
							vItem.setvLat(jsonVideoItem.getDouble("lat"));
							vItem.setvLon(jsonVideoItem.getDouble("lon"));
							vItem.setDisFromCurrentLocation(jsonVideoItem.getDouble("distance"));
							vItem.setvThumbnail(Utils.stringToImageFile(jsonVideoItem.getString("thumbnail")));
							
							videoList.add(vItem);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						feedListAdapter = new VideoListAdapter(videoList.size(), rootView.getContext(), videoList, rootView);
						videoListView.setAdapter(feedListAdapter);
						swipeRefreshLayout.setRefreshing(false);
						
					}
					
				});
			}
		}.start();
	}

	@Override
	public void onRefresh() {
		fetchVideos();
	}
	
	
	
//	private List<VideoItem> fetchLocalVideos(Context vContext) {
//		String selection = MediaStore.Video.Media.DATA +" like ?";
//        String[] selectionArgs = new String[]{"%"+getString(R.string.app_name)+"%"+Grapes.appVideoDirName+"%"};
//        String[] projection = new String[]{
////        		MediaStore.Video.Media.SIZE,
////        		MediaStore.Video.VideoColumns.DURATION,
////        		MediaStore.Video.VideoColumns.DATE_TAKEN,
////        		MediaStore.Video.VideoColumns.RESOLUTION,
////        		MediaStore.Video.VideoColumns.DISPLAY_NAME,
//        		MediaStore.Video.VideoColumns.DATA,
//        		MediaStore.Video.VideoColumns._ID
//		};
//        videoCursor = vContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                projection, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
//        int count = videoCursor.getCount();
//        VideoItem vItem;
//        String thumbName;
//        List<VideoItem> localVideoList = new ArrayList<VideoItem>();
//        for(int i=0;i<count;i++)
//		{
//			vItem = new VideoItem();
//	        videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//	        videoCursor.moveToPosition(i);
//			vItem.setVideoPath(videoCursor.getString(videoColumnIndex));
//			
//			thumbName = vItem.getVideoPath();
//			int pos1 = thumbName.lastIndexOf(File.separator);
//			int pos2 = thumbName.lastIndexOf(".");
//			if (pos2 > 0) {
//				thumbName = thumbName.substring(pos1, pos2);
//			}
//			
//	        videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
//	        videoCursor.moveToPosition(i);
//	        vItem.setVideoURI(Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()+"/"+videoCursor.getString(videoColumnIndex)));
//	        vItem.setvThumbnail(BitmapFactory.decodeFile(Grapes.appThumbsDir.getAbsolutePath()+File.separator+thumbName+".png"));
//	        localVideoList.add(vItem);
//		}
//        
//        return localVideoList;
//	}
}
