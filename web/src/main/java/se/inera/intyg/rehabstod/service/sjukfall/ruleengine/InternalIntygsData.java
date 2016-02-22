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
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;

/**
 * Created by Magnus Ekstrand on 2016-02-15.
 */
public class InternalIntygsData extends IntygsData {

    private static final int HASH_SEED = 31;

    private LocalDate startDatum;
    private LocalDate slutDatum;

    private boolean aktivtIntyg;

    public InternalIntygsData(InternalIntygsDataBuilder builder) {
        super();

        this.startDatum = builder.startDatum;
        this.slutDatum = builder.slutDatum;
        this.aktivtIntyg = builder.aktivtIntyg;

        this.setIntygsId(builder.intygsData.getIntygsId());
        this.setPatient(builder.intygsData.getPatient());
        this.setSkapadAv(builder.intygsData.getSkapadAv());
        this.setArbetsformaga(builder.intygsData.getArbetsformaga());
        this.setDiagnoskod(builder.intygsData.getDiagnoskod());
        this.setEnkeltIntyg(builder.intygsData.isEnkeltIntyg());
        this.setSigneringsTidpunkt(builder.intygsData.getSigneringsTidpunkt());
    }

    // Getters and setters

    public LocalDate getStartDatum() {
        return startDatum;
    }

    public LocalDate getSlutDatum() {
        return slutDatum;
    }

    public boolean isAktivtIntyg() {
        return aktivtIntyg;
    }

    public void setAktivtIntyg(boolean aktivtIntyg) {
        this.aktivtIntyg = aktivtIntyg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InternalIntygsData that = (InternalIntygsData) o;
        if (!startDatum.equals(that.startDatum)) {
            return false;
        }

        return slutDatum.equals(that.slutDatum);
    }

    @Override
    public int hashCode() {
        int result = startDatum.hashCode();
        result = HASH_SEED * result + slutDatum.hashCode();
        return result;
    }

    public static class InternalIntygsDataBuilder {

        private final IntygsData intygsData;

        private LocalDate startDatum;
        private LocalDate slutDatum;

        private boolean aktivtIntyg;

        public InternalIntygsDataBuilder(IntygsData intygsData, LocalDate aktivtDatum) {
            this.intygsData = intygsData;
            this.startDatum = lookupStartDatum(intygsData.getArbetsformaga().getFormaga());
            this.slutDatum = lookupSlutDatum(intygsData.getArbetsformaga().getFormaga());
            this.aktivtIntyg = hasAktivFormaga(intygsData.getArbetsformaga().getFormaga(), aktivtDatum);
        }

        public InternalIntygsData build() {
            return new InternalIntygsData(this);
        }

        private boolean hasAktivFormaga(List<Formaga> formagor, LocalDate aktivtDatum) {
            return formagor.stream()
                    .anyMatch(f -> isAktivFormaga(aktivtDatum, f));
        }

        private boolean isAktivFormaga(LocalDate aktivtDatum, Formaga f) {
            return f.getStartdatum().compareTo(aktivtDatum) < 1 && f.getSlutdatum().compareTo(aktivtDatum) > -1;
        }

        private LocalDate lookupStartDatum(List<Formaga> formagor) {
            Formaga formaga = formagor.stream()
                    .min((o1, o2) -> o1.getStartdatum().compareTo(o2.getStartdatum())).get();
            return formaga.getStartdatum();
        }

        private LocalDate lookupSlutDatum(List<Formaga> formagor) {
            Formaga formaga = formagor.stream()
                    .max((o1, o2) -> o1.getSlutdatum().compareTo(o2.getSlutdatum())).get();
            return formaga.getSlutdatum();
        }

    }
}
