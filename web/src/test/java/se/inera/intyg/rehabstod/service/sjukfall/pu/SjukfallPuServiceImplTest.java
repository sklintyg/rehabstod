package se.inera.intyg.rehabstod.service.sjukfall.pu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.pu.model.Person;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhetRS;
import se.inera.intyg.schemas.contract.Personnummer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Created by eriklupander on 2017-09-06.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallPuServiceImplTest {

    private static final String TOLVANSSON_PNR = "19121212-1212";
    private static final String ENHET_1 = "enhet-1";
    private static final String ENHET_2 = "enhet-2";

    @Mock
    private PUService puService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SjukfallPuServiceImpl testee;

    @Test
    public void testNoFilterWhenUserIsVardadmin() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, PersonSvar.Status.FOUND));

        List<SjukfallEnhetRS> sjukfallList = buildSjukfallList();
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsVardadmin() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhetRS> sjukfallList = buildSjukfallList();
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsLakareOnOtherUnit() {
        RehabstodUser rehabstodUser = buildLakare(ENHET_2);
        when(userService.getUser()).thenReturn(rehabstodUser);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhetRS> sjukfallList = buildSjukfallList();
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsIncludedWhenUserIsLakareOnSameUnit() {
        RehabstodUser rehabstodUser = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(rehabstodUser);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhetRS> sjukfallList = buildSjukfallList();
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionIsThrownWhenPuUnavailable() {
        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(null, false, PersonSvar.Status.ERROR));

        testee.enrichWithPatientNamesAndFilterSekretess(buildSjukfallList());
    }

    @Test
    public void testNameIsAppliedFromPersonSvar() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, PersonSvar.Status.FOUND));

        List<SjukfallEnhetRS> sjukfallList = buildSjukfallList();
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
        assertEquals("Fornamn Efternamn", sjukfallList.get(0).getPatient().getNamn());
    }

    @Test
    public void testNameIsReplacedByPlaceholderIfFromPersonSvarWasNotFound() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, PersonSvar.Status.NOT_FOUND));

        List<SjukfallEnhetRS> sjukfallList = buildSjukfallList();
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
        assertEquals("Namn okänt", sjukfallList.get(0).getPatient().getNamn());
    }

    private RehabstodUser buildLakare(String enhetHsaId) {
        Vardenhet valdVardenhet = new Vardenhet(enhetHsaId, "namnet");

        RehabstodUser user = mock(RehabstodUser.class);

        when(user.getValdVardenhet()).thenReturn(valdVardenhet);
        when(user.isLakare()).thenReturn(true);

        return user;
    }

    private RehabstodUser buildVardadmin() {
        RehabstodUser user = new RehabstodUser("hsa-123", "user-123");
        user.setRoles(new HashMap<>());
        return user;
    }

    private PersonSvar buildPersonSvar(String pnr, boolean sekretess, PersonSvar.Status status) {
        return new PersonSvar(buildPerson(pnr, sekretess), status);
    }

    private Person buildPerson(String pnr, boolean sekretess) {
        return new Person(new Personnummer(pnr), sekretess, false, "Fornamn", null, "Efternamn",
                "Gatan 1", "11212", "Orten");
    }

    private List<SjukfallEnhetRS> buildSjukfallList() {
        List<SjukfallEnhetRS> sjukfallList = new ArrayList<>();
        sjukfallList.add(buildSjukfall());
        return sjukfallList;
    }

    private SjukfallEnhetRS buildSjukfall() {
        SjukfallEnhetRS sjukfall = new SjukfallEnhetRS();
        sjukfall.setPatient(buildPatient());
        sjukfall.setVardEnhetId(ENHET_1);
        return sjukfall;
    }

    private Patient buildPatient() {
        return new Patient(TOLVANSSON_PNR, "Tolvan Tolvansson");
    }
}
