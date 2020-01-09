/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.web.controller.api.dto;

import java.time.LocalDate;
import java.util.List;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;
import se.inera.intyg.rehabstod.web.model.Sortering;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class GetSjukfallRequest {

    private Sortering sortering;
    private LangdIntervall langdIntervall;
    private LangdIntervall aldersIntervall;
    private LangdIntervall slutdatumIntervall;

    private boolean showPatientId;
    private LocalDate aktivtDatum;

    private List<String> lakare;
    private List<String> diagnosGrupper;

    private String patientId;
    private String fritext;
    private Integer komplettering;

    /**
     * The sole constructor.
     **/
    public GetSjukfallRequest() {
        aktivtDatum = LocalDate.now();
    }

    public Sortering getSortering() {
        return sortering;
    }

    public void setSortering(Sortering sortering) {
        this.sortering = sortering;
    }

    public LangdIntervall getLangdIntervall() {
        return langdIntervall;
    }

    public void setLangdIntervall(LangdIntervall langdIntervall) {
        this.langdIntervall = langdIntervall;
    }

    public LangdIntervall getAldersIntervall() {
        return aldersIntervall;
    }

    public void setAldersIntervall(LangdIntervall aldersIntervall) {
        this.aldersIntervall = aldersIntervall;
    }

    public LangdIntervall getSlutdatumIntervall() {
        return slutdatumIntervall;
    }

    public void setSlutdatumIntervall(LangdIntervall slutdatumIntervall) {
        this.slutdatumIntervall = slutdatumIntervall;
    }

    public LocalDate getAktivtDatum() {
        return aktivtDatum;
    }

    public void setAktivtDatum(LocalDate aktivtDatum) {
        this.aktivtDatum = aktivtDatum;
    }

    public List<String> getLakare() {
        return lakare;
    }

    public void setLakare(List<String> lakare) {
        this.lakare = lakare;
    }

    public List<String> getDiagnosGrupper() {
        return diagnosGrupper;
    }

    public void setDiagnosGrupper(List<String> diagnosGrupper) {
        this.diagnosGrupper = diagnosGrupper;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getFritext() {
        return fritext;
    }

    public void setFritext(String fritext) {
        this.fritext = fritext;
    }

    public boolean isShowPatientId() {
        return showPatientId;
    }

    public void setShowPatientId(boolean showPatientId) {
        this.showPatientId = showPatientId;
    }

    public Integer getKomplettering() {
        return komplettering;
    }

    public void setKomplettering(Integer komplettering) {
        this.komplettering = komplettering;
    }
}
