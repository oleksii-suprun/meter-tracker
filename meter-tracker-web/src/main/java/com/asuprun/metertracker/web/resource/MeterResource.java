package com.asuprun.metertracker.web.resource;

import com.asuprun.metertracker.web.domain.Meter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(MeterResource.PATH)
@Api(MeterResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface MeterResource {

    String PATH = "/meters";

    @GET
    @ApiOperation(value = "Returns list of meters", response = Meter.class, responseContainer = "List")
    List<Meter> getAll();
}
