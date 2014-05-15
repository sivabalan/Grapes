package com.fruitmill.grapes.adapter;

import java.util.List;

import android.content.Context;
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
	private List<VideoItem> videoListMap;
	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	
	public VideoListAdapter(int count, Context vContext, List<VideoItem> videoList, View videoListView) {
		super();
		this.count = count;
		this.vContext = vContext;
		this.videoListMap = videoList;
		this.vpVideoView = (VideoView) videoListView.findViewById(R.id.vp_video_view);
		this.vpVideoFrame = (FrameLayout) videoListView.findViewById(R.id.vp_video_frame);
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
				vpVideoView.setVideoURI(videoListMap.get(itemPosition).getVideoURI());
				vpVideoView.start();
			}
		});
        
        TextView thumbnailLabel = (TextView) videoItemRow.findViewById(R.id.vi_geo);
        thumbnailLabel.setText(Double.toString(videoListMap.get(position).getvLat())+" : "+
        		Double.toString(videoListMap.get(position).getvLon())+" : "+videoListMap.get(position).getVideoURI()); 
        
		return videoItemRow;
	}

	
	
}
