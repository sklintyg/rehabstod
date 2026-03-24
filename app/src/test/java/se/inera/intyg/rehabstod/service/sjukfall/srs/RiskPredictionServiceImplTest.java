/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.srs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import se.inera.intyg.rehabstod.application.api.model.Diagnos;
import se.inera.intyg.rehabstod.application.api.model.PatientData;
import se.inera.intyg.rehabstod.application.api.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.application.api.model.SjukfallPatient;
import se.inera.intyg.rehabstod.application.exceptions.SRSServiceException;
import se.inera.intyg.rehabstod.application.sjukfall.srs.RiskPredictionServiceImpl;
import se.inera.intyg.rehabstod.application.user.UserService;
import se.inera.intyg.rehabstod.infrastructure.security.auth.RehabstodUser;
import se.inera.intyg.rehabstod.infrastructure.security.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.infrastructure.security.common.model.Feature;
import se.inera.intyg.rehabstod.integration.hsatk.model.legacy.SelectableVardenhet;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.integration.srs.service.SRSIntegrationService;

/** Created by eriklupander on 2017-11-01. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RiskPredictionServiceImplTest {

  private static final String ENHET_1 = "enhet-1";
  private static final String ENHET_2 = "enhet-2";

  @Mock private SRSIntegrationService srsIntegrationService;

  @Mock private UserService userService;

  @InjectMocks private RiskPredictionServiceImpl testee;

  private RehabstodUser user;

  @BeforeEach
  void init() {
    user = buildUser(ENHET_1);
    when(userService.getUser()).thenReturn(user);
  }

  @Test
  void testNoInteractionWithSRSWhenFeatureNotActive() {
    when(user.getFeatures()).thenReturn(Collections.emptyMap());
    testee.updateWithRiskPredictions(buildSjukfallEnhetList(UUID.randomUUID().toString()));
    verifyNoInteractions(srsIntegrationService);
  }

  @Test
  void testNoInteractionWithSRSWhenFeatureNotActiveForUnit() {
    RehabstodUser user = buildUser(ENHET_2);
    when(user.getFeatures()).thenReturn(Collections.emptyMap());
    when(userService.getUser()).thenReturn(user);
    testee.updateWithRiskPredictions(buildSjukfallEnhetList(UUID.randomUUID().toString()));
    verifyNoInteractions(srsIntegrationService);
  }

  @Test
  void testPatientDataNoInteractionWithSRSWhenFeatureNotActive() {
    when(user.getFeatures()).thenReturn(Collections.emptyMap());
    testee.updateSjukfallPatientListWithRiskPredictions(
        buildSjukfallPatientList(UUID.randomUUID().toString()));
    verifyNoInteractions(srsIntegrationService);
  }

  @Test
  void testNoInteractionWithSRSWhenFeatureActiveButNoSjukfallSupplied() {
    testee.updateWithRiskPredictions(new ArrayList<>());
    verifyNoInteractions(srsIntegrationService);
  }

  @Test
  void testPatientDataNoInteractionWithSRSWhenFeatureActiveButNoSjukfallSupplied() {
    testee.updateSjukfallPatientListWithRiskPredictions(new ArrayList<>());
    verifyNoInteractions(srsIntegrationService);
  }

  @Test
  void testInteractionWithSRSWhenFeatureActive() {
    String intygsId = UUID.randomUUID().toString();

    when(srsIntegrationService.getRiskPrediktionerForIntygsId(anyList()))
        .thenReturn(buildRiskSignalList(intygsId));
    List<SjukfallEnhet> sjukfallEnhetList = buildSjukfallEnhetList(intygsId);
    testee.updateWithRiskPredictions(sjukfallEnhetList);

    assertEquals(1, sjukfallEnhetList.size());
    SjukfallEnhet sjukfallEnhet = sjukfallEnhetList.get(0);

    assertNotNull(sjukfallEnhet.getRiskSignal());
    assertEquals(intygsId, sjukfallEnhet.getRiskSignal().getIntygsId());
    assertEquals(2, sjukfallEnhet.getRiskSignal().getRiskKategori());
    assertEquals("beskrivning", sjukfallEnhet.getRiskSignal().getRiskDescription());

    verify(srsIntegrationService, times(1)).getRiskPrediktionerForIntygsId(anyList());
  }

  @Test
  void testPatientDataInteractionWithSRSWhenFeatureActive() {
    String intygsId = UUID.randomUUID().toString();

    when(srsIntegrationService.getRiskPrediktionerForIntygsId(anyList()))
        .thenReturn(buildRiskSignalList(intygsId));
    List<SjukfallPatient> sjukfallPatientList = buildSjukfallPatientList(intygsId);
    testee.updateSjukfallPatientListWithRiskPredictions(sjukfallPatientList);

    assertEquals(1, sjukfallPatientList.size());
    PatientData patientData = sjukfallPatientList.get(0).getIntyg().get(0);

    assertEquals(intygsId, patientData.getRiskSignal().getIntygsId());
    assertEquals(2, patientData.getRiskSignal().getRiskKategori());
    assertEquals("beskrivning", patientData.getRiskSignal().getRiskDescription());

    verify(srsIntegrationService, times(1)).getRiskPrediktionerForIntygsId(anyList());
  }

  @Test
  void testExpectedExceptionIsThrownWhenCallToSRSFails() {
    assertThrows(
        SRSServiceException.class,
        () -> {
          when(srsIntegrationService.getRiskPrediktionerForIntygsId(anyList()))
              .thenThrow(new RuntimeException("FEL FEL FEL"));
          testee.updateWithRiskPredictions(buildSjukfallEnhetList(UUID.randomUUID().toString()));
        });
  }

  @Test
  void testPatientListExpectedExceptionIsThrownWhenCallToSRSFails() {
    assertThrows(
        SRSServiceException.class,
        () -> {
          when(srsIntegrationService.getRiskPrediktionerForIntygsId(anyList()))
              .thenThrow(new RuntimeException("FEL FEL FEL"));
          testee.updateSjukfallPatientListWithRiskPredictions(
              buildSjukfallPatientList(UUID.randomUUID().toString()));
        });
  }

  @Test
  void testRiskLevelZeroIsFilteredOut() {
    String intygsId = UUID.randomUUID().toString();
    LocalDateTime now = LocalDateTime.now();
    List<String> diagnosisList = Arrays.asList("F43", "M79", "S52");
    List<RiskSignal> riskSignals =
        Arrays.asList(
            new RiskSignal(intygsId, 1, "beskrivning1", now),
            new RiskSignal(intygsId, 0, "beskrivning0", now));
    when(srsIntegrationService.getDiagnosisList()).thenReturn(diagnosisList);
    when(srsIntegrationService.getRiskPrediktionerForIntygsId(anyList())).thenReturn(riskSignals);
    List<SjukfallEnhet> sjukfallEnhetList = buildSjukfallEnhetList(intygsId);
    testee.updateWithRiskPredictions(sjukfallEnhetList);

    assertEquals(1, sjukfallEnhetList.size());
    assertEquals(1, sjukfallEnhetList.get(0).getRiskSignal().getRiskKategori());
  }

  @Test
  void testGetLatestRiskLevel() {
    String intygsId = UUID.randomUUID().toString();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime earlier = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
    LocalDateTime earliest = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
    List<RiskSignal> riskSignals =
        Arrays.asList(
            new RiskSignal(intygsId, 1, "beskrivning1", earlier),
            new RiskSignal(intygsId, 2, "beskrivning2", now),
            new RiskSignal(intygsId, 3, "beskrivning1", earliest),
            new RiskSignal(intygsId, 0, "beskrivning0", now));

    when(srsIntegrationService.getRiskPrediktionerForIntygsId(anyList())).thenReturn(riskSignals);
    List<SjukfallEnhet> sjukfallEnhetList = buildSjukfallEnhetList(intygsId);
    testee.updateWithRiskPredictions(sjukfallEnhetList);

    assertEquals(1, sjukfallEnhetList.size());
    assertEquals(2, sjukfallEnhetList.get(0).getRiskSignal().getRiskKategori());
  }

  private List<RiskSignal> buildRiskSignalList(String intygsId) {
    List<RiskSignal> list = new ArrayList<>();
    list.add(new RiskSignal(intygsId, 2, "beskrivning", LocalDateTime.now()));
    return list;
  }

  // sjukfall enhet builder.
  private List<SjukfallEnhet> buildSjukfallEnhetList(String intygsId) {
    List<SjukfallEnhet> list = new ArrayList<>();
    SjukfallEnhet se = new SjukfallEnhet();
    se.setIntygLista(Arrays.asList(intygsId));
    se.setDiagnos(new Diagnos("F438A", "F438A", "namn"));
    se.setAktivIntygsId(intygsId);
    list.add(se);
    return list;
  }

  private List<SjukfallEnhet> buildSjukfallEnhetList(int size) {
    List<SjukfallEnhet> list = new ArrayList<>();
    for (int a = 0; a < size; a++) {
      SjukfallEnhet se = new SjukfallEnhet();
      se.setAktivIntygsId("intyg-" + a);
      list.add(se);
    }
    return list;
  }

  // Patient data builders.
  private List<SjukfallPatient> buildSjukfallPatientList(String intygsId) {
    List<SjukfallPatient> list = new ArrayList<>();
    SjukfallPatient sjukfallPatient = new SjukfallPatient();
    sjukfallPatient.setIntyg(buildIntyg(intygsId));
    list.add(sjukfallPatient);
    return list;
  }

  private List<PatientData> buildIntyg(String intygsId) {
    List<PatientData> intyg = new ArrayList<>();
    intyg.add(buildPatientData(intygsId));
    return intyg;
  }

  private PatientData buildPatientData(String intygsId) {
    PatientData pd = new PatientData();
    pd.setIntygsId(intygsId);
    return pd;
  }

  // User builder
  private RehabstodUser buildUser(String unitHsaId) {
    RehabstodUser user = mock(RehabstodUser.class);
    SelectableVardenhet ve = mock(SelectableVardenhet.class);

    Feature f = new Feature();
    f.setGlobal(true);
    Map<String, Feature> features = Collections.singletonMap(AuthoritiesConstants.FEATURE_SRS, f);
    when(user.getFeatures()).thenReturn(features);
    return user;
  }
}
