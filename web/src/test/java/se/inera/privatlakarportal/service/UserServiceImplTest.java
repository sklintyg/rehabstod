package se.inera.privatlakarportal.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import se.inera.privatlakarportal.auth.PrivatlakarUser;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.privatlakarportal.common.model.RegistrationStatus;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.privatlakarportal.pu.model.Person;
import se.inera.privatlakarportal.pu.model.PersonSvar;
import se.inera.privatlakarportal.pu.services.PUService;
import se.inera.privatlakarportal.service.model.User;

import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by pebe on 2015-09-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String PERSON_ID = "191212121212";

    @Mock
    private PrivatlakareRepository privatlakareRepository;

    @Mock
    private PUService puService;

    @InjectMocks
    private UserServiceImpl userService;

    private Privatlakare privatlakareAuthorized, privatlakareNoHosp, privatlakareNotAuthorized;

    @Before
    public void setupUser() throws IOException {
        SecurityContextHolder.setContext(getSecurityContext(PERSON_ID, "Test User"));
        privatlakareAuthorized = new CustomObjectMapper().readValue(
            new ClassPathResource("UserServiceImplTest/test_authorized.json").getFile(), Privatlakare.class);
        privatlakareNotAuthorized = new CustomObjectMapper().readValue(
            new ClassPathResource("UserServiceImplTest/test_not_authorized.json").getFile(), Privatlakare.class);
        privatlakareNoHosp = new CustomObjectMapper().readValue(
                new ClassPathResource("UserServiceImplTest/test_no_hosp.json").getFile(), Privatlakare.class);
    }

    @Test
    public void testGetUser() {
        PrivatlakarUser user = userService.getUser();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Test User", user.getName());
    }

    @Test
    public void testGetUserNoRegistration() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);
        when(puService.getPerson(PERSON_ID)).thenReturn(new PersonSvar(new Person(PERSON_ID, false, "Test", "", "User", "", "", ""), PersonSvar.Status.FOUND));

        User user = userService.getUserWithStatus();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Test User", user.getName());
        assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
        assertEquals(RegistrationStatus.NOT_STARTED, user.getStatus());
        assertEquals(true, user.isNameFromPuService());
        assertEquals(false, user.isNameUpdated());
    }

    @Test
    public void testGetUserNotAuthorized() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakareNotAuthorized);
        when(puService.getPerson(PERSON_ID)).thenReturn(new PersonSvar(new Person(PERSON_ID, false, "Test", "", "User", "", "", ""), PersonSvar.Status.FOUND));

        User user = userService.getUserWithStatus();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Test User", user.getName());
        assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
        assertEquals(RegistrationStatus.NOT_AUTHORIZED, user.getStatus());
        assertEquals(true, user.isNameFromPuService());
        assertEquals(false, user.isNameUpdated());
    }

    @Test
    public void testGetUserNoHosp() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakareNoHosp);
        when(puService.getPerson(PERSON_ID)).thenReturn(new PersonSvar(new Person(PERSON_ID, false, "Test", "", "User", "", "", ""), PersonSvar.Status.FOUND));

        User user = userService.getUserWithStatus();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Test User", user.getName());
        assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
        assertEquals(RegistrationStatus.WAITING_FOR_HOSP, user.getStatus());
        assertEquals(true, user.isNameFromPuService());
        assertEquals(false, user.isNameUpdated());
    }

    @Test
    public void testGetUserRegistration() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakareAuthorized);
        when(puService.getPerson(PERSON_ID)).thenReturn(new PersonSvar(new Person(PERSON_ID, false, "Test", "", "User", "", "", ""), PersonSvar.Status.FOUND));

        User user = userService.getUserWithStatus();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Test User", user.getName());
        assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
        assertEquals(RegistrationStatus.AUTHORIZED, user.getStatus());
        assertEquals(true, user.isNameFromPuService());
        assertEquals(false, user.isNameUpdated());
    }

    @Test
    public void testGetUserNewName() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakareAuthorized);
        when(puService.getPerson(PERSON_ID)).thenReturn(new PersonSvar(new Person(PERSON_ID, false, "Ny", "", "User", "", "", ""), PersonSvar.Status.FOUND));

        User user = userService.getUserWithStatus();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Ny User", user.getName());
        assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
        assertEquals(RegistrationStatus.AUTHORIZED, user.getStatus());
        assertEquals(true, user.isNameFromPuService());
        assertEquals(true, user.isNameUpdated());
    }

    @Test
    public void testGetUserNotInPUService() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);
        when(puService.getPerson(PERSON_ID)).thenReturn(new PersonSvar(null, PersonSvar.Status.NOT_FOUND));

        User user = userService.getUserWithStatus();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Test User", user.getName());
        assertEquals(PersonSvar.Status.NOT_FOUND, user.getPersonSvarStatus());
        assertEquals(RegistrationStatus.NOT_STARTED, user.getStatus());
        assertEquals(false, user.isNameFromPuService());
        assertEquals(false, user.isNameUpdated());
    }

    @Test
    public void testGetUserErrorFromPUService() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);
        when(puService.getPerson(PERSON_ID)).thenReturn(new PersonSvar(null, PersonSvar.Status.ERROR));

        User user = userService.getUserWithStatus();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Test User", user.getName());
        assertEquals(PersonSvar.Status.ERROR, user.getPersonSvarStatus());
        assertEquals(RegistrationStatus.NOT_STARTED, user.getStatus());
        assertEquals(false, user.isNameFromPuService());
        assertEquals(false, user.isNameUpdated());
    }

    @Test
    public void testGetUserCantContactPUService() {
        when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);
        when(puService.getPerson(PERSON_ID)).thenThrow(new WebServiceException("Could not send message"));

        User user = userService.getUserWithStatus();
        assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
        assertEquals("Test User", user.getName());
        assertEquals(PersonSvar.Status.ERROR, user.getPersonSvarStatus());
        assertEquals(RegistrationStatus.NOT_STARTED, user.getStatus());
        assertEquals(false, user.isNameFromPuService());
        assertEquals(false, user.isNameUpdated());
    }

    @Test(expected = PrivatlakarportalServiceException.class)
    public void testGetUserNoLoggedInUser() {
        SecurityContextHolder.clearContext();
        userService.getUserWithStatus();
    }

    // Create a fake SecurityContext for a user
    private SecurityContext getSecurityContext(final String personId, final String name) {
        final PrivatlakarUser user = new PrivatlakarUser(personId, name);
        return new SecurityContext() {
            @Override
            public void setAuthentication(Authentication authentication) {
            }

            @Override
            public Authentication getAuthentication() {
                return new Authentication() {
                    @Override
                    public Object getPrincipal() {
                        return user;
                    }

                    @Override
                    public boolean isAuthenticated() {
                        return true;
                    }

                    @Override
                    public String getName() {
                        return "questionResource";
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return null;
                    }

                    @Override
                    public Object getCredentials() {
                        return null;
                    }

                    @Override
                    public Object getDetails() {
                        return null;
                    }

                    @Override
                    public void setAuthenticated(boolean isAuthenticated) {
                    }
                };
            }
        };
    }
}
