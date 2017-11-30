package com.semanticweb.group2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.*;

@Path("")
public class Index {
	
	@Path("getStates")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStates() {		
		
		String[] data = Location.getInstance().getStates();	
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
	
	@Path("getData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData(@QueryParam("states") String states,@QueryParam("type") String type,@QueryParam("categories") String categories) throws IOException {
		
		List<Data> data = null;
		try {
			data = DataFetcher.getInstance().fetchData(states.split(","), type, categories);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.ok(data).build();
	}
}
