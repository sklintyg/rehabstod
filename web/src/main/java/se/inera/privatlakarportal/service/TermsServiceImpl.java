package se.inera.privatlakarportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.privatlakarportal.integration.terms.services.dto.Terms;
import se.inera.privatlakarportal.persistence.model.MedgivandeText;
import se.inera.privatlakarportal.persistence.repository.MedgivandeTextRepository;

/**
 * Created by pebe on 2015-09-09.
 */
@Service
public class TermsServiceImpl implements TermsService {

    @Autowired
    MedgivandeTextRepository medgivandeTextRepository;

    @Override
    public Terms getTerms() {
        MedgivandeText medgivandeText = medgivandeTextRepository.findLatest();
        return new Terms(medgivandeText.getMedgivandeText(), medgivandeText.getVersion(), medgivandeText.getDatum());
    }
}
