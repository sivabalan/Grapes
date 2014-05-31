package com.fruitmill.grapes;

import java.io.File;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.fruitmill.grapes.adapter.VideoItem;
import com.fruitmill.grapes.adapter.VideoListAdapter;
import com.fruitmill.grapes.utils.CustomSwipeRefreshLayout;
import com.fruitmill.grapes.utils.Utils;

public class FeedFragment extends Fragment {

	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	private PullToRefresh swipeRefreshLayout;
	private BaseAdapter feedListAdapter;
	private View rootView;
	private ListView videoListView;
	private TextView lastUpdatedLabel;
	
	private class PullToRefresh extends CustomSwipeRefreshLayout {
		
		private MotionEvent mDownEvent;
				
		public PullToRefresh(Context context) {
			super(context);
		}
		
		@Override
		public boolean canChildScrollUp()
		{
			return videoListView.getFirstVisiblePosition() != 0;
		}

		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			final int action = event.getAction();
	        
	        switch (action) {
	            case MotionEvent.ACTION_DOWN:
	            	mDownEvent = MotionEvent.obtain(event);
	            	break;
	            case MotionEvent.ACTION_MOVE:
	            	
	            	if(mDownEvent != null)
	            	{
	            		 final float eventY = event.getY();
	                     float yDiff = eventY - mDownEvent.getY();
	                     if(yDiff > lastUpdatedLabel.getMeasuredHeight() && lastUpdatedLabel.getText() == "")
	                     {
	                    	 lastUpdatedLabel.setText(Utils.getTimeSinceLastUpdate("updated"));
	                     }
	            	}
	            	break;
	        }
			return super.onTouchEvent(event);
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
                
        swipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
			
        	@Override
        	public void onRefresh() {
        		fetchVideos();
        	}
		});
        
        swipeRefreshLayout.postAnimationHook =  new Runnable() {

			@Override
			public void run() {
				lastUpdatedLabel.setText("");
			}
        	
        };

		videoListView = (ListView) rootView.findViewById(R.id.feedListView);
		
		videoListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				boolean isCameraIconVisible = scrollState == SCROLL_STATE_IDLE ? true : false;
				Utils.toggleCameraIcon(isCameraIconVisible);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			}
		});
		
		if(MainActivity.feedVideoList == null || Utils.isFeedUpdateNecessary())
		{
			fetchVideos();
		}
		else
		{
			feedListAdapter = new VideoListAdapter(MainActivity.feedVideoList.size(), rootView.getContext(), MainActivity.feedVideoList, rootView, "feed");
			videoListView.setAdapter(feedListAdapter);
		}
        
		RelativeLayout feedLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_swipe_container, container, false);

		lastUpdatedLabel = (TextView) feedLayout.findViewById(R.id.lastUpdatedLabel);
		
		feedLayout.addView(swipeRefreshLayout, 0);
		
		return feedLayout;
	}
	
	public void fetchVideos() {
		if(MainActivity.feedVideoList == null || MainActivity.feedVideoList.size() < 1)
		{
			Utils.setAppStatusLabel(R.string.picking_grapes);
		}
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
						Utils.setAppStatusLabel(R.string.empty);
						swipeRefreshLayout.setRefreshing(false);
						Utils.updateLastUpdatedTime();
					}
					
				});
			}
		}.start();
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
