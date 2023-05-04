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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.common.model.IntygAccessControlMetaData;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallServiceException;
import se.inera.intyg.rehabstod.service.sjukfall.util.AESEncrypter;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

/**
 * @author Magnus Ekstrand on 2017-09-01.
 */
@Component
public class SjukfallEngineMapper {

    @Autowired
    private DiagnosFactory diagnosFactory;

    // api

    /**
     * Mapping from SjukfallEngine's format to Rehabstod internal format.
     */
    public SjukfallEnhet mapToSjukfallEnhetDto(se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet from,
        int maxDagarSedanAvslut, LocalDate today) {
        SjukfallEnhet to = new SjukfallEnhet();

        try {
            to.setVardGivareId(from.getVardgivare().getId());
            to.setVardGivareNamn(from.getVardgivare().getNamn());
            to.setVardEnhetId(from.getVardenhet().getId());
            to.setVardEnhetNamn(from.getVardenhet().getNamn());
            to.setLakare(map(from.getLakare()));
            to.setPatient(map(from.getPatient()));
            to.setDiagnos(mapDiagnos(from.getDiagnosKod()));
            to.setBiDiagnoser(mapDiagnos(from.getBiDiagnoser()));
            to.setStart(from.getStart());
            to.setSlut(from.getSlut());
            to.setSlutOmDagar(ChronoUnit.DAYS.between(today, from.getSlut()));
            to.setDagar(from.getDagar());
            to.setIntyg(from.getIntyg());
            to.setIntygLista(from.getIntygLista());
            to.setAktivGrad(from.getAktivGrad());
            to.setGrader(from.getGrader());
            to.setAktivIntygsId(from.getAktivIntygsId());
            to.setSysselsattning(from.getSysselsattning());
            to.setNyligenAvslutat(to.getSlutOmDagar() < 0 && to.getSlutOmDagar() + maxDagarSedanAvslut >= 0);
            to.setUid(AESEncrypter.encrypt(from.getPatient().getId()));
        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping SjukfallEngine format to internal format", e);
        }

        return to;
    }

    /**
     * Mapping from SjukfallEngine's format to Rehabstod internal format.
     */
    public SjukfallPatient mapToSjukfallPatientDto(se.inera.intyg.infra.sjukfall.dto.SjukfallPatient from,
        Map<String, IntygAccessControlMetaData> intygAccessMetaData) {
        SjukfallPatient to = new SjukfallPatient();

        try {
            to.setDiagnos(mapDiagnos(from.getDiagnosKod()));
            to.setStart(from.getStart());
            to.setSlut(from.getSlut());
            to.setDagar(from.getDagar());

            List<PatientData> patientData = from.getSjukfallIntygList()
                .stream()
                .map(sfi -> mapSjukfallIntygToPatientData(sfi, intygAccessMetaData.get(sfi.getIntygId())))
                .collect(Collectors.toList());

            // Sort patientData by start date with descending order
            Comparator<PatientData> dateComparator
                = Comparator.comparing(PatientData::getStart, Comparator.reverseOrder());

            patientData = patientData.stream().sorted(dateComparator).collect(Collectors.toList());
            to.setIntyg(patientData);

            clearDataOnIntygIfOtherUnit(to);

        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping SjukfallEngine format to internal format", e);
        }

        return to;
    }

    /**
     * Mapping from SjukfallEngine's format to Rehabstod internal format.
     */
    public PatientData mapSjukfallIntygToPatientData(se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg from,
        IntygAccessControlMetaData iacm) {
        PatientData to = new PatientData();

        try {
            to.setIntygsId(from.getIntygId());
            to.setVardgivareId(from.getVardgivareId());
            to.setVardgivareNamn(from.getVardgivareNamn());
            to.setVardenhetId(from.getVardenhetId());
            to.setVardenhetNamn(from.getVardenhetNamn());

            Patient patient = new Patient(from.getPatientId(), from.getPatientNamn());
            to.setPatient(patient);

            to.setDiagnos(mapDiagnos(from.getDiagnosKod()));
            to.setBidiagnoser(mapDiagnos(from.getBiDiagnoser()));
            to.setStart(from.getStartDatum());
            to.setSlut(from.getSlutDatum());
            to.setSigneringsTidpunkt(from.getSigneringsTidpunkt());
            to.setDagar(from.getDagar());
            to.setGrader(from.getGrader());
            to.setLakare(buildLakare(from.getLakareId(), from.getLakareNamn()));
            to.setSysselsattning(from.getSysselsattning());
            to.setAktivtIntyg(from.isAktivtIntyg());

            if (iacm != null) {
                to.setOtherVardgivare(!iacm.isInomVardgivare());
                to.setOtherVardenhet(!iacm.isInomVardenhet());
            }

        } catch (Exception e) {
            throw new SjukfallServiceException("Error mapping SjukfallEngine format to internal format", e);
        }

        return to;
    }

    private Lakare buildLakare(String lakareId, String lakareNamn) {
        return new Lakare(lakareId, lakareNamn);
    }

    public Diagnos getDiagnos(se.inera.intyg.infra.sjukfall.dto.DiagnosKod from) {
        return diagnosFactory.getDiagnos(from.getOriginalCode(), from.getCleanedCode(), from.getName());
    }

    // private scope

    private Lakare map(se.inera.intyg.infra.sjukfall.dto.Lakare from) {
        return new Lakare(from.getId(), from.getNamn());
    }

    private Patient map(se.inera.intyg.infra.sjukfall.dto.Patient from) {
        return new Patient(from.getId(), from.getNamn());
    }

    private Diagnos mapDiagnos(se.inera.intyg.infra.sjukfall.dto.DiagnosKod from) {
        return getDiagnos(from);
    }

    private List<Diagnos> mapDiagnos(List<se.inera.intyg.infra.sjukfall.dto.DiagnosKod> from) {
        return Optional.ofNullable(from).orElse(Collections.emptyList()).stream()
            .map(this::mapDiagnos)
            .collect(Collectors.toList());
    }


    private void clearDataOnIntygIfOtherUnit(SjukfallPatient sjukfallPatient) {

        sjukfallPatient.getIntyg()
            .stream()
            .filter(patientData -> patientData.isOtherVardgivare() || patientData.isOtherVardenhet())
            .forEach(this::clearPatientData);

        Optional<PatientData> firstWithDiagnos = sjukfallPatient.getIntyg().stream()
            .filter(patientData -> patientData.getDiagnos() != null)
            .findFirst();

        if (firstWithDiagnos.isPresent()) {
            sjukfallPatient.setDiagnos(firstWithDiagnos.get().getDiagnos());
        } else {
            sjukfallPatient.setDiagnos(null);
        }
    }

    private void clearPatientData(PatientData patientData) {
        patientData.setBidiagnoser(new ArrayList<>());
        patientData.setDiagnos(null);
        patientData.setGrader(new ArrayList<>());
        patientData.setLakare(null);
        patientData.setSysselsattning(new ArrayList<>());
    }

}
