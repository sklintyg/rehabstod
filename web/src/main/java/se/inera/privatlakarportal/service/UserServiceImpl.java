package se.inera.privatlakarportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import se.inera.privatlakarportal.auth.PrivatlakarUser;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.common.utils.PrivatlakareUtils;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.privatlakarportal.pu.model.PersonSvar;
import se.inera.privatlakarportal.pu.services.PUService;
import se.inera.privatlakarportal.common.model.RegistrationStatus;
import se.inera.privatlakarportal.common.monitoring.util.HashUtility;
import se.inera.privatlakarportal.service.model.User;

import javax.transaction.Transactional;

/**
 * Created by pebe on 2015-08-11.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    PrivatlakareRepository privatlakareRepository;

    @Autowired
    PUService puService;

    @Override
    public PrivatlakarUser getUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        return (PrivatlakarUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    @Transactional
    public User getUserWithStatus() {
        PrivatlakarUser privatlakarUser = getUser();

        if (privatlakarUser == null) {
            throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.NOT_FOUND, "No logged in user");
        }

        RegistrationStatus status;
        Privatlakare privatlakare = privatlakareRepository.findByPersonId(privatlakarUser.getPersonalIdentityNumber());
        if (privatlakare == null) {
            status = RegistrationStatus.NOT_STARTED;
        } else if (!privatlakare.isGodkandAnvandare()) {
            status = RegistrationStatus.NOT_AUTHORIZED;
        } else if (!PrivatlakareUtils.hasLakareLegitimation(privatlakare)) {
            status = RegistrationStatus.WAITING_FOR_HOSP;
        } else {
            status = RegistrationStatus.AUTHORIZED;
        }

        boolean nameUpdated = false;
        PersonSvar.Status personSvarStatus;
        try {
            PersonSvar personSvar = puService.getPerson(privatlakarUser.getPersonalIdentityNumber());
            personSvarStatus = personSvar.getStatus();
            if (personSvar.getStatus() == PersonSvar.Status.FOUND && personSvar.getPerson() != null) {
                String name = personSvar.getPerson().getFornamn() + " " + personSvar.getPerson().getEfternamn();
                privatlakarUser.updateNameFromPuService(name);

                // Check if name has changed and update in database
                if (privatlakare != null && !name.equals(privatlakare.getFullstandigtNamn())) {
                    LOG.info("Updated name for user '{}'", HashUtility.hash(privatlakarUser.getPersonalIdentityNumber()));
                    privatlakare.setFullstandigtNamn(name);
                    privatlakareRepository.save(privatlakare);
                    nameUpdated = true;
                }

            } else if (personSvar.getStatus() == PersonSvar.Status.NOT_FOUND) {
                LOG.warn("Person '{}' not found in puService", HashUtility.hash(privatlakarUser.getPersonalIdentityNumber()));
            } else {
                LOG.error("puService returned error status for personId '{}'", HashUtility.hash(privatlakarUser.getPersonalIdentityNumber()));
            }
        } catch (RuntimeException e) {
            LOG.error("Failed to contact puService", e);
            personSvarStatus = PersonSvar.Status.ERROR;
        }

        return new User(privatlakarUser, personSvarStatus, status, nameUpdated);
    }

}
