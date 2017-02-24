package se.inera.intyg.rehabstod.service.sjukfall.nameresolver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.service.hsa.EmployeeNameService;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2017-02-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallEmployeeNameResolverTest {

    private final String lakareId1 = "IFV1239877878-1049";
    private final String lakareNamn1 = "Jan Nilsson";
    private final String lakareId2 = "IFV1239877878-104B";
    private final String lakareNamn2 = "Åsa Andersson";
    private final String lakareId3 = "IFV1239877878-1047";
    private final String lakareNamn3 = "Jan Nilsson";
    private final String lakareNamn3Alt = "Jan Namnbytarsson";

    @Mock
    private EmployeeNameService employeeNameService;

    @InjectMocks
    private SjukfallEmployeeNameResolver testee = new SjukfallEmployeeNameResolverImpl();

    @Test
    public void testUpdateDuplicateDoctorNamesWithHsaId() {
        List<InternalSjukfall> sjukfallList = createSjukfallList();
        testee.updateDuplicateDoctorNamesWithHsaId(sjukfallList);

        assertEquals(lakareNamn1 + " (" + lakareId1 + ")", sjukfallList.get(0).getLakare().getNamn());
        assertEquals(lakareNamn2, sjukfallList.get(3).getLakare().getNamn());
        assertEquals(lakareNamn3 + " (" + lakareId3 + ")", sjukfallList.get(5).getLakare().getNamn());
    }

    @Test
    public void testUpdateNoDuplicateDoctorNames() {
        List<InternalSjukfall> sjukfallList = createSjukfallList()
                .stream()
                .filter(sf -> sf.getLakare().getNamn().equals(lakareNamn2))
                .collect(Collectors.toList());

        testee.updateDuplicateDoctorNamesWithHsaId(sjukfallList);
        assertEquals(lakareNamn2, sjukfallList.get(0).getLakare().getNamn());
        assertEquals(lakareNamn2, sjukfallList.get(1).getLakare().getNamn());
    }

    @Test
    public void testEmployeeNameLakare1HasNoRecordShowHsaIdAsName() {
        List<InternalSjukfall> sjukfallList = createSjukfallList();
        when(employeeNameService.getEmployeeHsaName(lakareId1)).thenReturn(null);
        testee.enrichWithHsaEmployeeNames(sjukfallList);
        assertEquals(lakareId1, sjukfallList.get(0).getLakare().getNamn());
    }

    @Test
    public void testEmployeeNameLakare2HasRecordSameAsNameOnSjukfall() {
        List<InternalSjukfall> sjukfallList = createSjukfallList();
        when(employeeNameService.getEmployeeHsaName(lakareId2)).thenReturn(lakareNamn2);
        testee.enrichWithHsaEmployeeNames(sjukfallList);
        assertEquals(lakareNamn2, sjukfallList.get(2).getLakare().getNamn());
    }

    @Test
    public void testEmployeeNameLakare3HasRecordDifferentNameOnSjukfall() {
        List<InternalSjukfall> sjukfallList = createSjukfallList();
        when(employeeNameService.getEmployeeHsaName(lakareId3)).thenReturn(lakareNamn3Alt);
        testee.enrichWithHsaEmployeeNames(sjukfallList);
        assertEquals(lakareNamn3Alt, sjukfallList.get(4).getLakare().getNamn());
    }

    private List<InternalSjukfall> createSjukfallList() {
        List<InternalSjukfall> sjukfallList = new ArrayList<>();

        sjukfallList.add(createSjukfall(lakareId1, lakareNamn1));
        sjukfallList.add(createSjukfall(lakareId1, lakareNamn1));
        sjukfallList.add(createSjukfall(lakareId2, lakareNamn2));
        sjukfallList.add(createSjukfall(lakareId2, lakareNamn2));
        sjukfallList.add(createSjukfall(lakareId3, lakareNamn3));
        sjukfallList.add(createSjukfall(lakareId3, lakareNamn3));

        return sjukfallList;
    }

    private InternalSjukfall createSjukfall(String lakareId, String lakareNamn) {
        InternalSjukfall sjukfall = new InternalSjukfall();
        Lakare lakare = new Lakare(lakareId, lakareNamn);
        sjukfall.setLakare(lakare);

        return sjukfall;
    }
}
