package se.inera.intyg.rehabstod.integration.it.stub;

import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;

/**
 * Created by eriklupander on 2016-02-01.
 */
public interface SjukfallIntygDataGenerator {

    List<IntygsData> generateIntygsData(Integer numberOfPatients, Integer intygPerPatient);
}
