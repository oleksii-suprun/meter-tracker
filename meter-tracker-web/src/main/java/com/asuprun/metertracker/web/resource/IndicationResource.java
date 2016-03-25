package com.asuprun.metertracker.web.resource;

import com.asuprun.metertracker.web.dto.DigitDto;
import com.asuprun.metertracker.web.dto.IndicationDto;
import com.asuprun.metertracker.web.resource.response.ErrorResponse;
import io.swagger.annotations.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path(IndicationResource.PATH)
@Api(IndicationResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IndicationResource {

    String PATH = "/indications";
    String PARAM_UNRECOGNIZED = "unrecognized";
    String PARAM_METER_ID = "meterId";

    @GET
    @ApiOperation(value = "Returns list of indications", response = IndicationDto.class, responseContainer = "List")
    List<IndicationDto> get(@QueryParam(PARAM_METER_ID) @ApiParam("Meter id") Long meterId,
                            @QueryParam(PARAM_UNRECOGNIZED)
                            @ApiParam(value = "If specified, method returns only recognized or not recognized indications")
                            Boolean unrecognized);

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Finds indication by ID", response = IndicationDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "No such indication", response = ErrorResponse.class)
    })
    Response get(@PathParam("id") @ApiParam("Target indication id") long id);

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Removes indication by ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Indication successfully deleted"),
            @ApiResponse(code = 404, message = "No such indication", response = ErrorResponse.class)
    })
    Response delete(@PathParam("id") @ApiParam("Target indication id") long id);

    @GET
    @Path("/{id}/digits")
    @ApiOperation("Returns list of digits for current indication ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Digits successfully parsed", responseContainer = "List", response = DigitDto.class),
            @ApiResponse(code = 404, message = "No such indication", response = ErrorResponse.class)
    })
    List<DigitDto> digits(@PathParam("id") @ApiParam("Target indication id") long id);

    @POST
    @Path("/{id}/digits")
    @ApiOperation(value = "Saves list of digits for current indication ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Digits successfully uploaded"),
            @ApiResponse(code = 404, message = "No such indication", response = ErrorResponse.class)
    })
    Response digits(@PathParam("id") @ApiParam("Target indication id") long id,
                    @ApiParam("List of digits") @Valid List<DigitDto> digits);

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation("Uploads image and parse it")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Indication parsed and created", response = IndicationDto.class),
            @ApiResponse(code = 400, message = "Cannot find indication borders. Image cannot be parsed", response = ErrorResponse.class),
            @ApiResponse(code = 409, message = "Image was already uploaded", response = ErrorResponse.class)
    })
    Response upload(@Multipart("file") @ApiParam("Image file") Attachment file,
                    @Multipart("meterId") @ApiParam("Meter id") Long meterId) throws Exception;
}
