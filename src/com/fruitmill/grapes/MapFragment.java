package com.fruitmill.grapes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MapFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		
		FragmentManager fgm = getActivity().getSupportFragmentManager();
		SupportMapFragment temp = (SupportMapFragment) fgm.findFragmentById(R.id.gMap);
		GoogleMap googleMap = temp.getMap();
		
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(MainActivity.location.getLatitude(), MainActivity.location.getLongitude())).zoom(12).build();
 
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		// create marker
		MarkerOptions marker = new MarkerOptions().position(new LatLng(MainActivity.location.getLatitude(), MainActivity.location.getLongitude())).title("You are here!");
		
		// adding marker
		Marker locationMarker = googleMap.addMarker(marker);
		locationMarker.showInfoWindow();
		
		return rootView;
	}

}
