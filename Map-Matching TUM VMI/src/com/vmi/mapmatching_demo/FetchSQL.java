package com.vmi.mapmatching_demo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.mapsforge.core.GeoPoint;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.javadocmd.simplelatlng.window.RectangularWindow;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

class FetchSQL extends AsyncTask<Void,Void,String> {
	private static String DEBUG = null;

	static double rangeQueryRadius = 105;

	private static Location rawGPSFix;
	private static double rawGPSFixTimestamp;

	static ArrayList<CandidateNode> closeNodesList;
	static ArrayList<CandidateNode> relevantNodesList;
	public static double lonMin, lonMax, latMin, latMax;

	@Override
	protected String doInBackground(Void... params) {
		getCloseNodesAndSTMatch();
		return DEBUG;
	}

	public void getCloseNodesAndSTMatch(){
		ResultSet rsStartNodes = null;

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		//Establishes a connection the PSQL database
		String url = "jdbc:postgresql://" + SQLSettingsActivity.getSQLSettings().getIPAddress()+"/"+ SQLSettingsActivity.getSQLSettings().getDBName()+ 
				"?user="+ SQLSettingsActivity.getSQLSettings().getUsername()+"&password="+ SQLSettingsActivity.getSQLSettings().getPassword();
		Connection conn;
		try { 
			DriverManager.setLoginTimeout(5);
			conn = DriverManager.getConnection(url);
			Statement stStartNodes = conn.createStatement();

			String sqlStartNodes;

			//Creates a rectangular window used for the range query (for selecting the candidate line strings and nodes)
			RectangularWindow rectangularWindow = new RectangularWindow(new LatLng(rawGPSFix.getLatitude()
					, rawGPSFix.getLongitude()), rangeQueryRadius, rangeQueryRadius, LengthUnit.METER);
			latMin = rectangularWindow.getMinLatitude();     
			latMax =  rectangularWindow.getMaxLatitude();
			lonMin =  rectangularWindow.getLeftLongitude();
			lonMax =  rectangularWindow.getRightLongitude();

			//The SQL Query
			sqlStartNodes = "SELECT ST_NPoints(st_segmentize(way,0.0001)), name, way, st_x(ST_PointN(st_segmentize(way,0.0001),generate_series(1, ST_NPoints(st_segmentize(way,0.0001))))), " +
					"st_y(ST_PointN(st_segmentize(way,0.0001),generate_series(1, ST_NPoints(st_segmentize(way,0.0001))))),"
					+ "oneway from planet_osm_line where ((st_x(st_startpoint(way)) between "+ lonMin + " and " +
					lonMax + " and st_y(st_startpoint(way)) between " + latMin + " and " + latMax + ") or ((st_x(ST_Line_Interpolate_Point(way, 0.5)) between "+ 
					lonMin + " and " + lonMax + ") and (st_y(ST_Line_Interpolate_Point(way, 0.5)) between " + (latMin) + " and " +
					latMax + ")) or ((st_x(ST_Line_Interpolate_Point(way, 0.25)) between "+ 
					lonMin + " and " + lonMax + ") and (st_y(ST_Line_Interpolate_Point(way, 0.25)) between " + (latMin) + " and " +
					latMax + ")) or ((st_x(ST_Line_Interpolate_Point(way, 0.75)) between "+ 
					lonMin + " and " + lonMax + ") and (st_y(ST_Line_Interpolate_Point(way, 0.75)) between " + (latMin) + " and " +
					latMax + ")) or ((st_x(ST_Line_Interpolate_Point(way, 0.85)) between "+ 
					lonMin + " and " + lonMax + ") and (st_y(ST_Line_Interpolate_Point(way, 0.85)) between " + (latMin) + " and " +
					latMax + ")) or ((st_x(ST_Line_Interpolate_Point(way, 0.15)) between "+ 
					lonMin + " and " + lonMax + ") and (st_y(ST_Line_Interpolate_Point(way, 0.15)) between " + (latMin) + " and " +
					latMax + ")) or (st_x(st_endpoint(way)) between " + lonMin + " and " + lonMax + " and " +
					"st_y(st_endpoint(way)) between " + latMin + " and " + latMax +  
					")) and highway!='footway' and (highway='tertiary'or highway='residential' or highway='traffic_signals' " +
					"or highway='service' or highway='secondary')"+";";

			//Log.d(DEBUG, "SQL StartNodes Command: " + sqlStartNodes);

			//Execution of the query and the ResultSet returned
			rsStartNodes = stStartNodes.executeQuery(sqlStartNodes);

			STMatching(rsStartNodes);

			rsStartNodes.close();
			stStartNodes.close();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void STMatching(ResultSet rsStartNodes){
		closeNodesList = new ArrayList<CandidateNode>();
		relevantNodesList = new ArrayList<CandidateNode>();

		try {
			while(rsStartNodes.next()) {

				String name = rsStartNodes.getString(2);
				String way = rsStartNodes.getString(3);

				//read out the StartNodes Latitudes and Longitudes from the ResultSet
				Double StartLongitude = Double.parseDouble(rsStartNodes.getString(4));
				Double StartLatitude = Double.parseDouble(rsStartNodes.getString(5));

				int maxSpeed = 0;
				/*try{
					if(!rsStartNodes.getString(7).isEmpty())
						maxSpeed = rsStartNodes.getInt(7);
				}
				catch(NullPointerException e){
				}*/

				CandidateNode newStartCandidate= new CandidateNode(StartLatitude, StartLongitude, rawGPSFix, name, way, maxSpeed, rawGPSFixTimestamp);

				if(StartLongitude >(lonMin) && StartLongitude<(lonMax)&&
						StartLatitude>(latMin) && StartLatitude<(latMax)){
					closeNodesList.add(newStartCandidate);
				}
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		//Log.d(DEBUG, "Road nodes: " + new Integer(roadNodesList.size()).toString());
		//Log.d(DEBUG, "Close nodes: " + new Integer(closeNodesList.size()).toString());

		for(CandidateNode e:closeNodesList){
			boolean doNotAdd = false;
			for(int i=0; i< closeNodesList.size(); i++){
				if(e.getWayName().equals(closeNodesList.get(i).getWayName()) && e.getDistanceToGPSFix()>closeNodesList.get(i).getDistanceToGPSFix()){
					doNotAdd = true;
					break;
				}
			}

			if(!doNotAdd){
				relevantNodesList.add(e);
				//Log.d(DEBUG, "New close point: Distance: "+ e.distanceToPoint + " Latitude "+e.getLatitude() + " Longitude " + 
				//		e.getLongitude() + " Street " + e.getStreetName() + " Way hash: " + e.getWayName().hashCode()); 
			}
		}

		if(relevantNodesList.size()>0){
			STMatching.updateRelevantNodesList(relevantNodesList);
			STMatching.assignObservationProbability();
			STMatching.assignTransmissionProbability();
		}
		//OverlayMapViewer.setCandidatePoints(closeNodesList);
	}

	@Override
	protected void onPostExecute(String value) {
	}

	public static void setGpsFixData(Location newFix){
		rawGPSFix = newFix;
		rawGPSFixTimestamp = ((double)newFix.getTime())/1000;

		//if(newFix.hasAccuracy())
		//	rangeQueryRadius = 1.5*newFix.getAccuracy();
	}

	public static void setRangeQueryRadius(int rangeRadius){
		rangeQueryRadius = rangeRadius;
	}

	public ArrayList<CandidateNode> returnNodesList(String parameter){
		if(parameter.equals("close"))
			return closeNodesList;
		else return relevantNodesList;
	}
}