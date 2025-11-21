package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.factories.UserFactory;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    private User admin,
            selfClient,
            otherClient;

    @BeforeEach
    void setup() {
        admin = UserFactory.createRoleAdminUser("admin@ds.com");
        selfClient = UserFactory.createRoleClientUser("client@ds.com");

        otherClient = UserFactory.createRoleClientUser("other@ds.com");
        otherClient.setId(3L);

    }

    @Test
    void validateSelfOrAdminShouldDoNothingWhenAdminLogged() {
        Mockito.doReturn(admin).when(userService).authenticated();

        Assertions.assertDoesNotThrow(() -> authService.validateSelfOrAdmin(admin.getId()));
    }

    @Test
    void validateSelfOrAdminShouldDoNothingWhenSelfLogged() {
        Mockito.doReturn(selfClient).when(userService).authenticated();

        Assertions.assertDoesNotThrow(() -> authService.validateSelfOrAdmin(selfClient.getId()));
    }

    @Test
    void validateSelfOrAdminShouldThrowForbiddenExceptionWhenOtherClientLogged() {
        Mockito.doReturn(selfClient).when(userService).authenticated();

        Assertions.assertThrows(ForbiddenException.class, () -> authService.validateSelfOrAdmin(otherClient.getId()));
    }
}
