package com.asuprun.metertracker.web.resource;

import com.asuprun.metertracker.TestUtils;
import com.asuprun.metertracker.core.utils.ImageUtils;
import com.asuprun.metertracker.web.config.ApplicationConfig;
import com.asuprun.metertracker.web.config.RepositoryConfig;
import com.asuprun.metertracker.web.config.RestConfig;
import com.asuprun.metertracker.web.domain.Digit;
import com.asuprun.metertracker.web.domain.Indication;
import com.asuprun.metertracker.web.dto.DigitDto;
import com.asuprun.metertracker.web.dto.IndicationDto;
import com.asuprun.metertracker.web.resource.response.ErrorResponse;
import com.asuprun.metertracker.web.service.IndicationService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.asuprun.metertracker.web.resource.IndicationResource.*;
import static org.junit.Assert.*;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfig.class, RepositoryConfig.class, RestConfig.class})
@ActiveProfiles(ApplicationConfig.Profiles.TEST)
@TestExecutionListeners(listeners = {DbUnitTestExecutionListener.class}, mergeMode = MERGE_WITH_DEFAULTS)
@DatabaseSetup("classpath:datasets/dataset.xml")
public class IndicationResourceTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Autowired
    private WebClient client;

    @Autowired
    private IndicationService indicationService;

    @Before
    public void setUp() {
        client.path(PATH);
        client.accept(MediaType.APPLICATION_JSON);
        client.type(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testGetAll() {
        Collection<? extends IndicationDto> indications = client.getCollection(IndicationDto.class);

        assertNotNull(indications);
        assertEquals(4, indications.size());
    }

    @Test
    public void testGetUnrecognized() throws ParseException {
        client.query(PARAM_UNRECOGNIZED, true);
        List<IndicationDto> indications = new ArrayList<>(client.getCollection(IndicationDto.class));

        assertEquals(200, client.getResponse().getStatus());
        assertNotNull(indications);
        assertEquals(1, indications.size());
        assertEquals(3, indications.get(0).getId());
        assertEquals(5, indications.get(0).getOriginalImageId());
        assertEquals(6, indications.get(0).getIndicationImageId());
        assertEquals("Hot Water", indications.get(0).getMeterName());

        assertEquals(DATE_FORMAT.parse("2015-03-20 08:12:52.000"), indications.get(0).getCreated());
        assertEquals(DATE_FORMAT.parse("2015-03-20 11:23:42.000"), indications.get(0).getUploaded());
        assertNull(indications.get(0).getValue());
    }

    @Test
    public void testGetUnrecognizedCold() {
        client.query(PARAM_UNRECOGNIZED, true).query(PARAM_METER_ID, 2);
        Collection<? extends IndicationDto> indications = client.getCollection(IndicationDto.class);

        assertEquals(200, client.getResponse().getStatus());
        assertNotNull(indications);
        assertTrue(indications.isEmpty());
    }

    @Test
    public void testGetCold() {
        client.query(PARAM_UNRECOGNIZED, false).query(PARAM_METER_ID, 2);
        Collection<? extends IndicationDto> indications = client.getCollection(IndicationDto.class);

        assertEquals(200, client.getResponse().getStatus());
        assertNotNull(indications);
        assertEquals(2, indications.size());
    }

    @Test
    public void testGetUnrecognizedHot() {
        client.query(PARAM_UNRECOGNIZED, true).query(PARAM_METER_ID, 3);
        Collection<? extends IndicationDto> indications = client.getCollection(IndicationDto.class);

        assertEquals(200, client.getResponse().getStatus());
        assertNotNull(indications);
        assertEquals(1, indications.size());
    }

    @Test
    public void testGetHot() {
        client.query(PARAM_UNRECOGNIZED, false).query(PARAM_METER_ID, 3);
        Collection<? extends IndicationDto> indications = client.getCollection(IndicationDto.class);

        assertEquals(200, client.getResponse().getStatus());
        assertNotNull(indications);
        assertEquals(1, indications.size());
    }

    @Test
    public void testGetDigits() {
        client.path(1).path("digits");
        List<Digit> digits = new ArrayList<>(client.getCollection(Digit.class));

        assertEquals(200, client.getResponse().getStatus());
        assertNotNull(digits);
        assertEquals(8, digits.size());
    }

    @Test
    public void testGetDigitsNotFound() {
        client.path(Long.MAX_VALUE).path("digits");

        Response response = client.get();
        assertEquals(404, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("No such indication", errorResponse.getMessage());
    }

    @Test
    public void testGet() throws ParseException {
        client.path(1);
        IndicationDto indication = client.get(IndicationDto.class);

        assertEquals(200, client.getResponse().getStatus());
        assertNotNull(indication);
        assertEquals(1, indication.getId());
        assertEquals(0.087, indication.getValue(), 1e-6);
        assertEquals(1, indication.getOriginalImageId());
        assertEquals(2, indication.getIndicationImageId());
        assertEquals(DATE_FORMAT.parse("2015-02-20 09:12:34.000"), indication.getCreated());
        assertEquals(DATE_FORMAT.parse("2015-02-20 10:12:00.000"), indication.getUploaded());
        assertEquals("Cold Water", indication.getMeterName());
    }

    @Test
    public void testGetNotFound() {
        client.path(Long.MAX_VALUE);

        Response response = client.get();
        assertEquals(404, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("No such indication", errorResponse.getMessage());
    }

    @Test
    @DirtiesContext
    public void testDelete() {
        client.path(1);

        Response response = client.delete();
        assertEquals(204, response.getStatus());

        assertFalse(indicationService.findById(1).isPresent());
    }

    @Test
    public void testDeleteNotFound() {
        client.path(Long.MAX_VALUE);

        Response response = client.delete();
        assertEquals(404, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("No such indication", errorResponse.getMessage());
    }

    @Test
    @DirtiesContext
    public void testUpload() throws IOException, ParseException {
        client.type(MediaType.MULTIPART_FORM_DATA);

        final String fileName = "images/cold/00004_006.jpg";
        final long meterId = 1; // electric meter

        IndicationDto indication;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            MultipartBody body = new MultipartBody(Arrays.asList(
                    new Attachment("file", inputStream, new ContentDisposition("attachment;filename=" + fileName)),
                    new Attachment("meterId", "text/plain", meterId)));
            indication = client.post(body, IndicationDto.class);
        }

        assertEquals(201, client.getResponse().getStatus());
        assertNotNull(indication);
        assertEquals(5, client.getCollection(IndicationDto.class).size());
        assertNull(indication.getValue());
        assertTrue(indication.getOriginalImageId() > 0);
        assertTrue(indication.getIndicationImageId() > indication.getOriginalImageId());
        assertTrue(indication.getUploaded().before(new Date()));
        assertEquals(DATE_FORMAT.parse("2015-04-22 08:43:49.255"), indication.getCreated());
        assertEquals("Electricity", indication.getMeterName());
    }

    @Test
    public void testUploadConflict() throws IOException {
        client.type(MediaType.MULTIPART_FORM_DATA);

        final String fileName = "images/cold/00000_087.jpg";
        final long meterId = 1;

        Response response;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            MultipartBody body = new MultipartBody(Arrays.asList(
                    new Attachment("file", inputStream, new ContentDisposition("attachment;filename=" + fileName)),
                    new Attachment("meterId", "text/plain", meterId)));
            response = client.post(body);
        }

        assertEquals(409, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("This image was already uploaded", errorResponse.getMessage());
    }

    @Test
    public void testUploadBadImage() throws IOException {
        client.type(MediaType.MULTIPART_FORM_DATA);

        final String fileName = "application.properties";
        final long meterId = 1;

        Response response;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            MultipartBody body = new MultipartBody(Arrays.asList(
                    new Attachment("file", inputStream, new ContentDisposition("attachment;filename=" + fileName)),
                    new Attachment("meterId", "text/plain", meterId)));
            response = client.post(body);
        }

        assertEquals(400, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("Bad image file provided.", errorResponse.getMessage());
    }

    @Test
    public void testUploadNoIndication() throws IOException {
        client.type(MediaType.MULTIPART_FORM_DATA);

        final long meterId = 1;

        Response response;
        byte[] bytes = ImageUtils.imageToJpgBytes(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            MultipartBody body = new MultipartBody(Arrays.asList(
                    new Attachment("file", inputStream, new ContentDisposition("attachment;filename=test.jpg")),
                    new Attachment("meterId", "text/plain", meterId)));
            response = client.post(body);
        }

        assertEquals(400, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("Cannot detect indication region.", errorResponse.getMessage());
    }

    @Test
    public void testPostDigitsEmpty() {
        client.path(1).path("digits");

        Response response = client.post(Collections.emptyList());

        assertEquals(400, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("Incorrect number of digits", errorResponse.getMessage());
    }

    @Test
    public void testPostDigitsNoIndication() {
        client.path(Long.MAX_VALUE).path("digits");

        Response response = client.post(Collections.emptyList());

        assertEquals(404, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("No such indication", errorResponse.getMessage());
    }

    @Test
    @DirtiesContext
    public void testPostDigits() {
        client.path(3).path("digits");

        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        List<DigitDto> digits = "00001701".chars()
                .mapToObj(i -> new Digit(ImageUtils.imageToJpgBytes(image), String.valueOf((char) i)))
                .map(DigitDto::toDto)
                .collect(Collectors.toList());
        Response response = client.post(digits);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Optional<Indication> indication = indicationService.findById(3);
        assertTrue(indication.isPresent());
        assertEquals(1.701, indication.get().getValue(), 1e-6);
        assertEquals(1, indication.get().getConsumption().intValue());
    }

    @Test
    public void testPostDigitsEmptyValue() {
        client.path(3).path("digits");

        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        Response response = client.post(Collections.singletonList(
                DigitDto.toDto(new Digit(ImageUtils.imageToJpgBytes(image), ""))));

        assertEquals(400, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("Digit value must not be empty", errorResponse.getMessage());
    }

    @Test
    public void testPostDigitsNull() {
        client.path(3).path("digits");

        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        Response response = client.post(Collections.singletonList(
                DigitDto.toDto(new Digit(ImageUtils.imageToJpgBytes(image), null))));

        assertEquals(400, response.getStatus());
        assertTrue(response.hasEntity());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("Digit value must not be empty", errorResponse.getMessage());
    }

    @Test
    @DirtiesContext
    public void testPostDigitsNoImage() {
        client.path(3).path("digits");
        List<DigitDto> digits = "01234567".chars()
                .mapToObj(i -> new Digit(null, String.valueOf((char) i)))
                .map(DigitDto::toDto)
                .collect(Collectors.toList());
        Response response = client.post(digits);

        assertEquals(204, response.getStatus());

        Optional<Indication> indication = indicationService.findById(3);
        assertTrue(indication.isPresent());
        assertEquals(01234.567, indication.get().getValue(), 1e-6);
        assertEquals(1234, indication.get().getConsumption().intValue());
    }

    @Test
    @DirtiesContext
    public void testIndicationValueUpdate() {
        IndicationDto dto = IndicationDto.builder()
                .withId(4)
                .withValue(3.326)
                .build();

        client.path(dto.getId());
        Response response = client.put(dto);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Optional<Indication> indication = indicationService.findById(dto.getId());
        assertTrue(indication.isPresent());
        assertEquals(3.326, indication.get().getValue(), 1e-6);
        assertEquals(3, indication.get().getConsumption().intValue());
    }

    @Test
    @DirtiesContext
    public void testIndicationValueUpdateWithSubsequent() {
        IndicationDto dto = IndicationDto.builder()
                .withId(1)
                .withValue(1.743)
                .build();

        client.path(dto.getId());
        Response response = client.put(dto);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Optional<Indication> indication = indicationService.findById(dto.getId());
        assertTrue(indication.isPresent());
        assertEquals(1.743, indication.get().getValue(), 1e-6);
        assertEquals(0, indication.get().getConsumption().intValue());

        Optional<Indication> nextIndication = indicationService.findById(4);
        assertTrue(nextIndication.isPresent());
        assertEquals(2.084, nextIndication.get().getValue(), 1e-6);
        assertEquals(1, nextIndication.get().getConsumption().intValue());
    }

    @Test
    public void testIndicationValueUpdateNotFound() {
        IndicationDto dto = IndicationDto.builder()
                .withId(Long.MAX_VALUE)
                .build();

        client.path(dto.getId());
        Response response = client.put(dto);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("No such indication", errorResponse.getMessage());
    }

    @Test
    public void testIndicationValueUpdateBadRequestNegativeValue() {
        IndicationDto dto = IndicationDto.builder()
                .withId(1)
                .withValue(-1)
                .build();

        client.path(dto.getId());
        Response response = client.put(dto);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("Value must be greater or equals to 0", errorResponse.getMessage());
    }

    @Test
    public void testIndicationValueUpdateBadRequestIndicationNotProvided() {
        client.path(1);
        Response response = client.put(null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ErrorResponse errorResponse = TestUtils.readErrorResponseEntity(response);
        assertNotNull(errorResponse);
        assertEquals("No indication data provided", errorResponse.getMessage());
    }
}
