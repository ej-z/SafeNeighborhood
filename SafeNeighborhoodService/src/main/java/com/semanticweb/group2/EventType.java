package com.semanticweb.group2;

import java.util.*;

public class EventType {
	
	private String[] types = {"Crime", "Fire", "Disaster", "Disease"};
	private String[][] categories = {{"Arson_Crime","DrugOffense","Murder","Rape","Theft"},
	{"Arson_Fire","Basic","Wildlands"},
	{"Earthquake","Tsunami","Volcano"},
	{"DIPHTHERIA","HEPATITIS_A","MEASELS","MUMPS","PERTUSSIS","POLIO","RUBELLA","SMALLPOX"}};
	private int[] zipcodeSupport = {1,1,0,0};
	private List<TypeData> typeData;
	private HashMap<String, String> categoryTypes;
	
	private EventType() {
		
		categoryTypes = new HashMap<String, String>();
		typeData = new ArrayList<TypeData>();
		for(int i = 0; i < types.length; i++)
		{
			typeData.add(new TypeData(types[i], zipcodeSupport[i], categories[i]));
			for(int j = 0; j < categories[i].length; j++)
			{
				categoryTypes.put(categories[i][j], types[i]);
			}
		}
			
		
	}
	
private static EventType single_instance = null;
	
	public static EventType getInstance()
    {
        if (single_instance == null)
            single_instance = new EventType();
 
        return single_instance;
    }
	
	public List<TypeData> GetTypeData(){
		return typeData;
	}
	
	public String GetType(String category) {
		return categoryTypes.get(category);
	}
}
