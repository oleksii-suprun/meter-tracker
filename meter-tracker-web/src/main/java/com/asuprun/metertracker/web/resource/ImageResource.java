package com.asuprun.metertracker.web.resource;

import io.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/resource")
@Api(value = "/resource", description = "Operation about images")
public interface ImageResource {

    @GET
    @Path("/{id}")
    @Produces("image/jpeg")
    @ApiOperation("Returns image by its id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Resource is found"),
            @ApiResponse(code = 404, message = "Resource not found")
    })
    Response getImage(@PathParam("id") @ApiParam("Target image id") String id);
}
