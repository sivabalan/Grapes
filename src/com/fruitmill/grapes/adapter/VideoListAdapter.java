package com.fruitmill.grapes.adapter;

import java.util.HashMap;

import com.fruitmill.grapes.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoListAdapter extends BaseAdapter {
	
	private int count;
	private Context vContext;
	private int videoColumnIndex;
	private Cursor vCursor;
	private String videoFilePath;
	private HashMap<String,Bitmap> thumbnailMap;
	private Bitmap bmThumbnail;
	
	public VideoListAdapter(int count, Context vContext, Cursor vCursor) {
		super();
		this.count = count;
		this.vContext = vContext;
		this.vCursor = vCursor;
		this.thumbnailMap = new HashMap<String,Bitmap>();
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
        if (videoItemRow == null) {
        	LayoutInflater inflater = (LayoutInflater) vContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        	videoItemRow = 	inflater.inflate(R.layout.list_video_item_vview, parent, false);
        }
        videoColumnIndex = vCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        vCursor.moveToPosition(position);
        videoFilePath  = vCursor.getString(videoColumnIndex);

        VideoView vThumbnail = (VideoView) videoItemRow.findViewById(R.id.vi_thumbnail);
        vThumbnail.setVideoPath(videoFilePath);
        
        vThumbnail.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((VideoView) v).start();
				return false;
			}
		});
		
        //vThumbnail.start();
//        ImageView imageThumbnail = (ImageView) videoItemRow.findViewById(R.id.vi_thumbnail);
//        if(thumbnailMap.containsKey(videoFilePath))
//        {
//        	bmThumbnail = thumbnailMap.get(videoFilePath);
//        }
//        else 
//        {
//        	bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoFilePath, Thumbnails.FULL_SCREEN_KIND);
//        	thumbnailMap.put(videoFilePath, bmThumbnail);
//        }
//        imageThumbnail.setImageBitmap(bmThumbnail);
        
        //TextView thumbnailLabel = (TextView) videoItemRow.findViewById(R.id.vi_fileName);
        //thumbnailLabel.setText(videoFilePath); 
        
		return videoItemRow;
	}

}
