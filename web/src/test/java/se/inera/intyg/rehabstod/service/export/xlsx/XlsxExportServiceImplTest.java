package se.inera.intyg.rehabstod.service.export.xlsx;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.inera.intyg.rehabstod.web.model.Sortering;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by eriklupander on 2016-02-24.
 */
public class XlsxExportServiceImplTest {

    private XlsxExportServiceImpl testee = new XlsxExportServiceImpl();

    @Test
    public void testBuildXlsx() throws IOException {
        byte[] data = testee.export(buildSjukfallList(), buildPrintRequest());
        assertNotNull(data);
        assertTrue(data.length > 0);
        IOUtils.write(data, new FileOutputStream(new File("/Users/eriklupander/intyg/dev.xlsx")));
    }

    private PrintSjukfallRequest buildPrintRequest() {
        PrintSjukfallRequest req = new PrintSjukfallRequest();
        req.setPersonnummer(buildPersonnummerList());
        req.setDiagnosGrupper(buildDiagnosGrupper());
        req.setLakare(buildLakare());
        req.setLangdIntervall(buildLangdIntervall());
        req.setMaxIntygsGlapp(5);
        req.setSortering(buildSortering());
        return req;
    }

    private Sortering buildSortering() {
        Sortering sortering = new Sortering();
        sortering.setKolumn("Namn");
        sortering.setOrder("ASC");
        return sortering;
    }

    private LangdIntervall buildLangdIntervall() {
        LangdIntervall langdIntervall = new LangdIntervall();
        langdIntervall.setMax(90);
        langdIntervall.setMin(30);
        return langdIntervall;
    }

    private List<String> buildLakare() {
        List<String> lakare = new ArrayList<>();
        lakare.add("Jan Nilsson");
        lakare.add("Ove Mört");
        return lakare;
    }

    private List<String> buildDiagnosGrupper() {
        List<String> diagnosGrupper = new ArrayList<>();
        diagnosGrupper.add("H00-H59: Sjukdomar i ögat och närliggande organ");
        diagnosGrupper.add("J00-J99: Andningsorganens sjukdomar");
        diagnosGrupper.add("M00-M99: Sjukdomar i muskuloskeletala systemet och bindväven");
        return diagnosGrupper;
    }

    private List<String> buildPersonnummerList() {
        List<String> personnummerList = new ArrayList<>();
        personnummerList.add("19121212-1212");
        return personnummerList;
    }

    private List<InternalSjukfall> buildSjukfallList() {
        List<InternalSjukfall> sjukfallList = new ArrayList<>();

        sjukfallList.add(buildInternalSjukfall());
        sjukfallList.add(buildInternalSjukfall());
        return sjukfallList;

    }

    private InternalSjukfall buildInternalSjukfall() {
        InternalSjukfall sjukfall = new InternalSjukfall();
        sjukfall.setSjukfall(buildSjukfall());
        return sjukfall;
    }

    private Sjukfall buildSjukfall() {
        Sjukfall sjukfall = new Sjukfall();
        sjukfall.setAktivGrad(75);
        sjukfall.setDagar(65);
        sjukfall.setDiagnos(buildDiagnos());
        sjukfall.setGrader(buildGrader());
        sjukfall.setIntyg(2);
        sjukfall.setLakare("Jan Nilsson");
        sjukfall.setPatient(buildPatient());
        sjukfall.setStart(LocalDate.now().minusMonths(2));
        sjukfall.setSlut(LocalDate.now().plusWeeks(2));
        return sjukfall;
    }

    private Patient buildPatient() {
        Patient patient = new Patient();
        patient.setAlder(54);
        patient.setId("19121212-1212");
        patient.setKon(Gender.M);
        patient.setNamn("Tolvan Tolvansson");
        return patient;
    }

    private List<Integer> buildGrader() {
        return Arrays.asList(50, 75);
    }

    private Diagnos buildDiagnos() {
        Diagnos diagnos = new Diagnos();
        diagnos.setKod("J22");
        return diagnos;
    }
}
