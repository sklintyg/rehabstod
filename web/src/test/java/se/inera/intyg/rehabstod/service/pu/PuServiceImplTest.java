/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.pu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.pu.integration.api.model.Person;
import se.inera.intyg.infra.pu.integration.api.model.PersonSvar;
import se.inera.intyg.infra.pu.integration.api.services.PUService;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.inera.intyg.schemas.contract.Personnummer;

/**
 * Created by eriklupander on 2017-09-06.
 */
@RunWith(MockitoJUnitRunner.class)
public class PuServiceImplTest {

    private static final String TOLVANSSON_PNR = "19121212-1212";
    private static final String TOLVANSSON_PNR_INVALID = "19121212-1211";
    private static final String VARDGIVARE_1 = "vg-1";
    private static final String VARDGIVARE_2 = "vg-2";
    private static final String VARDENHET_1 = "vg-1-ve-1";
    private static final String VARDENHET_2 = "vg-1-ve-2";
    private static final String VARDENHET_3 = "vg-2-ve-2";
    private static final String LAKARE1_HSA_ID = "lakare-1";
    private static final String LAKARE2_HSA_ID = "lakare-2";
    private static final String LAKARE1_NAMN = "Läkare Läkarsson";

    @Mock
    private PUService puService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PuServiceImpl testee;

    @Before
    public void init() {
        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, false, false));

        when(puService.getPersons(Arrays.asList(createPnr(TOLVANSSON_PNR)))).thenReturn(
            buildPersonMap(buildPersonSvar(TOLVANSSON_PNR, false, false)));

    }

    @Test
    public void testShouldFilterOnProtectedPersonIfVardadmin() {
        when(userService.getUser()).thenReturn(buildVardadmin());
        assertTrue(testee.shouldFilterSickLeavesOnProtectedPerson(userService.getUser()));
    }

    @Test
    public void testShouldNotFilterOnProtectedPersonIfLakare() {
        final var user = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(user);
        assertFalse(testee.shouldFilterSickLeavesOnProtectedPerson(userService.getUser()));
    }

    @Test
    public void testShouldNotFilterOnProtectedPersonIfLakareAsRehabkoordinator() {
        final var user = buildLakareAsRehabkoordinator(VARDENHET_1);
        when(userService.getUser()).thenReturn(user);
        assertFalse(testee.shouldFilterSickLeavesOnProtectedPerson(userService.getUser()));
    }

    @Test
    public void testNoFilterWhenUserIsVardadmin() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(false, false);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsVardadmin() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(true, false);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testAvlidenIsFiltered() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(true, false);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsVardadminFilterOnly() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testFilterSekretessForPatientHistoryNoSekretess() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_1);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, false, false));

        List<IntygData> intygData = buildIntygList(TOLVANSSON_PNR);
        List<IntygData> returnedData = testee.filterSekretessForPatientHistory(intygData);
        assertEquals(3, returnedData.size());
    }

    @Test
    public void testFilterSekretessForPatientHistorySekretessOnCareUnit() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_1);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, true, false));

        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);

        List<IntygData> intygData = buildIntygList(TOLVANSSON_PNR);
        List<IntygData> returnedData = testee.filterSekretessForPatientHistory(intygData);
        assertEquals(1, returnedData.size());
    }

    @Test
    public void testFilterSekretessForPatientHistorySekretessOnSubUnit() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_1);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, true, false));

        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);

        List<IntygData> intygData = buildIntygList(TOLVANSSON_PNR);
        List<IntygData> returnedData = testee.filterSekretessForPatientHistory(intygData);
        assertEquals(1, returnedData.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testFilterSekretessForPatientHistoryError() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_1);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(PersonSvar.error());

        List<IntygData> intygData = buildIntygList(TOLVANSSON_PNR);
        testee.filterSekretessForPatientHistory(intygData);
    }

    @Test
    public void testFilterSekretessForPatientHistoryNotFound() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_1);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(PersonSvar.notFound());

        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);

        List<IntygData> intygData = buildIntygList(TOLVANSSON_PNR);
        List<IntygData> returnedData = testee.filterSekretessForPatientHistory(intygData);
        assertEquals(1, returnedData.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsLakareOnOtherUnit() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_2);
        when(userService.getUser()).thenReturn(rehabstodUser);

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testFilterSekretessForSummaryWhenPuServiceThrowsException() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(rehabstodUser);
        when(puService.getPersons(anyList())).thenReturn(new HashMap());

        testee.filterSekretessForSummary(buildSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsLakareOnOtherUnitFilterOnly() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_2);
        when(userService.getUser()).thenReturn(rehabstodUser);

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsIncludedWhenUserIsLakareOnSameUnit() {
        RehabstodUser rehabstodUser = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(rehabstodUser);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsIncludedWhenUserIsLakareAsRehabkoordinatorAndSignedTheIntyg() {
        RehabstodUser rehabstodUser = buildLakareAsRehabkoordinator(VARDENHET_1);
        when(userService.getUser()).thenReturn(rehabstodUser);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);
        when(puService.getPersons(Arrays.asList(createPnr(TOLVANSSON_PNR)))).thenReturn(buildPersonMap());
        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsExcludedWhenUserIsLakareAsRehabkoordinatorOnSameUnitButIsNotSigning() {
        RehabstodUser rehabstodUser = buildLakareAsRehabkoordinator(VARDENHET_1);
        when(rehabstodUser.getHsaId()).thenReturn(LAKARE2_HSA_ID);
        when(userService.getUser()).thenReturn(rehabstodUser);

        when(puService.getPersons(Arrays.asList(createPnr(TOLVANSSON_PNR)))).thenReturn(buildPersonMap());

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testFilterSekretessForSummaryWhenPuIsUnavailable() {
        when(puService.getPersons(anyList())).thenReturn(new HashMap<>());

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionIsThrownWhenPersonSvarIncludesAnError() {
        mockPersonSvarError();
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(buildSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testNameIsAppliedFromPersonSvar() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(false, false);
        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
        assertEquals("Fornamn Efternamn", sjukfallList.get(0).getPatient().getNamn());
    }

    @Test
    public void testNameIsReplacedWhenLakareAccessesSekretessmarkerad() {
        RehabstodUser user = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);

        assertEquals(sjukfallList.size(), 1);
        assertEquals(sjukfallList.get(0).getPatient().getNamn(), PuService.SEKRETESS_SKYDDAD_NAME_PLACEHOLDER);
    }

    @Test
    public void testNameIsReplacedByPlaceholderIfFromPersonSvarWasNotFound() {
        RehabstodUser user = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);

        mockPersonSvarNotFound();

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
        assertEquals(PuService.SEKRETESS_SKYDDAD_NAME_UNKNOWN, sjukfallList.get(0).getPatient().getNamn());
    }

    @Test
    public void testSjukfallIsRemovedIfVardadminAndPersonSvarWasNotFound() {
        when(userService.getUser()).thenReturn(buildVardadmin());
        mockPersonSvarNotFound();

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallNotAllowedForVardadminIfSekretessmarkering() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, true, false));
        testee.enrichSjukfallWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallThrowsExceptionIfPuError() {

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            PersonSvar.error());
        testee.enrichSjukfallWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testGetPatientSjukfallWhenPatientNotFoundInPu() {

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            PersonSvar.notFound());
        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallNotAllowedForLakareOtherUnitIfSekretessmarkering() {
        RehabstodUser user = buildLakare(VARDENHET_2);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(false);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, true, false));

        testee.enrichSjukfallWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testGetPatientSjukfallAllowedForLakareSameUnitIfSekretessmarkering() {
        RehabstodUser user = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, true, false));

        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals(1, patientSjukfallList.get(0).getIntyg().size());
        assertEquals("Skyddad personuppgift", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    @Test
    public void testGetPatientSjukfallAllowedForLakareSameUnitIfNotSekretessmarkering() {
        RehabstodUser user = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(user);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, false, false));

        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals(1, patientSjukfallList.get(0).getIntyg().size());
        assertEquals("Fornamn Efternamn", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    @Test(expected = IllegalStateException.class)
    public void testFilterSekretessForSummaryWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(lakare);

        testee.filterSekretessForSummary(buildSjukfallList(TOLVANSSON_PNR_INVALID));
    }

    @Test
    public void testFilterSekretessForSummaryWhenPatientAvliden() {
        RehabstodUser lakare = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        mockPersonSvar(true, false);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testEnrichPatientsWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(lakare);

        testee.enrichSjukfallWithPatientNamesAndFilterSekretess(buildSjukfallList(TOLVANSSON_PNR_INVALID));
    }

    @Test(expected = RuntimeException.class)
    public void testEnrichPatientWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(VARDENHET_1);

        List<SjukfallPatient> sjukfallPatientList = buildPatientSjukfallList(TOLVANSSON_PNR_INVALID);
        testee.enrichSjukfallWithPatientNameAndFilterSekretess(sjukfallPatientList);
    }

    @Test
    public void testPatientNameSetToSekretessWhenApplicableOnPatientView() {
        RehabstodUser lakare = buildLakare(VARDENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(VARDENHET_1)).thenReturn(true);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, true, false));
        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals("Skyddad personuppgift", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsExceptionWhenAvliden() {
        RehabstodUser lakare = buildLakare(VARDENHET_1);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
            buildPersonSvar(TOLVANSSON_PNR, false, true));
        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichSjukfallWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(0, patientSjukfallList.size());
    }

    @Test
    public void testThatPersonnummerListOnlyConsistsOfValidPatientIDs() {
        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR, TOLVANSSON_PNR_INVALID);
        List<Personnummer> personnummerList = testee.getPersonnummerListFromSjukfall(sjukfallList);
        assertEquals(1, personnummerList.size());
        assertEquals(TOLVANSSON_PNR, personnummerList.get(0).getOriginalPnr());
    }

    private void mockPersonSvar(boolean avliden, boolean sekretess) {
        when(puService.getPersons(anyList())).
            thenReturn(buildPersonMap(buildPersonSvar(TOLVANSSON_PNR, sekretess, avliden)));
    }

    private void mockPersonSvarError() {
        Map<Personnummer, PersonSvar> personSvarMap = new HashMap<>();
        personSvarMap.put(createPnr(TOLVANSSON_PNR), PersonSvar.error());
        when(puService.getPersons(anyList())).thenReturn(personSvarMap);
    }

    private void mockPersonSvarNotFound() {
        Map<Personnummer, PersonSvar> personSvarMap = new HashMap<>();
        personSvarMap.put(createPnr(TOLVANSSON_PNR), PersonSvar.notFound());
        when(puService.getPersons(anyList())).thenReturn(personSvarMap);
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

    private List<PatientData> buildIntyg(String... personnummer) {
        List<PatientData> patientData = new ArrayList<>();
        Arrays.stream(personnummer)
            .forEach(pnr -> patientData.add(buildPatientData(pnr, "Patient-" + pnr)));
        return patientData;
    }

    private PatientData buildPatientData(String pnr, String namn) {
        PatientData patientData = new PatientData();
        patientData.setVardenhetId(VARDENHET_1);
        patientData.setPatient(buildPatient(pnr, namn));
        patientData.setLakare(new Lakare(LAKARE1_HSA_ID, LAKARE1_NAMN));
        return patientData;
    }

    private RehabstodUser buildLakare(String enhetHsaId) {
        Vardenhet valdVardenhet = new Vardenhet(enhetHsaId, "namnet");

        RehabstodUser user = mock(RehabstodUser.class);
        Map<String, Role> roleMap = new HashMap<>();
        roleMap.put("LAKARE", new Role());

        when(user.isLakare()).thenReturn(true);
        when(user.getRoles()).thenReturn(roleMap);
        return user;
    }

    private RehabstodUser buildLakareAsRehabkoordinator(String enhetHsaId) {
        Vardenhet valdVardenhet = new Vardenhet(enhetHsaId, "namnet");

        RehabstodUser user = mock(RehabstodUser.class);
        Map<String, Role> roleMap = new HashMap<>();

        when(user.isLakare()).thenReturn(true);
        when(user.getRoles()).thenReturn(roleMap);
        when(user.getHsaId()).thenReturn(LAKARE1_HSA_ID);
        return user;
    }

    private RehabstodUser buildVardadmin() {
        RehabstodUser user = new RehabstodUser("hsa-123", "user-123", false);
        user.setRoles(new HashMap<>());
        return user;
    }

    private PersonSvar buildPersonSvar(String pnr, boolean sekretess, boolean avliden) {
        return PersonSvar.found(buildPerson(pnr, sekretess, avliden));
    }

    private Person buildPerson(String pnr, boolean sekretess, boolean avliden) {
        return new Person(createPnr(pnr), sekretess, avliden, "Fornamn", null, "Efternamn",
            "Gatan 1", "11212", "Orten");
    }

    private List<IntygData> buildIntygList(String pnr) {
        List<IntygData> intygData = new ArrayList<>();
        intygData.add(buildIntyg(pnr, VARDGIVARE_1, VARDENHET_1));
        intygData.add(buildIntyg(pnr, VARDGIVARE_1, VARDENHET_2));
        intygData.add(buildIntyg(pnr, VARDGIVARE_2, VARDENHET_3));
        return intygData;
    }

    private List<SjukfallEnhet> buildSjukfallList(String... personnummer) {
        List<SjukfallEnhet> sjukfallList = new ArrayList<>();
        Arrays.stream(personnummer)
            .forEach(pnr -> sjukfallList.add(buildSjukfall(pnr, "Patient-" + pnr)));
        return sjukfallList;
    }

    private SjukfallEnhet buildSjukfall(String pnr, String namn) {
        SjukfallEnhet sjukfall = new SjukfallEnhet();
        sjukfall.setPatient(buildPatient(pnr, namn));
        sjukfall.setVardEnhetId(VARDENHET_1);
        sjukfall.setLakare(new Lakare(LAKARE1_HSA_ID, LAKARE1_NAMN));
        return sjukfall;
    }

    private IntygData buildIntyg(String pnr, String vardgivare, String enhet) {
        IntygData intygData = new IntygData();
        intygData.setPatientId(pnr);
        intygData.setVardgivareId(vardgivare);
        intygData.setVardenhetId(enhet);
        return intygData;
    }

    private Map<Personnummer, PersonSvar> buildPersonMap(PersonSvar... arr) {
        Map<Personnummer, PersonSvar> persons = new HashMap<>();
        Arrays.stream(arr).forEach(ps -> {
            Personnummer pnr = ps.getPerson() == null ? null : ps.getPerson().personnummer();
            persons.put(pnr, ps);
        });
        return persons;
    }

    private Map<Personnummer, PersonSvar> buildPersonMap() {
        Map<Personnummer, PersonSvar> persons = new HashMap<>();
        persons.put(createPnr(TOLVANSSON_PNR), buildPersonSvar(TOLVANSSON_PNR, true, false));
        return persons;
    }

    private Patient buildPatient(String pnr, String namn) {
        return new Patient(pnr, namn);
    }

    private Personnummer createPnr(String pnr) {
        return Personnummer
            .createPersonnummer(pnr)
            .orElseThrow(() -> new IllegalArgumentException("Cannot create Personnummer object with pnr: " + pnr));
    }
}