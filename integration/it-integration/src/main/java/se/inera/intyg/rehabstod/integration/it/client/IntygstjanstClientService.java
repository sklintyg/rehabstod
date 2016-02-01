package se.inera.intyg.rehabstod.integration.it.client;

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;

/**
 * Created by eriklupander on 2016-01-29.
 */
public interface IntygstjanstClientService {

    ListActiveSickLeavesForCareUnitResponseType getSjukfall(String unitHsaId);
}
