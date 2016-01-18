package se.inera.privatlakarportal.service;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.privatlakarportal.integration.terms.services.dto.Terms;
import se.inera.privatlakarportal.persistence.model.MedgivandeText;
import se.inera.privatlakarportal.persistence.repository.MedgivandeTextRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by pebe on 2015-09-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class TermsServiceImplTest {

    @Mock
    private MedgivandeTextRepository medgivandeTextRepository;

    @InjectMocks
    private TermsServiceImpl termsService;

    @Test
    public void testGetTerms() {

        MedgivandeText medgivandeText = new MedgivandeText();
        medgivandeText.setVersion(1L);
        medgivandeText.setMedgivandeText("Testtext");
        medgivandeText.setDatum(LocalDateTime.parse("2015-09-01"));
        when(medgivandeTextRepository.findLatest()).thenReturn(medgivandeText);

        Terms response = termsService.getTerms();
        assertEquals(LocalDateTime.parse("2015-09-01"), response.getDate());
        assertEquals("Testtext", response.getText());
        assertEquals(1L, response.getVersion());
    }
}
