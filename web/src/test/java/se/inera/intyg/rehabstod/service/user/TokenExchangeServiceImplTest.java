package se.inera.intyg.rehabstod.service.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenExchangeServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void exchange() {

        Mockito
                .when(restTemplate.getForEntity("http://localhost:8080/employee/E001", Employee.class))
          .thenReturn(new ResponseEntity(emp, HttpStatus.OK));
    }

    @Test
    public void refresh() {
    }
}