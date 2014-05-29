package com.fruitmill.grapes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.fruitmill.grapes.adapter.VideoItem;
import com.fruitmill.grapes.adapter.VideoListAdapter;
import com.fruitmill.grapes.utils.Utils;

public class FeedFragment extends Fragment implements OnRefreshListener {

	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	private PullToRefresh swipeRefreshLayout;
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
	
	private class PullToRefresh extends SwipeRefreshLayout {
		public PullToRefresh(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean canChildScrollUp()
		{
			return videoListView.getFirstVisiblePosition() != 0;
		}
	}
	
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
		
		swipeRefreshLayout = new PullToRefresh(rootView.getContext());
		swipeRefreshLayout.setBackgroundResource(R.color.dark_gray);
        swipeRefreshLayout.addView(rootView);
        swipeRefreshLayout.setColorScheme(R.color.purple1,R.color.purple2,R.color.purple3,R.color.purple4);
        swipeRefreshLayout.setOnRefreshListener(this);

		videoListView = (ListView) rootView.findViewById(R.id.feedListView);
		
		if(MainActivity.feedVideoList == null || Utils.isFeedUpdateNecessary())
		{
			fetchVideos();
		}
		else
		{
			feedListAdapter = new VideoListAdapter(MainActivity.feedVideoList.size(), rootView.getContext(), MainActivity.feedVideoList, rootView, "feed");
			videoListView.setAdapter(feedListAdapter);
		}
        
		return swipeRefreshLayout;
	}
	
	public void fetchVideos() {
		
		swipeRefreshLayout.setRefreshing(true);
		if(MainActivity.location == null)
		{
			Toast.makeText(getActivity(), "Location can't be determined", Toast.LENGTH_SHORT).show();
			return;
		}
		
		MainActivity.prevLocation = new Location(MainActivity.location);
		
		new Thread() {
			public void run() {
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "query"));
				nameValuePairs.add(new BasicNameValuePair("maxd", Integer.toString(Grapes.videoRadius)));
				nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(MainActivity.location.getLatitude())));
				nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(MainActivity.location.getLongitude())));
				nameValuePairs.add(new BasicNameValuePair("vc", Integer.toString(Grapes.videoFetchCount)));
			
				MainActivity.feedVideoList = (Utils.isOnline(getActivity()))? Utils.doGrapesQuery(nameValuePairs) : fetchCachedVideos(getActivity());
				
				
				if(MainActivity.feedVideoList == null)
				{
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(rootView.getContext(), "Error connecting to Grapes backend. Please try refreshing by swiping down.", Toast.LENGTH_LONG).show();
							swipeRefreshLayout.setRefreshing(false);
						}
					});
					return;
				}
				
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						feedListAdapter = new VideoListAdapter(MainActivity.feedVideoList.size(), rootView.getContext(), MainActivity.feedVideoList, rootView, "feed");
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
	
	
	private List<VideoItem> fetchCachedVideos(Context vContext) {
		String selection = MediaStore.Video.Media.DATA +" like ?";
        String[] selectionArgs = new String[]{"%"+getString(R.string.app_name)+"%"+Grapes.appCachedVideoDirName+"%"};
        String[] projection = new String[]{
        		MediaStore.Video.VideoColumns.DATA,
        		MediaStore.Video.VideoColumns._ID,
        		MediaStore.Video.VideoColumns.LATITUDE,
        		MediaStore.Video.VideoColumns.LONGITUDE
		};
        Cursor videoCursor = vContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
        int count = videoCursor.getCount();
        VideoItem vItem;
        String thumbName;
        List<VideoItem> localVideoList = new ArrayList<VideoItem>();
        for(int i=0;i<count;i++)
		{
			vItem = new VideoItem();
	        int videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
	        videoCursor.moveToPosition(i);
			vItem.setVideoPath(videoCursor.getString(videoColumnIndex));
			
			thumbName = vItem.getVideoPath();
			int pos1 = thumbName.lastIndexOf(File.separator);
			int pos2 = thumbName.lastIndexOf(".");
			if (pos2 > 0) {
				thumbName = thumbName.substring(pos1, pos2);
			}
			
	        videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
	        videoCursor.moveToPosition(i);
	        vItem.setVideoURI(Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()+"/"+videoCursor.getString(videoColumnIndex)));
	        vItem.setvThumbnail(BitmapFactory.decodeFile(Grapes.appCachedThumbsDir.getAbsolutePath()+File.separator+thumbName+".png"));
	        localVideoList.add(vItem);
		}
        
        Collections.sort(localVideoList, new Comparator<VideoItem>(){
        	@Override
            public int compare(final VideoItem object1, final VideoItem object2) {
               return (int) (object1.getDisFromCurrentLocation() - object2.getDisFromCurrentLocation());
            }
        });
        
        return localVideoList;
	}
}
