package com.fruitmill.grapes.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VideoListAdapter extends BaseAdapter {
	
	private int count;
	private Context vContext;
	private int video_column_index;
	private Cursor vCursor;
	
	public VideoListAdapter(int count, Context vContext, Cursor vCursor) {
		super();
		this.count = count;
		this.vContext = vContext;
		this.vCursor = vCursor;
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
		TextView tv = new TextView(vContext.getApplicationContext());
        String id = null;
        if (convertView == null) {
            video_column_index = vCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            vCursor.moveToPosition(position);
            id = vCursor.getString(video_column_index);
            video_column_index = vCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            vCursor.moveToPosition(position);
            id += " Size(KB):" + vCursor.getString(video_column_index);
            tv.setText(id);
        } else
            tv = (TextView) convertView;
		return tv;
	}

}
