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
package se.inera.intyg.rehabstod.web.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.rehabstod.integration.it.dto.CreateSickLeaveRequestDTO;
import se.inera.intyg.rehabstod.integration.it.dto.TestDataOptionsDTO;
import se.inera.intyg.rehabstod.service.sjukfall.testability.TestabilityService;
import se.inera.intyg.rehabstod.service.testability.FakeLoginService;
import se.inera.intyg.rehabstod.web.controller.api.dto.FakeLoginDTO;
import se.inera.intyg.rehabstod.web.controller.api.dto.TestabilityResponseDTO;

@Slf4j
@RestController
@Profile("testability")
@RequestMapping("/api/testability")
@RequiredArgsConstructor
public class TestabilityController {

    private final TestabilityService testabilityService;
    private final FakeLoginService fakeLoginService;

    @PostMapping(value = "/createDefault")
    public TestabilityResponseDTO createDefaultTestData() {
        return new TestabilityResponseDTO(testabilityService.getDefaultTestData());
    }

    @PostMapping(value = "/createSickLeave", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TestabilityResponseDTO createSickLeave(@RequestBody CreateSickLeaveRequestDTO request) {
        return new TestabilityResponseDTO(testabilityService.createSickleave(request));
    }

    @GetMapping(value = "/testDataOptions")
    public TestDataOptionsDTO getTestDataOptions() {
        return testabilityService.getTestDataOptions();
    }

    @PostMapping(value = "/fake")
    public void login(@RequestBody FakeLoginDTO fakeLoginDTO, final HttpServletRequest request) {
        fakeLoginService.login(fakeLoginDTO.getHsaId(), fakeLoginDTO.getEnhetId(), request);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        fakeLoginService.logout(request.getSession(false));
    }

    @RequestMapping(value = "/commissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public String commissions() {
        return readFile("testability/commissions.json");
    }

    @RequestMapping(value = "/persons", produces = MediaType.APPLICATION_JSON_VALUE)
    public String persons() {
        return readFile("/testability/persons.json");
    }

    private static String readFile(String path) {
        final var cpr = new ClassPathResource(path);
        try (final var inputStream = cpr.getInputStream()) {
            return new String(
                FileCopyUtils.copyToByteArray(inputStream),
                StandardCharsets.UTF_8
            );
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
