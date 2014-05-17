package com.fruitmill.grapes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import android.widget.VideoView;

import com.fruitmill.grapes.adapter.VideoItem;
import com.fruitmill.grapes.adapter.VideoListAdapter;

public class FeedFragment extends Fragment {

	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	private List<VideoItem> videoList;
	
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
