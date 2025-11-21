package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.factories.UserDetailsFactory;
import com.devsuperior.dscommerce.factories.UserFactory;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    @Spy
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserUtil customUserUtil;

    private User userRoleClient;

    private List<UserDetailsProjection> roleClientUserDetails;

    private String existingEmail,
                nonExistingEmail;

    @BeforeEach
    void setup() {
        existingEmail = "ex@ds.com";
        nonExistingEmail = "non-ex@ds.com";

        userRoleClient = UserFactory.createRoleClientUser(existingEmail);

        roleClientUserDetails = UserDetailsFactory.createRoleClientUserDetails(existingEmail);

        Mockito.when(userRepository.searchUserAndRolesByEmail(existingEmail)).thenReturn(roleClientUserDetails);
        Mockito.when(userRepository.searchUserAndRolesByEmail(nonExistingEmail)).thenReturn(List.of());

        Mockito.when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(userRoleClient));
        Mockito.when(userRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        UserDetails result = userService.loadUserByUsername(existingEmail);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingEmail);
    }

    @Test
    void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        Throwable exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(nonExistingEmail);
        });

        Assertions.assertEquals("Email not found", exception.getMessage());
    }

    @Test
    void authenticatedShouldReturnUserExists() {
        Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(existingEmail);

        User user = userService.authenticated();

       Assertions.assertNotNull(user);
       Assertions.assertEquals(user.getUsername(), existingEmail);
    }

    @Test
    void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        Mockito.when(customUserUtil.getLoggedUsername()).thenThrow(ClassCastException.class);

        Throwable exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.authenticated();
        });

        Assertions.assertEquals("Email not found", exception.getMessage());
    }

    @Test
    void getMeShouldReturnUserDTOWhenUserAuthenticated() {
        Mockito.doReturn(userRoleClient).when(userService).authenticated();

        UserDTO userDTO = userService.getMe();

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(userDTO.getEmail(), existingEmail);

    }

    @Test
    void getMeShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        Throwable exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.getMe();
        });
    }
}
