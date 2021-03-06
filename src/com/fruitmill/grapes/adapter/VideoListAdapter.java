package com.fruitmill.grapes.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.fruitmill.grapes.R;
import com.fruitmill.grapes.utils.Utils;

public class VideoListAdapter extends BaseAdapter {
	
	private int count;
	private Context vContext;
	private List<VideoItem> videoListMap;
	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	private String callingFragment;
	
	public VideoListAdapter(int count, Context vContext, List<VideoItem> videoList, View videoListView, String fragmentType) {
		super();
		//this.count = count;
		this.count = count+1;
		this.vContext = vContext;
		this.videoListMap = new ArrayList<VideoItem>();
		this.videoListMap.add(0,new VideoItem());
		this.videoListMap.addAll(videoList);
		this.vpVideoView = (VideoView) videoListView.findViewById(R.id.vp_video_view);
		this.vpVideoFrame = (FrameLayout) videoListView.findViewById(R.id.vp_video_frame);
		this.callingFragment = fragmentType;
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
        final VideoItem cItem = videoListMap.get(position);
        
        LayoutInflater inflater = (LayoutInflater) vContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        if(position == 0)
        {
        	videoItemRow = inflater.inflate(R.layout.list_video_item_empty, parent, false);
        	return videoItemRow;
        }
        
        final int itemPosition = position;
        if (videoItemRow == null || videoItemRow.getHeight() == 0) {
        	if(callingFragment == "feed")
        	{
        		videoItemRow = 	inflater.inflate(R.layout.list_video_item, parent, false);
        	}
        	else
        	{
        		videoItemRow = 	inflater.inflate(R.layout.list_video_item_my_videos, parent, false);
        	}
        }
        
        if(callingFragment == "feed")
        {
        	final ImageButton vDownload = (ImageButton) videoItemRow.findViewById(R.id.vi_download);
        	vDownload.setOnClickListener(new OnClickListener() {

        		@Override
        		public void onClick(View v) {
        			Utils.saveVideoFromFeed(cItem, vDownload);
        		}
        	});

        	ImageButton vReport = (ImageButton) videoItemRow.findViewById(R.id.vi_report_video);
        	vReport.setOnClickListener(new OnClickListener() {

        		@Override
        		public void onClick(View v) {
        			Utils.reportVideo(cItem);
        		}
        	});

        	ImageButton vRateUp = (ImageButton) videoItemRow.findViewById(R.id.vi_thumbs_up);
        	vRateUp.setOnClickListener(new OnClickListener() {

        		@Override
        		public void onClick(View v) {
        			Utils.rateVideo(cItem, "up");
        		}
        	});

        	ImageButton vRateDown = (ImageButton) videoItemRow.findViewById(R.id.vi_thumbs_down);
        	vRateDown.setOnClickListener(new OnClickListener() {

        		@Override
        		public void onClick(View v) {
        			Utils.rateVideo(cItem, "down");
        		}
        	});
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
        if(cItem.getDisplayAddress() == "")
        {
        	new ReverseGeocoderTask().execute(vContext, thumbnailLabel, cItem.getvLat(), cItem.getvLon());
        }
        else
        {
        	thumbnailLabel.setText(cItem.getDisplayAddress());
        }
        
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
				return null;
			}
			
			List<Object> tempList = new ArrayList<Object>();
			tempList.add(params[0]);
			tempList.add(params[1]);
			tempList.add(addresses.get(0));
			return tempList;
		}

		@Override
		protected void onPostExecute(List<Object> params) {
			
			if(params != null)
			{
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
	
}
