package com.vmi.mapmatching_demo;

public class CandidateSegment {

	CandidateNode StartNode, EndNode;
	boolean IsOneWay = false;

	public CandidateSegment(CandidateNode StartNode, CandidateNode EndNode, String isOneWay){
		this.StartNode=StartNode;
		this.EndNode=EndNode;
		if(isOneWay.equals("yes"))
			this.IsOneWay=true;
	}

	public CandidateNode getStartNode(){
		return StartNode;
	}

	public CandidateNode getEndNode(){
		return EndNode;
	}

	public boolean isOneWay(){
		return IsOneWay;
	}

	public boolean equals(CandidateSegment AnotherSegment){
		if(this.StartNode.equals(AnotherSegment.getStartNode())){
			if(this.EndNode.equals(AnotherSegment.getEndNode())){
				return true;
			}
		}
		return false;
	}
}
