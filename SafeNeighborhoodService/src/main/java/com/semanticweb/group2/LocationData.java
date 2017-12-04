package com.semanticweb.group2;

import java.util.*;

public class LocationData {

	public String Location;
	public List<PointData> Points;
	
	public LocationData(String location) {
		Location = location;
		Points = new ArrayList<PointData>();
	}
}
