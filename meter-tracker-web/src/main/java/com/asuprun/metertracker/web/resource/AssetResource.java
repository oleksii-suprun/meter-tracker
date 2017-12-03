package com.asuprun.metertracker.web.resource;

import io.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(AssetResource.PATH)
@Api(AssetResource.PATH)
public interface AssetResource {

    String PATH = "/asset";

    @GET
    @Path("/{id}")
    @Produces("image/jpeg")
    @ApiOperation("Returns image by its id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Image is found"),
            @ApiResponse(code = 404, message = "Image not found")
    })
    Response getImage(@PathParam("id") @ApiParam("Target image id") String id);
}
