/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.service.export.pdf;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import se.inera.intyg.common.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.inera.intyg.rehabstod.web.model.Sortering;

/**
 * Created by marced on 24/02/16.
 */

public class PdfExportServiceImplTest {
    RehabstodUser user;

    @Before
    public void setUp() throws Exception {
        user = new RehabstodUser("HSA1111", "Johannes Nielsen-Kornbach");
        user.setValdVardenhet(new SelectableVardenhet() {
            @Override
            public String getId() {
                return "1111";
            }

            @Override
            public String getNamn() {
                return "Gläntans vårdcentral";
            }

            @Override
            public List<String> getHsaIds() {
                return null;
            }
        });
    }

    @Test
    public void testExportIssuedByMe() throws Exception {
        PdfExportService exporter = new PdfExportServiceImpl();
        user.setUrval(Urval.ISSUED_BY_ME);
        final byte[] export = exporter.export(createSjukFallList(), createPrintRequest(), user);
        assertTrue(export.length > 0);
        // Files.write(Paths.get("./test_issued_by_me.pdf"), export);
    }

    @Test
    public void testExportAll() throws Exception {
        PdfExportService exporter = new PdfExportServiceImpl();
        user.setUrval(Urval.ALL);
        final byte[] export = exporter.export(createSjukFallList(), createPrintRequest(), user);
        assertTrue(export.length > 0);

        // Files.write(Paths.get("./test_all.pdf"), export);
    }

    private PrintSjukfallRequest createPrintRequest() {
        PrintSjukfallRequest r = new PrintSjukfallRequest();

        r.setDiagnosGrupper(Arrays.asList("M00-M99", "J00-J99"));
        r.setFritext("Fritext");
        r.setLakare(Arrays.asList("Per Karlsson", "Johan Nilsson"));
        LangdIntervall langdIntervall = new LangdIntervall();

        langdIntervall.setMin(1);
        langdIntervall.setMax(366);
        r.setLangdIntervall(langdIntervall);
        r.setMaxIntygsGlapp(30);
        final Sortering sortering = new Sortering();
        sortering.setKolumn("Personnummer");
        sortering.setOrder("Stigande");
        r.setSortering(sortering);
        return r;
    }

    private List<InternalSjukfall> createSjukFallList() {
        List<InternalSjukfall> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(createSjukFall(i, "19121212-" + i));
        }
        return list;
    }

    private static InternalSjukfall createSjukFall(int index, String personNummer) {
        Sjukfall sjukfall = new Sjukfall();

        Patient patient = new Patient();
        patient.setId(personNummer);
        patient.setAlder(50 + index / 2);
        patient.setNamn("patient " + personNummer);
        patient.setKon(index % 2 == 0 ? Gender.M : Gender.F);
        sjukfall.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        final Diagnos diagnos = new Diagnos();
        diagnos.setKapitel("M00-M99");
        diagnos.setKod("M16");
        diagnos.setIntygsVarde("M16" + index);

        sjukfall.setDiagnos(diagnos);
        sjukfall.setStart(new LocalDate().plusDays(index));
        sjukfall.setSlut(sjukfall.getStart().plusDays(index));
        sjukfall.setDagar(index * 2 + index % 3);
        sjukfall.setIntyg(1);
        sjukfall.setGrader(index % 3 == 0 ? Arrays.asList(25, 50) : Arrays.asList(50, 75));
        sjukfall.setAktivGrad(50);

        Lakare lakare = new Lakare();
        lakare.setNamn("Doktor Glas");
        sjukfall.setLakare(lakare);

        InternalSjukfall is = new InternalSjukfall();
        is.setSjukfall(sjukfall);

        return is;
    }
}
