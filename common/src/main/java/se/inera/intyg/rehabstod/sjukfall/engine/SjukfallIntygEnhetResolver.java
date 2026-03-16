/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.sjukfall.engine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.rehabstod.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.sjukfall.dto.IntygParametrar;
import se.inera.intyg.rehabstod.sjukfall.dto.SjukfallIntyg;
import se.inera.intyg.rehabstod.sjukfall.services.SjukfallEngineServiceException;

public class SjukfallIntygEnhetResolver {

  private static final Logger LOG = LoggerFactory.getLogger(SjukfallIntygEnhetResolver.class);

  private SjukfallIntygEnhetCreator creator = new SjukfallIntygEnhetCreator();

  public SjukfallIntygEnhetResolver(SjukfallIntygEnhetCreator creator) {
    this.creator = creator;
  }

  public Map<String, List<SjukfallIntyg>> resolve(
      List<IntygData> intygsData, IntygParametrar parameters) {

    int maxIntygsGlapp = parameters.getMaxIntygsGlapp();
    LOG.debug("Start resolving certificate information...");
    LOG.debug(
        "  - max days between certificates: {}, active date: {}",
        maxIntygsGlapp,
        parameters.getAktivtDatum());

    if (intygsData == null || intygsData.isEmpty()) {
      LOG.info("There was no in-data! Returning empty list");
      return new HashMap<>();
    }

    if (maxIntygsGlapp < 0) {
      LOG.info(
          "Maximal days between certificates was {}. Value must be equal or greater than zero",
          maxIntygsGlapp);
      return new HashMap<>();
    }

    Map<String, List<SjukfallIntyg>> createdMap = createMap(intygsData, parameters);
    Map<String, List<SjukfallIntyg>> reducedMap = reduceMap(createdMap, maxIntygsGlapp);

    LOG.debug("...stop resolving certificate information.");
    return reducedMap;
  }

  Map<String, List<SjukfallIntyg>> createMap(
      List<IntygData> intygsData, IntygParametrar parameters) {
    if (intygsData == null || intygsData.isEmpty()) {
      return new HashMap<>();
    }
    return creator.create(intygsData, parameters);
  }

  Map<String, List<SjukfallIntyg>> reduceMap(
      Map<String, List<SjukfallIntyg>> intygsDataMap, int maxIntygsGlapp) {

    LOG.debug(
        "  - Reduce certificates. Only certificates fulfilling request parameter 'maxIntygsGlapp' will be concerned.");

    Map<String, List<SjukfallIntyg>> resultMap = new HashMap<>();

    for (Map.Entry<String, List<SjukfallIntyg>> entry : intygsDataMap.entrySet()) {
      List<SjukfallIntyg> reducedList = reduceList(entry.getValue(), maxIntygsGlapp);
      if (!reducedList.isEmpty()) {
        resultMap.put(entry.getKey(), reducedList);
      }
    }

    return resultMap;
  }

  List<SjukfallIntyg> reduceList(List<SjukfallIntyg> values, int maxIntygsGlapp) {

    SjukfallIntyg sjukfallIntyg =
        values.stream().filter(e -> e.isAktivtIntyg()).findFirst().orElse(null);

    if (sjukfallIntyg == null) {
      sjukfallIntyg =
          values.stream()
              .filter(e -> e.isNyligenAvslutat())
              .findFirst()
              .orElseThrow(
                  () ->
                      new SjukfallEngineServiceException(
                          "Unable to find a 'aktivt eller nyligen avslutat intyg'"));
    }

    int aktivtIndex = values.indexOf(sjukfallIntyg);

    List<SjukfallIntyg> left = new ArrayList<>();
    if (aktivtIndex > 0) {
      left = values.subList(0, aktivtIndex);
    }

    List<SjukfallIntyg> right = new ArrayList<>();
    if (aktivtIndex < values.size() - 1) {
      right = values.subList(aktivtIndex + 1, values.size());
    }

    right = reduceRight(right, maxIntygsGlapp, sjukfallIntyg.getSlutDatum());
    left = reduceLeft(left, maxIntygsGlapp, getCompareDate(right, sjukfallIntyg));

    List<SjukfallIntyg> reducedList = new ArrayList<>();
    reducedList.addAll(left);
    reducedList.add(sjukfallIntyg);
    reducedList.addAll(right);

    return reducedList;
  }

  List<SjukfallIntyg> reduceRight(
      List<SjukfallIntyg> right, int maxIntygsGlapp, LocalDate initialCompareDate) {
    right.sort((o1, o2) -> o1.getStartDatum().compareTo(o2.getStartDatum()));

    List<SjukfallIntyg> list = new ArrayList<>();
    LocalDate compareDate = initialCompareDate;

    for (SjukfallIntyg nextRight : right) {
      LocalDate start = nextRight.getStartDatum();
      LocalDate lastValidStartDate = compareDate.plusDays(maxIntygsGlapp + 1);

      if (lastValidStartDate.compareTo(start) > -1) {
        list.add(nextRight);
      } else {
        break;
      }

      compareDate = nextRight.getSlutDatum();
    }

    return list;
  }

  List<SjukfallIntyg> reduceLeft(
      List<SjukfallIntyg> left, int maxIntygsGlapp, LocalDate initialCompareDate) {
    List<SjukfallIntyg> list = new ArrayList<>();
    LocalDate compareDate = initialCompareDate;

    for (int i = left.size() - 1; i >= 0; i--) {
      SjukfallIntyg nextLeft = left.get(i);

      LocalDate end = nextLeft.getSlutDatum();
      LocalDate lastValidEndDate = compareDate.minusDays(maxIntygsGlapp + 1);

      if (lastValidEndDate.compareTo(end) < 1) {
        list.add(0, nextLeft);
      } else {
        break;
      }

      compareDate = nextLeft.getStartDatum();
    }

    return list;
  }

  private LocalDate getCompareDate(List<SjukfallIntyg> right, SjukfallIntyg sjukfallIntyg) {
    LocalDate smallest;

    if (right == null || right.isEmpty()) {
      return sjukfallIntyg.getStartDatum();
    } else if (right.size() == 1) {
      smallest = right.get(0).getStartDatum();
    } else {
      smallest =
          right.stream()
              .min((o1, o2) -> o1.getStartDatum().compareTo(o2.getStartDatum()))
              .get()
              .getStartDatum();
    }

    if (smallest.isBefore(sjukfallIntyg.getStartDatum())) {
      return smallest;
    }

    return sjukfallIntyg.getStartDatum();
  }
}
