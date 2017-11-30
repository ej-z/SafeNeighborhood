package com.semanticweb.group2;

import java.io.*;
import java.util.*;

public class Location {

	static HashMap<String, List<String>> StateZipMapping;
	static HashMap<String, LatLong> ZipLatLongMapping;
	
	private Location()
	{		
		String csvFile = Config.getInstance().getProperty("geoinfo.path");
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        StateZipMapping = new HashMap<String, List<String>>();
        ZipLatLongMapping = new HashMap<String, LatLong>();
        try {

            br = new BufferedReader(new FileReader(csvFile));
            br.readLine();
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] data = line.split(cvsSplitBy);

                if(StateZipMapping.containsKey(data[1]))
                {
                	StateZipMapping.get(data[1]).add(data[0]);                	
                }
                else
                {
                	List<String> zipcodes = new ArrayList<String>();
                	zipcodes.add(data[0]);
                	StateZipMapping.put(data[1], zipcodes);
                }
                ZipLatLongMapping.put(data[0], new LatLong(data[3], data[4]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	private static Location single_instance = null;
	
	public static Location getInstance()
    {
        if (single_instance == null)
            single_instance = new Location();
 
        return single_instance;
    }
	
	public String[] getStates(){		
		
			Set<String> states = StateZipMapping.keySet();
			return states.toArray(new String[states.size()]);
	}
	
	public String[] getZipCodes(String state){
		
		if(StateZipMapping.containsKey(state))
		{
			List<String> zipcodes = StateZipMapping.get(state);
			return zipcodes.toArray(new String[zipcodes.size()]);
		}
		
		return null;
	}
	
	public LatLong getLatLong(String zipcode){
		
		if(ZipLatLongMapping.containsKey(zipcode))
			return ZipLatLongMapping.get(zipcode);
		
		return null;
	}
}
