package se.inera.intyg.rehabstod.integration.it.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.getactivesickleavesforcareunit.v1.GetActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.getactivesickleavesforcareunit.v1.GetActiveSickLeavesForCareUnitResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.getactivesickleavesforcareunit.v1.GetActiveSickLeavesForCareUnitType;

/**
 * Created by eriklupander on 2016-01-29.
 */
@Service
public class IntygstjanstClientServiceImpl implements IntygstjanstClientService {

    @Autowired
    private GetActiveSickLeavesForCareUnitResponderInterface service;

    @Value("${it.service.logicalAddress}")
    private String logicalAddress;

    @Override
    public GetActiveSickLeavesForCareUnitResponseType getSjukfall(String unitHsaId) {
        GetActiveSickLeavesForCareUnitType params = new GetActiveSickLeavesForCareUnitType();
        GetActiveSickLeavesForCareUnitResponseType response = service.getActiveSickLeavesForCareUnit(logicalAddress, params);
        return response;
    }
}
