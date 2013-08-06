/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.vmi.mapmatching_demo;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorFactory;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorInternal;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
/**
 * Main View Class.
 */
public class OverlayMapViewer extends MapActivity {
	public static MapController myMapController;
	private static MapView mapView;
	private static ArrayItemizedOverlay itemizedOverlay;
	private static Drawable itemCandidatesMarker;
	private static ArrayWayOverlay wayOverlay, wayOverlayRed, nodesOverlay;

	public static int counter = 0;
	public static int IS_THREAD_RUNNING = 0;
	private static int pollingTime = 5000;
	private static String DEBUG = "debug";

	MyLocationListener NewLocationListener;
	
	private static class MyItemizedOverlay extends ArrayItemizedOverlay {
		private final Context context;

		/**
		 * Constructs a new MyItemizedOverlay.
		 * 
		 * @param defaultMarker
		 *            the default marker (may be null).
		 * @param context
		 *            the reference to the application context.
		 */
		MyItemizedOverlay(Drawable defaultMarker, Context context) {
			super(defaultMarker);
			this.context = context;
		}

		/**
		 * Handles a tap event on the given item.
		 */
		@Override
		protected boolean onTap(int index) {
			OverlayItem item = createItem(index);
			if (item != null) {
				Builder builder = new AlertDialog.Builder(this.context);
				builder.setIcon(android.R.drawable.ic_menu_info_details);
				builder.setTitle(item.getTitle());
				builder.setMessage(item.getSnippet());
				builder.setPositiveButton("OK", null);
				builder.show();
			}
			return true;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		STMatching.initPoint = true;
		
		mapView = (MapView) findViewById(R.id.publicTransportMapView);
		mapView.setMapGenerator(MapGeneratorFactory.createMapGenerator(MapGeneratorInternal.MAPNIK));
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		myMapController = mapView.getController();

		// create the default marker for overlay items
		Drawable itemGPSMarker = getResources().getDrawable(R.drawable.marker_red);

		// create an individual marker for an overlay item
		itemCandidatesMarker = getResources().getDrawable(R.drawable.marker_green);

		// initialize the Paint objects
		initPaintObjects();

		// create the ItemizedOverlay and add the items
		if(itemizedOverlay==null)
			itemizedOverlay = new MyItemizedOverlay(itemGPSMarker, this);

		//initial connection to TUM-VMI Server
		SQLSettingsActivity.initSQLConfig();

		final LocationManager NetworkLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		NewLocationListener = new MyLocationListener();
		NetworkLocManager.requestLocationUpdates(NetworkLocManager.getBestProvider(createFineCriteria(), true)
				, pollingTime , 0, NewLocationListener);


		final Button clearPointsButton = (Button) findViewById(R.id.clear_button_points);
		final Button clearLinesButton = (Button) findViewById(R.id.clear_button_lines);

		clearPointsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				while(OverlayMapViewer.IS_THREAD_RUNNING==1){
					;}
				itemizedOverlay.clear();
			}
		});
		
		clearLinesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				while(OverlayMapViewer.IS_THREAD_RUNNING==1){
					;}
				wayOverlay.clear();
				wayOverlayRed.clear();
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:    
			Intent myIntent = new Intent(OverlayMapViewer.this, SQLSettingsActivity.class);
			OverlayMapViewer.this.startActivity(myIntent);
			break;
		case R.id.placeholder1:     			
			Intent myIntent2 = new Intent(OverlayMapViewer.this, GeneralSettingsActivity.class);
			OverlayMapViewer.this.startActivity(myIntent2);
			break;
		case R.id.quit: finish();
		break;
		}
		return true;
	}

	/** this criteria needs high accuracy, high power, and cost */
	public static Criteria createFineCriteria() {

		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		c.setAltitudeRequired(false);
		c.setBearingRequired(false);
		c.setSpeedRequired(false);
		c.setCostAllowed(true);
		return c;		 
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	public static void addToItemizedOverlay(OverlayItem overlayItem){
		IS_THREAD_RUNNING = 1;
		itemizedOverlay.addItem(overlayItem);
		try{
			mapView.getOverlays().add(itemizedOverlay);}
		catch(IllegalThreadStateException e){
		}
		IS_THREAD_RUNNING = 0;
	}

	//Use to draw a list of candidate points - testing purposes
	/*
	public static void setCandidatePoints(ArrayList<CandidateNode> candidateNodeList){
		int CandidateCounter = 1;
		for (CandidateNode currentCandidateNode : candidateNodeList ){
			String messageOnShow = "Source: Fix No. " + counter + "\n" + "Latitude: " + currentCandidateNode.getLatitude() + "\nLongitude: " +
					currentCandidateNode.getLongitude() + "\n" + "Observation probability: " + currentCandidateNode.getObservationProbability();
			GeoPoint newCandidatePoint = new GeoPoint(currentCandidateNode.getLatitude(), currentCandidateNode.getLongitude());

			//addCandidateNodeLine(parentPoint, newCandidatePoint);
			OverlayItem newCandidatePointLocation = new OverlayItem(newCandidatePoint, "Candidate Point " + CandidateCounter, messageOnShow,
					ItemizedOverlay.boundCenterBottom(itemCandidatesMarker));
			while(OverlayMapViewer.IS_THREAD_RUNNING==1){
				;}
			addToItemizedOverlay(newCandidatePointLocation);
			while(IS_THREAD_RUNNING==1){
				;}

			CandidateCounter++;}
	}
	*/
	
	public static void setCandidatePoint(CandidateNode currentCandidateNode){
		String messageOnShow = "Source: Fix No. " + counter + "\n" + "Latitude: " + currentCandidateNode.getLatitude() + "\nLongitude: " +
				currentCandidateNode.getLongitude() + "\n" + "Observation probability: " + currentCandidateNode.getObservationProbability();
		GeoPoint newCandidatePoint = new GeoPoint(currentCandidateNode.getLatitude(), currentCandidateNode.getLongitude());

		//addCandidateNodeLine(parentPoint, newCandidatePoint);
		OverlayItem newCandidatePointLocation = new OverlayItem(newCandidatePoint, "Matched Point", messageOnShow,
				ItemizedOverlay.boundCenterBottom(itemCandidatesMarker));
		while(OverlayMapViewer.IS_THREAD_RUNNING==1){
			;}
		addToItemizedOverlay(newCandidatePointLocation);
		while(IS_THREAD_RUNNING==1){
			;}
	}

	public static void initPaintObjects(){
		Paint wayDefaultPaintOutline, wayRedPaintOutline, wayDefaultPaintFill, wayRedPaintFill, nodesDefaultPaintFill, nodesDefaultPaintOutline;

		// create the default paint objects for overlay circles
		Paint circleDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleDefaultPaintFill.setStyle(Paint.Style.FILL);
		circleDefaultPaintFill.setColor(Color.BLUE);
		circleDefaultPaintFill.setAlpha(64);

		Paint circleDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleDefaultPaintOutline.setStyle(Paint.Style.STROKE);
		circleDefaultPaintOutline.setColor(Color.BLUE);
		circleDefaultPaintOutline.setAlpha(128);
		circleDefaultPaintOutline.setStrokeWidth(3);

		// create an individual paint object for an overlay circle
		Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(Color.MAGENTA);
		circlePaint.setAlpha(96);

		// create the default paint objects for overlay ways
		wayDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayDefaultPaintFill.setStyle(Paint.Style.STROKE);
		wayDefaultPaintFill.setColor(Color.BLUE);
		wayDefaultPaintFill.setAlpha(160);
		wayDefaultPaintFill.setStrokeWidth(2);
		wayDefaultPaintFill.setStrokeJoin(Paint.Join.ROUND);

		wayDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayDefaultPaintOutline.setStyle(Paint.Style.STROKE);
		wayDefaultPaintOutline.setColor(Color.BLUE);
		wayDefaultPaintOutline.setAlpha(128);
		wayDefaultPaintOutline.setStrokeWidth(2);
		wayDefaultPaintOutline.setStrokeJoin(Paint.Join.ROUND);

		// create the default paint objects for overlay ways
		wayRedPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayRedPaintFill.setStyle(Paint.Style.STROKE);
		wayRedPaintFill.setColor(Color.RED);
		wayRedPaintFill.setAlpha(160);
		wayRedPaintFill.setStrokeWidth(2);
		wayRedPaintFill.setStrokeJoin(Paint.Join.ROUND);

		wayRedPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayRedPaintOutline.setStyle(Paint.Style.STROKE);
		wayRedPaintOutline.setColor(Color.RED);
		wayRedPaintOutline.setAlpha(128);
		wayRedPaintOutline.setStrokeWidth(2);
		wayRedPaintOutline.setStrokeJoin(Paint.Join.ROUND);

		// create the default paint objects for overlay ways
		nodesDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		nodesDefaultPaintFill.setStyle(Paint.Style.STROKE);
		nodesDefaultPaintFill.setColor(Color.RED);
		nodesDefaultPaintFill.setAlpha(160);
		nodesDefaultPaintFill.setStrokeWidth(1);
		nodesDefaultPaintFill.setStrokeJoin(Paint.Join.ROUND);

		nodesDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
		nodesDefaultPaintOutline.setStyle(Paint.Style.STROKE);
		nodesDefaultPaintOutline.setColor(Color.RED);
		nodesDefaultPaintOutline.setAlpha(128);
		nodesDefaultPaintOutline.setStrokeWidth(1);
		nodesDefaultPaintOutline.setStrokeJoin(Paint.Join.ROUND);

		nodesOverlay = new ArrayWayOverlay(nodesDefaultPaintFill, nodesDefaultPaintOutline);
		wayOverlay = new ArrayWayOverlay(wayDefaultPaintFill, wayDefaultPaintOutline);
		wayOverlayRed = new ArrayWayOverlay(wayRedPaintFill, wayRedPaintOutline);
	}

	public static void addWayLine(GeoPoint geoPoint1, GeoPoint geoPoint2, int color){
		// create the WayOverlay and add the ways
		OverlayWay way1 = new OverlayWay(new GeoPoint[][] { { geoPoint1, geoPoint2 } });

		if(color == Color.BLUE){
			wayOverlay.addWay(way1);
			try{
				mapView.getOverlays().add(wayOverlay);}
			catch(IllegalThreadStateException e){
			}
		}
		else if(color == Color.RED){
			wayOverlayRed.addWay(way1);
			try{
				mapView.getOverlays().add(wayOverlayRed);}
			catch(IllegalThreadStateException e){
			}
		}
	}

	public static void buildRoadSegment(CandidateNode node1, CandidateNode node2, int color){
		addWayLine(node1.toGeopoint(), node2.toGeopoint(), color);
	}

	public static void buildWaySegment(Location node1, Location node2, int color){
		GeoPoint newCandidatePoint1 = new GeoPoint(node1.getLatitude(), node1.getLongitude());
		GeoPoint newCandidatePoint2 = new GeoPoint(node2.getLatitude(), node2.getLongitude());

		addWayLine(newCandidatePoint1, newCandidatePoint2, color);
	}
}

