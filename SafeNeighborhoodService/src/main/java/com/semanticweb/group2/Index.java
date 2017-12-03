package com.semanticweb.group2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.util.*;

@Path("")
public class Index {
	
	@Path("/")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public InputStream index() throws FileNotFoundException {		
		
		File f = new File(getClass().getClassLoader().getResource("MapTest.html").getFile());
		
		return new FileInputStream(f);
	}
	
	@Path("getStates")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStates() {		
		
		String[] data = Location.getInstance().getStates();	
		return Response.ok(data).build();
	}
	
	@Path("getZipcodes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getZipcodes(@QueryParam("state") String state) {		
		
		String[] data = Location.getInstance().getZipCodes(state);	
		return Response.ok(data).build();
	}
	
	@Path("getTypes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTypes() {		
		
		String[] data = {"Crime","Fire","Disaster","Disease"};		
		return Response.ok(data).build();
	}
	
	@Path("getCategories")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCategories(@QueryParam("type") String type) {		
		
		String[] data = null;	
		String[] CrimeCategories = {"Arson","DrugOffense","Murder","Rape","Theft"};
		String[] FireCategories = {"Arson","Basic","Wildlands"};
		String[] DisasterCategories = {"Earthquake","Hurricane","Tsunami","Volcano"};
		String[] DiseaseCategories = {"DIPHTHERIA","HEPATITIS_A","MEASELS","MUMPS","PERTUSSIS","POLIO","RUBELLA","SMALLPOX"};
		
		if(type.equals("Crime"))
			data = CrimeCategories;
			
		if(type.equals("Fire"))
			data = FireCategories;
		
		if(type.equals("Disaster"))
			data = DisasterCategories;
		
		if(type.equals("Disease"))
			data = DiseaseCategories;
		
		return Response.ok(data).build();
	}
	
	@Path("getHeatMapData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHeatMapData(@QueryParam("locations") String locations,@QueryParam("type") String type,@QueryParam("categories") String categories,@QueryParam("isState") int isState) throws IOException {
		
		List<HeatMapData> data = null;
		try {
			data = DataFetcher.getInstance().fetchHeatMapData(locations, type, categories, isState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.ok(data).build();
	}
	
	@Path("getChartData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChartData(@QueryParam("states") String locations,@QueryParam("type") String type,@QueryParam("categories") String categories,@QueryParam("isState") int isState) throws IOException {
		
		List<ChartData> data = null;
		try {
			data = DataFetcher.getInstance().fetchChartData(locations, type, categories, isState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.ok(data).build();
	}
}
