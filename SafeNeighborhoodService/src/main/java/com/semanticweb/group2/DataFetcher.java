package com.semanticweb.group2;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
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
	
	Model _fire = null;
	Model _crime = null;
	Model _disaster= null;
	Model _disease= null;
	
	private DataFetcher(){
	}
	
	private static DataFetcher single_instance = null;
	
	public static DataFetcher getInstance() throws IOException
    {
        if (single_instance == null)
            single_instance = new DataFetcher(); 
        
        return single_instance;
    }
	
	public List<Data> fetchData(String[] states, String type, String categories)
	{
		String[] Categories = categories.split(",");
		String endpoint = buildEndpoint(type);
		
		return fetchResults(endpoint, states, Categories);
		
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
	
	private List<Data> fetchResults(String endpoint, String[] states, String[] categories)
	{
		return runQuery(endpoint, "SELECT ?loc (count(?loc) as ?count)" + 
				"WHERE {" + 
				"    ?r rdf:type ?type." +
				"    ?r sn:occured_at ?loc." + 
				"    ?loc rdfs:subClassOf ?state."+
				BuildFilter("?state", states) +
				BuildFilter("?type", categories) +
				"} GROUP BY ?loc");  //add the query string
	}
	
	private List<Data> runQuery(String endpoint, String queryRequest)
	{
		  StringBuffer queryStr = new StringBuffer();			
		  	 
		  queryStr.append("PREFIX sn" + ": <" + snNamespace + "> ");
		  queryStr.append("PREFIX rdfs" + ": <" +  "http://www.w3.org/2000/01/rdf-schema#" + "> ");
		  queryStr.append("PREFIX rdf" + ": <" + "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "> ");
		  queryStr.append("PREFIX foaf" + ": <" + "http://xmlns.com/foaf/0.1/" + "> ");
				
		  queryStr.append(queryRequest);
		  QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, queryStr.toString());
		  
		  List<Data> queryResult = new ArrayList<Data>();
				
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
						  queryResult.add(new Data(zipcode, l.Latitude, l.Longitude, c.getString()));
				  }			
			  } 
		  }
		  finally { qexec.close();}		
		  
		  return queryResult;
	}
}
