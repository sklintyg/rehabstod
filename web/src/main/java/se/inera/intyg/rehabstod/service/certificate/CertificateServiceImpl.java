package se.inera.intyg.rehabstod.service.certificate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;

/**
 * Created by eriklupander on 2016-02-01.
 */
@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    IntygstjanstIntegrationService intygstjanstIntegrationService;

    @Override
    public List<IntygsData> getIntygsData(String hsaId) {
        return intygstjanstIntegrationService.getIntygsDataForCareUnit(hsaId);
    }
}
