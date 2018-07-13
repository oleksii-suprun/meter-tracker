package com.asuprun.metertracker.web.resource;

import com.asuprun.metertracker.web.domain.DashboardItem;
import com.asuprun.metertracker.web.dto.ConsumptionDataDto;
import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(DashboardResource.PATH)
@Api(DashboardResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public interface DashboardResource {

    String PATH = "/dashboard/items";

    @GET
    List<DashboardItem> getItems();

    @GET
    @Path("/{dashboardItemId}/series")
    List<ConsumptionDataDto> consumptionDataSeries(@PathParam("dashboardItemId") long itemId);

}
