package se.inera.privatlakarportal.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.inera.ifv.hsawsresponder.v3.GetHospPersonResponseType;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.common.model.Registration;
import se.inera.privatlakarportal.common.model.RegistrationStatus;
import se.inera.privatlakarportal.common.service.DateHelperService;
import se.inera.privatlakarportal.common.service.MailService;
import se.inera.privatlakarportal.hsa.services.HospPersonService;
import se.inera.privatlakarportal.hsa.services.HospUpdateService;
import se.inera.privatlakarportal.hsa.services.exception.HospUpdateFailedToContactHsaException;
import se.inera.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.privatlakarportal.persistence.model.Medgivande;
import se.inera.privatlakarportal.persistence.model.MedgivandeText;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.model.PrivatlakareId;
import se.inera.privatlakarportal.persistence.model.Specialitet;
import se.inera.privatlakarportal.persistence.repository.MedgivandeTextRepository;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareIdRepository;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.privatlakarportal.service.model.HospInformation;
import se.inera.privatlakarportal.service.model.RegistrationWithHospInformation;
import se.inera.privatlakarportal.service.model.SaveRegistrationResponseStatus;
import se.inera.privatlakarportal.service.monitoring.MonitoringLogService;

/**
 * Created by pebe on 2015-06-26.
 */
@Service
public class RegisterServiceImpl implements RegisterService {

    @Value("${mail.admin.notification.interval}")
    private int hsaIdNotificationInterval;

    private static final Logger LOG = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PrivatlakareRepository privatlakareRepository;

    @Autowired
    private MedgivandeTextRepository medgivandeTextRepository;

    @Autowired
    private PrivatlakareIdRepository privatlakareidRepository;

    @Autowired
    private HospPersonService hospPersonService;

    @Autowired
    private HospUpdateService hospUpdateService;

    @Autowired
    private MailService mailService;

    @Autowired
    private DateHelperService dateHelperService;

    @Autowired
    @Qualifier("webMonitoringLogService")
    private MonitoringLogService monitoringService;

    @Override
    public HospInformation getHospInformation() {
        GetHospPersonResponseType response = hospPersonService.getHospPerson(userService.getUser().getPersonalIdentityNumber());

        if (response == null) {
            return null;
        }

        HospInformation hospInformation = new HospInformation();
        hospInformation.setPersonalPrescriptionCode(response.getPersonalPrescriptionCode());
        hospInformation.setSpecialityNames(response.getSpecialityNames().getSpecialityName());
        hospInformation.setHsaTitles(response.getHsaTitles().getHsaTitle());

        return hospInformation;
    }

    @Override
    @Transactional
    public RegistrationWithHospInformation getRegistration() {
        Privatlakare privatlakare = privatlakareRepository.findByPersonId(userService.getUser().getPersonalIdentityNumber());

        if (privatlakare == null) {
            return new RegistrationWithHospInformation(null, null);
        }

        Registration registration = new Registration();

        registration.setAdress(privatlakare.getPostadress());
        registration.setAgarForm(privatlakare.getAgarform());
        registration.setArbetsplatskod(privatlakare.getArbetsplatsKod());
        // Detta fält plus några till längre ner kan ha flera värden enligt informationsmodellen men implementeras som bara ett värde.
        if (privatlakare.getBefattningar() != null && !privatlakare.getBefattningar().isEmpty()) {
            registration.setBefattning(privatlakare.getBefattningar().iterator().next().getKod());
        }
        registration.setEpost(privatlakare.getEpost());
        registration.setKommun(privatlakare.getKommun());
        registration.setLan(privatlakare.getLan());
        registration.setPostnummer(privatlakare.getPostnummer());
        registration.setPostort(privatlakare.getPostort());
        registration.setTelefonnummer(privatlakare.getTelefonnummer());
        if (privatlakare.getVardformer() != null && !privatlakare.getVardformer().isEmpty()) {
            registration.setVardform(privatlakare.getVardformer().iterator().next().getKod());
        }
        registration.setVerksamhetensNamn(privatlakare.getVardgivareNamn());
        if (privatlakare.getVerksamhetstyper() != null && !privatlakare.getVerksamhetstyper().isEmpty()) {
            registration.setVerksamhetstyp(privatlakare.getVerksamhetstyper().iterator().next().getKod());
        }

        HospInformation hospInformation = new HospInformation();
        if (privatlakare.getLegitimeradeYrkesgrupper() != null) {
            List<String> legitimeradeYrkesgrupper = new ArrayList<String>();
            for (LegitimeradYrkesgrupp legitimeradYrkesgrupp : privatlakare.getLegitimeradeYrkesgrupper()) {
                legitimeradeYrkesgrupper.add(legitimeradYrkesgrupp.getNamn());
            }
            if (!legitimeradeYrkesgrupper.isEmpty()) {
                hospInformation.setHsaTitles(legitimeradeYrkesgrupper);
            }
        }

        if (privatlakare.getSpecialiteter() != null) {
            List<String> specialiteter = new ArrayList<String>();
            for (Specialitet specialitet : privatlakare.getSpecialiteter()) {
                specialiteter.add(specialitet.getNamn());
            }
            if (!specialiteter.isEmpty()) {
                hospInformation.setSpecialityNames(specialiteter);
            }
        }

        hospInformation.setPersonalPrescriptionCode(privatlakare.getForskrivarKod());

        return new RegistrationWithHospInformation(registration, hospInformation);
    }

    @Override
    @Transactional
    public RegistrationStatus createRegistration(Registration registration, Long godkantMedgivandeVersion) {

        if (registration == null || !registration.checkIsValid()) {
            LOG.error("createRegistration: CreateRegistrationRequest is not valid");
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
                    "CreateRegistrationRequest is not valid");
        }

        if (godkantMedgivandeVersion == null || godkantMedgivandeVersion <= 0) {
            LOG.error("createRegistration: Not allowed to create registration without medgivande");
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
                    "Not allowed to create registration without medgivande");
        }

        if (privatlakareRepository.findByPersonId(userService.getUser().getPersonalIdentityNumber()) != null) {
            LOG.error("createRegistration: Registration already exists");
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.ALREADY_EXISTS,
                    "Registration already exists");
        }

        if (!userService.getUser().isNameFromPuService()) {
            LOG.error("createRegistration: Not allowed to create registration without updated name from PU-service");
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Not allowed to create registration without updated name from PU-service");
        }

        Privatlakare privatlakare = new Privatlakare();

        privatlakare.setMedgivande(createMedgivandeSet(godkantMedgivandeVersion, privatlakare));

        privatlakare.setRegistreringsdatum(dateHelperService.now());
        privatlakare.setPersonId(userService.getUser().getPersonalIdentityNumber());
        privatlakare.setFullstandigtNamn(userService.getUser().getName());
        privatlakare.setGodkandAnvandare(true);

        // PrivatlakareId uses an autoincrement column to get next value
        PrivatlakareId privatlakareId = privatlakareidRepository.save(new PrivatlakareId());

        String hsaId = generateHsaId(privatlakareId);

        privatlakare.setEnhetsId(hsaId);
        privatlakare.setHsaId(hsaId);
        privatlakare.setVardgivareId(hsaId);

        // Set properties from client
        convertRegistrationToPrivatlakare(registration, privatlakare);

        // Lookup hospPerson in HSA
        RegistrationStatus status = null;
        try {
            status = hospUpdateService.updateHospInformation(privatlakare, true);
        } catch (HospUpdateFailedToContactHsaException e) {
            LOG.error("Failed to contact HSA with error {}, setting status {} in the meantime.", e, RegistrationStatus.WAITING_FOR_HOSP);
            status = RegistrationStatus.WAITING_FOR_HOSP;
        }

        // Determine if an administrator needs to be notified about HSA ID's running out
        if (privatlakareidRepository.findLatestGeneratedHsaId() != 0 && 
                privatlakareidRepository.findLatestGeneratedHsaId() % hsaIdNotificationInterval == 0) {
            mailService.sendHsaGenerationStatusEmail();
        }
        mailService.sendRegistrationStatusEmail(status, privatlakare);

        privatlakareRepository.save(privatlakare);

        monitoringService.logUserRegistered(privatlakare.getPersonId(), godkantMedgivandeVersion, privatlakare.getHsaId(), status);
        return status;
    }

    @Override
    public SaveRegistrationResponseStatus saveRegistration(Registration registration) {
        if (registration == null || !registration.checkIsValid()) {
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
                    "SaveRegistrationRequest is not valid");
        }

        Privatlakare privatlakare = privatlakareRepository.findByPersonId(userService.getUser().getPersonalIdentityNumber());

        if (privatlakare == null) {
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.NOT_FOUND,
                    "Registration not found");
        }

        convertRegistrationToPrivatlakare(registration, privatlakare);

        privatlakareRepository.save(privatlakare);
        
        monitoringService.logUserDetailsChanged(privatlakare.getPersonId());

        return SaveRegistrationResponseStatus.OK;
    }

    @Override
    public boolean removePrivatlakare(String personId) {
        Privatlakare toDelete = privatlakareRepository.findByPersonId(personId);
        if (toDelete == null) {
            LOG.error("No Privatlakare with id {} found in the database!", personId);
            return false;
        }
        privatlakareRepository.delete(toDelete);
        LOG.info("Deleted Privatlakare with id {}", personId);

        monitoringService.logUserDeleted(personId);

        return true;
    }

    // Visible for test purposes only!
    @Override
    public void injectHsaInterval(int hsaIdNotificationInterval) {
        this.hsaIdNotificationInterval = hsaIdNotificationInterval;
    }

    /* Private helpers */

    /**
     *  Generate next hsaId,
     *  Format: "SE" + ineras orgnr (inkl "sekelsiffror", alltså 165565594230) + "-" + "WEBCERT" + femsiffrigt löpnr.
     * @param privatlakareId
     * @return
     */
    private String generateHsaId(PrivatlakareId privatlakareId) {
        return "SE165565594230-WEBCERT" + StringUtils.leftPad(Integer.toString(privatlakareId.getId()), 5, '0');
    }

    private Set<Medgivande> createMedgivandeSet(Long godkantMedgivandeVersion, Privatlakare privatlakare) {
        MedgivandeText medgivandeText = medgivandeTextRepository.findOne(godkantMedgivandeVersion);
        if (medgivandeText == null) {
            LOG.error("createRegistration: Could not find medgivandetext with version '{}'", godkantMedgivandeVersion);
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
                    "Could not find medgivandetext matching godkantMedgivandeVersion");
        }
        Medgivande medgivande = new Medgivande();
        medgivande.setGodkandDatum(dateHelperService.now());
        medgivande.setMedgivandeText(medgivandeText);
        medgivande.setPrivatlakare(privatlakare);
        Set<Medgivande> medgivandeSet = new HashSet<>();
        medgivandeSet.add(medgivande);
        return medgivandeSet;
    }

    private void convertRegistrationToPrivatlakare(Registration registration, Privatlakare privatlakare) {
        privatlakare.setAgarform("Privat");
        privatlakare.setArbetsplatsKod(registration.getArbetsplatskod());
        privatlakare.setEnhetsNamn(registration.getVerksamhetensNamn());
        privatlakare.setEpost(registration.getEpost());
        privatlakare.setKommun(registration.getKommun());
        privatlakare.setLan(registration.getLan());
        privatlakare.setPostadress(registration.getAdress());
        privatlakare.setPostnummer(registration.getPostnummer());
        privatlakare.setPostort(registration.getPostort());
        privatlakare.setTelefonnummer(registration.getTelefonnummer());
        privatlakare.setVardgivareNamn(registration.getVerksamhetensNamn());

        /* Effectively change the oneToMany cardinality of the following to act as oneToOne, see javadoc for more info*/
        privatlakare.updateBefattningar(registration.getBefattning());
        privatlakare.updateVardformer(registration.getVardform());
        privatlakare.updateVerksamhetstyper(registration.getVerksamhetstyp());
    }

}
