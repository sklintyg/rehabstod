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
package se.inera.intyg.rehabstod.web.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;

/**
 * @author Magnus Ekstrand on 2017-09-01.
 */
public class PatientData {

    private String vardgivareId;
    private String vardgivareNamn;
    private String vardenhetNamn;
    private String vardenhetId;

    private Patient patient;

    private Diagnos diagnos;
    private List<Diagnos> bidiagnoser;

    private LocalDate start;
    private LocalDate slut;

    private LocalDateTime signeringsTidpunkt;

    private int dagar;

    private List<Integer> grader;

    private Lakare lakare;

    private List<String> sysselsattning;

    private boolean aktivtIntyg;

    private String intygsId;
    private Integer obesvaradeKompl;
    private Integer unansweredOther;

    private RiskSignal riskSignal;

    private boolean otherVardgivare;
    private boolean otherVardenhet;

    // - - - getters and setters

    public String getVardgivareId() {
        return vardgivareId;
    }

    public void setVardgivareId(String vardgivareId) {
        this.vardgivareId = vardgivareId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    public void setVardgivareNamn(String vardgivareNamn) {
        this.vardgivareNamn = vardgivareNamn;
    }

    public String getVardenhetNamn() {
        return vardenhetNamn;
    }

    public void setVardenhetNamn(String vardenhetNamn) {
        this.vardenhetNamn = vardenhetNamn;
    }

    public String getVardenhetId() {
        return vardenhetId;
    }

    public void setVardenhetId(String vardenhetId) {
        this.vardenhetId = vardenhetId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Diagnos getDiagnos() {
        return diagnos;
    }

    public void setDiagnos(Diagnos diagnos) {
        this.diagnos = diagnos;
    }

    public List<Diagnos> getBidiagnoser() {
        return bidiagnoser;
    }

    public void setBidiagnoser(List<Diagnos> bidiagnoser) {
        this.bidiagnoser = bidiagnoser;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getSlut() {
        return slut;
    }

    public void setSlut(LocalDate slut) {
        this.slut = slut;
    }

    public LocalDateTime getSigneringsTidpunkt() {
        return signeringsTidpunkt;
    }

    public void setSigneringsTidpunkt(LocalDateTime signeringsTidpunkt) {
        this.signeringsTidpunkt = signeringsTidpunkt;
    }

    public int getDagar() {
        return dagar;
    }

    public void setDagar(int dagar) {
        this.dagar = dagar;
    }

    public List<Integer> getGrader() {
        return grader;
    }

    public void setGrader(List<Integer> grader) {
        this.grader = grader;
    }

    public Lakare getLakare() {
        return lakare;
    }

    public void setLakare(Lakare lakare) {
        this.lakare = lakare;
    }

    public List<String> getSysselsattning() {
        return sysselsattning;
    }

    public void setSysselsattning(List<String> sysselsattning) {
        this.sysselsattning = sysselsattning;
    }

    public boolean isAktivtIntyg() {
        return aktivtIntyg;
    }

    public void setAktivtIntyg(boolean aktivtIntyg) {
        this.aktivtIntyg = aktivtIntyg;
    }

    public String getIntygsId() {
        return intygsId;
    }

    public void setIntygsId(String intygsId) {
        this.intygsId = intygsId;
    }

    public RiskSignal getRiskSignal() {
        return riskSignal;
    }

    public void setRiskSignal(RiskSignal riskSignal) {
        this.riskSignal = riskSignal;
    }

    public boolean isOtherVardgivare() {
        return otherVardgivare;
    }

    public void setOtherVardgivare(boolean otherVardgivare) {
        this.otherVardgivare = otherVardgivare;
    }

    public boolean isOtherVardenhet() {
        return otherVardenhet;
    }

    public void setOtherVardenhet(boolean otherVardenhet) {
        this.otherVardenhet = otherVardenhet;
    }

    public Integer getObesvaradeKompl() {
        return obesvaradeKompl;
    }

    public void setObesvaradeKompl(Integer obesvaradeKompl) {
        this.obesvaradeKompl = obesvaradeKompl;
    }

    public Integer getUnansweredOther() {
        return unansweredOther;
    }

    public void setUnansweredOther(Integer unansweredOther) {
        this.unansweredOther = unansweredOther;
    }
}
