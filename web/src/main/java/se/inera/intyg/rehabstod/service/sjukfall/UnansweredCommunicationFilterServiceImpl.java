package se.inera.intyg.rehabstod.service.sjukfall;

import se.inera.intyg.rehabstod.service.sjukfall.dto.UnansweredCommunicationFilterType;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

public class UnansweredCommunicationFilterServiceImpl implements UnansweredCommunicationFilterService {
    @Override
    public boolean filter(SjukfallEnhet sickLeave, String filterTypeId) {
        if (filterTypeId == null || filterTypeId.isBlank()) {
            return true;
        }

        final var convertedFilterTypeId = UnansweredCommunicationFilterType.fromId(filterTypeId);

        if(convertedFilterTypeId == null) {
            return true;
        }

        switch (convertedFilterTypeId) {
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_1:
                return sickLeave.getUnansweredOther() + sickLeave.getObesvaradeKompl() == 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_2:
                return sickLeave.getUnansweredOther() + sickLeave.getObesvaradeKompl() > 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_3:
                return sickLeave.getObesvaradeKompl() > 0;
            case UNANSWERED_COMMUNICATION_FILTER_TYPE_4:
                return sickLeave.getUnansweredOther() > 0;
            default:
                return true;
        }
    }
}
