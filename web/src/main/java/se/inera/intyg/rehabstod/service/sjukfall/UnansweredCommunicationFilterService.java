package se.inera.intyg.rehabstod.service.sjukfall;

import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

public interface UnansweredCommunicationFilterService {
    boolean filter(SjukfallEnhet sickLeave, String filterTypeId);
}
