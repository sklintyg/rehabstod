package se.inera.privatlakarportal.persistence.repository;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import se.inera.privatlakarportal.persistence.config.PersistenceConfigDev;
import se.inera.privatlakarportal.persistence.config.PersistenceConfigTest;
import se.inera.privatlakarportal.persistence.model.HospUppdatering;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
public class HospUppdateringRepositoryTest {

    @Autowired
    private HospUppdateringRepository hospUppdateringRepository;

    @Test
    public void testFind() {
        HospUppdatering hospUppdatering = new HospUppdatering();
        hospUppdatering.setSenasteHospUppdatering(new LocalDateTime());

        HospUppdatering saved = hospUppdateringRepository.save(hospUppdatering);
        HospUppdatering read = hospUppdateringRepository.findSingle();

        assertEquals(saved.getSenasteHospUppdatering(), read.getSenasteHospUppdatering());
    }
}
