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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Simple in-memory store for consents.
 * Created by Magnus Ekstrand on 2018-10-10.
 */
@Component
public class SamtyckestjanstStubStore {

    private List<ConsentData> repository = new ArrayList<>();

    public List<ConsentData> getAll() {
        return repository;
    }

    public void removeAll() {
        repository.clear();
    }

    public List<ConsentData> getAllForPerson(String personId) {
        return repository.stream().filter(consentData -> consentData.getPersonId().equals(personId)).collect(Collectors.toList());
    }

    public boolean isConsentWithinInterval(String personId, LocalDate queryDateFrom, LocalDate queryDateTo) {
        return getAllForPerson(personId).stream()
                .anyMatch(consentData -> isWithinInterval(consentData, queryDateFrom) || isWithinInterval(consentData, queryDateTo));
    }

    private boolean isWithinInterval(ConsentData consentData, LocalDate queryDate) {
        // Either touches start/end or is in within
        return queryDate.isEqual(consentData.getBlockFrom()) || queryDate.isEqual(consentData.getBlockTo())
                || (queryDate.isAfter(consentData.getBlockFrom()) && queryDate.isBefore(consentData.getBlockTo()));
    }

    public void add(ConsentData data) {
        repository.add(data);
    }

    public void remove(String personId) {
        repository.removeIf(consentData -> consentData.getPersonId().equals(personId));
    }
}
