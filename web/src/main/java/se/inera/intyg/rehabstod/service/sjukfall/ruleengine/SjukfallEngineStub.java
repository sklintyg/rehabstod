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

import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

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
public class SjukfallEngineStub extends SjukfallEngine {

    public SjukfallEngineStub() {
        super();
    }

    @Override
    public List<InternalSjukfall> calculate(List<IntygsData> intygsData, String hsaId, Urval urval, GetSjukfallRequest requestData) {
        return getSjukfall(intygsData, hsaId, urval, requestData);
    }

    private List<InternalSjukfall> getSjukfall(List<IntygsData> intygsData, String hsaId, Urval urval, GetSjukfallRequest requestData) {
        List<InternalSjukfall> sjukfall = new ArrayList();

        List<IntygsData> filteredIntyg = intygsData.stream()
                .filter(distinctByKey(o -> o.getPatient().getPersonId().getExtension())).collect(Collectors.toList());

        if (urval != null && urval.equals(Urval.ISSUED_BY_ME)) {
            filteredIntyg = filteredIntyg.stream().filter(o -> o.getSkapadAv().getPersonalId().getExtension().equals(hsaId)).collect(Collectors.toList());
        }

        // CHECKSTYLE:OFF MagicNumber
        for (IntygsData intyg : filteredIntyg) {
            InternalSjukfall internalSjukfall = new InternalSjukfall();
            Sjukfall fall = new Sjukfall();
            // Patient
            fall.setPatient(getPatient(intyg));

            // Diagnos
            fall.setDiagnos(getDiagnos(intyg));

            fall.setDagar(ThreadLocalRandom.current().nextInt(1, 500 + 1));
            fall.setStart(intygsData.get(0).getArbetsformaga().getFormaga().get(0).getStartdatum());
            fall.setSlut(intygsData.get(0).getArbetsformaga().getFormaga().get(0).getSlutdatum());

            List<Integer> grader = new ArrayList<>();
            grader.add(100);
            grader.add(50);
            fall.setGrader(grader);
            fall.setAktivGrad(100);
            fall.setIntyg(4);

            fall.setLakare(intyg.getSkapadAv().getFullstandigtNamn());
            internalSjukfall.setSjukfall(fall);
            internalSjukfall.setVardEnhetId(intyg.getSkapadAv().getEnhet().getEnhetsId().getExtension());
            internalSjukfall.setVardEnhetNamn(intyg.getSkapadAv().getEnhet().getEnhetsnamn());
            internalSjukfall.setVardGivareId(intyg.getSkapadAv().getEnhet().getVardgivare().getVardgivarId().getExtension());
            internalSjukfall.setVardGivareNamn(intyg.getSkapadAv().getEnhet().getVardgivare().getVardgivarnamn());
            sjukfall.add(internalSjukfall);
        }
        // CHECKSTYLE:ON MagicNumber

        return sjukfall;
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
