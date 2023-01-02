/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.infra.sjukfall.dto.Formaga;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallServiceException;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

/**
 * @author Magnus Ekstrand on 2017-09-01.
 */
@Component
public class IntygstjanstMapper {

    // api

    /**
     * Mapping from Intygstjänsten's format to SjukfallEngine format.
     */
    public IntygData map(IntygsData from) {
        IntygData to = new IntygData();

        try {
            to.setIntygId(from.getIntygsId());
            to.setPatientId(from.getPatient().getPersonId().getExtension());
            to.setPatientNamn(from.getPatient().getFullstandigtNamn());
            to.setLakareId(from.getSkapadAv().getPersonalId().getExtension());
            to.setLakareNamn(from.getSkapadAv().getFullstandigtNamn());
            to.setVardenhetId(from.getSkapadAv().getEnhet().getEnhetsId().getExtension());
            to.setVardenhetNamn(from.getSkapadAv().getEnhet().getEnhetsnamn());
            to.setVardgivareId(from.getSkapadAv().getEnhet().getVardgivare().getVardgivarId().getExtension());
            to.setVardgivareNamn(from.getSkapadAv().getEnhet().getVardgivare().getVardgivarnamn());
            to.setDiagnosKod(new DiagnosKod(from.getDiagnoskod()));
            to.setFormagor(mapFormagor(from.getArbetsformaga().getFormaga()));
            to.setSigneringsTidpunkt(from.getSigneringsTidpunkt());
            to.setEnkeltIntyg(from.isEnkeltIntyg());
            to.setBiDiagnoser(mapDiagnoser(from.getBidiagnoser()));
            to.setSysselsattning(from.getSysselsattning());

        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping Intygstjänsten's format to SjukfallEngine format", e);
        }

        return to;
    }

    // private

    private List<DiagnosKod> mapDiagnoser(List<String> from) {
        return Optional.ofNullable(from).orElse(Collections.emptyList()).stream()
            .map(DiagnosKod::new)
            .collect(Collectors.toList());
    }

    private List<Formaga> mapFormagor(List<se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga> from) {
        return Optional.ofNullable(from).orElse(Collections.emptyList()).stream()
            .map(this::createFormaga)
            .collect(Collectors.toList());
    }

    private Formaga createFormaga(se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga from) {
        return new Formaga(from.getStartdatum(), from.getSlutdatum(), from.getNedsattning());
    }

}
