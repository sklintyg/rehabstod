package se.inera.intyg.rehabstod.integration.it.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitType;

/**
 * Created by eriklupander on 2016-01-29.
 */
@Service
public class IntygstjanstClientServiceImpl implements IntygstjanstClientService {

    @Autowired
    private ListActiveSickLeavesForCareUnitResponderInterface service;

    @Value("${it.service.logicalAddress}")
    private String logicalAddress;

    @Override
    public ListActiveSickLeavesForCareUnitResponseType getSjukfall(String unitHsaId) {
        ListActiveSickLeavesForCareUnitType params = new ListActiveSickLeavesForCareUnitType();
        ListActiveSickLeavesForCareUnitResponseType response = service.listActiveSickLeavesForCareUnit(logicalAddress, params);
        return response;
    }
}
