package se.inera.intyg.rehabstod.service.sjukfall.nameresolver;

import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

import java.util.List;

/**
 * Created by eriklupander on 2017-02-23.
 */
public interface SjukfallEmployeeNameResolver {

    void enrichWithHsaEmployeeNames(List<SjukfallEnhet> sjukfallList);

    void updateDuplicateDoctorNamesWithHsaId(List<SjukfallEnhet> sjukfallList);

}
