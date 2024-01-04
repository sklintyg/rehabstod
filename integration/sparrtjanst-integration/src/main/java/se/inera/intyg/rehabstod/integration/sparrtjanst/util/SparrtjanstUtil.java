/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.integration.sparrtjanst.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Optional;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.blocking.v4.AccessingActorType;
import se.riv.informationsecurity.authorization.blocking.v4.IIType;

/**
 * Created by marced on 2018-09-26.
 */
public final class SparrtjanstUtil {

    public static final String KODVERK_SAMORDNINGSNUMMER = "1.2.752.129.2.1.3.3";
    public static final String KODVERK_PERSONNUMMER = "1.2.752.129.2.1.3.1";

    private static final int SAMORDNING_MONTH_INDEX = 6;
    private static final int SAMORDNING_MONTH_VALUE_MIN = 6;

    private SparrtjanstUtil() {

    }

    /**
     * Controls if a civic registration number is a 'samordningsnummer' or not.
     *
     * @param personNummer the civic registration number
     * @return true if the civic registration number is a 'samordningsnummer', otherwise false
     */
    private static boolean isSamordningsNummer(Personnummer personNummer) {
        // In order to determine if a personnummer is a samordningsnummer, we need to have a normalized yyyyMMddNNNN
        // number. If we cannot parse the encapsulated string, it certainly isn't a personnummer.
        if (Optional.ofNullable(personNummer).isPresent()) {
            String normalizedPersonnummer = personNummer.getPersonnummer();
            char dateDigit = normalizedPersonnummer.charAt(SAMORDNING_MONTH_INDEX);
            return Character.getNumericValue(dateDigit) >= SAMORDNING_MONTH_VALUE_MIN;
        }

        // An invalid personnummer cannot be a samordningsnummer.
        return false;
    }

    public static String getRootForPersonnummer(Personnummer personnummer) {
        return isSamordningsNummer(personnummer) ? KODVERK_SAMORDNINGSNUMMER : KODVERK_PERSONNUMMER;
    }

    public static IIType buildIITypeForPersonOrSamordningsnummer(Personnummer personnummer) {
        IIType patient = new IIType();
        patient.setRoot(getRootForPersonnummer(personnummer));
        patient.setExtension(personnummer.getPersonnummer());
        return patient;
    }

    public static AccessingActorType buildAccessingActorType(String careProviderId, String careUnitId, String employeeId) {
        // In AccessingActorType schema, alla properties are mandatory...
        Preconditions.checkArgument(!Strings.isNullOrEmpty(careProviderId), "careProviderId is mandatory");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(careUnitId), "careUnitId is mandatory");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(employeeId), "employeeId is mandatory");
        AccessingActorType accessingActor = new AccessingActorType();
        accessingActor.setCareProviderId(careProviderId);
        accessingActor.setCareUnitId(careUnitId);
        accessingActor.setEmployeeId(employeeId);
        return accessingActor;
    }
}
