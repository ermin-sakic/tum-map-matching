package com.vmi.mapmatching_demo;


import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

	private static String DEBUG = "debug";
	private static Location lastLocation = null;

	@Override
	public void onLocationChanged(Location location) {
		if(lastLocation == null || (lastLocation.distanceTo(location)>50)){
			double latitude, longitude, altitude;
			float speed, bearing, accuracy;
			String provider = null;
			boolean hasBearing, hasSpeed, hasAccuracy, hasAltitude;

			OverlayMapViewer.counter++;

			latitude = location.getLatitude();
			longitude = location.getLongitude();
			provider = location.getProvider();

			hasBearing = location.hasBearing();
			hasSpeed = location.hasSpeed();
			hasAccuracy = location.hasAccuracy();
			hasAltitude = location.hasAltitude();

			String messageOnFix = "The " + provider + " Fix No.: " + OverlayMapViewer.counter + "\n" + "Latitude: " + latitude + "\nLongitude: " +
					longitude;

			if(hasBearing){
				bearing = location.getBearing();
				messageOnFix = messageOnFix + "\nBearing = " + bearing;
			}
			if(hasSpeed){
				speed = location.getSpeed();
				messageOnFix = messageOnFix + "\nSpeed = " + speed;
			}
			if(hasAccuracy){
				accuracy = location.getAccuracy();
				messageOnFix = messageOnFix + "\nAccuracy = " + accuracy;
			}
			if(hasAltitude){
				altitude = location.getAltitude();
				messageOnFix = messageOnFix + "\nAltitude = " + altitude;
			}

			Log.d(DEBUG, "Got a new location.");		

			GeoPoint newGeoPoint = new GeoPoint(latitude, longitude);
			OverlayItem newLocation = new OverlayItem(newGeoPoint, "Unmapped fix No." + OverlayMapViewer.counter, messageOnFix);

			while(OverlayMapViewer.IS_THREAD_RUNNING==1){
				;}
			OverlayMapViewer.addToItemizedOverlay(newLocation);
			while(OverlayMapViewer.IS_THREAD_RUNNING==1){
				;}

			OverlayMapViewer.myMapController.setCenter(newGeoPoint);

			FetchSQL.setGpsFixData(location);
			
			lastLocation = location;

			//Execute the JBDC/SQL Query
			if(SQLSettingsActivity.areSettingsSet())
				new FetchSQL().execute();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(DEBUG, "Changed provider to: "+provider);
	}
}
