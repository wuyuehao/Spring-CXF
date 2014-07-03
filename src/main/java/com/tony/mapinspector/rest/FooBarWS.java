package com.tony.mapinspector.rest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

@Component
public class FooBarWS {
	@GET
	@Path("/test/{param}")
	@Produces({ MediaType.APPLICATION_JSON })
	public FooBar getMessage(@PathParam("param") String msg,
			@Context HttpServletResponse response) {
		response.addHeader("CC_HEAD", msg);
		return new FooBar("key", msg);
	}

}
