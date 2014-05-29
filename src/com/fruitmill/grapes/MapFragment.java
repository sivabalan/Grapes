package com.fruitmill.grapes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.fruitmill.grapes.adapter.MapSearchAdapter;
import com.fruitmill.grapes.adapter.VideoItem;
import com.fruitmill.grapes.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;


public class MapFragment extends Fragment {
	
	private VideoView vpVideoView;
	private FrameLayout vpVideoFrame;
	
	private GoogleMap googleMap;
	
	public Location getMapLocation() {
		return this.googleMap.getMyLocation();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		
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
		
		FragmentManager fgm = getActivity().getSupportFragmentManager();
		SupportMapFragment temp = (SupportMapFragment) fgm.findFragmentById(R.id.gMap);
		googleMap = temp.getMap();
		
		Location tempLocation = null;
		
		if(MainActivity.location != null)
		{
			Log.v("Location Thing","Got from MainActivity");
			tempLocation = MainActivity.location;
		}
//		else
//		{
//			Log.v("Location Thing","Got from map");
//			tempLocation = googleMap.getMyLocation();			
//		}
		
		if(tempLocation != null)
		{
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
	                new LatLng(tempLocation.getLatitude(), tempLocation.getLongitude())).zoom(12).build();
	 
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
			// create marker
			MarkerOptions marker = new MarkerOptions().position(new LatLng(tempLocation.getLatitude(), tempLocation.getLongitude())).title("You are here!");
			
			// adding marker
			Marker locationMarker = googleMap.addMarker(marker);
			locationMarker.showInfoWindow();
		}
		
		
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.setMyLocationEnabled(true);
		
		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			
			private float currentZoom = -1;
			private double currentVisibleRadius = -1; 
			private Location currentLocation = null;
			
		    @Override
		    public void onCameraChange(CameraPosition pos) {
		    	
		    	boolean fetchAdditionalMarkers = false;
		    	
		    	Location location = new Location("Test");
				location.setLatitude(pos.target.latitude);
				location.setLongitude(pos.target.longitude);
				location.setTime(new Date().getTime());
				
				if(currentLocation == null)
				{
					currentLocation = new Location(location);
				}
				
				if(currentLocation.distanceTo(location) > Grapes.minMapDistBeforeFetchingMarkers)
				{
					fetchAdditionalMarkers = true;
				}
				currentLocation = new Location(location);
		    	
				
		        if (pos.zoom != currentZoom){
		            
		        	double newVisibleRadius = getVisibleMapRadius();
		        	
		        	if(newVisibleRadius - currentVisibleRadius > Grapes.minMapDistBeforeFetchingMarkers)
		        	{
		        		fetchAdditionalMarkers = true;
		        	}
		        	currentVisibleRadius = newVisibleRadius;
		        	currentZoom = pos.zoom;
		            
		            //Log.v("zoomLevel",Double.toString(currentZoom));
		        }
		        
		        if(fetchAdditionalMarkers && MainActivity.location != null)
		        {
		        	showAllVideosOnMapView();
		        }
		    }
		});
		
		
		
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				if(marker.getTitle() != null)
				{
					vpVideoFrame.setVisibility(View.VISIBLE);
					vpVideoView.setVideoURI(Uri.parse(marker.getTitle()));
					vpVideoView.start();
				}
				return true;
			}
		});
		
		return rootView;
	}
	
	
	public double getVisibleMapRadius() {
		VisibleRegion vr = googleMap.getProjection().getVisibleRegion();
		double left = vr.latLngBounds.southwest.longitude;
		double top = vr.latLngBounds.northeast.latitude;
		double right = vr.latLngBounds.northeast.longitude;
		double bottom = vr.latLngBounds.southwest.latitude;

		Location center = new Location("center");
		center.setLatitude( vr.latLngBounds.getCenter().latitude);
		center.setLongitude( vr.latLngBounds.getCenter().longitude);
		Location MiddleLeftCornerLocation = new Location(center); //(center's latitude,vr.latLngBounds.southwest.longitude)
		MiddleLeftCornerLocation.setLongitude(left);
		return center.distanceTo(MiddleLeftCornerLocation); //calculate distance between middleLeftcorner and center
	}
	
	public void findPlaces(String location) {
		new GeocoderTask().execute(location, 1);
	}
	
	public void loadSuggestions(String location) {
		new GeocoderTask().execute(location, 3);
	}
	
	// An AsyncTask class for accessing the GeoCoding Web Service
	private class GeocoderTask extends AsyncTask<Object, Void, List<Object>>{

		@Override
		protected List<Object> doInBackground(Object... params) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getActivity());
			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName((String)params[0], (Integer)params[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Object> tempList = new ArrayList<Object>();
			tempList.add(params[0]);
			tempList.add(params[1]);
			tempList.add(addresses);
			return tempList;
		}

		@Override
		protected void onPostExecute(List<Object> params) {
			
			boolean isSuggestion = (Integer) params.get(1) > 1 ? true : false;
			List<Address> addresses = (ArrayList<Address>) params.get(2);
			if(addresses==null || addresses.size()==0){
				Toast.makeText(getActivity(), "No Location found", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(isSuggestion) 
			{
				String[] columns = new String[] { "_id", "address", "latitude", "longitude" };
		        Object[] temp = new Object[] { 0, "default", 0.0, 0.0 };
		 
		        MatrixCursor cursor = new MatrixCursor(columns);
		        String addressText;
		        Address address;
		        for(int i = 0; i < addresses.size(); i++) {
		 
		            temp[0] = i;
		            address = addresses.get(i);
		            addressText = String.format("%s %s",
		            		address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0)+"," : "",
		    						address.getCountryName());
		            temp[1] = addressText;
		            temp[2] = address.getLatitude();
		            temp[3] = address.getLongitude();
		 
		            cursor.addRow(temp);
		 
		        }
		        
		        MainActivity.searchView.setSuggestionsAdapter(new MapSearchAdapter(getActivity(), cursor, addresses));
			}
			else
			{
				showNearbyVideosOnMap(addresses.get(0));
			}
			
		}
	}

	public void showNearbyVideosOnMap(Address address) {
		// Clears all the existing markers on the map
		googleMap.clear();

		// Adding Markers on Google Map for each matching address
		LatLng latLng;
		MarkerOptions markerOptions;
		

		// Creating an instance of GeoPoint, to display in Google Map
		latLng = new LatLng(address.getLatitude(), address.getLongitude());

		String addressText = String.format("%s %s",
				address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0)+"," : "",
						address.getCountryName());

		markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.title(addressText);
		

		googleMap.addMarker(markerOptions);

		// Locate the first location
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		
		if(MainActivity.location != null)
		{
			showAllVideosOnMapView();
		}
		
	}
	
	
	public void showAllVideosOnMapView() {
		
		final double videoRadius = getVisibleMapRadius();
		
	 	new Thread() {
			public void run() {
								
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("action", "query"));
				nameValuePairs.add(new BasicNameValuePair("maxd", Double.toString(videoRadius)));
				nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(MainActivity.location.getLatitude())));
				nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(MainActivity.location.getLongitude())));
				nameValuePairs.add(new BasicNameValuePair("vc", "1000"));

				final List<VideoItem> videoList = Utils.doGrapesQuery(nameValuePairs);
				
				if(videoList == null)
				{
					return;
				}
				
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						renderGrapesMarkers(videoList);
					}
					
				});
				
			}
		}.start();
	}
	
	public void renderGrapesMarkers(List<VideoItem> videoList) {
		LatLng latLng;
		MarkerOptions markerOptions;
		VideoItem vItem;
		
		for(int i = 0; i < videoList.size(); i++)
		{
			vItem = (VideoItem) videoList.get(i);
			if(vItem.getVideoURI() != null)
			{
				latLng = new LatLng(vItem.getvLat(), vItem.getvLon());
				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_grapes));
				markerOptions.title(vItem.getVideoURI().toString());
				
				googleMap.addMarker(markerOptions);
			}
		}
	}
	
	
}
