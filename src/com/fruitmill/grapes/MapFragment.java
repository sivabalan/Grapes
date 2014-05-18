package com.fruitmill.grapes;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment {
	
	private GoogleMap googleMap;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		
		FragmentManager fgm = getActivity().getSupportFragmentManager();
		SupportMapFragment temp = (SupportMapFragment) fgm.findFragmentById(R.id.gMap);
		googleMap = temp.getMap();
		
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(MainActivity.location.getLatitude(), MainActivity.location.getLongitude())).zoom(12).build();
 
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		// create marker
		MarkerOptions marker = new MarkerOptions().position(new LatLng(MainActivity.location.getLatitude(), MainActivity.location.getLongitude())).title("You are here!");
		
		// adding marker
		Marker locationMarker = googleMap.addMarker(marker);
		locationMarker.showInfoWindow();
		
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.setMyLocationEnabled(true);
		
		return rootView;
	}
	
	public void findPlaces(String location) {
		new GeocoderTask().execute(location);
	}
	
	// An AsyncTask class for accessing the GeoCoding Web Service
	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getActivity());
			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses) {

			if(addresses==null || addresses.size()==0){
				Toast.makeText(getActivity(), "No Location found", Toast.LENGTH_SHORT).show();
			}

			// Clears all the existing markers on the map
			googleMap.clear();

			// Adding Markers on Google Map for each matching address
			LatLng latLng;
			MarkerOptions markerOptions;
			for(int i=0;i<addresses.size();i++){

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				latLng = new LatLng(address.getLatitude(), address.getLongitude());

				String addressText = String.format("%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
								address.getCountryName());

				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(addressText);

				googleMap.addMarker(markerOptions);

				// Locate the first location
				if(i==0)
					googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
			}
		}
	}

}
