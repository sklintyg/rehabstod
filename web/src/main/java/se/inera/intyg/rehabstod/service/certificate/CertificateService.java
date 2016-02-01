package se.inera.intyg.rehabstod.service.certificate;

import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;

/**
 * Created by eriklupander on 2016-02-01.
 */
public interface CertificateService {

    List<IntygsData> getIntygsData(String hsaId);

}
