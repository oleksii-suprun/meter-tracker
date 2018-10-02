package com.asuprun.metertracker.web.repository;

import com.asuprun.metertracker.web.config.ApplicationConfig;
import com.asuprun.metertracker.web.config.RepositoryConfig;
import com.asuprun.metertracker.web.domain.Indication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfig.class, RepositoryConfig.class})
@ActiveProfiles(ApplicationConfig.Profiles.TEST)
public class IndicationRepositoryTest {

    @Autowired
    private IndicationRepository indicationRepository;

    @Test
    @Sql("classpath:datasets/default.sql")
    @Transactional
    public void testFindRecognizedBefore() {
        Optional<Indication> last = indicationRepository.findRecognizedBefore(2, new Date());

        assertTrue(last.isPresent());
        assertTrue("Received indication is not last or it does not contain value", last
                .filter(i -> i.getMeter().getId() == 2)
                .filter(i -> i.getId() == 4) // last indication id is 4
                .filter(i -> i.getValue() != null) // value MUST NOT be null
                .isPresent());
    }

    @Test
    @Sql("classpath:datasets/default.sql")
    @Transactional
    public void testFindRecognizedAfter() {
        Instant instant = Instant.parse("2015-03-10T00:00:00Z");
        Optional<Indication> last = indicationRepository.findRecognizedAfter(2, Date.from(instant));

        assertTrue(last.isPresent());
        assertTrue("Received indication is not last or it does not contain value", last
                .filter(i -> i.getMeter().getId() == 2)
                .filter(i -> i.getId() == 4) // last indication id is 4
                .filter(i -> i.getValue() != null) // value MUST NOT be null
                .isPresent());
    }

    @Test
    public void testFindRecognizedAfterReturnsEmpty() {
        Instant instant = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC); // tomorrow
        Optional<Indication> last = indicationRepository.findRecognizedAfter(3, Date.from(instant));

        assertFalse(last.isPresent());
    }
}
