/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Befattningar;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.HosPersonal;

/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public final class HosPersonalT {

    public HosPersonalT() {
    }

    public static class HosPersonalBuilder implements Builder<HosPersonal> {

        private HsaId personalId;
        private String fullstandigtNamn;
        private Befattningar befattningar;
        private Enhet enhet;

        public HosPersonalBuilder() {
        }

        public HosPersonalBuilder personalId(HsaId personalId) {
            this.personalId = personalId;
            return this;
        }

        public HosPersonalBuilder fullstandigtNamn(String fullstandigtNamn) {
            this.fullstandigtNamn = fullstandigtNamn;
            return this;
        }

        public HosPersonalBuilder befattningar(Befattningar befattningar) {
            this.befattningar = befattningar;
            return this;
        }

        public HosPersonalBuilder enhet(Enhet enhet) {
            this.enhet = enhet;
            return this;
        }

        @Override
        public HosPersonal build() {
            HosPersonal hosPersonal = new HosPersonal();
            hosPersonal.setPersonalId(personalId);
            hosPersonal.setFullstandigtNamn(fullstandigtNamn);
            hosPersonal.setEnhet(enhet);

            return hosPersonal;
        }
    }
}
