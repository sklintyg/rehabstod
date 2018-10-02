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
package se.inera.intyg.rehabstod.integration.sparrtjanst.stub;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Simple in-memory store for blocks.
 * Created by marced on 2018-10-01.
 */
@Component
public class SparrtjanstStubStore {
    private List<BlockData> repository = new ArrayList<>();

    public List<BlockData> getAll() {
        return repository;
    }

    public void removeAll() {
        repository.clear();
    }

    public List<BlockData> getAllForPerson(String personId) {
        return repository.stream().filter(blockData -> blockData.getPersonId().equals(personId)).collect(Collectors.toList());
    }

    public boolean isBlockedAtDate(String personId, LocalDate queryDateFrom, LocalDate queryDateTo) {
        return getAllForPerson(personId).stream()
                .anyMatch(blockData -> isWithinInterval(blockData, queryDateFrom) || isWithinInterval(blockData, queryDateTo));
    }

    private boolean isWithinInterval(BlockData blockData, LocalDate queryDate) {
        // Either touches start/end or is in within
        return queryDate.isEqual(blockData.getBlockFrom()) || queryDate.isEqual(blockData.getBlockTo())
                || (queryDate.isAfter(blockData.getBlockFrom()) && queryDate.isBefore(blockData.getBlockTo()));
    }

    public void add(BlockData data) {
        repository.add(data);
    }

    public void remove(String personId) {
        repository.removeIf(blockData -> blockData.getPersonId().equals(personId));
    }
}
