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
		
		List<TypeData> typeData = EventType.getInstance().GetTypeData();		
		return Response.ok(typeData).build();
	}
		
	@Path("getHeatMapData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHeatMapData(@QueryParam("locations") String locations,@QueryParam("categories") String categories,@QueryParam("isState") int isState) throws IOException {
		
		List<HeatMapData> data = null;
		try {
			data = DataFetcher.getInstance().fetchHeatMapData(locations, categories, isState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.ok(data).build();
	}
	
	@Path("getChartData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChartData(@QueryParam("locations") String locations,@QueryParam("categories") String categories,@QueryParam("isState") int isState) throws IOException {
		
		List<ChartData> data = null;
		try {
			data = DataFetcher.getInstance().fetchChartData(locations, categories, isState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*data = new ArrayList<ChartData>();
		ChartData c1 = new ChartData("Crime");
		LocationData l1 = new LocationData("AK");
		l1.Points.add(new PointData("Murder", "89"));
		l1.Points.add(new PointData("Robbery", "189"));
		l1.Points.add(new PointData("Rape", "56"));
		LocationData l2 = new LocationData("AZ");
		l2.Points.add(new PointData("Murder", "34"));
		l2.Points.add(new PointData("Robbery", "462"));
		l2.Points.add(new PointData("Rape", "34"));
		c1.Data.add(l1);
		c1.Data.add(l2);
		ChartData c2 = new ChartData("Fire");
		LocationData l3 = new LocationData("AK");
		l3.Points.add(new PointData("Arson", "55"));
		l3.Points.add(new PointData("Basic", "34"));
		LocationData l4 = new LocationData("AZ");
		l4.Points.add(new PointData("Arson", "78"));
		l4.Points.add(new PointData("Basic", "57"));
		c2.Data.add(l3);
		c2.Data.add(l4);
		data.add(c1);
		data.add(c2);*/
		return Response.ok(data).build();
	}
}
