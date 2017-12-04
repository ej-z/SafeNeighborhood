package com.semanticweb.group2;

import java.io.IOException;
import java.util.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class DataFetcher {

	String snNamespace = Config.getInstance().getProperty("sn.namespace");
	String fusekiServer = Config.getInstance().getProperty("fuseki.uri");	
	
	private DataFetcher(){
	}
	
	private static DataFetcher single_instance = null;
	
	public static DataFetcher getInstance() throws IOException
    {
        if (single_instance == null)
            single_instance = new DataFetcher(); 
        
        return single_instance;
    }
	
	public List<HeatMapData> fetchHeatMapData(String locations, String categories, int isState)
	{
		String[] Locations = locations.split(",");
		String[] Categories = categories.split(",");		
		
		return fetchHeatMapResults(getEndpoint(), Locations, Categories, isState);
	}
	
	public List<ChartData> fetchChartData(String locations, String categories, int isState)
	{
		String[] Locations = locations.split(",");
		String[] Categories = categories.split(",");
		
		return fetchChartResults(getEndpoint(), Locations, Categories, isState);
	}
	
	private String getEndpoint()
	{
		return fusekiServer+"/ds/query";
		
		/*if(type.equals("Crime"))
			return fusekiServer+"/"+crimeDataset+"/query";
			
		if(type.equals("Fire"))
			return fusekiServer+"/"+fireDataset+"/query";
		
		if(type.equals("Disaster"))
			return fusekiServer+"/"+disasterDataset+"/query";
		
		if(type.equals("Disease"))
			return fusekiServer+"/"+diseaseDataset+"/query";
		
		return null;*/
	}	
	
	private String BuildFilter(String field, String[] filters)
	{
		String filter = " FILTER ("+field+" IN (";
		StringBuilder filterBuilder = new StringBuilder();
		
		filterBuilder.append(filter);
		for(String f : filters)
		{
			filterBuilder.append("sn:"+f+", ");
		}
		
		filterBuilder.setLength(filterBuilder.length() - 2);
		filterBuilder.append(")) ");
		return filterBuilder.toString();
	}
	
	private List<HeatMapData> fetchHeatMapResults(String endpoint, String[] locations, String[] categories, int isState)
	{
		String stateQuery = "SELECT * WHERE {{";
		stateQuery +=" SELECT (?loc as ?location) (count(?loc) as ?count)";
		stateQuery +=" WHERE {    ?r rdf:type ?type.   " ;
		stateQuery +=" ?r sn:occured_at ?loc.  "   ;
		stateQuery +=" ?loc rdfs:subClassOf ?state. ";
		stateQuery += BuildFilter("?state", locations);
		stateQuery += BuildFilter("?type", categories) + "}";
		stateQuery += " GROUP BY ?loc ";
		stateQuery += "} UNION {";
		stateQuery += "SELECT (?loc as ?location) (count(?loc) as ?count)";
		stateQuery += "WHERE {    ?r rdf:type ?type.    ";
		stateQuery += "?r sn:occured_at ?loc.";
		stateQuery += "OPTIONAL  { ?r sn:incidents_per_1000000 ?i . FILTER (?i > 0) }";
		stateQuery += BuildFilter("?state", locations);
		stateQuery += BuildFilter("?type", categories) + "}"; 
		stateQuery += "GROUP BY ?loc }}";
		
		String zipcodeQuery = "SELECT (?loc as ?location) (count(?loc) as ?count)";
		zipcodeQuery += "WHERE {    ?r rdf:type ?type.    ";
		zipcodeQuery += "?r sn:occured_at ?loc.";
		zipcodeQuery += BuildFilter("?state", locations);
		zipcodeQuery += BuildFilter("?type", categories) + "}"; 
		zipcodeQuery += "GROUP BY ?loc";
		
		return isState == 1? runHeatMapQuery(endpoint, stateQuery) : runHeatMapQuery(endpoint, zipcodeQuery);
	}
		
	
	private List<ChartData> fetchChartResults(String endpoint, String[] locations, String[] categories, int isState)
	{
		String stateQuery = "SELECT * WHERE {{";
		stateQuery +=" SELECT (?state as ?location) ?type (count(?type) as ?count)";
		stateQuery +=" WHERE {    ?r rdf:type ?type.   " ;
		stateQuery +=" ?r sn:occured_at ?loc.  "   ;
		stateQuery +=" ?loc rdfs:subClassOf ?state. ";
		stateQuery += BuildFilter("?state", locations);
		stateQuery += BuildFilter("?type", categories) + "}";
		stateQuery += " GROUP BY ?state  ?type";
		stateQuery += "} UNION {";
		stateQuery += "SELECT (?loc as ?location) ?type (count(?type) as ?count)";
		stateQuery += "WHERE {    ?r rdf:type ?type.    ";
		stateQuery += "?r sn:occured_at ?loc.";
		stateQuery += "OPTIONAL  { ?r sn:incidents_per_1000000 ?i . FILTER (?i > 0) }";
		stateQuery += BuildFilter("?state", locations);
		stateQuery += BuildFilter("?type", categories) + "}"; 
		stateQuery += "GROUP BY ?loc ?type }}";
		
		String zipcodeQuery = "SELECT (?loc as ?location) ?type (count(?type) as ?count)";
		zipcodeQuery += "WHERE {    ?r rdf:type ?type.    ";
		zipcodeQuery += "?r sn:occured_at ?loc.";
		zipcodeQuery += BuildFilter("?state", locations);
		zipcodeQuery += BuildFilter("?type", categories) + "}"; 
		zipcodeQuery += "GROUP BY ?loc ?type";
		
		return isState == 1? runChartQuery(endpoint, stateQuery) : runChartQuery(endpoint, zipcodeQuery);
		
	}
	
	private List<HeatMapData> runHeatMapQuery(String endpoint, String queryRequest)
	{
		  StringBuffer queryStr = new StringBuffer();			
		  	 
		  queryStr.append("PREFIX sn" + ": <" + snNamespace + "> ");
		  queryStr.append("PREFIX rdfs" + ": <" +  "http://www.w3.org/2000/01/rdf-schema#" + "> ");
		  queryStr.append("PREFIX rdf" + ": <" + "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "> ");
				
		  queryStr.append(queryRequest);
		  QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, queryStr.toString());
		  
		  List<HeatMapData> queryResult = new ArrayList<HeatMapData>();
				
		  try 
		  {
			  ResultSet response = qexec.execSelect();
				
			  while( response.hasNext())
			  {
				  QuerySolution soln = response.nextSolution();
				  RDFNode location = soln.get("?location");
				  RDFNode count = soln.get("?count");
				  if(location != null && count != null)
				  {					  
					  String zipcode = location.toString().substring(snNamespace.length());
					  LatLong l = Location.getInstance().getLatLong(zipcode);
					  Literal c = count.asLiteral();
					  if(l != null)
						  queryResult.add(new HeatMapData(zipcode, l.Latitude, l.Longitude, c.getString()));
				  }			
			  } 
		  }
		  finally { qexec.close();}		
		  
		  return queryResult;
	}
	
	private List<ChartData> runChartQuery(String endpoint, String queryRequest)
	{
		  StringBuffer queryStr = new StringBuffer();			
		  	 
		  queryStr.append("PREFIX sn" + ": <" + snNamespace + "> ");
		  queryStr.append("PREFIX rdfs" + ": <" +  "http://www.w3.org/2000/01/rdf-schema#" + "> ");
		  queryStr.append("PREFIX rdf" + ": <" + "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "> ");
				
		  queryStr.append(queryRequest);
		  System.out.println(queryStr.toString());
		  QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, queryStr.toString());
		  
		  List<ChartData> queryResult = new ArrayList<ChartData>();
				
		  String oldl = null, oldt = null;
		  ChartData chr = null;
		  LocationData loc = null;
		  try 
		  {
			  ResultSet response = qexec.execSelect();
				
			  while( response.hasNext())
			  {
				  QuerySolution soln = response.nextSolution();
				  RDFNode location = soln.get("?location");
				  RDFNode type = soln.get("?type");
				  RDFNode count = soln.get("?count");
				  if(location != null && count != null)
				  {					  
					  String l = location.toString().substring(snNamespace.length());
					  String c = type.toString().substring(snNamespace.length());
					  String t = EventType.getInstance().GetType(c);
					  Literal cnt = count.asLiteral();
					  if(t.equals(oldt))
					  {
						  if(l.equals(oldl)) {
							  loc.Points.add(new PointData(c, cnt.getString()));
						  }
						  else {
							  LocationData ld = new LocationData(l);
							  ld.Points.add(new PointData(c, cnt.getString()));
							  loc = ld;
							  chr.Data.add(ld);
						  }
					  }
					  else {
						  chr = new ChartData(t);
						  LocationData ld = new LocationData(l);
						  ld.Points.add(new PointData(c, cnt.getString()));
						  loc = ld;
						  chr.Data.add(ld);
						  queryResult.add(chr);
					  }
						  
				  }			
			  } 
		  }
		  finally { qexec.close();}		
		  
		  return queryResult;
	}
}
