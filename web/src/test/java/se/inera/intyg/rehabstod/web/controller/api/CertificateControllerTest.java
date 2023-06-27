package se.inera.intyg.rehabstod.web.controller.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.rehabstod.service.certificate.CertificateService;
import se.inera.intyg.rehabstod.service.sjukfall.util.PatientIdEncryption;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetAGCertificatesForPersonRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetLUCertificatesForPersonRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CertificateControllerTest {
    private static final String ENCRYPTED_PATIENT_ID = "ENCRYPTED_PATIENT_ID";
    private static final String PATIENT_ID = "PATIENT_ID";

    @Mock
    private PatientIdEncryption patientIdEncryption;

    @Mock
    private CertificateService certificateService;

    @InjectMocks
    private CertificateController certificateController;

    @Nested
    class AGForPerson {
        @Test
        void shouldUsePatientIdEncryptionIfPersonIdIsNotSet() {
            when(patientIdEncryption.decrypt(anyString())).thenReturn(ENCRYPTED_PATIENT_ID);
            final var request = new GetAGCertificatesForPersonRequest();
            request.setEncryptedPatientId(ENCRYPTED_PATIENT_ID);
            final var captor = ArgumentCaptor.forClass(String.class);

            certificateController.getAGForPerson(request);

            verify(certificateService).getAGCertificatesForPerson(captor.capture());
            assertEquals(ENCRYPTED_PATIENT_ID, captor.getValue());
        }

        @Test
        void shouldUsePersonIdIfEncryptedIsNotSet() {
            final var request = new GetAGCertificatesForPersonRequest();
            request.setPersonId(PATIENT_ID);
            final var captor = ArgumentCaptor.forClass(String.class);

            certificateController.getAGForPerson(request);

            verify(certificateService).getAGCertificatesForPerson(captor.capture());
            assertEquals(PATIENT_ID, captor.getValue());
        }

        @Test
        void shouldUseEncryptionIdIfBothAreSet() {
            when(patientIdEncryption.decrypt(anyString())).thenReturn(ENCRYPTED_PATIENT_ID);
            final var request = new GetAGCertificatesForPersonRequest();
            request.setPersonId(PATIENT_ID);
            request.setEncryptedPatientId(ENCRYPTED_PATIENT_ID);
            final var captor = ArgumentCaptor.forClass(String.class);

            certificateController.getAGForPerson(request);

            verify(certificateService).getAGCertificatesForPerson(captor.capture());
            assertEquals(ENCRYPTED_PATIENT_ID, captor.getValue());
        }
    }

    @Nested
    class LUForPerson {
        @Test
        void shouldUsePatientIdEncryptionIfPersonIdIsNotSet() {
            when(patientIdEncryption.decrypt(anyString())).thenReturn(ENCRYPTED_PATIENT_ID);
            final var request = new GetLUCertificatesForPersonRequest();
            request.setEncryptedPatientId(ENCRYPTED_PATIENT_ID);
            final var captor = ArgumentCaptor.forClass(String.class);

            certificateController.getLUForPerson(request);

            verify(certificateService).getLUCertificatesForPerson(captor.capture());
            assertEquals(ENCRYPTED_PATIENT_ID, captor.getValue());
        }

        @Test
        void shouldUsePersonIdIfEncryptedIsNotSet() {
            final var request = new GetLUCertificatesForPersonRequest();
            request.setPersonId(PATIENT_ID);
            final var captor = ArgumentCaptor.forClass(String.class);

            certificateController.getLUForPerson(request);

            verify(certificateService).getLUCertificatesForPerson(captor.capture());
            assertEquals(PATIENT_ID, captor.getValue());
        }

        @Test
        void shouldUseEncryptionIdIfBothAreSet() {
            when(patientIdEncryption.decrypt(anyString())).thenReturn(ENCRYPTED_PATIENT_ID);
            final var request = new GetLUCertificatesForPersonRequest();
            request.setPersonId(PATIENT_ID);
            request.setEncryptedPatientId(ENCRYPTED_PATIENT_ID);
            final var captor = ArgumentCaptor.forClass(String.class);

            certificateController.getLUForPerson(request);

            verify(certificateService).getLUCertificatesForPerson(captor.capture());
            assertEquals(ENCRYPTED_PATIENT_ID, captor.getValue());
        }
    }
}
