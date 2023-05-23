package se.inera.intyg.rehabstod.service.sjukfall;

import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.it.dto.SetRekoStatusToSickLeaveRequestDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;

import java.time.LocalDateTime;

@Service
public class SetRekoStatusToSickLeaveServiceImpl implements SetRekoStatusToSickLeaveService {

    private final IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    private final UserService userService;

    public SetRekoStatusToSickLeaveServiceImpl(IntygstjanstRestIntegrationService intygstjanstRestIntegrationService, UserService userService) {
        this.intygstjanstRestIntegrationService = intygstjanstRestIntegrationService;
        this.userService = userService;
    }

    public void set(String patientId, String status, LocalDateTime sickLeaveTimestamp) {
        final var user = userService.getUser();
        final var careUnitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        final var unitId = user.isValdVardenhetMottagning() ? user.getValdVardenhet().getId() : null;

        final var request = new SetRekoStatusToSickLeaveRequestDTO(
                patientId,
                status,
                user.getValdVardgivare().getId(),
                careUnitId,
                unitId,
                user.getHsaId(),
                user.getNamn(),
                sickLeaveTimestamp
        );

        intygstjanstRestIntegrationService.setRekoStatusForSickLeave(request);
    }
}
