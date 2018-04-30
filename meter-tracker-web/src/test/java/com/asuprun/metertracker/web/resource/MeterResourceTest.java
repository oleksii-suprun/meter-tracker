package com.asuprun.metertracker.web.resource;

import com.asuprun.metertracker.web.config.ApplicationConfig;
import com.asuprun.metertracker.web.config.RepositoryConfig;
import com.asuprun.metertracker.web.config.RestConfig;
import com.asuprun.metertracker.web.domain.Meter;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfig.class, RepositoryConfig.class, RestConfig.class})
@ActiveProfiles(ApplicationConfig.Profiles.TEST)
@TestExecutionListeners(listeners = {DbUnitTestExecutionListener.class}, mergeMode = MERGE_WITH_DEFAULTS)
public class MeterResourceTest {

    @Autowired
    private WebClient client;

    @Before
    public void setUp() {
        client.path(MeterResource.PATH);
        client.accept(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void testGetAll() {
        Collection<? extends Meter> meters = client.getCollection(Meter.class);

        assertEquals(200, client.getResponse().getStatus());
        assertNotNull(meters);
        assertEquals(3, meters.size());
    }
}
