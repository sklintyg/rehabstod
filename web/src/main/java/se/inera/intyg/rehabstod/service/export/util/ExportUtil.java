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
package se.inera.intyg.rehabstod.service.export.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

/**
 * We need to export SjukfallEnhet in the exact same order as the personnummer list
 * provides them.
 *
 * Created by eriklupander on 2016-02-24.
 */
public final class ExportUtil {

    private ExportUtil() {

    }

    public static List<SjukfallEnhet> sortForExport(List<String> personnummer, List<SjukfallEnhet> sjukfall) {

        List<SjukfallEnhet> finalList = new ArrayList<>();

        personnummer.stream().forEach(pNr ->
            addSjukfallForPnr(sjukfall, finalList, pNr)
        );

        return finalList;
    }

    private static void addSjukfallForPnr(List<SjukfallEnhet> sjukfall, List<SjukfallEnhet> finalList, String pNr) {
        Optional<SjukfallEnhet> sjukfallForPatient = sjukfall.stream()
            .filter(is -> is.getPatient().getId().equals(pNr))
            .findFirst();

        if (sjukfallForPatient.isPresent()) {
            finalList.add(sjukfallForPatient.get());
        }
    }
}
