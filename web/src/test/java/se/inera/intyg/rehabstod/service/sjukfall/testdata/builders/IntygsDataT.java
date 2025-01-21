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
package se.inera.intyg.rehabstod.service.sjukfall.testdata.builders;

import java.time.LocalDateTime;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Arbetsformaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;


/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public final class IntygsDataT {

    private IntygsDataT() {
    }

    public static class IntygsDataBuilder implements Builder<IntygsData> {

        private String intygsId;
        private Patient patient;
        private HosPersonal skapadAv;
        private String diagnoskod;
        private Arbetsformaga arbetsformaga;
        private boolean enkeltIntyg;
        private LocalDateTime signeringsTidpunkt;

        public IntygsDataBuilder() {
        }

        public IntygsDataBuilder intygsId(String intygsId) {
            this.intygsId = intygsId;
            return this;
        }

        public IntygsDataBuilder patient(Patient patient) {
            this.patient = patient;
            return this;
        }

        public IntygsDataBuilder skapadAv(HosPersonal skapadAv) {
            this.skapadAv = skapadAv;
            return this;
        }

        public IntygsDataBuilder diagnoskod(String diagnoskod) {
            this.diagnoskod = diagnoskod;
            return this;
        }

        public IntygsDataBuilder arbetsformaga(Arbetsformaga arbetsformaga) {
            this.arbetsformaga = arbetsformaga;
            return this;
        }

        public IntygsDataBuilder enkeltIntyg(boolean enkeltIntyg) {
            this.enkeltIntyg = enkeltIntyg;
            return this;
        }

        public IntygsDataBuilder signeringsTidpunkt(LocalDateTime signeringsTidpunkt) {
            this.signeringsTidpunkt = signeringsTidpunkt;
            return this;
        }

        @Override
        public IntygsData build() {
            IntygsData intygsData = new IntygsData();
            intygsData.setIntygsId(this.intygsId);
            intygsData.setPatient(this.patient);
            intygsData.setSkapadAv(this.skapadAv);
            intygsData.setDiagnoskod(this.diagnoskod);
            intygsData.setArbetsformaga(this.arbetsformaga);
            intygsData.setEnkeltIntyg(this.enkeltIntyg);
            intygsData.setSigneringsTidpunkt(this.signeringsTidpunkt);

            return intygsData;
        }

    }
}
