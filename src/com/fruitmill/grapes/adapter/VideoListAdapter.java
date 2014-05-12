package com.fruitmill.grapes.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.fruitmill.grapes.R;

public class VideoListAdapter extends BaseAdapter {
	
	private int count;
	private Context vContext;
	private int videoColumnIndex;
	private Cursor vCursor;
	private List<VideoItem> videoListMap;
	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	private VideoItem vItem;
	
	public VideoListAdapter(int count, Context vContext, Cursor vCursor, Activity parent) {
		super();
		this.count = count;
		this.vContext = vContext;
		this.vCursor = vCursor;
		this.videoListMap = new ArrayList<>();
		this.vpVideoView = (VideoView) parent.findViewById(R.id.vp_video_view);
		this.vpVideoFrame = (FrameLayout) parent.findViewById(R.id.vp_video_frame);
		
		populateVideoList(vCursor);
	}
	
	private void populateVideoList(Cursor vc) {
		
		for(int i=0;i<count;i++)
		{
			vItem = new VideoItem();
			videoColumnIndex = vCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			vCursor.moveToPosition(i);
	        vItem.setVideoPath(vCursor.getString(videoColumnIndex));
	        videoColumnIndex = vCursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION);
	        vCursor.moveToPosition(i);
	        vItem.setvLon(vCursor.getString(videoColumnIndex));
	        vItem.setvThumbnail(ThumbnailUtils.createVideoThumbnail(vItem.getVideoPath(), Thumbnails.FULL_SCREEN_KIND));
	        videoListMap.add(vItem);
		}
        
	}
	
	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View videoItemRow = convertView;
        final int itemPosition = position;
        if (videoItemRow == null) {
        	LayoutInflater inflater = (LayoutInflater) vContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        	videoItemRow = 	inflater.inflate(R.layout.list_video_item, parent, false);
        }
        
        ImageView imageThumbnail = (ImageView) videoItemRow.findViewById(R.id.vi_thumbnail);
        imageThumbnail.setImageBitmap(videoListMap.get(position).getvThumbnail());
        
        imageThumbnail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vpVideoFrame.setVisibility(View.VISIBLE);
				vpVideoView.setVideoPath(videoListMap.get(itemPosition).getVideoPath());
				vpVideoView.start();
			}
		});
        
        TextView thumbnailLabel = (TextView) videoItemRow.findViewById(R.id.vi_geo);
        thumbnailLabel.setText(videoListMap.get(position).getvLon()); 
        
		return videoItemRow;
	}

	
	
}
