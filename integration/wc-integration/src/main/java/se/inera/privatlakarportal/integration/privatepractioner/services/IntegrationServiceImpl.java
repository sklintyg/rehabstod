package se.inera.privatlakarportal.integration.privatepractioner.services;

import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.privatlakarportal.common.integration.kodverk.Befattningar;
import se.inera.privatlakarportal.common.integration.kodverk.Vardformer;
import se.inera.privatlakarportal.common.integration.kodverk.Verksamhetstyper;
import se.inera.privatlakarportal.common.service.DateHelperService;
import se.inera.privatlakarportal.common.utils.PrivatlakareUtils;
import se.inera.privatlakarportal.hsa.services.HospUpdateService;
import se.inera.privatlakarportal.persistence.model.Befattning;
import se.inera.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.model.Specialitet;
import se.inera.privatlakarportal.persistence.model.Vardform;
import se.inera.privatlakarportal.persistence.model.Verksamhetstyp;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerResponseType;
import se.riv.infrastructure.directory.privatepractitioner.types.v1.ArbetsplatsKod;
import se.riv.infrastructure.directory.privatepractitioner.types.v1.CV;
import se.riv.infrastructure.directory.privatepractitioner.types.v1.HsaId;
import se.riv.infrastructure.directory.privatepractitioner.types.v1.PersonId;
import se.riv.infrastructure.directory.privatepractitioner.v1.BefattningType;
import se.riv.infrastructure.directory.privatepractitioner.v1.EnhetType;
import se.riv.infrastructure.directory.privatepractitioner.v1.GeografiskIndelningType;
import se.riv.infrastructure.directory.privatepractitioner.v1.HoSPersonType;
import se.riv.infrastructure.directory.privatepractitioner.v1.LegitimeradYrkesgruppType;
import se.riv.infrastructure.directory.privatepractitioner.v1.ResultCodeEnum;
import se.riv.infrastructure.directory.privatepractitioner.v1.SpecialitetType;
import se.riv.infrastructure.directory.privatepractitioner.v1.VardgivareType;
import se.riv.infrastructure.directory.privatepractitioner.v1.VerksamhetType;
import se.riv.infrastructure.directory.privatepractitioner.validateprivatepractitionerresponder.v1.ValidatePrivatePractitionerResponseType;

/**
 * Created by pebe on 2015-08-17.
 */
@Service
public class IntegrationServiceImpl implements IntegrationService {

    @Autowired
    PrivatlakareRepository privatlakareRepository;

    @Autowired
    HospUpdateService hospUpdateService;

    @Autowired
    DateHelperService dateHelperService;

    @Override
    @Transactional
    public GetPrivatePractitionerResponseType getPrivatePractitionerByHsaId(String personHsaId) {

        GetPrivatePractitionerResponseType response = new GetPrivatePractitionerResponseType();

        Privatlakare privatlakare = privatlakareRepository.findByHsaId(personHsaId);

        if (privatlakare == null) {
            response.setHoSPerson(null);
            response.setResultCode(ResultCodeEnum.ERROR);
            response.setResultText("No private practitioner with hsa id: " + personHsaId + " exists.");
        }
        else {
            response.setResultCode(ResultCodeEnum.OK);
            checkFirstLogin(privatlakare);
            convertPrivatlakareToResponse(privatlakare, response);
        }

        return response;
    }

    @Override
    @Transactional
    public GetPrivatePractitionerResponseType getPrivatePractitionerByPersonId(String personalIdentityNumber) {

        GetPrivatePractitionerResponseType response = new GetPrivatePractitionerResponseType();

        Privatlakare privatlakare = privatlakareRepository.findByPersonId(personalIdentityNumber);

        if (privatlakare == null) {
            response.setHoSPerson(null);
            response.setResultCode(ResultCodeEnum.ERROR);
            response.setResultText("No private practitioner with personal identity number: " + personalIdentityNumber + " exists.");
        }
        else {
            response.setResultCode(ResultCodeEnum.OK);
            checkFirstLogin(privatlakare);
            convertPrivatlakareToResponse(privatlakare, response);
        }

        return response;
    }

    @Override
    @Transactional
    public ValidatePrivatePractitionerResponseType validatePrivatePractitionerByHsaId(String personHsaId) {

        ValidatePrivatePractitionerResponseType response = new ValidatePrivatePractitionerResponseType();

        Privatlakare privatlakare = privatlakareRepository.findByHsaId(personHsaId);

        if (privatlakare == null) {
            response.setResultCode(ResultCodeEnum.ERROR);
            response.setResultText("No private practitioner with hsa id: " + personHsaId + " exists.");
        } else {
            hospUpdateService.checkForUpdatedHospInformation(privatlakare);
            if (privatlakare.isGodkandAnvandare() && PrivatlakareUtils.hasLakareLegitimation(privatlakare)) {
                response.setResultCode(ResultCodeEnum.OK);
                // Check if this is the first time the user logins to Webcert after getting godkand status
                checkFirstLogin(privatlakare);
            } else {
                response.setResultCode(ResultCodeEnum.ERROR);
                response.setResultText("Private practitioner with hsa id: " + personHsaId + " is not authorized to use webcert.");
            }
        }

        return response;
    }

    @Override
    @Transactional
    public ValidatePrivatePractitionerResponseType validatePrivatePractitionerByPersonId(String personalIdentityNumber) {

        ValidatePrivatePractitionerResponseType response = new ValidatePrivatePractitionerResponseType();

        Privatlakare privatlakare = privatlakareRepository.findByPersonId(personalIdentityNumber);

        if (privatlakare == null) {
            response.setResultCode(ResultCodeEnum.ERROR);
            response.setResultText("No private practitioner with personal identity number: " + personalIdentityNumber + " exists.");
        } else {
            hospUpdateService.checkForUpdatedHospInformation(privatlakare);
            if (privatlakare.isGodkandAnvandare() && PrivatlakareUtils.hasLakareLegitimation(privatlakare)) {
                response.setResultCode(ResultCodeEnum.OK);
                // Check if this is the first time the user logins to Webcert after getting godkand status
                checkFirstLogin(privatlakare);
            } else {
                response.setResultCode(ResultCodeEnum.ERROR);
                response.setResultText("Private practitioner with personal identity number: " + personalIdentityNumber + " is not authorized to use webcert.");
            }
        }

        return response;
    }

    private void checkFirstLogin(Privatlakare privatlakare) {
        LocalDateTime localDateTime = dateHelperService.now();
        // If startdatum is not set, set current time as startdatum for privatlakare
        if (privatlakare.getEnhetStartdatum() == null || privatlakare.getVardgivareStartdatum() == null) {
            privatlakare.setEnhetStartdatum(localDateTime);
            privatlakare.setVardgivareStartdatum(localDateTime);
            privatlakareRepository.save(privatlakare);
        }
    }

    private static final String ARBETSPLATSKOD_ROOT = "1.2.752.29.4.71";
    private static final String HSAID_ROOT = "1.2.752.129.2.1.4.1";
    private static final String PERSONID_ROOT = "1.2.752.129.2.1.3.1";

    private HsaId convertToHsaId(String ext) {
        if (HSAID_ROOT == null || ext == null) {
            return null;
        }
        HsaId hsaId = new HsaId();
        hsaId.setRoot(HSAID_ROOT);
        hsaId.setExtension(ext);
        return hsaId;
    }

    private ArbetsplatsKod convertToArbetsplatsKod(String ext) {
        if (ARBETSPLATSKOD_ROOT == null || ext == null) {
            return null;
        }
        ArbetsplatsKod arbetsplatsKod = new ArbetsplatsKod();
        arbetsplatsKod.setRoot(ARBETSPLATSKOD_ROOT);
        arbetsplatsKod.setExtension(ext);
        return arbetsplatsKod;
    }

    private PersonId convertToPersonId(String ext) {
        if (PERSONID_ROOT == null || ext == null) {
            return null;
        }
        PersonId personId = new PersonId();
        personId.setRoot(PERSONID_ROOT);
        personId.setExtension(ext);
        return personId;
    }

    private void convertPrivatlakareToResponse(Privatlakare privatlakare, GetPrivatePractitionerResponseType response) {
        HoSPersonType hoSPersonType = new HoSPersonType();

        VardgivareType vardgivareType = new VardgivareType();
        vardgivareType.setSlutdatum(privatlakare.getVardgivareSlutdatum());
        vardgivareType.setStartdatum(privatlakare.getVardgivareStartdatum());
        vardgivareType.setVardgivareId(convertToHsaId(privatlakare.getVardgivareId()));
        vardgivareType.setVardgivarenamn(privatlakare.getVardgivareNamn());

        GeografiskIndelningType geografiskIndelningType = new GeografiskIndelningType();
        CV kommunCv = new CV();
        kommunCv.setDisplayName(privatlakare.getKommun());
        geografiskIndelningType.setKommun(kommunCv);
        CV lanCv = new CV();
        lanCv.setDisplayName(privatlakare.getLan());
        geografiskIndelningType.setLan(lanCv);

        EnhetType enhetType = new EnhetType();
        enhetType.setAgarform(privatlakare.getAgarform());
        enhetType.setArbetsplatskod(convertToArbetsplatsKod(privatlakare.getArbetsplatsKod()));
        enhetType.setEnhetsId(convertToHsaId(privatlakare.getEnhetsId()));
        enhetType.setEnhetsnamn(privatlakare.getEnhetsNamn());
        enhetType.setEpost(privatlakare.getEpost());
        enhetType.setGeografiskIndelning(geografiskIndelningType);
        enhetType.setPostadress(privatlakare.getPostadress());
        enhetType.setPostnummer(privatlakare.getPostnummer());
        enhetType.setPostort(privatlakare.getPostort());
        enhetType.setSlutdatum(privatlakare.getEnhetSlutDatum());
        enhetType.setStartdatum(privatlakare.getEnhetStartdatum());
        enhetType.setTelefonnummer(privatlakare.getTelefonnummer());
        enhetType.setVardgivare(vardgivareType);

        VerksamhetType verksamhetType = new VerksamhetType();
        if (privatlakare.getVardformer() != null) {
            for (Vardform vardform : privatlakare.getVardformer()) {

                CV vardformCv = new CV();
                vardformCv.setCodeSystemName(Vardformer.VARDFORM_NAME);
                vardformCv.setCodeSystemVersion(Vardformer.VARDFORM_VERSION);
                vardformCv.setCodeSystem(Vardformer.VARDFORM_OID);
                vardformCv.setCode(vardform.getKod());
                vardformCv.setDisplayName(Vardformer.getDisplayName(vardform.getKod()));

                verksamhetType.getVardform().add(vardformCv);
            }
        }
        if (privatlakare.getVerksamhetstyper() != null) {
            for (Verksamhetstyp verksamhetstyp : privatlakare.getVerksamhetstyper()) {

                CV verksamhetstypCv = new CV();
                verksamhetstypCv.setCodeSystemName(Verksamhetstyper.VERKSAMHETSTYP_NAME);
                verksamhetstypCv.setCodeSystemVersion(Verksamhetstyper.VERKSAMHETSTYP_VERSION);
                verksamhetstypCv.setCodeSystem(Verksamhetstyper.VERKSAMHETSTYP_OID);
                verksamhetstypCv.setCode(verksamhetstyp.getKod());
                verksamhetstypCv.setDisplayName(Verksamhetstyper.getDisplayName(verksamhetstyp.getKod()));

                verksamhetType.getVerksamhet().add(verksamhetstypCv);
            }
        }
        enhetType.setVerksamhetstyp(verksamhetType);

        hoSPersonType.setEnhet(enhetType);
        hoSPersonType.setForskrivarkod(privatlakare.getForskrivarKod());
        hoSPersonType.setFullstandigtNamn(privatlakare.getFullstandigtNamn());
        hoSPersonType.setGodkandAnvandare(privatlakare.isGodkandAnvandare());
        hoSPersonType.setHsaId(convertToHsaId(privatlakare.getHsaId()));
        hoSPersonType.setPersonId(convertToPersonId(privatlakare.getPersonId()));

        if (privatlakare.getBefattningar() != null) {
            for (Befattning befattning : privatlakare.getBefattningar()) {

                BefattningType befattningType = new BefattningType();
                befattningType.setKod(befattning.getKod());
                befattningType.setNamn(Befattningar.getDisplayName(befattning.getKod()));

                hoSPersonType.getBefattning().add(befattningType);
            }
        }

        if (privatlakare.getLegitimeradeYrkesgrupper() != null) {
            for (LegitimeradYrkesgrupp legitimeradYrkesgrupp : privatlakare.getLegitimeradeYrkesgrupper()) {
                LegitimeradYrkesgruppType legitimeradYrkesgruppType = new LegitimeradYrkesgruppType();
                legitimeradYrkesgruppType.setKod(legitimeradYrkesgrupp.getKod());
                legitimeradYrkesgruppType.setNamn(legitimeradYrkesgrupp.getNamn());
                hoSPersonType.getLegitimeradYrkesgrupp().add(legitimeradYrkesgruppType);
            }
        }

        if (privatlakare.getSpecialiteter() != null) {
            for (Specialitet specialitet : privatlakare.getSpecialiteter()) {

                SpecialitetType specialitetType = new SpecialitetType();
                specialitetType.setKod(specialitet.getKod());
                specialitetType.setNamn(specialitet.getNamn());

                hoSPersonType.getSpecialitet().add(specialitetType);
            }
        }

        response.setHoSPerson(hoSPersonType);
    }


}
