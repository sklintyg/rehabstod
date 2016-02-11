/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosBeskrivningService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 *
 * This is to be deleted. It's only purpose is to create stubbed data until the rule engine is in place.
 */
@Component
public class SjukfallCalculatorEngineStub extends SjukfallCalculatorEngine {

    public SjukfallCalculatorEngineStub() {
        super();
    }

    @Autowired
    private DiagnosBeskrivningService diagnosBeskrivningService;

    @Override
    public List<Sjukfall> calculate(List<IntygsData> intygsData, GetSjukfallRequest requestData) {
        return getSjukfall(intygsData, requestData);
    }

    private List<Sjukfall> getSjukfall(List<IntygsData> intygsData, GetSjukfallRequest requestData) {
        List<Sjukfall> sjukfall = new ArrayList();

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        List<IntygsData> filteredIntyg = intygsData.stream()
                .filter(distinctByKey(o -> o.getPatient().getPersonId().getExtension())).collect(Collectors.toList());

        try {
            // CHECKSTYLE:OFF MagicNumber
            for (IntygsData intyg : filteredIntyg) {
                Sjukfall fall = new Sjukfall();

                // Patient
                Patient patient = new Patient();
                patient.setNamn(intyg.getPatient().getFornamn() + " " + intyg.getPatient().getEfternamn());
                patient.setId(intyg.getPatient().getPersonId().getExtension());
                patient.setAlder(ThreadLocalRandom.current().nextInt(20, 70 + 1));
                fall.setPatient(patient);

                // Diagnos
                Diagnos diagnos = new Diagnos();
                diagnos.setIntygsVarde(intyg.getDiagnos().getKod());
                diagnos.setKapitel(intyg.getDiagnos().getGrupp());
                diagnos.setKod(intyg.getDiagnos().getKod());
                diagnos.setBeskrivning(diagnosBeskrivningService.getDiagnosBeskrivning(intyg.getDiagnos().getKod()));
                fall.setDiagnos(diagnos);

                fall.setDagar(ThreadLocalRandom.current().nextInt(1, 500 + 1));
                fall.setStartVE(formatter.parse("2016-02-01"));
                fall.setStartVG(formatter.parse("2016-01-01"));
                fall.setSlut(formatter.parse("2016-03-01"));

                List<Integer> grader = new ArrayList<>();
                grader.add(100);
                grader.add(50);
                fall.setGrader(grader);
                fall.setAktivGrad(100);
                fall.setIntyg(4);

                fall.setLakare(intyg.getSkapadAv().getFullstandigtNamn());

                sjukfall.add(fall);
            }
            // CHECKSTYLE:ON MagicNumber
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sjukfall;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
