package com.vmi.mapmatching_demo;

import java.util.ArrayList;

import org.mapsforge.core.GeoPoint;

import android.location.Location;

public class CandidateNode {

	String streetName, wayID;

	double nodeLatitude, nodeLongitude;
	Location respondingGPSFix, nodeLocation;
	
	int maxSpeed;
	
	boolean startCandidate = false, endCandidate = false, connected = false, bestMatch = false;
	
	float distanceToRespondingGPSFix;
	
	double observationProbability, timestamp;
	
	//contains all candidate nodes responding to the previous GPS Fix, however, the actual transmission probability is computed only for the 
	//most probable previous match 
	ArrayList<CandidateNode> pastNodesList = new ArrayList<CandidateNode>();
	ArrayList<Double> transmissionProbabilities = new ArrayList<Double>();
	
	//The spatial analysis function results (in regard to one or more previously obtained candidate nodes) are saved here
	ArrayList<Double> spatialAnalysisFunctionResults = new ArrayList<Double>();
	
	ArrayList<Double> temporalAnalysisFunctionResults = new ArrayList<Double>();

	public CandidateNode(double latitude, double longitude, Location parentGPSFix, String name, String way, int maxSpeed, double parentGPSFixTimestamp){

		Location locationOfThePoint = new Location("");
		locationOfThePoint.setLatitude(latitude);
		locationOfThePoint.setLongitude(longitude);

		this.nodeLocation = locationOfThePoint;
		this.nodeLatitude = latitude;
		this.nodeLongitude = longitude;
		this.respondingGPSFix = parentGPSFix;
		this.streetName = name;
		this.wayID = way;
		this.distanceToRespondingGPSFix = respondingGPSFix.distanceTo(locationOfThePoint);
		this.maxSpeed = maxSpeed;
		this.timestamp = parentGPSFixTimestamp;
	}

	public boolean equals(CandidateNode NodeToCompare){
		if(this.nodeLatitude == NodeToCompare.getLatitude())
		{
			if(this.nodeLongitude == NodeToCompare.getLongitude())
			{
				if(this.respondingGPSFix.equals(NodeToCompare.respondingGPSFix))
					return true;
			}
		}
		return false;
	}

	public double getLatitude(){
		return this.nodeLatitude;
	}

	public double getLongitude(){
		return this.nodeLongitude;
	}

	public Location getParentFix(){
		return this.respondingGPSFix;
	}
	
	public String getStreetName(){
		return streetName;
	}

	public String getWayName(){
		return this.wayID;
	}

	public double getDistanceToGPSFix(){
		return this.distanceToRespondingGPSFix;
	}
	
	public void setStartOrEndNode(String input){
		if(input.equals("start"))
			startCandidate = true;
		else endCandidate = true;
	}

	public GeoPoint toGeopoint(){
		return new GeoPoint(this.nodeLatitude, this.nodeLongitude);
	}
	
	public void setObservationProbability(double probability){
		this.observationProbability = probability;
	}
	
	public double getObservationProbability(){
		return this.observationProbability;
	}
	
	public double getTimestamp(){
		return this.timestamp;
	}
	
	public int getMaxSpeed(){
		return this.maxSpeed;
	}
	
	public void setTransmissionProbability(CandidateNode pastCandidate, Double transmissionProbability){
		this.pastNodesList.add(pastCandidate);
		this.transmissionProbabilities.add(transmissionProbability);
		this.spatialAnalysisFunctionResults.add(transmissionProbability*observationProbability);
	}
	
	public void setTemporalAnalysisResults(Double temporalAnalysisResult){
		this.temporalAnalysisFunctionResults.add(temporalAnalysisResult);
	}
	
	public void setLocation(Location myLocation){
		this.nodeLocation = myLocation;
	}
	
	public Location getLocation(){
		return this.nodeLocation;
	}
}
