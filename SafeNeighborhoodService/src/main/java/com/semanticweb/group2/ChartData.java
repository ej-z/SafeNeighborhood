package com.semanticweb.group2;

import java.util.*;

public class ChartData {
	
	public String Type;
	public List<LocationData> Data;
	
	public ChartData(String type) {
		Type = type;
		Data = new ArrayList<LocationData>();
	}

}
