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

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 10/02/16.
 */
@Component
public class SjukfallResolverImpl implements SjukfallResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallResolverImpl.class);

    private SjukfallMapper mapper;

    @Autowired
    public SjukfallResolverImpl(SjukfallMapper mapper) {
        this.mapper = mapper;
    }


    // API

    @Override
    public List<Sjukfall> resolve(List<IntygsData> intygsData, int maxIntygsGlapp, LocalDate aktivtDatum) {

        if (intygsData == null || intygsData.size() == 0) {
            LOG.debug("There was no in-data! Returning empty list");
            return new ArrayList<Sjukfall>();
        }

        if (maxIntygsGlapp < 0) {
            LOG.debug("Maximal days between certificates was {}. Value must be equal or greater than zero", maxIntygsGlapp);
            return new ArrayList<Sjukfall>();
        }

        // Create an map with personnummer as key holding each person's intygsdata
        Map<String, List<SortableIntygsData>> map = toMap(intygsData, aktivtDatum);

        // Create Sjukfall objects
        List<Sjukfall> sjukfallList = assembleSjukfall(map, maxIntygsGlapp, aktivtDatum);

        return sjukfallList;
    }


    // Package scope

    List<Sjukfall> assembleSjukfall(Map<String, List<SortableIntygsData>> map, int maxIntygsGlapp, LocalDate aktivtDatum) {

        List<Sjukfall> list = new ArrayList<>();

        // 2. Remove intyg that is not within 'maxIntygsGlapp'
        //sortedMap.entrySet().stream().forEach(e -> e.setValue(reduce(e.getValue(), maxIntygsGlapp)));

        // 3.
        mapper.map(new ArrayList<>());

        return list;
    }

    /**
     * Method returns a map with intermediate IntygsData objects.
     * Patient's personal-id is used as key.
     *
     * @param intygsData a list with basic IntygsData
     * @param aktivtDatum a date used for decision if a certificate is active or not
     * @return
     */
    Map<String, List<SortableIntygsData>> toMap(List<IntygsData> intygsData, LocalDate aktivtDatum) {
        Map<String, List<SortableIntygsData>> map = new HashMap();

        if (intygsData == null || intygsData.size() == 0) {
            return map;
        }

        SortableIntygsDataCreator creator = new SortableIntygsDataCreator();
        map = creator.create(intygsData, aktivtDatum);

        return map;
    }

}
