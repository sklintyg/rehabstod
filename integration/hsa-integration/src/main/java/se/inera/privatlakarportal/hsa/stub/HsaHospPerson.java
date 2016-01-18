package se.inera.privatlakarportal.hsa.stub;

import java.util.ArrayList;
import java.util.List;

public class HsaHospPerson {
    String personalIdentityNumber;
    String personalPrescriptionCode;
    List<String> educationCodes = new ArrayList<String>();
    List<String> restrictions = new ArrayList<String>();
    List<String> restrictionCodes = new ArrayList<String>();
    List<String> titleCodes = new ArrayList<String>();
    List<String> specialityCodes = new ArrayList<String>();
    List<String> specialityNames = new ArrayList<String>();
    List<String> hsaTitles = new ArrayList<String>();

    public String getPersonalIdentityNumber() {
        return personalIdentityNumber;
    }

    public void setPersonalIdentityNumber(String personalIdentityNumber) {
        this.personalIdentityNumber = personalIdentityNumber;
    }

    public String getPersonalPrescriptionCode() {
        return personalPrescriptionCode;
    }

    public void setPersonalPrescriptionCode(String personalPrescriptionCode) {
        this.personalPrescriptionCode = personalPrescriptionCode;
    }

    public List<String> getEducationCodes() {
        return educationCodes;
    }

    public void setEducationCodes(List<String> educationCodes) {
        this.educationCodes = educationCodes;
    }

    public List<String> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<String> restrictions) {
        this.restrictions = restrictions;
    }

    public List<String> getRestrictionCodes() {
        return restrictionCodes;
    }

    public void setRestrictionCodes(List<String> restrictionCodes) {
        this.restrictionCodes = restrictionCodes;
    }

    public List<String> getTitleCodes() {
        return titleCodes;
    }

    public void setTitleCodes(List<String> titleCodes) {
        this.titleCodes = titleCodes;
    }

    public List<String> getSpecialityCodes() {
        return specialityCodes;
    }

    public void setSpecialityCodes(List<String> specialityCodes) {
        this.specialityCodes = specialityCodes;
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
