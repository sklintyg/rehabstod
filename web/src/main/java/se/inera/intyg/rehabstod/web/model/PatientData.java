/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 * <p>
 * This file is part of sklintyg (https://github.com/sklintyg).
 * <p>
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.web.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Magnus Ekstrand on 2017-09-01.
 */
public class PatientData {

    private Patient patient;

    private Diagnos diagnos;
    private List<Diagnos> bidiagnoser;

    private LocalDate start;
    private LocalDate slut;

    private LocalDateTime signeringsTidpunkt;

    private int dagar;

    private List<Integer> grader;

    private String lakare;
    private List<String> sysselsattning;

    boolean aktivtIntyg;

    private String enhetId;


    // - - - getters and setters

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

    public String getLakare() {
        return lakare;
    }

    public void setLakare(String lakare) {
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

    public String getEnhetId() {
        return enhetId;
    }

    public void setEnhetId(String enhetId) {
        this.enhetId = enhetId;
    }
}
