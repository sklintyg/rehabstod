/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.certificate.builder.DiagnosedCertificateBuilder;
import se.inera.intyg.infra.certificate.builder.SickLeaveCertificateBuilder;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate.WorkCapacity;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.sjukfall.komplettering.KompletteringInfoDecorator;

@RunWith(MockitoJUnitRunner.class)
public class CertificateServiceImplTest {

    private static final String CERT_TYPE_LUSE = "LUSE";
    private static final String UNIT_1 = "UNIT1";
    private static final String CERT_ID_1 = "1";
    private static final String CERT_ID_2 = "2";
    private static final String PERSON_ID = "191212121212";
    private static final String PERSON_NAME = "Tolvan";
    private static final String DOCTOR_HSAID = "HSAID1";
    private static final LocalDateTime SIGN_TIME = LocalDateTime.now();
    private static final String DOCTOR_NAME = "Doctor1";
    private static final String CARE_PROVIDER_ID = "Provider1";
    private static final String CARE_UNIT_ID = "UnitId1";
    private static final String CARE_UNIT_NAME = "UnitName1";
    private static final String DIAGNOSE_CODE = "W58";
    private static final String DIAGNOSE_CODE2 = "J22";
    private static final String DIAGNOSE_CODE3 = "A01";
    private static final String OCCUPATION = "OCCUPATION";
    private static final LocalDate START_DATE_1 = LocalDate.of(2020, 7, 1);
    private static final LocalDate END_DATE_1 = LocalDate.of(2020, 7, 3);
    private static final LocalDate START_DATE_2 = LocalDate.of(2020, 7, 4);
    private static final LocalDate END_DATE_2 = LocalDate.of(2020, 7, 5);
    private static final LocalDate START_DATE_3 = LocalDate.of(2020, 7, 6);
    private static final LocalDate END_DATE_3 = LocalDate.of(2020, 7, 9);
    private static final int REDUCTION_1 = 100;
    private static final int REDUCTION_2 = 75;
    private static final int REDUCTION_3 = 50;

    @Mock
    KompletteringInfoDecorator kompletteringInfoDecorator;

    @Mock
    IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;

    @InjectMocks
    CertificateServiceImpl service;

    @Test
    public void getLUCertificatesForCareUnit() {

        var diagnosedCertificateList = buildDiagnosedCertificateList();
        when(intygstjanstRestIntegrationService.getDiagnosedCertificatesForCareUnit(any(List.class), any(List.class), any(), any()))
            .thenReturn(diagnosedCertificateList);

        var luCertificates = service.getLUCertificatesForCareUnit(null, null);

        assertNotNull(luCertificates);
        assertEquals(2, luCertificates.size());
        assertEquals(CERT_ID_1, luCertificates.get(0).getCertificateId());
        assertEquals(CERT_ID_2, luCertificates.get(1).getCertificateId());
        assertEquals(DIAGNOSE_CODE, luCertificates.get(0).getDiagnose().getKod());
        assertEquals(DIAGNOSE_CODE, luCertificates.get(1).getDiagnose().getKod());
    }

    @Test
    public void getLUCertificatesForPerson() {

        var diagnosedCertificateList = buildDiagnosedCertificateList();
        when(intygstjanstRestIntegrationService
            .getDiagnosedCertificatesForPerson(anyString(), any(List.class), any(), any(), any(List.class)))
            .thenReturn(diagnosedCertificateList);

        var luCertificates = service.getLUCertificatesForPerson(PERSON_ID);

        assertNotNull(luCertificates);
        assertEquals(2, luCertificates.size());
        assertEquals(CERT_ID_1, luCertificates.get(0).getCertificateId());
        assertEquals(CERT_ID_2, luCertificates.get(1).getCertificateId());
        assertEquals(DIAGNOSE_CODE, luCertificates.get(0).getDiagnose().getKod());
        assertEquals(DIAGNOSE_CODE, luCertificates.get(1).getDiagnose().getKod());
    }

    @Test
    public void getAGCertificatesForPerson() {

        var sickLeaveCertificateList = buildSickLeaveCertificateList();
        when(intygstjanstRestIntegrationService
            .getSickLeaveCertificatesForPerson(anyString(), any(List.class), any(), any(), any(List.class)))
            .thenReturn(sickLeaveCertificateList);

        var agCertificates = service.getAGCertificatesForPerson(PERSON_ID);

        assertNotNull(agCertificates);
        assertEquals(2, agCertificates.size());
        assertEquals(CERT_ID_1, agCertificates.get(0).getCertificateId());
        assertEquals(CERT_ID_2, agCertificates.get(1).getCertificateId());
        assertEquals(DIAGNOSE_CODE, agCertificates.get(0).getDiagnose().getKod());
        assertEquals(DIAGNOSE_CODE, agCertificates.get(1).getDiagnose().getKod());
        assertEquals(START_DATE_1, agCertificates.get(0).getStart());
        assertEquals(END_DATE_3, agCertificates.get(0).getEnd());
        assertEquals(REDUCTION_1, agCertificates.get(0).getDegree().get(0).intValue());
        assertEquals(REDUCTION_2, agCertificates.get(0).getDegree().get(1).intValue());
        assertEquals(REDUCTION_3, agCertificates.get(0).getDegree().get(2).intValue());
    }

    private ArrayList<DiagnosedCertificate> buildDiagnosedCertificateList() {
        var certificates = new ArrayList<DiagnosedCertificate>();

        certificates.add(buildDiagnosedCertificate(CERT_ID_1));
        certificates.add(buildDiagnosedCertificate(CERT_ID_2));

        return certificates;
    }

    private DiagnosedCertificate buildDiagnosedCertificate(String certId) {
        return (new DiagnosedCertificateBuilder(certId))
            .certificateType(CERT_TYPE_LUSE)
            .personId(PERSON_ID)
            .patientFullName(PERSON_NAME)
            .personalHsaId(DOCTOR_HSAID)
            .signingDateTime(SIGN_TIME)
            .signingDoctorName(DOCTOR_NAME)
            .careProviderId(CARE_PROVIDER_ID)
            .careUnitId(CARE_UNIT_ID)
            .careUnitName(CARE_UNIT_NAME)
            .diagnoseCode(DIAGNOSE_CODE)
            .secondaryDiagnoseCodes(Arrays.asList(DIAGNOSE_CODE2, DIAGNOSE_CODE3))
            .build();
    }

    private ArrayList<SickLeaveCertificate> buildSickLeaveCertificateList() {
        var certificates = new ArrayList<SickLeaveCertificate>();

        certificates.add(buildSickLeaveCertificate("1"));
        certificates.add(buildSickLeaveCertificate("2"));

        return certificates;
    }

    private SickLeaveCertificate buildSickLeaveCertificate(String certId) {
        return (new SickLeaveCertificateBuilder(certId))
            .certificateType(CERT_TYPE_LUSE)
            .personId(PERSON_ID)
            .patientFullName(PERSON_NAME)
            .personalHsaId(DOCTOR_HSAID)
            .signingDateTime(SIGN_TIME)
            .signingDoctorName(DOCTOR_NAME)
            .careProviderId(CARE_PROVIDER_ID)
            .careUnitId(CARE_UNIT_ID)
            .careUnitName(CARE_UNIT_NAME)
            .diagnoseCode(DIAGNOSE_CODE)
            .secondaryDiagnoseCodes(Arrays.asList(DIAGNOSE_CODE2, DIAGNOSE_CODE3))
            .workCapacityList(buildWorkCapacityList())
            .occupation(OCCUPATION)
            .build();
    }

    private List<WorkCapacity> buildWorkCapacityList() {
        var list = new ArrayList<WorkCapacity>();

        var workCapacity1 = new WorkCapacity();
        workCapacity1.setStartDate(START_DATE_1);
        workCapacity1.setEndDate(END_DATE_1);
        workCapacity1.setReduction(REDUCTION_1);
        list.add(workCapacity1);

        var workCapacity2 = new WorkCapacity();
        workCapacity2.setStartDate(START_DATE_2);
        workCapacity2.setEndDate(END_DATE_2);
        workCapacity2.setReduction(REDUCTION_2);
        list.add(workCapacity2);

        var workCapacity3 = new WorkCapacity();
        workCapacity3.setStartDate(START_DATE_3);
        workCapacity3.setEndDate(END_DATE_3);
        workCapacity3.setReduction(REDUCTION_3);
        list.add(workCapacity3);

        return list;
    }
}