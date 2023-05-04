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
package se.inera.intyg.rehabstod.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;

/**
 * Created by eriklupander on 2016-02-19.
 */
public class SjukfallEnhet {

    private String vardGivareId;
    private String vardGivareNamn;
    private String vardEnhetId;
    private String vardEnhetNamn;

    private Lakare lakare;
    private Patient patient;

    private Diagnos diagnos;
    private List<Diagnos> biDiagnoser;

    private LocalDate start;
    private LocalDate slut;
    private long slutOmDagar;

    private int dagar;
    private int intyg;
    private int obesvaradeKompl;
    private int unansweredOther;

    @JsonIgnore
    private List<String> intygLista;

    private int aktivGrad;

    private List<Integer> grader;

    @JsonIgnore
    private String aktivIntygsId;

    private List<String> sysselsattning;

    private RiskSignal riskSignal;
    private boolean nyligenAvslutat;
    private String uid;

    // - - - getters and setters

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getVardGivareId() {
        return vardGivareId;
    }

    public void setVardGivareId(String vardGivareId) {
        this.vardGivareId = vardGivareId;
    }

    public String getVardGivareNamn() {
        return vardGivareNamn;
    }

    public void setVardGivareNamn(String vardGivareNamn) {
        this.vardGivareNamn = vardGivareNamn;
    }

    public String getVardEnhetId() {
        return vardEnhetId;
    }

    public void setVardEnhetId(String vardEnhetId) {
        this.vardEnhetId = vardEnhetId;
    }

    public String getVardEnhetNamn() {
        return vardEnhetNamn;
    }

    public void setVardEnhetNamn(String vardEnhetNamn) {
        this.vardEnhetNamn = vardEnhetNamn;
    }

    public Lakare getLakare() {
        return lakare;
    }

    public void setLakare(Lakare lakare) {
        this.lakare = lakare;
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

    public List<Diagnos> getBiDiagnoser() {
        return biDiagnoser;
    }

    public void setBiDiagnoser(List<Diagnos> biDiagnoser) {
        this.biDiagnoser = biDiagnoser;
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

    public long getSlutOmDagar() {
        return slutOmDagar;
    }

    public void setSlutOmDagar(long slutOmDagar) {
        this.slutOmDagar = slutOmDagar;
    }

    public int getDagar() {
        return dagar;
    }

    public void setDagar(int dagar) {
        this.dagar = dagar;
    }

    public int getIntyg() {
        return intyg;
    }

    public void setIntyg(int intyg) {
        this.intyg = intyg;
    }

    public int getAktivGrad() {
        return aktivGrad;
    }

    public void setAktivGrad(int aktivGrad) {
        this.aktivGrad = aktivGrad;
    }

    public List<Integer> getGrader() {
        return grader;
    }

    public void setGrader(List<Integer> grader) {
        this.grader = grader;
    }

    public String getAktivIntygsId() {
        return aktivIntygsId;
    }

    public void setAktivIntygsId(String aktivIntygsId) {
        this.aktivIntygsId = aktivIntygsId;
    }

    public List<String> getSysselsattning() {
        return sysselsattning;
    }

    public void setSysselsattning(List<String> sysselsattning) {
        this.sysselsattning = sysselsattning;
    }

    public RiskSignal getRiskSignal() {
        return riskSignal;
    }

    public void setRiskSignal(RiskSignal riskSignal) {
        this.riskSignal = riskSignal;
    }

    public boolean isNyligenAvslutat() {
        return nyligenAvslutat;
    }

    public void setNyligenAvslutat(boolean nyligenAvslutat) {
        this.nyligenAvslutat = nyligenAvslutat;
    }

    public int getObesvaradeKompl() {
        return obesvaradeKompl;
    }

    public void setObesvaradeKompl(int obesvaradeKompl) {
        this.obesvaradeKompl = obesvaradeKompl;
    }

    public int getUnansweredOther() {
        return unansweredOther;
    }

    public void setUnansweredOther(int unansweredOther) {
        this.unansweredOther = unansweredOther;
    }

    public List<String> getIntygLista() {
        return intygLista;
    }

    public void setIntygLista(List<String> intygLista) {
        this.intygLista = intygLista;
    }
}
