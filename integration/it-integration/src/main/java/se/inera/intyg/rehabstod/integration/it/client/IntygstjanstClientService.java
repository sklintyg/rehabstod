package se.inera.intyg.rehabstod.integration.it.client;

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.getactivesickleavesforcareunit.v1.GetActiveSickLeavesForCareUnitResponseType;

/**
 * Created by eriklupander on 2016-01-29.
 */
public interface IntygstjanstClientService {

    GetActiveSickLeavesForCareUnitResponseType getSjukfall(String unitHsaId);
}
