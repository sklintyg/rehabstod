/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.diagnos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.rehabstod.application.diagnos.DiagnosKoderLoaderImpl;
import se.inera.intyg.rehabstod.application.diagnos.IcdCodeConverter;
import se.inera.intyg.rehabstod.infrastructure.config.properties.AppProperties;
import se.inera.intyg.rehabstod.infrastructure.config.properties.AppProperties.Resources;

/** Created by eriklupander on 2016-04-14. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DiagnosKoderLoaderImpl.class, IcdCodeConverter.class, DiagnosKoderLoaderImplTest.TestConfig.class})
class DiagnosKoderLoaderImplTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public AppProperties appProperties() {
      return new AppProperties(null, null, null, null, null, null,
          new Resources(null, null, null, null, null, "placeholder-ksh97p", "placeholder-icd10se", 500), null);
    }
  }

  @Autowired private DiagnosKoderLoaderImpl diagnosKoderLoader;

  @Test
  void testLoadDiagnosKoder() throws IOException {
    specifyDiagnosKodFiler();
    Map<String, String> diagnosKoder = diagnosKoderLoader.loadDiagnosKoder();
    assertNotNull(diagnosKoder);
    assertEquals(39350, diagnosKoder.size());
  }

  private void specifyDiagnosKodFiler() {
    ReflectionTestUtils.setField(
        diagnosKoderLoader,
        "diagnosisCodeIcd10SeFile",
        loadDiagnosKodFile("icd10se/icd-10-se.tsv"));
    ReflectionTestUtils.setField(
        diagnosKoderLoader, "diagnosKodKS97PKodFile", loadDiagnosKodFile("KSH97P_KOD.ANS"));
  }

  private String loadDiagnosKodFile(String file) {
    return "classpath:DiagnosKoderLoaderTest/" + file;
  }
}
