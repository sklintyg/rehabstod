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
import javax.annotation.PostConstruct;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

/**
 * Simple in-memory store for blocks.
 * Created by marced on 2018-10-01.
 */
@Component
public class SparrtjanstStubStore {
    private static final String VE_TSTNMT2321000156_105Q = "TSTNMT2321000156-105Q";
    private static final String VE_CENTRUM_VAST = "centrum-vast";
    private static final String VG1 = "TSTNMT2321000156-105M";
    private static final String VG2 = "vastmanland";


    private List<BlockData> repository = new ArrayList<>();

    @PostConstruct
    public void init() {
        generateDefaultData();
    }

    // CHECKSTYLE:OFF MagicNumber
    private void generateDefaultData() {
        // sjf data
        int days = 10;
        String pnr = "201212121212";
        LocalDate blockFrom = LocalDate.now().minusDays(days);
        LocalDate blockTo = LocalDate.now().plusDays(days);

        repository.add(new BlockData(pnr, blockFrom, blockTo, VG1, VE_TSTNMT2321000156_105Q));
        repository.add(new BlockData(pnr, blockFrom, blockTo, VG2, VE_CENTRUM_VAST));
    }
    // CHECKSTYLE:ON MagicNumber

    public List<BlockData> getAll() {
        return repository;
    }

    public void removeAll() {
        repository.clear();
    }

    public List<BlockData> getAllForPerson(String personId) {
        return repository.stream().filter(blockData -> blockData.getPersonId().equals(personId)).collect(Collectors.toList());
    }

    public boolean isBlockedAtDate(String personId, LocalDate queryDateFrom, LocalDate queryDateTo,
            String vardGivareId, String vardEnhetId) {
        return getAllForPerson(personId).stream()
                .filter(blockData -> {
                    boolean vardGivare = Strings.isNullOrEmpty(blockData.getVardGivareId())
                            || blockData.getVardGivareId().equals(vardGivareId);
                    boolean vardEnhet = Strings.isNullOrEmpty(blockData.getVardEnhetId()) || blockData.getVardEnhetId().equals(vardEnhetId);

                    return vardGivare && vardEnhet;
                })
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
