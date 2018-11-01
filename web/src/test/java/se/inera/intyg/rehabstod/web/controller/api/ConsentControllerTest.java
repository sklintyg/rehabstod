/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.controller.api;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences;
import se.inera.intyg.rehabstod.common.integration.json.CustomObjectMapper;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.sjukfall.ConsentService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.RegisterExtendedConsentRequest;

import static org.mockito.Mockito.when;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsentControllerTest {

    private static final String PERSON_ID = "19121212-1212";
    private static final String VARDGIVARE_ID = "VG123";
    private static final String VARDENHETS_ID = "VEA";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RehabstodUser rehabstodUserMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private ConsentService consentServiceMock;

    @InjectMocks
    ConsentController testee = new ConsentController();

    private final List<String> vardgivareIds = Arrays.asList("VG123", "VG456", "VG789", "VG147");
    private final List<String> vardenhetsIds = Arrays.asList("VEA", "VEB", "VEC", "VED");

    @Before
    public void before() {
        when(userServiceMock.getUser()).thenReturn(rehabstodUserMock);
        when(rehabstodUserMock.getValdVardgivare()).thenReturn(new Vardgivare(VARDGIVARE_ID, "vårdgivare"));
        when(rehabstodUserMock.getValdVardenhet()).thenReturn(new Vardenhet(VARDENHETS_ID, "enhet"));
        when(rehabstodUserMock.getUrval()).thenReturn(Urval.ALL);
        RehabstodUserPreferences preferences = RehabstodUserPreferences.empty();
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG, "5");
        preferences.updatePreference(RehabstodUserPreferences.Preference.MAX_ANTAL_DAGAR_SEDAN_SJUKFALL_AVSLUT, "0");
        when(rehabstodUserMock.getPreferences()).thenReturn(preferences);
    }
/*
    @Test
    public void testRegisterExtendedConsent() {
        List<IntygData> result = new ArrayList<IntygData>() {{
            addAll(createIntygDataList1());
            addAll(createIntygDataList4());
        }};

        when(consentServiceMock
                .getIntygDataForPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class), any(IntygParametrar.class)))
                .thenReturn(result);

        testee.registerConsent(createRequest(PERSON_ID));

        verify(consentServiceMock).getIntygDataForPatient(anyString(), anyString(), anyString(), anyString(), any(Urval.class), any(IntygParametrar.class));
    }

    @Test
    public void testGetUniqueVardgivareAndVardenheter() {
        Map<String, Set<String>> unique = testee.getUniqueVardgivareAndVardenheter(createIntygDataList());

        assertTrue(getValues(unique,"VG456").containsAll(Arrays.asList("VEB", "VEC")));
        assertTrue(getValues(unique,"VG789").containsAll(Arrays.asList("VEC")));
        assertTrue(getValues(unique,"VG123").containsAll(Arrays.asList("VEA", "VED")));
        assertTrue(getValues(unique,"VG147").containsAll(Arrays.asList("VED")));
    }*/

    @Test
    public void convertToJson() throws IOException {
        RegisterExtendedConsentRequest request = createRequest(PERSON_ID);
        StringWriter jsonWriter = new StringWriter();
        CustomObjectMapper objectMapper = new CustomObjectMapper();
        objectMapper.writeValue(jsonWriter, request);
        System.out.println(jsonWriter);
    }

    private RegisterExtendedConsentRequest createRequest(String personId) {
        RegisterExtendedConsentRequest request = new RegisterExtendedConsentRequest();
        request.setPatientId(personId);
        return request;
    }

    private List<IntygData> createIntygDataList() {
        return new ArrayList<IntygData>() {{
            addAll(createIntygDataList1());
            addAll(createIntygDataList2());
            addAll(createIntygDataList3());
            addAll(createIntygDataList4());
        }};
    }

    private List<IntygData> createIntygDataList1() {
        return new ArrayList<IntygData>() {{
            // VG123 and VEA
            add(createIntygData(vardgivareIds.get(0), vardenhetsIds.get(0)));
            add(createIntygData(vardgivareIds.get(0), vardenhetsIds.get(0)));
        }};
    }

    private List<IntygData> createIntygDataList2() {
        return new ArrayList<IntygData>() {{
            // VG456 and VEB
            add(createIntygData(vardgivareIds.get(1), vardenhetsIds.get(1)));
            // VG789 and VEC
            add(createIntygData(vardgivareIds.get(2), vardenhetsIds.get(2)));
            // VG147 and VED
            add(createIntygData(vardgivareIds.get(3), vardenhetsIds.get(3)));
        }};
    }

    private List<IntygData> createIntygDataList3() {
        return new ArrayList<IntygData>() {{
            // VG123 and VEA
            add(createIntygData(vardgivareIds.get(0), vardenhetsIds.get(0)));
            // VG456 and VEB
            add(createIntygData(vardgivareIds.get(1), vardenhetsIds.get(1)));
            // VG789 and VEC
            add(createIntygData(vardgivareIds.get(2), vardenhetsIds.get(2)));
            // VG147 and VED
            add(createIntygData(vardgivareIds.get(3), vardenhetsIds.get(3)));
        }};
    }

    private List<IntygData> createIntygDataList4() {
        return new ArrayList<IntygData>() {{
            // VG123 and VED
            add(createIntygData(vardgivareIds.get(0), vardenhetsIds.get(3)));
            // VG456 and VEC
            add(createIntygData(vardgivareIds.get(1), vardenhetsIds.get(2)));
        }};
    }

    private IntygData createIntygData(String vgId, String veId) {
        IntygData data = new IntygData();
        data.setVardgivareId(vgId);
        data.setVardenhetId(veId);
        return data;
    }

    private Set<String> getValues(Map<String, Set<String>> map, String key) {
        return map.get(key);
    }

}
