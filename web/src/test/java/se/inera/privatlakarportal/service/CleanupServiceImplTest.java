package se.inera.privatlakarportal.service;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import se.inera.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.privatlakarportal.common.service.DateHelperService;
import se.inera.privatlakarportal.hsa.services.HospPersonService;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;

import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by pebe on 2015-09-30.
 */
@RunWith(MockitoJUnitRunner.class)
public class CleanupServiceImplTest {

    @Mock
    DateHelperService dateHelperService;

    @Mock
    HospPersonService hospPersonService;

    @Mock
    PrivatlakareRepository privatlakareRepository;

    @InjectMocks
    CleanupServiceImpl cleanupService;

    @Test
    public void testCleanup() throws IOException {

        LocalDateTime date = LocalDateTime.parse("2015-09-30");
        when(dateHelperService.now()).thenReturn(date);

        ArrayList<Privatlakare> list = new ArrayList<>();
        Privatlakare p1 = new CustomObjectMapper().readValue(new ClassPathResource("CleanupServiceImplTest/test1.json").getFile(), Privatlakare.class);
        Privatlakare p2 = new CustomObjectMapper().readValue(new ClassPathResource("CleanupServiceImplTest/test2.json").getFile(), Privatlakare.class);
        Privatlakare p3 = new CustomObjectMapper().readValue(new ClassPathResource("CleanupServiceImplTest/test3.json").getFile(), Privatlakare.class);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        // Should check for current date minus 12 months
        LocalDateTime date2 = LocalDateTime.parse("2014-09-30");
        when(privatlakareRepository.findNeverHadLakarBehorighetAndRegisteredBefore(date2)).thenReturn(list);

        when(hospPersonService.removeFromCertifier(any(String.class),any(String.class),any(String.class))).thenReturn(true);

        cleanupService.scheduledCleanupPrivatlakare();

        verify(privatlakareRepository).findNeverHadLakarBehorighetAndRegisteredBefore(date2);
        verify(privatlakareRepository).delete(p1);
        verify(privatlakareRepository).delete(p2);
        verify(privatlakareRepository).delete(p3);
        verifyNoMoreInteractions(privatlakareRepository);
    }

    @Test
    public void testCleanupFailedToContactHSA() throws IOException {

        LocalDateTime date = LocalDateTime.parse("2015-09-30");
        when(dateHelperService.now()).thenReturn(date);

        ArrayList<Privatlakare> list = new ArrayList<>();
        Privatlakare p1 = new CustomObjectMapper().readValue(new ClassPathResource("CleanupServiceImplTest/test1.json").getFile(), Privatlakare.class);
        Privatlakare p2 = new CustomObjectMapper().readValue(new ClassPathResource("CleanupServiceImplTest/test2.json").getFile(), Privatlakare.class);
        Privatlakare p3 = new CustomObjectMapper().readValue(new ClassPathResource("CleanupServiceImplTest/test3.json").getFile(), Privatlakare.class);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        // Should check for current date minus 12 months
        LocalDateTime date2 = LocalDateTime.parse("2014-09-30");
        when(privatlakareRepository.findNeverHadLakarBehorighetAndRegisteredBefore(date2)).thenReturn(list);

        when(hospPersonService.removeFromCertifier(any(String.class), any(String.class), any(String.class))).thenReturn(true);
        // Test failed with exception
        when(hospPersonService.removeFromCertifier(eq("personId1"), eq("SE165565594230-WEBCERT00001"), any(String.class))).thenThrow(new WebServiceException());
        // Test failed with error response
        when(hospPersonService.removeFromCertifier(eq("personId2"), eq("SE165565594230-WEBCERT00002"), any(String.class))).thenReturn(false);

        cleanupService.scheduledCleanupPrivatlakare();

        verify(privatlakareRepository).findNeverHadLakarBehorighetAndRegisteredBefore(date2);
        // p1 doesn't get deleted since we failed to call handleCertifier for p1
        // p2 doesn't get deleted since we failed to call handleCertifier for p2
        verify(privatlakareRepository).delete(p3);
        verifyNoMoreInteractions(privatlakareRepository);
    }
}
