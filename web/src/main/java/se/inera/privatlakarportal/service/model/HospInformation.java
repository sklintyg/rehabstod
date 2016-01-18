package se.inera.privatlakarportal.service.model;

import java.util.List;

public class HospInformation {
    String personalPrescriptionCode;
    List<String> specialityNames;
    List<String> hsaTitles;

    public String getPersonalPrescriptionCode() {
        return personalPrescriptionCode;
    }

    public void setPersonalPrescriptionCode(String personalPrescriptionCode) {
        this.personalPrescriptionCode = personalPrescriptionCode;
    }

    public List<String> getSpecialityNames() {
        return specialityNames;
    }

    public void setSpecialityNames(List<String> specialityNames) {
        this.specialityNames = specialityNames;
    }

    public List<String> getHsaTitles() {
        return hsaTitles;
    }

    public void setHsaTitles(List<String> hsaTitles) {
        this.hsaTitles = hsaTitles;
    }
}
