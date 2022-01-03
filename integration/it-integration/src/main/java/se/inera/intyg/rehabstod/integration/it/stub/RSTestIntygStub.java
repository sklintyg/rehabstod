/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.it.stub;

// CHECKSTYLE:OFF LineLength

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

// CHECKSTYLE:ON LineLength

/**
 * Created by Magnus Ekstrand on 2018-10-26.
 */
@Service
@Profile({"rhs-it-stub"})
public class RSTestIntygStub {

    private List<IntygsData> intygsData = new ArrayList<>();

    private List<DiagnosedCertificate> luCertificateData = new ArrayList<>();

    private List<SickLeaveCertificate> agCertificateData = new ArrayList<>();

    @Autowired
    private RSTestDataGenerator rsTestDataGenerator;

    @Value("${rhs.sjukfall.stub.numberOfPatients}")
    private Integer numberOfPatients;

    @Value("${rhs.sjukfall.stub.intygPerPatient}")
    private Integer intygPerPatient;

    @PostConstruct
    public void init() {
        var stubData = rsTestDataGenerator.generateIntygsData(numberOfPatients, intygPerPatient);
        intygsData = stubData.getIntygsData();
        luCertificateData = stubData.getDiagnosedCertificates();
        agCertificateData = stubData.getSickLeaveCertificates();
    }

    public List<IntygsData> getIntygsData() {
        return intygsData;
    }

    public List<DiagnosedCertificate> getLUCertificateData() {
        return luCertificateData;
    }

    public List<SickLeaveCertificate> getAGCertificateData() {
        return agCertificateData;
    }
}
