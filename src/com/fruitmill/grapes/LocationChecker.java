package com.fruitmill.grapes;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class LocationChecker extends Activity {
	private LocationManager locationManager;
	private Criteria criteria;
	private String provider;
	private MyLocationListener locationListener;
	private Location location;
	
	public LocationChecker(LocationManager locManager) {
		// Get the location manager
		locationManager = locManager;
		// Define the criteria how to select the location provider
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);	
		
		criteria.setCostAllowed(false); 
		
		// get the best provider depending on the criteria
		provider = locationManager.getBestProvider(criteria, false);
	    
		// the last known location of this provider
		Location location = locationManager.getLastKnownLocation(provider);
		
		locationListener = new MyLocationListener();
		
		if (location != null) {
			locationListener.onLocationChanged(location);
		} else {
			// leads to the settings because there is no last known location
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
		// location updates: at least 1 meter and 200millsecs change
		//locationManager.requestLocationUpdates(provider, 200, 1, locationListener);
	}
	
	private class MyLocationListener implements LocationListener {
		
	  @Override
	  public void onLocationChanged(Location loc) {
		// Initialize the location fields
		  String op = "Latitude: "+String.valueOf(loc.getLatitude()) +
		  "Longitude: "+String.valueOf(loc.getLongitude());
		  
		  location = loc;
		  
		  Toast.makeText(getApplicationContext(),  "Location changed : "+op, Toast.LENGTH_SHORT).show();
	  }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
		  Toast.makeText(getApplicationContext(), provider + "'s status changed to "+status +"!",
			        Toast.LENGTH_SHORT).show();
	  }

	  @Override
	  public void onProviderEnabled(String provider) {
		  Toast.makeText(getApplicationContext(), "Provider " + provider + " enabled!",
	        Toast.LENGTH_SHORT).show();

	  }

	  @Override
	  public void onProviderDisabled(String provider) {
		  Toast.makeText(getApplicationContext(), "Provider " + provider + " disabled!",
	        Toast.LENGTH_SHORT).show();
	  }
   }
}
