/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.komplettering;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.rehabstod.integration.wc.service.WcIntegrationService;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

@RunWith(MockitoJUnitRunner.class)
public class KompletteringInfoDecoratorImplTest {

    @Mock
    private WcIntegrationService wcIntegrationService;

    @InjectMocks
    private KompletteringInfoDecoratorImpl testee;

    @Test
    public void updateSjukfallEnhetKompetteringar() {
        Map<String, Integer> kompl = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            kompl.put(Integer.toString(i), i);
        }

        when(wcIntegrationService.getCertificateAdditionsForIntyg(any(List.class))).thenReturn(kompl);
        List<SjukfallEnhet> sjukfall = new ArrayList<>();

        final SjukfallEnhet sjukfall0 = createSjukfall("0");
        final SjukfallEnhet sjukfall1 = createSjukfall("1");
        final SjukfallEnhet sjukfall23 = createSjukfall("2", "3");
        final SjukfallEnhet sjukfall456 = createSjukfall("4", "5", "6");
        final SjukfallEnhet sjukfallNotPresent = createSjukfall("Zero");
        sjukfall.add(sjukfall0);
        sjukfall.add(sjukfall1);
        sjukfall.add(sjukfall23);
        sjukfall.add(sjukfall456);
        sjukfall.add(sjukfallNotPresent);

        testee.updateSjukfallEnhetKompetteringar(sjukfall);

        assertEquals(0, sjukfall0.getObesvaradeKompl());
        assertEquals(1, sjukfall1.getObesvaradeKompl());
        assertEquals(5, sjukfall23.getObesvaradeKompl());
        assertEquals(15, sjukfall456.getObesvaradeKompl());
        assertEquals(0, sjukfallNotPresent.getObesvaradeKompl());
    }

    private SjukfallEnhet createSjukfall(String... intygIds) {
        SjukfallEnhet sfe = new SjukfallEnhet();
        sfe.setIntygLista(Arrays.asList(intygIds));
        return sfe;
    }
}
