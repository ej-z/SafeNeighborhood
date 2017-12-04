package com.semanticweb.group2;

import java.util.*;;

public class ChartDataList {

	private HashMap<String, Integer> indexes;
	private int index;
	public List<ChartData> chartData;
	
	public ChartDataList() {
		indexes = new HashMap<String, Integer>();
		index = 0;
		chartData = new ArrayList<ChartData>();
	}
	
	public void AddData(String type, String location, String subType, String count ) {
		if(indexes.containsKey(type)) {
			int i = indexes.get(type);
			chartData.get(i).AddLocation(location, subType, count);
		}
		else {
			indexes.put(type, index++);
			
			ChartData cd = new ChartData(type);
			cd.AddLocation(location, subType, count);
			chartData.add(cd);
		}
	}
}
