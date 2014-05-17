package com.fruitmill.grapes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fruitmill.grapes.adapter.VideoItem;
import com.fruitmill.grapes.adapter.VideoListAdapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.VideoView;

public class MyVideosFragment extends Fragment {
	
	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	private Cursor videoCursor;
	private int videoColumnIndex;
	private List<VideoItem> videoList;
	
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
		
		videoList = fetchLocalVideos(rootView.getContext());
		
		ListView videoListView = (ListView) rootView.findViewById(R.id.myVideosListView);
        
        videoListView.setAdapter(new VideoListAdapter(videoList.size(), rootView.getContext(), videoList, rootView));
        
		return rootView;
	}

	private List<VideoItem> fetchLocalVideos(Context vContext) {
		String selection = MediaStore.Video.Media.DATA +" like ?";
        String[] selectionArgs = new String[]{"%"+getString(R.string.app_name)+"%"+Grapes.appVideoDirName+"%"};
        String[] projection = new String[]{
//        		MediaStore.Video.Media.SIZE,
//        		MediaStore.Video.VideoColumns.DURATION,
//        		MediaStore.Video.VideoColumns.DATE_TAKEN,
//        		MediaStore.Video.VideoColumns.RESOLUTION,
//        		MediaStore.Video.VideoColumns.DISPLAY_NAME,
        		MediaStore.Video.VideoColumns.DATA,
        		MediaStore.Video.VideoColumns._ID,
        		MediaStore.Video.VideoColumns.LATITUDE,
        		MediaStore.Video.VideoColumns.LONGITUDE
		};
        videoCursor = vContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
        int count = videoCursor.getCount();
        VideoItem vItem;
        String thumbName;
        List<VideoItem> localVideoList = new ArrayList<VideoItem>();
        for(int i=0;i<count;i++)
		{
			vItem = new VideoItem();
	        videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
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
	        vItem.setvThumbnail(BitmapFactory.decodeFile(Grapes.appThumbsDir.getAbsolutePath()+File.separator+thumbName+".png"));
	        videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.LONGITUDE);
	        videoCursor.moveToPosition(i);
	        vItem.setvLon(videoCursor.getDouble(videoColumnIndex));
	        videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.LATITUDE);
	        videoCursor.moveToPosition(i);
	        vItem.setvLat(videoCursor.getDouble(videoColumnIndex));
	        localVideoList.add(vItem);
		}
        
        return localVideoList;
	}
	
	
}
