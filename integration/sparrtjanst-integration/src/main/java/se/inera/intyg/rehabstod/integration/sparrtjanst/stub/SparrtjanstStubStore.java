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
package se.inera.intyg.rehabstod.integration.sparrtjanst.stub;

import com.google.common.base.Strings;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * Simple embedded redis cache store for blocks.
 * Created by marced on 2018-10-01.
 */
@Component
public class SparrtjanstStubStore {

    private static final String VE_TSTNMT2321000156_105Q = "TSTNMT2321000156-105Q";
    private static final String VE_CENTRUM_VAST = "centrum-vast";
    private static final String VE_3 = "IFV1239877878-103H";
    private static final String VG1 = "TSTNMT2321000156-105M";
    private static final String VG2 = "vastmanland";
    private static final String VG3 = "ifv-testdata";

    // inject the actual template
    @Autowired
    @Qualifier("rediscache")
    private RedisTemplate<Object, Object> redisTemplate;

    // inject the template as ValueOperations
    @Resource(name = "rediscache")
    private ValueOperations<String, BlockData> valueOps;

    @PostConstruct
    public void init() {
        generateDefaultData();
    }

    public void add(BlockData data) {
        valueOps.set(assembleCacheKey(data), data);
    }

    public List<BlockData> getAll() {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*"));
        return valueOps.multiGet(keys);
    }

    public void remove(String personId) {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*" + personId));
        valueOps.getOperations().delete(keys);
    }

    public void removeAll() {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*"));
        valueOps.getOperations().delete(keys);
    }

    public List<BlockData> getAllForPerson(String personId) {
        Set<String> keys = valueOps.getOperations().keys(assemblePattern("*" + personId));
        return valueOps.multiGet(keys);
    }

    public boolean isBlockedAtDate(String personId, LocalDate queryDateFrom, LocalDate queryDateTo,
        String vardGivareId, String vardEnhetId) {
        return getAllForPerson(personId).stream()
            .filter(blockData -> {
                boolean isVg = Strings.isNullOrEmpty(blockData.getVardGivareId()) || blockData.getVardGivareId().equals(vardGivareId);
                boolean isVe = Strings.isNullOrEmpty(blockData.getVardEnhetId()) || blockData.getVardEnhetId().equals(vardEnhetId);
                return isVg && isVe;
            })
            .anyMatch(blockData -> isWithinInterval(blockData, queryDateFrom) || isWithinInterval(blockData, queryDateTo));
    }

    private String assemblePattern(String pattern) {
        return SparrtjanstStubConfiguration.CACHE_NAME + pattern;
    }

    private String assembleCacheKey(BlockData data) {
        return assembleCacheKey(data.getVardGivareId(), data.getVardEnhetId(), data.getPersonId());
    }

    private String assembleCacheKey(String vgHsaId, String veHsaId, String patientId) {
        return Stream.of(SparrtjanstStubConfiguration.CACHE_NAME, vgHsaId, veHsaId, patientId)
            .collect(Collectors.joining(":"));
    }

    // CHECKSTYLE:OFF MagicNumber
    private void generateDefaultData() {
        // sjf data
        int days = 10;
        String pnr = "201212121212";
        LocalDate blockFrom = LocalDate.now().minusDays(days);
        LocalDate blockTo = LocalDate.now().plusDays(days);

        add(new BlockData(pnr, blockFrom, blockTo, VG1, VE_TSTNMT2321000156_105Q));
        add(new BlockData(pnr, blockFrom, blockTo, VG2, VE_CENTRUM_VAST));
        add(new BlockData(pnr, LocalDate.now().minusDays(120), blockTo, VG3, VE_3));
    }
    // CHECKSTYLE:ON MagicNumber

    private boolean isWithinInterval(BlockData blockData, LocalDate queryDate) {
        // Either touches start/end or is in within
        return queryDate.isEqual(blockData.getBlockFrom()) || queryDate.isEqual(blockData.getBlockTo())
            || (queryDate.isAfter(blockData.getBlockFrom()) && queryDate.isBefore(blockData.getBlockTo()));
    }

}
