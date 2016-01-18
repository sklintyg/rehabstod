package se.inera.privatlakarportal.hsa.services;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import se.inera.ifv.hsawsresponder.v3.GetHospPersonResponseType;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.common.service.MailService;
import se.inera.privatlakarportal.common.utils.PrivatlakareUtils;
import se.inera.privatlakarportal.hsa.monitoring.MonitoringLogService;
import se.inera.privatlakarportal.hsa.services.exception.HospUpdateFailedToContactHsaException;
import se.inera.privatlakarportal.persistence.model.HospUppdatering;
import se.inera.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.model.Specialitet;
import se.inera.privatlakarportal.persistence.repository.HospUppdateringRepository;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.privatlakarportal.common.model.RegistrationStatus;

import javax.transaction.Transactional;
import javax.xml.ws.WebServiceException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pebe on 2015-09-03.
 */
@Service
public class HospUpdateServiceImpl implements HospUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(HospUpdateServiceImpl.class);

    @Autowired
    PrivatlakareRepository privatlakareRepository;

    @Autowired
    HospUppdateringRepository hospUppdateringRepository;

    @Autowired
    HospPersonService hospPersonService;

    @Autowired
    MailService mailService;

    @Autowired
    @Qualifier("hsaMonitoringLogService")
    private MonitoringLogService monitoringService;

    @Override
    @Scheduled(cron = "${privatlakarportal.hospupdate.cron}")
    @Transactional
    public void scheduledUpdateHospInformation() {
        String skipUpdate = System.getProperty("scheduled.update.skip", "false");
        LOG.debug("scheduled.update.skip = " + skipUpdate);
        if (skipUpdate.equalsIgnoreCase("true")) {
            LOG.info("Skipping scheduled updateHospInformation");
        } else {
            LOG.info("Starting scheduled updateHospInformation");
            updateHospInformation();
        }
    }

    @Override
    public void updateHospInformation() {
        // Get our last hosp update time from database
        HospUppdatering hospUppdatering = hospUppdateringRepository.findSingle();

        // Get last hosp update time from HSA
        LocalDateTime hsaHospLastUpdate;
        try {
            hsaHospLastUpdate = hospPersonService.getHospLastUpdate();
        } catch(WebServiceException e) {
            LOG.error("Failed to getHospLastUpdate from HSA with exception {}", e);
            return;
        }

        // If hospUppdatering is null this is our first update ever
        if (hospUppdatering == null ||
            hospUppdatering.getSenasteHospUppdatering().isBefore(hsaHospLastUpdate)) {

            LOG.info("Hospinformation has been updated in HSA since our last update");

            // Save hosp update time in database
            if (hospUppdatering == null) {
                hospUppdatering = new HospUppdatering(hsaHospLastUpdate);
            } else {
                hospUppdatering.setSenasteHospUppdatering(hsaHospLastUpdate);
            }
            hospUppdateringRepository.save(hospUppdatering);

            // Find privatlakare without hospinformation
            List<Privatlakare> privatlakareList = privatlakareRepository.findNeverHadLakarBehorighet();
            for(Privatlakare privatlakare : privatlakareList) {

                LOG.info("Checking privatlakare '{}' for updated hosp information", privatlakare.getPersonId());
                try {
                    RegistrationStatus status = updateHospInformation(privatlakare, true);
                    LOG.info("updateHospInformation returned status '{}'", status);

                    // Check if information has been updated
                    if (status == RegistrationStatus.AUTHORIZED ||
                            status == RegistrationStatus.NOT_AUTHORIZED) {
                        privatlakareRepository.save(privatlakare);
                        mailService.sendRegistrationStatusEmail(status, privatlakare);
                    }
                } catch(HospUpdateFailedToContactHsaException e) {
                    LOG.error("Failed to contact HSA with error '{}'", e.getMessage());
                }
            }
        }
    }

    @Override
    public RegistrationStatus updateHospInformation(Privatlakare privatlakare, boolean shouldRegisterInCertifier)
            throws HospUpdateFailedToContactHsaException {

        if (shouldRegisterInCertifier) {
            try {
                if (!hospPersonService.addToCertifier(privatlakare.getPersonId(), privatlakare.getHsaId())) {
                    LOG.error("Failed to call handleCertifier in HSA, this call will be retried at next hosp update cycle.");
                }
            } catch (WebServiceException e) {
                LOG.error("Failed to call handleCertifier in HSA with error {}, this call will be retried at next hosp update cycle.", e.getMessage());
                throw new HospUpdateFailedToContactHsaException(e);
            }
        }

        GetHospPersonResponseType hospPersonResponse;
        try {
            hospPersonResponse = hospPersonService.getHospPerson(privatlakare.getPersonId());
        } catch (WebServiceException e) {
            LOG.error("Failed to call getHospPerson in HSA, this call will be retried at next hosp update cycle.");
            throw new HospUpdateFailedToContactHsaException(e);
        }

        if (hospPersonResponse == null) {
            privatlakare.setLegitimeradeYrkesgrupper(new HashSet<LegitimeradYrkesgrupp>());
            privatlakare.setSpecialiteter(new ArrayList<Specialitet>());
            privatlakare.setForskrivarKod(null);

            monitoringService.logHospWaiting(privatlakare.getPersonId());
            return RegistrationStatus.WAITING_FOR_HOSP;
        } else {
            privatlakare.setSpecialiteter(getSpecialiteter(privatlakare, hospPersonResponse));
            privatlakare.setLegitimeradeYrkesgrupper(getLegitimeradeYrkesgrupper(privatlakare, hospPersonResponse));
            privatlakare.setForskrivarKod(hospPersonResponse.getPersonalPrescriptionCode());

            if (PrivatlakareUtils.hasLakareLegitimation(privatlakare)) {
                monitoringService.logUserAuthorizedInHosp(privatlakare.getPersonId());
                if (!privatlakare.isGodkandAnvandare()) {
                    return RegistrationStatus.NOT_AUTHORIZED;
                }
                return RegistrationStatus.AUTHORIZED;
            } else {
                monitoringService.logUserNotAuthorizedInHosp(privatlakare.getPersonId());
                return RegistrationStatus.NOT_AUTHORIZED;
            }
        }
    }

    @Override
    @Transactional
    public void checkForUpdatedHospInformation(Privatlakare privatlakare) {
        try {
            LocalDateTime hospLastUpdate = hospPersonService.getHospLastUpdate();
            if (privatlakare.getSenasteHospUppdatering() == null ||
                    privatlakare.getSenasteHospUppdatering().isBefore(hospLastUpdate)) {

                LOG.debug("Hosp has been updated since last login for privlakare '{}'. Updating hosp information", privatlakare.getPersonId());

                try {
                    updateHospInformation(privatlakare, false);
                    privatlakare.setSenasteHospUppdatering(hospLastUpdate);
                    privatlakareRepository.save(privatlakare);
                } catch(HospUpdateFailedToContactHsaException e) {
                    LOG.error("Failed to update hosp information for privatlakare '{}' due to {}", privatlakare.getPersonId(), e);
                }
            }
        } catch(WebServiceException e) {
            LOG.error("Failed to getHospLastUpdate from HSA in checkForUpdatedHospInformation for privatlakare '{}' due to {}", privatlakare.getPersonId(), e);
        }
    }

    /* Private helpers */

    private List<Specialitet> getSpecialiteter(Privatlakare privatlakare, GetHospPersonResponseType hospPersonResponse) {
        List<Specialitet> specialiteter = new ArrayList<>();
        if (hospPersonResponse.getSpecialityCodes().getSpecialityCode().size() !=
            hospPersonResponse.getSpecialityNames().getSpecialityName().size()) {
            LOG.error("getHospPerson getSpecialityCodes count " +
                    hospPersonResponse.getSpecialityCodes().getSpecialityCode().size() +
                    "doesn't match getSpecialityNames count '{}' != '{}'" +
                    hospPersonResponse.getSpecialityNames().getSpecialityName().size());
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Inconsistent data from HSA");
        } else {
            for (int i = 0; i < hospPersonResponse.getSpecialityCodes().getSpecialityCode().size(); i++) {
                specialiteter.add(new Specialitet(privatlakare,
                        hospPersonResponse.getSpecialityNames().getSpecialityName().get(i),
                        hospPersonResponse.getSpecialityCodes().getSpecialityCode().get(i)));
            }
        }
        return specialiteter;
    }

    private Set<LegitimeradYrkesgrupp> getLegitimeradeYrkesgrupper(Privatlakare privatlakare, GetHospPersonResponseType hospPersonResponse) {
        Set<LegitimeradYrkesgrupp> legitimeradYrkesgrupper = new HashSet<>();
        if (hospPersonResponse.getHsaTitles().getHsaTitle().size() !=
            hospPersonResponse.getTitleCodes().getTitleCode().size()) {
            LOG.error("getHospPerson getHsaTitles count " +
                    hospPersonResponse.getHsaTitles().getHsaTitle().size() +
                    "doesn't match getTitleCodes count '{}' != '{}'" +
                    hospPersonResponse.getTitleCodes().getTitleCode().size());
            throw new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Inconsistent data from HSA");
        } else {
            for (int i = 0; i < hospPersonResponse.getHsaTitles().getHsaTitle().size(); i++) {
                legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare,
                        hospPersonResponse.getHsaTitles().getHsaTitle().get(i),
                        hospPersonResponse.getTitleCodes().getTitleCode().get(i)));
            }
        }
        return legitimeradYrkesgrupper;
    }
    
}
