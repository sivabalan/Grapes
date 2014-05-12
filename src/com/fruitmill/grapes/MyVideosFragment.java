package com.fruitmill.grapes;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.VideoView;

public class MyVideosFragment extends Fragment {
	
	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_my_videos, container, false);
		
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
		return rootView;
	}

	
	
}
