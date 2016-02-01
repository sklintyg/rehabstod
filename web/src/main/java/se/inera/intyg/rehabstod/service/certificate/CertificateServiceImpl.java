package se.inera.intyg.rehabstod.service.certificate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

/**
 * Created by eriklupander on 2016-02-01.
 */
@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private IntygstjanstIntegrationService intygstjanstIntegrationService;

    @Autowired
    private UserService userService;

    @Override
    public List<IntygsData> getIntygsData() {
        String hsaId = userService.getUser().getValdVardenhet().getId();
        return intygstjanstIntegrationService.getIntygsDataForCareUnit(hsaId);
    }
}
