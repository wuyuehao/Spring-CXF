package com.tony.mapinspector.rest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

public interface DependencyRS {

	@GET
	@Path("inspector/{class_name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String inspect(@PathParam("class_name") String className,
			@Context HttpServletResponse response);

	@GET
	@Path("inspector/data/{class_name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String read(@PathParam("class_name") String className,
			 @DefaultValue("0") @QueryParam("map_id") Long mapId,
			@Context HttpServletResponse response);

}
