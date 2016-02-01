package se.inera.intyg.rehabstod.integration.it.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ResultCodeEnum;
import se.inera.intyg.rehabstod.integration.it.client.IntygstjanstClientService;
import se.inera.intyg.rehabstod.integration.it.exception.IntygstjanstIntegrationException;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;

/**
 * Created by eriklupander on 2016-02-01.
 */
@Service
public class IntygstjanstIntegrationServiceImpl implements IntygstjanstIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(IntygstjanstIntegrationServiceImpl.class);

    @Autowired
    private IntygstjanstClientService intygstjanstClientService;

    @Override
    public List<IntygsData> getIntygsDataForCareUnit(String hsaId) {
        ListActiveSickLeavesForCareUnitResponseType responseType = intygstjanstClientService.getSjukfall(hsaId);
        if (responseType.getResultCode() == ResultCodeEnum.OK) {
            return responseType.getIntygsLista().getIntygsData();
        } else {
            log.error("An error occured fetching sick leave certificates. Error type: {0}. Error msg: {1}", responseType.getResultCode().name(), responseType.getComment());
            throw new IntygstjanstIntegrationException();
        }

    }
}
