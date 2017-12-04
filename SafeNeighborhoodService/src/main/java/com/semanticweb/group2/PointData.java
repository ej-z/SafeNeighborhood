package com.semanticweb.group2;

public class PointData {

	public String label;
	public int y;
	
	public PointData(String subType, String count) {
		label = subType;
		y = Integer.parseInt(count);
	}
}
