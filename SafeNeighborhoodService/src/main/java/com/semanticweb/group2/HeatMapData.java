package com.semanticweb.group2;

public class HeatMapData {
	
	public String zipcode;
	public String latitude;
	public String longitude;
	public String count;
	
	public HeatMapData()
	{}
	
	public HeatMapData(String zipcode, String latitude, String longitude, String count)
	{
		this.zipcode = zipcode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.count = count;
	}

}
