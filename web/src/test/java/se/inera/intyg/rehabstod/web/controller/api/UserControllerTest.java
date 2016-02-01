package se.inera.intyg.rehabstod.web.controller.api;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.ChangeSelectedUnitRequest;

/**
 * Created by marced on 01/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RehabstodUser rehabUserMock;

    @Mock
    UserService userService;

    @InjectMocks
    private UserController userController = new UserController();

    @Before
    public void before() {
        when(userService.getUser()).thenReturn(rehabUserMock);
        when(rehabUserMock.getValdVardenhet()).thenReturn(new Vardenhet("123", "enhet"));
    }

    @Test
    public void testCreateGet() {

        userController.getUser();

        verify(userService).getUser();
    }

    @Test
    public void testChangeEnhetSuccess() {
        ChangeSelectedUnitRequest req = new ChangeSelectedUnitRequest("123");
        when(rehabUserMock.changeValdVardenhet(eq(req.getId()))).thenReturn(true);

        userController.changeSelectedUnitOnUser(req);

        verify(userService).getUser();
        verify(rehabUserMock).changeValdVardenhet(eq(req.getId()));

    }

    @Test
    public void testChangeEnhetFails() {
        ChangeSelectedUnitRequest req = new ChangeSelectedUnitRequest("123");
        when(rehabUserMock.changeValdVardenhet(eq(req.getId()))).thenReturn(false);

        thrown.expect(AuthoritiesException.class);

        userController.changeSelectedUnitOnUser(req);

        verify(userService).getUser();
        verify(rehabUserMock).changeValdVardenhet(eq(req.getId()));

    }
}
