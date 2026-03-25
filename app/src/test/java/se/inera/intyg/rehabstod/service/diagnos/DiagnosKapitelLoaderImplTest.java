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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.rehabstod.application.diagnos.DiagnosKapitelLoaderImpl;
import se.inera.intyg.rehabstod.application.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.config.properties.AppProperties;
import se.inera.intyg.rehabstod.config.properties.AppProperties.Resources;

/** Created by eriklupander on 2016-04-14. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DiagnosKapitelLoaderImpl.class, DiagnosKapitelLoaderImplTest.TestConfig.class})
class DiagnosKapitelLoaderImplTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public AppProperties appProperties() {
      return new AppProperties(null, null, null, null, null, null,
          new Resources(null, null, null, "placeholder", null, null, null, 500), null);
    }
  }

  @Autowired private DiagnosKapitelLoaderImpl testee;

  @Test
  void testLoadDiagnosKapitel() throws IOException {
    ReflectionTestUtils.setField(
        testee, "diagnosKapitelFile", "classpath:DiagnosKapitelLoaderTest/diagnoskapitel.txt");
    List<DiagnosKapitel> diagnosKapitel = testee.loadDiagnosKapitel();
    assertNotNull(diagnosKapitel);
    assertEquals(22, diagnosKapitel.size());
  }

  @Test
  void testLoadDiagnosKapitelEmptyFile() throws IOException {
    ReflectionTestUtils.setField(
        testee, "diagnosKapitelFile", "classpath:DiagnosKapitelLoaderTest/diagnoskapitel_tom.txt");
    List<DiagnosKapitel> diagnosKapitel = testee.loadDiagnosKapitel();
    assertNotNull(diagnosKapitel);
    assertEquals(0, diagnosKapitel.size());
  }

  @Test
  void testLoadDiagnosKapitelInvaludFileThrowsException() throws IOException {
    ReflectionTestUtils.setField(
        testee,
        "diagnosKapitelFile",
        "classpath:DiagnosKapitelLoaderTest/diagnoskapitel_invalid.txt");
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testee.loadDiagnosKapitel();
        });
  }
}
