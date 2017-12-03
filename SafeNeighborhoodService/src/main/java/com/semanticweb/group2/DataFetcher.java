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
	String fireDataset = Config.getInstance().getProperty("fire.dataset");
	String crimeDataset = Config.getInstance().getProperty("crime.dataset");
	String diseaseDataset = Config.getInstance().getProperty("disease.dataset");
	String disasterDataset = Config.getInstance().getProperty("disaster.dataset");
	
	private DataFetcher(){
	}
	
	private static DataFetcher single_instance = null;
	
	public static DataFetcher getInstance() throws IOException
    {
        if (single_instance == null)
            single_instance = new DataFetcher(); 
        
        return single_instance;
    }
	
	public List<HeatMapData> fetchHeatMapData(String locations, String types, String categories, int isState)
	{
		String[] Locations = locations.split(",");
		String[] Types = types.split(",");
		String[] Categories = categories.split(",");
		List<HeatMapData> result = new ArrayList<HeatMapData>();
		
		for(int i = 0; i < Types.length; i++) {
			String endpoint = buildEndpoint(Types[i]);
			result.addAll(fetchHeatMapResults(endpoint, Locations, Categories[i].split("|"), isState));
		}		
		
		return 	result;	
	}
	
	public List<ChartData> fetchChartData(String locations, String types, String categories, int isState)
	{
		String[] Locations = locations.split(",");
		String[] Types = types.split(",");
		String[] Categories = categories.split(",");
		List<ChartData> result = new ArrayList<ChartData>();
		
		for(int i = 0; i < Types.length; i++) {
			String endpoint = buildEndpoint(Types[i]);
			result.addAll(fetchChartResults(endpoint, Locations, Categories[i].split("|"), isState));
		}		
		
		return 	result;	
	}
	
	private String buildEndpoint(String type)
	{
		if(type.equals("Crime"))
			return fusekiServer+"/"+crimeDataset+"/query";
			
		if(type.equals("Fire"))
			return fusekiServer+"/"+fireDataset+"/query";
		
		if(type.equals("Disaster"))
			return fusekiServer+"/"+disasterDataset+"/query";
		
		if(type.equals("Disease"))
			return fusekiServer+"/"+diseaseDataset+"/query";
		
		return null;
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
		String q = isState == 1 ? "    ?loc rdfs:subClassOf ?state."+BuildFilter("?state", locations) : BuildFilter("?loc", locations); 
		return runHeatMapQuery(endpoint, "SELECT ?loc (count(?loc) as ?count)" + 
				"WHERE {" + 
				"    ?r rdf:type ?type." +
				"    ?r sn:occured_at ?loc." + 
				q +
				BuildFilter("?type", categories) +
				"} GROUP BY ?loc");  //add the query string
	}
	
	private List<ChartData> fetchChartResults(String endpoint, String[] locations, String[] categories, int isState)
	{
		String q = isState == 1 ? "    ?loc rdfs:subClassOf ?state."+BuildFilter("?state", locations) : BuildFilter("?loc", locations); 
		return runChartQuery(endpoint, "SELECT ?loc, ?mastertype, ?type, (count(?loc) as ?count)" + 
				"WHERE {" + 
				"    ?r rdf:type ?type." +
				"    ?r sn:occured_at ?loc." + 
				"    ?type rdfs:subClassOf ?mastertype." + 
				q +
				"} GROUP BY ?loc ?mastertype ?type");  //add the query string
	}
	
	private List<HeatMapData> runHeatMapQuery(String endpoint, String queryRequest)
	{
		  StringBuffer queryStr = new StringBuffer();			
		  	 
		  queryStr.append("PREFIX sn" + ": <" + snNamespace + "> ");
		  queryStr.append("PREFIX rdfs" + ": <" +  "http://www.w3.org/2000/01/rdf-schema#" + "> ");
		  queryStr.append("PREFIX rdf" + ": <" + "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "> ");
		  queryStr.append("PREFIX foaf" + ": <" + "http://xmlns.com/foaf/0.1/" + "> ");
				
		  queryStr.append(queryRequest);
		  QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, queryStr.toString());
		  
		  List<HeatMapData> queryResult = new ArrayList<HeatMapData>();
				
		  try 
		  {
			  ResultSet response = qexec.execSelect();
				
			  while( response.hasNext())
			  {
				  QuerySolution soln = response.nextSolution();
				  RDFNode location = soln.get("?loc");
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
		  queryStr.append("PREFIX foaf" + ": <" + "http://xmlns.com/foaf/0.1/" + "> ");
				
		  queryStr.append(queryRequest);
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
				  RDFNode location = soln.get("?loc");
				  RDFNode mastertype = soln.get("?mastertype");
				  RDFNode type = soln.get("?type");
				  RDFNode count = soln.get("?count");
				  if(location != null && count != null)
				  {					  
					  String l = location.toString().substring(snNamespace.length());
					  String t = mastertype.toString().substring(snNamespace.length());
					  String c = type.toString().substring(snNamespace.length());
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
