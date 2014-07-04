package com.tony.mapinspector.rest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.tony.mapinspector.entity.Mapping;

@Path("mapping/")
public interface IMappingManager {

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Mapping read(@PathParam("id") String id,
			@Context HttpServletResponse response);

	@POST
	@Path("/")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void create(Mapping mapping, @Context HttpServletResponse response);

}
