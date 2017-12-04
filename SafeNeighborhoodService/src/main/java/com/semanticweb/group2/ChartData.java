package com.semanticweb.group2;

import java.util.*;

public class ChartData {
	
	public String Type;
	public List<LocationData> Data;
	private HashMap<String, Integer> indexes;
	private int index;
	
	public ChartData(String type) {
		Type = type;
		Data = new ArrayList<LocationData>();
		indexes = new HashMap<String, Integer>();
		index = 0;
	}
	
	public void AddLocation(String location, String subType, String count ) {
		if(indexes.containsKey(location)) {
			int i = indexes.get(location);
			Data.get(i).Points.add(new PointData(subType, count));
		}
		else {
			indexes.put(location, index++);
			LocationData ld = new LocationData(location);
			ld.Points.add(new PointData(subType, count));
			Data.add(ld);
		}
	}

}
