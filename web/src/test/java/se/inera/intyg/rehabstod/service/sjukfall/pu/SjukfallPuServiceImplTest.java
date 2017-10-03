/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
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
    private static final String TOLVANSSON_PNR_INVALID = "19121212-1211";
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

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsVardadmin() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsVardadminFilterOnly() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsLakareOnOtherUnit() {
        RehabstodUser rehabstodUser = buildLakare(ENHET_2);
        when(userService.getUser()).thenReturn(rehabstodUser);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsLakareOnOtherUnitFilterOnly() {
        RehabstodUser rehabstodUser = buildLakare(ENHET_2);
        when(userService.getUser()).thenReturn(rehabstodUser);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsIncludedWhenUserIsLakareOnSameUnit() {
        RehabstodUser rehabstodUser = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(rehabstodUser);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionIsThrownWhenPuUnavailable() {
        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(null, false, PersonSvar.Status.ERROR));

        testee.enrichWithPatientNamesAndFilterSekretess(buildSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testNameIsAppliedFromPersonSvar() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, PersonSvar.Status.FOUND));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
        assertEquals("Fornamn Efternamn", sjukfallList.get(0).getPatient().getNamn());
    }

    @Test
    public void testNameIsReplacedWhenLakareAccessesSekretessmarkerad() {
        RehabstodUser user = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);

        assertEquals(sjukfallList.size(), 1);
        assertEquals(sjukfallList.get(0).getPatient().getNamn(), SjukfallPuService.SEKRETESS_SKYDDAD_NAME_PLACEHOLDER);
    }

    @Test
    public void testNameIsReplacedByPlaceholderIfFromPersonSvarWasNotFound() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, PersonSvar.Status.NOT_FOUND));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
        assertEquals(SjukfallPuService.SEKRETESS_SKYDDAD_NAME_UNKNOWN, sjukfallList.get(0).getPatient().getNamn());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallNotAllowedForVardadminIfSekretessmarkering() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        testee.enrichWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallThrowsExceptionIfPuError() {

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.ERROR));

        testee.enrichWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testGetPatientSjukfallWhenPatientNotFoundInPu() {

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.NOT_FOUND));
        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallNotAllowedForLakareOtherUnitIfSekretessmarkering() {
        RehabstodUser user = buildLakare(ENHET_2);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(false);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        testee.enrichWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testGetPatientSjukfallAllowedForLakareSameUnitIfSekretessmarkering() {
        RehabstodUser user = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));

        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals(1, patientSjukfallList.get(0).getIntyg().size());
        assertEquals("Sekretessmarkerad uppgift", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    @Test
    public void testGetPatientSjukfallAllowedForLakareSameUnitIfNotSekretessmarkering() {
        RehabstodUser user = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, PersonSvar.Status.FOUND));

        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals(1, patientSjukfallList.get(0).getIntyg().size());
        assertEquals("Fornamn Efternamn", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    @Test
    public void testFilterWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        PersonSvar personSvar = buildPersonSvar(TOLVANSSON_PNR_INVALID, true, PersonSvar.Status.ERROR);
        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR_INVALID))).thenReturn(personSvar);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR_INVALID);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testEnrichPatientsWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        PersonSvar personSvar = buildPersonSvar(TOLVANSSON_PNR_INVALID, true, PersonSvar.Status.ERROR);
        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR_INVALID))).thenReturn(personSvar);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR_INVALID);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnrichPatientWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        PersonSvar personSvar = buildPersonSvar(TOLVANSSON_PNR_INVALID, true, PersonSvar.Status.ERROR);
        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR_INVALID))).thenReturn(personSvar);

        List<SjukfallPatient> sjukfallPatientList = buildPatientSjukfallList(TOLVANSSON_PNR_INVALID);
        testee.enrichWithPatientNameAndFilterSekretess(sjukfallPatientList);
    }

    @Test
    public void testPatientNameSetToSekretessWhenApplicableOnPatientView() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(new Personnummer(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, PersonSvar.Status.FOUND));
        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals("Sekretessmarkerad uppgift", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    private List<SjukfallPatient> buildPatientSjukfallList(String pnr) {
        List<SjukfallPatient> sjukfallList = new ArrayList<>();
        sjukfallList.add(buildPatientSjukfall(pnr));
        return sjukfallList;
    }

    private SjukfallPatient buildPatientSjukfall(String pnr) {
        SjukfallPatient sjukfallPatient = new SjukfallPatient();
        sjukfallPatient.setIntyg(buildIntyg(pnr));
        return sjukfallPatient;
    }

    private List<PatientData> buildIntyg(String pnr) {
        List<PatientData> patientData = new ArrayList<>();
        patientData.add(buildPatientData(pnr));
        return patientData;
    }

    private PatientData buildPatientData(String pnr) {
        PatientData patientData = new PatientData();
        patientData.setVardenhetId(ENHET_1);
        patientData.setPatient(buildPatient(pnr));
        return patientData;
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

    private List<SjukfallEnhet> buildSjukfallList(String pnr) {
        List<SjukfallEnhet> sjukfallList = new ArrayList<>();
        sjukfallList.add(buildSjukfall(pnr));
        return sjukfallList;
    }

    private SjukfallEnhet buildSjukfall(String pnr) {
        SjukfallEnhet sjukfall = new SjukfallEnhet();
        sjukfall.setPatient(buildPatient(pnr));
        sjukfall.setVardEnhetId(ENHET_1);
        return sjukfall;
    }

    private Patient buildPatient(String pnr) {
        return new Patient(pnr, "Tolvan Tolvansson");
    }
}
