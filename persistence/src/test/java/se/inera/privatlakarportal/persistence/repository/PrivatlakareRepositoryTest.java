package se.inera.privatlakarportal.persistence.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.joda.time.LocalDateTime;
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
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.repository.util.PrivatelakareTestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PersistenceConfigTest.class, PersistenceConfigDev.class })
@ActiveProfiles({ "dev" })
public class PrivatlakareRepositoryTest {

    @Autowired
    private PrivatlakareRepository privatlakareRepository;

    @Before
    public void clear() {
        privatlakareRepository.deleteAll();
    }

    @Test
    public void testFindByPersonId() {
        Privatlakare saved = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("191212121212", 1, true));
        Privatlakare read = privatlakareRepository.findByPersonId(saved.getPersonId());
        Privatlakare read2 = privatlakareRepository.findByHsaId(saved.getHsaId());

        assertEquals(read, read2);

        assertThat(read.getAgarform(), is(equalTo(saved.getAgarform())));
        assertThat(read.getArbetsplatsKod(), is(equalTo(saved.getArbetsplatsKod())));
        assertThat(read.getEnhetsId(), is(equalTo(saved.getEnhetsId())));
        assertThat(read.getEnhetsNamn(), is(equalTo(saved.getEnhetsNamn())));
        assertThat(read.getEnhetSlutDatum(), is(equalTo(saved.getEnhetSlutDatum())));
        assertThat(read.getEnhetStartdatum(), is(equalTo(saved.getEnhetStartdatum())));
        assertThat(read.getEpost(), is(equalTo(saved.getEpost())));
        assertThat(read.getForskrivarKod(), is(equalTo(saved.getForskrivarKod())));
        assertThat(read.getFullstandigtNamn(), is(equalTo(saved.getFullstandigtNamn())));
        assertThat(read.isGodkandAnvandare(), is(equalTo(saved.isGodkandAnvandare())));
        assertThat(read.getHsaId(), is(equalTo(saved.getHsaId())));
        assertThat(read.getKommun(), is(equalTo(saved.getKommun())));
        assertThat(read.getLan(), is(equalTo(saved.getLan())));
        assertThat(read.getPersonId(), is(equalTo(saved.getPersonId())));
        assertThat(read.getPostadress(), is(equalTo(saved.getPostadress())));
        assertThat(read.getPostnummer(), is(equalTo(saved.getPostnummer())));
        assertThat(read.getPostort(), is(equalTo(saved.getPostort())));
        assertThat(read.getTelefonnummer(), is(equalTo(saved.getTelefonnummer())));
        assertThat(read.getVardgivareId(), is(equalTo(saved.getVardgivareId())));
        assertThat(read.getVardgivareNamn(), is(equalTo(saved.getVardgivareNamn())));
        assertThat(read.getVardgivareSlutdatum(), is(equalTo(saved.getVardgivareSlutdatum())));
        assertThat(read.getVardgivareStartdatum(), is(equalTo(saved.getVardgivareStartdatum())));

        assertThat(read.getBefattningar(), is(equalTo(saved.getBefattningar())));
        assertThat(read.getLegitimeradeYrkesgrupper(), is(equalTo(saved.getLegitimeradeYrkesgrupper())));
        assertArrayEquals(read.getSpecialiteter().toArray(), saved.getSpecialiteter().toArray());
        assertThat(read.getVerksamhetstyper(), is(equalTo(saved.getVerksamhetstyper())));
        assertThat(read.getVardformer(), is(equalTo(saved.getVardformer())));
    }

    @Test
    public void testFindWithoutLakarBehorighet() {
        Privatlakare p1 = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("p1", 1, true));
        Privatlakare p2 = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("p2", 2, false));
        Privatlakare p3 = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("p3", 3, false));
        Privatlakare p4 = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("p4", 4, true));
        Privatlakare p5 = PrivatelakareTestUtil.buildPrivatlakare("p5", 5, false);
        p5.setEnhetStartdatum(LocalDateTime.parse("2015-08-01"));
        p5.setVardgivareStartdatum(LocalDateTime.parse("2015-08-01"));
        privatlakareRepository.save(p5);

        List<Privatlakare> list = privatlakareRepository.findWithoutLakarBehorighet();

        assertThat("p1 should not be in the list, it has läkarbehörighet", !list.contains(p1));
        assertThat("p2 should be in the list, it does not have läkarbehörighet", list.contains(p2));
        assertThat("p3 should be in the list, it does not have läkarbehörighet", list.contains(p3));
        assertThat("p4 should not be in the list, it has läkarbehörighet", !list.contains(p4));
        assertThat("p5 should be in the list, it does not have läkarbehörighet", list.contains(p5));
        assertEquals(3, list.size());
    }

    @Test
    public void testFindNeverHadLakarBehorighet() {
        Privatlakare p1 = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("p1", 1, true));
        Privatlakare p2 = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("p2", 2, false));
        Privatlakare p3 = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("p3", 3, false));
        Privatlakare p4 = privatlakareRepository.save(PrivatelakareTestUtil.buildPrivatlakare("p4", 4, true));
        Privatlakare p5 = PrivatelakareTestUtil.buildPrivatlakare("p5", 5, false);
        p5.setEnhetStartdatum(LocalDateTime.parse("2015-08-01"));
        p5.setVardgivareStartdatum(LocalDateTime.parse("2015-08-01"));
        privatlakareRepository.save(p5);

        List<Privatlakare> list = privatlakareRepository.findNeverHadLakarBehorighet();

        assertThat("p1 should not be in the list, it has läkarbehörighet", !list.contains(p1));
        assertThat("p2 should be in the list, it never had läkarbehörighet", list.contains(p2));
        assertThat("p3 should be in the list, it never had läkarbehörighet", list.contains(p3));
        assertThat("p4 should not be in the list, it has läkarbehörighet", !list.contains(p4));
        assertThat("p5 should not be in the list, it previously had läkarbehörighet and logged in to webcert", !list.contains(p5));
        assertEquals(2, list.size());
    }

    @Test
    public void testFindNeverHadBehorighetAndRegisteredBefore() {
        Privatlakare p1 = PrivatelakareTestUtil.buildPrivatlakare("p1", 1, true);
        privatlakareRepository.save(p1);
        Privatlakare p2 = PrivatelakareTestUtil.buildPrivatlakare("p2", 2, false);
        p2.setRegistreringsdatum(LocalDateTime.parse("2015-09-30"));
        privatlakareRepository.save(p2);
        Privatlakare p3 = PrivatelakareTestUtil.buildPrivatlakare("p3", 3, false);
        p3.setRegistreringsdatum(LocalDateTime.parse("2014-01-01"));
        privatlakareRepository.save(p3);
        Privatlakare p4 = PrivatelakareTestUtil.buildPrivatlakare("p4", 4, true);
        privatlakareRepository.save(p4);
        Privatlakare p5 = PrivatelakareTestUtil.buildPrivatlakare("p5", 5, false);
        p5.setEnhetStartdatum(LocalDateTime.parse("2015-08-01"));
        p5.setVardgivareStartdatum(LocalDateTime.parse("2015-08-01"));
        privatlakareRepository.save(p5);
        Privatlakare p6 = PrivatelakareTestUtil.buildPrivatlakare("p6", 6, false);
        p6.setRegistreringsdatum(LocalDateTime.parse("2015-10-01"));
        privatlakareRepository.save(p6);

        LocalDateTime date = LocalDateTime.parse("2015-09-30");
        List<Privatlakare> list = privatlakareRepository.findNeverHadLakarBehorighetAndRegisteredBefore(date);

        assertThat("p1 should not be in the list, it has läkarbehörighet", !list.contains(p1));
        assertThat("p2 should be in the list, it never had läkarbehörighet and registered on 2015-09-30", list.contains(p2));
        assertThat("p3 should be in the list, it never had läkarbehörighet and registered before 2015-09-30", list.contains(p3));
        assertThat("p4 should not be in the list, it has läkarbehörighet", !list.contains(p4));
        assertThat("p5 should not be in the list, it previously had läkarbehörighet and logged in to webcert", !list.contains(p5));
        assertThat("p6 should not be in the list, it never had läkarbehörighet but registered after 2015-09-30", !list.contains(p6));
        assertEquals(2, list.size());
    }

}
