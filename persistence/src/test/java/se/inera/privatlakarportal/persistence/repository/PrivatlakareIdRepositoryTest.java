package se.inera.privatlakarportal.persistence.repository;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import se.inera.privatlakarportal.persistence.config.PersistenceConfigDev;
import se.inera.privatlakarportal.persistence.config.PersistenceConfigTest;
import se.inera.privatlakarportal.persistence.model.PrivatlakareId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
public class PrivatlakareIdRepositoryTest {

    @Autowired
    private PrivatlakareIdRepository privatlakareIdRepository;

    @Before
    public void clear() {
        privatlakareIdRepository.deleteAll();
    }

    @Test
    public void testFindMaxId() {
        privatlakareIdRepository.save(new PrivatlakareId());
        privatlakareIdRepository.save(new PrivatlakareId());
        privatlakareIdRepository.save(new PrivatlakareId());
        assertEquals(new Integer(3), privatlakareIdRepository.findLatestGeneratedHsaId());
    }
}
