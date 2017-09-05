package se.inera.intyg.rehabstod.service.sjukfall.pu;

import se.inera.intyg.rehabstod.web.model.SjukfallEnhetRS;

import java.util.List;

/**
 * Created by eriklupander on 2017-09-05.
 */
public interface SjukfallPuService {
    void enrichWithPatientNamesAndFilterSekretess(List<SjukfallEnhetRS> sjukfallList);
}
