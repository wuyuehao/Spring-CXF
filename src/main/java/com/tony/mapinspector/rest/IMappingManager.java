package com.tony.mapinspector.rest;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.tony.mapinspector.entity.LightMapping;
import com.tony.mapinspector.entity.Mapping;

@Path("mapping/")
public interface IMappingManager {
	
	@GET
	@Path("/from_class")
	@Produces({ MediaType.APPLICATION_JSON })
	public CommonResponseBase readUniqueFromClass(
			@Context HttpServletResponse response);

	@GET
	@Path("/class/{classname}")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<LightMapping> readAll(@PathParam("classname") String className,
			@Context HttpServletResponse response);

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Mapping read(@PathParam("id") Long id,
			@Context HttpServletResponse response);

	@POST
	@Path("/")
	@Consumes({ MediaType.APPLICATION_JSON })
	public CommonResponseBase create(Mapping mapping, @Context HttpServletResponse response);

	@PUT
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	public String update(@PathParam("id") Long id, Mapping mapping,
			@Context HttpServletResponse response);

	@PATCH
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	public String patch(@PathParam("id") Long id,
			@QueryParam("key") String key, @QueryParam("value") String value,
			@QueryParam("type") String type, @QueryParam("to_type") String toType,
			@DefaultValue("false") @QueryParam("remove") Boolean remove,
			@Context HttpServletResponse response);

}
