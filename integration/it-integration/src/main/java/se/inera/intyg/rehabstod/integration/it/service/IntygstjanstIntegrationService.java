package se.inera.intyg.rehabstod.integration.it.service;

import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;

/**
 * Created by eriklupander on 2016-02-01.
 */
public interface IntygstjanstIntegrationService {
    List<IntygsData> getIntygsDataForCareUnit(String hsaId);
}
