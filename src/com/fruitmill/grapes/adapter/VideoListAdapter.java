package com.fruitmill.grapes.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.fruitmill.grapes.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class VideoListAdapter extends BaseAdapter {
	
	private int count;
	private Context vContext;
	private List<VideoItem> videoListMap;
	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	
	public VideoListAdapter(int count, Context vContext, List<VideoItem> videoList, View videoListView) {
		super();
		//this.count = count;
		this.count = count+1;
		this.vContext = vContext;
		this.videoListMap = videoList;
		this.videoListMap.add(0,new VideoItem());
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
        
        LayoutInflater inflater = (LayoutInflater) vContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        if(position == 0)
        {
        	videoItemRow = inflater.inflate(R.layout.list_video_item_empty, parent, false);
        	return videoItemRow;
        }
        
        final int itemPosition = position;
        if (videoItemRow == null) {
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
        
        TextView thumbnailLabel = (TextView) videoItemRow.findViewById(R.id.vi_place);
        
        VideoItem cItem = videoListMap.get(position);
        
//        try {
//        	
//			thumbnailLabel.setText(Double.toString(cItem.getvLat())+" : "+
//					Double.toString(cItem.getvLon())+" : "+cItem.getVideoURI());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			Log.v("Error position",Integer.toString(position));
//			e.printStackTrace();
//		} 
        
        new ReverseGeocoderTask().execute(vContext, thumbnailLabel, cItem.getvLat(), cItem.getvLon());
        
		return videoItemRow;
	}

	// An AsyncTask class for accessing the GeoCoding Web Service
	private class ReverseGeocoderTask extends AsyncTask<Object, Void, List<Object>>{

		@Override
		protected List<Object> doInBackground(Object... params) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder((Context) params[0]);
			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocation((Double) params[2], (Double) params[3], 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<Object> tempList = new ArrayList<Object>();
			tempList.add(params[0]);
			tempList.add(params[1]);
			tempList.add(addresses.get(0));
			return tempList;
		}

		@Override
		protected void onPostExecute(List<Object> params) {
			
			Context context = (Context) params.get(0);
			TextView placeLabel = (TextView) params.get(1);
			Address address = (Address) params.get(2);
			
			if(address==null){
				//Toast.makeText(context, "No Location found", Toast.LENGTH_SHORT).show();
				placeLabel.setText(Double.toString(address.getLatitude())+" : "+
						Double.toString(address.getLongitude()));
			}
			else
			{
				placeLabel.setText(address.getAddressLine(0));
			}
			
		}
	}
	
}
