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
package se.inera.intyg.rehabstod.service.sjukfall.nameresolver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.service.hsa.EmployeeNameService;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

/**
 * Created by eriklupander on 2017-02-23.
 */
@Service
public class SjukfallEmployeeNameResolverImpl implements SjukfallEmployeeNameResolver {

    @Autowired
    private EmployeeNameService employeeNameService;

    @Override
    public void enrichWithHsaEmployeeNames(List<SjukfallEnhet> sjukfallList) {
        sjukfallList.forEach(sf -> updateEmployeeName(sf.getLakare()));
    }

    @Override
    public void enrichSjukfallPaientWithHsaEmployeeNames(List<SjukfallPatient> sjukfallList) {
        sjukfallList.forEach(sf -> {
            if (sf.getIntyg() != null) {
                sf.getIntyg().forEach(i -> updateEmployeeName(i.getLakare()));
            }
        });
    }

    private void updateEmployeeName(Lakare lakare) {
        if (lakare == null) {
            return;
        }
        String employeeHsaName = employeeNameService.getEmployeeHsaName(lakare.getHsaId());
        if (employeeHsaName != null) {
            lakare.setNamn(employeeHsaName);
        } else {
            lakare.setNamn(lakare.getHsaId());
        }
    }

    @Override
    public void updateDuplicateDoctorNamesWithHsaId(List<SjukfallEnhet> sjukfallList) {
        // Get number of unique lakare hsaIds
        long numberOfHsaIds = sjukfallList.stream().map(sf -> sf.getLakare().getHsaId()).distinct().count();
        long numberOfLakareNames = sjukfallList.stream().map(sf -> sf.getLakare().getNamn()).distinct().count();

        // If these counts don't add up we need to post-process the names.
        if (numberOfHsaIds != numberOfLakareNames) {

            // Make sure there are no two doctors in the list with the same name, but different hsaId's
            Map<String, List<Lakare>> collect = sjukfallList.stream()
                .map(SjukfallEnhet::getLakare)
                .collect(Collectors.groupingBy(Lakare::getNamn));

            for (Map.Entry<String, List<Lakare>> entry : collect.entrySet()) {

                double numberOfUnique = entry.getValue().stream().map(Lakare::getHsaId).distinct().count();
                if (numberOfUnique > 1) {
                    entry.getValue().forEach(lakare -> {
                        lakare.setNamn(lakare.getNamn() + " (" + lakare.getHsaId() + ")");
                    });
                }
            }
        }
    }
}
