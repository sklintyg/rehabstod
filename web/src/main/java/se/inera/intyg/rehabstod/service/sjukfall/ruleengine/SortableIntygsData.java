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


import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

/**
 * Created by Magnus Ekstrand on 2016-02-15.
 */
public class SortableIntygsData extends IntygsData {

    private static final int HASH_SEED = 31;

    private LocalDate startDatum;
    private LocalDate slutDatum;
    private LocalDateTime signeringsTidpunkt;

    private boolean aktivtIntyg;


    public SortableIntygsData() {
        super();
    }

    public static SortableIntygsData createInstance(IntygsData intygsData, LocalDate aktivtDatum) {
        // Copy intygsData object
        SortableIntygsData sortable = copy(intygsData);

        // Calculate start and end dates and if it's active
        sortable.setStartDatum(lookupStartDatum(intygsData.getArbetsformaga().getFormaga()));
        sortable.setSlutDatum(lookupSlutDatum(intygsData.getArbetsformaga().getFormaga()));
        sortable.setAktivtIntyg(hasAktivFormaga(intygsData.getArbetsformaga().getFormaga(), aktivtDatum));

        return sortable;
    }

    public LocalDate getStartDatum() {
        return startDatum;
    }

    public void setStartDatum(LocalDate startDatum) {
        this.startDatum = startDatum;
    }

    public LocalDate getSlutDatum() {
        return slutDatum;
    }

    public void setSlutDatum(LocalDate slutDatum) {
        this.slutDatum = slutDatum;
    }

    public boolean isAktivtIntyg() {
        return aktivtIntyg;
    }

    public void setAktivtIntyg(boolean aktivtIntyg) {
        this.aktivtIntyg = aktivtIntyg;
    }

    public LocalDateTime getSigneringsTidpunkt() {
        return signeringsTidpunkt;
    }

    public void setSigneringsTidpunkt(LocalDateTime signeringsTidpunkt) {
        this.signeringsTidpunkt = signeringsTidpunkt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SortableIntygsData that = (SortableIntygsData) o;

        if (!startDatum.equals(that.startDatum)) return false;
        return slutDatum.equals(that.slutDatum);

    }

    @Override
    public int hashCode() {
        int result = startDatum.hashCode();
        result = HASH_SEED * result + slutDatum.hashCode();
        return result;
    }

    static boolean hasAktivFormaga(List<Formaga> formagor, LocalDate aktivtDatum) {
        return !formagor.stream()
                .anyMatch(f -> f.getStartdatum().isBefore(aktivtDatum) || f.getSlutdatum().isAfter(aktivtDatum));
    }

    static LocalDate lookupStartDatum(List<Formaga> formagor) {
        Formaga formaga = formagor.stream()
                .min((o1, o2) -> o1.getStartdatum().compareTo(o2.getStartdatum())).get();
        return formaga.getStartdatum();
    }

    static LocalDate lookupSlutDatum(List<Formaga> formagor) {
        Formaga formaga = formagor.stream()
                .max((o1, o2) -> o1.getSlutdatum().compareTo(o2.getSlutdatum())).get();
        return formaga.getSlutdatum();
    }

    private static SortableIntygsData copy(IntygsData intygsData) {
        SortableIntygsData o = new SortableIntygsData();
        o.setIntygsId(intygsData.getIntygsId());
        o.setPatient(intygsData.getPatient());
        o.setSkapadAv(intygsData.getSkapadAv());
        o.setDiagnoskod(intygsData.getDiagnoskod());
        o.setArbetsformaga(intygsData.getArbetsformaga());
        o.setEnkeltIntyg(intygsData.isEnkeltIntyg());
        o.setSigneringsTidpunkt(intygsData.getSigneringsTidpunkt());

        return o;
    }

}
