/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.intyg.rehabstod.service.sjukfall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeaveLengthInterval;
import se.inera.intyg.rehabstod.integration.it.dto.SickLeavesRequestDTO;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKategori;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.SickLeavesFilterRequestDTO;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;

@Service
public class CreateSickLeaveRequestServiceImpl implements CreateSickLeaveRequestService {

    private final PuService puService;
    private final UserService userService;

    public CreateSickLeaveRequestServiceImpl(PuService puService, UserService userService) {
        this.puService = puService;
        this.userService = userService;
    }

    @Override
    public SickLeavesRequestDTO create(SickLeavesFilterRequestDTO filterRequest, boolean includeParameters) {
        final var user = userService.getUser();
        final var careUnitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        final var unitId = user.isValdVardenhetMottagning() ? user.getValdVardenhet().getId() : null;
        return SickLeavesRequestDTO.builder()
            .unitId(unitId)
            .careUnitId(careUnitId)
            .fromPatientAge(filterRequest.getFromPatientAge())
            .toPatientAge(filterRequest.getToPatientAge())
            .fromSickLeaveEndDate(filterRequest.getFromSickLeaveEndDate())
            .toSickLeaveEndDate(filterRequest.getToSickLeaveEndDate())
            .rekoStatusTypeIds(filterRequest.getRekoStatusTypeIds())
            .occupationTypeIds(filterRequest.getOccupationTypeIds())
            .textSearch(filterRequest.getTextSearch())
            .doctorIds(getDoctorIds(user, filterRequest.getDoctorIds()))
            .maxCertificateGap(includeParameters ? ControllerUtil.getMaxGlapp(user) : 0)
            .maxDaysSinceSickLeaveCompleted(includeParameters ? ControllerUtil.getMaxDagarSedanSjukfallAvslut(user) : 0)
            .sickLeaveLengthIntervals(convertSickLeaveLengthIntervals(filterRequest.getSickLeaveLengthIntervals()))
            .diagnosisChapters(convertDiagnosisChapters(filterRequest.getDiagnosisChapters()))
            .protectedPersonFilterId(puService.shouldFilterSickLeavesOnProtectedPerson(user) ? null : user.getHsaId())
            .build();
    }

    private List<String> getDoctorIds(RehabstodUser user, List<String> filterDoctorIds) {
        final List<String> list = new ArrayList<>();
        if (filterDoctorIds != null) {
            list.addAll(filterDoctorIds);
        }

        if (user.getUrval().equals(Urval.ISSUED_BY_ME)) {
            list.add(user.getHsaId());
        }
        return list;
    }

    private List<SickLeaveLengthInterval> convertSickLeaveLengthIntervals(
        List<se.inera.intyg.rehabstod.service.sjukfall.dto.SickLeaveLengthInterval> intervals) {
        if (intervals == null) {
            return Collections.emptyList();
        }
        return intervals.stream()
            .map(interval -> new SickLeaveLengthInterval(interval.getFrom(), interval.getTo()))
            .collect(Collectors.toList());
    }

    private List<se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel> convertDiagnosisChapters(List<DiagnosKapitel> diagnosisChapters) {
        if (diagnosisChapters == null) {
            return Collections.emptyList();
        }
        return diagnosisChapters
            .stream()
            .map(
                diagnosisChapter ->
                    new se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel(
                        convertDiagnosisCategory(diagnosisChapter.getFrom()),
                        convertDiagnosisCategory(diagnosisChapter.getTo()),
                        diagnosisChapter.getName()
                    )
            )
            .collect(Collectors.toList());
    }

    private se.inera.intyg.infra.sjukfall.dto.DiagnosKategori convertDiagnosisCategory(DiagnosKategori diagnosisCategory) {
        return new se.inera.intyg.infra.sjukfall.dto.DiagnosKategori(diagnosisCategory.getLetter(), diagnosisCategory.getNumber());
    }
}
