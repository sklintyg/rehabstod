/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.service.filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.sjukfall.dto.RekoStatusTypeDTO;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.integration.it.dto.PopulateFiltersRequestDTO;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstRestIntegrationService;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.certificate.CertificateService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKategori;
import se.inera.intyg.rehabstod.service.pu.PuService;
import se.inera.intyg.rehabstod.service.sjukfall.dto.OccupationTypeDTO;
import se.inera.intyg.rehabstod.service.sjukfall.dto.PopulateLUFilterResponseDTO;
import se.inera.intyg.rehabstod.service.sjukfall.dto.PopulateSickLeaveFilterResponseDTO;
import se.inera.intyg.rehabstod.service.sjukfall.dto.UnansweredCommunicationFilterType;
import se.inera.intyg.rehabstod.service.sjukfall.dto.UnansweredCommunicationFilterTypeDTO;
import se.inera.intyg.rehabstod.service.sjukfall.nameresolver.SjukfallEmployeeNameResolver;
import se.inera.intyg.rehabstod.service.user.FeatureService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.util.ControllerUtil;
import se.inera.intyg.rehabstod.web.model.Lakare;

@Service
public class PopulateFiltersServiceImpl implements PopulateFiltersService {

    private final UserService userService;
    private final IntygstjanstRestIntegrationService intygstjanstRestIntegrationService;
    private final DiagnosKapitelService diagnosKapitelService;
    private final PuService puService;
    private final FeatureService featureService;
    private final CertificateService certificateService;

    private final SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver;

    public PopulateFiltersServiceImpl(
        UserService userService,
        IntygstjanstRestIntegrationService intygstjanstRestIntegrationService,
        DiagnosKapitelService diagnosKapitelService,
        PuService puService, FeatureService featureService, CertificateService certificateService,
        SjukfallEmployeeNameResolver sjukfallEmployeeNameResolver) {
        this.userService = userService;
        this.intygstjanstRestIntegrationService = intygstjanstRestIntegrationService;
        this.diagnosKapitelService = diagnosKapitelService;
        this.puService = puService;
        this.featureService = featureService;
        this.certificateService = certificateService;
        this.sjukfallEmployeeNameResolver = sjukfallEmployeeNameResolver;
    }

    @Override
    public PopulateSickLeaveFilterResponseDTO populateSickLeaveFilters() {
        final var user = userService.getUser();
        final var careUnitId = ControllerUtil.getEnhetsIdForQueryingIntygstjansten(user);
        final var unitId = user.isValdVardenhetMottagning() ? user.getValdVardenhet().getId() : null;
        final var request = getRequest(user, unitId, careUnitId);
        final var responseFromIT = intygstjanstRestIntegrationService.getPopulatedFiltersForActiveSickLeaves(request);
        return new PopulateSickLeaveFilterResponseDTO(
            convertDoctors(responseFromIT.getActiveDoctors()),
            diagnosKapitelService.getDiagnosKapitelList(),
            convertDiagnosisChapters(responseFromIT.getDiagnosisChapters()),
            responseFromIT.getNbrOfSickLeaves(),
            convertRekoStatuses(responseFromIT.getRekoStatusTypes()),
            convertOccupationTypes(responseFromIT.getOccupationTypes()),
            getUnansweredCommunicationTypes(),
            featureService.isFeatureActive(AuthoritiesConstants.FEATURE_SRS)
        );
    }

    @Override
    public PopulateLUFilterResponseDTO populateLUFilters() {
        return PopulateLUFilterResponseDTO.builder()
            .allDiagnosisChapters(diagnosKapitelService.getDiagnosKapitelList())
            .doctors(certificateService.getDoctorsForUnit().getDoctors())
            .build();
    }

    private List<UnansweredCommunicationFilterTypeDTO> getUnansweredCommunicationTypes() {
        return Arrays
            .stream(UnansweredCommunicationFilterType.values())
            .map((type) -> new UnansweredCommunicationFilterTypeDTO(type.toString(), type.getName()))
            .collect(Collectors.toList());
    }

    private List<OccupationTypeDTO> convertOccupationTypes(
        List<se.inera.intyg.infra.sjukfall.dto.OccupationTypeDTO> occupationTypeDTOList) {
        if (occupationTypeDTOList == null) {
            return Collections.emptyList();
        }
        return occupationTypeDTOList.stream()
            .map(occupationTypeDTO -> new OccupationTypeDTO(occupationTypeDTO.getId(), occupationTypeDTO.getName()))
            .collect(Collectors.toList());
    }

    private List<se.inera.intyg.rehabstod.service.sjukfall.dto.RekoStatusTypeDTO> convertRekoStatuses(List<RekoStatusTypeDTO> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list
            .stream()
            .map((status) -> new se.inera.intyg.rehabstod.service.sjukfall.dto.RekoStatusTypeDTO(status.getId(), status.getName()))
            .collect(Collectors.toList());
    }

    private List<Lakare> convertDoctors(List<se.inera.intyg.infra.sjukfall.dto.Lakare> listToConvert) {
        if (listToConvert == null) {
            return Collections.emptyList();
        }
        final var lakareList = listToConvert.stream()
            .map((lakare) -> new Lakare(lakare.getId(), sjukfallEmployeeNameResolver.getEmployeeName(lakare.getId())))
            .collect(Collectors.toList());

        sjukfallEmployeeNameResolver.decorateAnyDuplicateNamesWithHsaId(lakareList);

        return lakareList;
    }

    private List<DiagnosKapitel> convertDiagnosisChapters(List<se.inera.intyg.infra.sjukfall.dto.DiagnosKapitel> diagnosisChapters) {
        if (diagnosisChapters == null) {
            return Collections.emptyList();
        }
        return diagnosisChapters
            .stream()
            .map(
                (diagnosisChapter) ->
                    new DiagnosKapitel(
                        convertDiagnosisCategory(diagnosisChapter.getFrom()),
                        convertDiagnosisCategory(diagnosisChapter.getTo()),
                        diagnosisChapter.getName()
                    )
            )
            .collect(Collectors.toList());
    }

    private DiagnosKategori convertDiagnosisCategory(se.inera.intyg.infra.sjukfall.dto.DiagnosKategori diagnosisCategory) {
        return new DiagnosKategori(diagnosisCategory.getLetter(), diagnosisCategory.getNumber());
    }


    private PopulateFiltersRequestDTO getRequest(RehabstodUser user, String unitId, String careUnitId) {
        final var request = new PopulateFiltersRequestDTO();
        request.setMaxDaysSinceSickLeaveCompleted(ControllerUtil.getMaxDagarSedanSjukfallAvslut(user));
        request.setUnitId(unitId);
        request.setCareUnitId(careUnitId);
        request.setProtectedPersonFilterId(puService.shouldFilterSickLeavesOnProtectedPerson(user) ? null : user.getHsaId());
        if (user.getUrval() == Urval.ISSUED_BY_ME) {
            request.setDoctorId(user.getHsaId());
        }
        return request;

    }
}
