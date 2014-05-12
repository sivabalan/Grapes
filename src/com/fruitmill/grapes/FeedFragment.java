package com.fruitmill.grapes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.fruitmill.grapes.adapter.VideoItem;
import com.fruitmill.grapes.adapter.VideoListAdapter;

public class FeedFragment extends Fragment {

	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	private List<VideoItem> videoList;
	
	private String[] remoteVideoList = {
			"http://dl.dropboxusercontent.com/1/view/vrchc1ywv40lvbr/grapes-public/961572535",
			"http://dl.dropboxusercontent.com/1/view/qobsyrdhj9inssd/grapes-public/-1387871915",
			"http://dl.dropboxusercontent.com/1/view/v9salzyvfhqp1zt/grapes-public/1558049880",
			"http://dl.dropboxusercontent.com/1/view/g8uicrz2jy9pmv6/grapes-public/1978224610",
			"http://dl.dropboxusercontent.com/1/view/vkys6rlj7xjdfzl/grapes-public/133635181"
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
		
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
		
		videoList = fetchVideos(rootView.getContext());
		
		ListView videoListView = (ListView) rootView.findViewById(R.id.feedListView);
        
        videoListView.setAdapter(new VideoListAdapter(videoList.size(), rootView.getContext(), videoList, rootView));
		
		return rootView;
	}
	
	private List<VideoItem> fetchVideos(Context vContext) { 
		VideoItem vItem;
		List<VideoItem> localVideoList = new ArrayList<VideoItem>();
		
		for(int i=0;i<remoteVideoList.length;i++)
		{
			vItem = new VideoItem();
			vItem.setVideoURI(Uri.parse(remoteVideoList[i]));
			vItem.setvThumbnail(ThumbnailUtils.createVideoThumbnail(remoteVideoList[i], Thumbnails.MINI_KIND));
			//Toast.makeText(vContext, remoteVideoList[i], Toast.LENGTH_SHORT).show();
			localVideoList.add(vItem);
		}
		return localVideoList;
	}
}
