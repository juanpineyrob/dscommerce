package com.devsuperior.dscommerce.factories;

import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;

import java.time.LocalDate;

public class UserFactory {
    public static User createRoleClientUser(String username) {
        LocalDate birthDate = LocalDate.of(2003, 6, 27);

        User user = new User(1L, "Juan", username, "098515556", "ju@n123", birthDate);
        user.addRole(new Role(1L, "ROLE_CLIENT"));

        return user;
    }

    public static User createRoleAdminUser(String username) {
        LocalDate birthDate = LocalDate.of(2003, 6, 27);

        User user = new User(1L, "Juan", username, "098515556", "ju@n123", birthDate);
        user.addRole(new Role(1L, "ROLE_ADMIN"));

        return user;
    }
}