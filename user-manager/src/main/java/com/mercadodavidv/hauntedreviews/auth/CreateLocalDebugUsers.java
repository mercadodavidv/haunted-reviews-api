package com.mercadodavidv.hauntedreviews.auth;

import com.mercadodavidv.hauntedreviews.user.Role;
import java.util.Set;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initializes test users when running locally and using an in-memory database.
 */
@Profile("(local & h2) & !test")
@Component
@DependsOnDatabaseInitialization
public class CreateLocalDebugUsers {

  CreateLocalDebugUsers(UserIdentityService userIdentityService) {
    userIdentityService.saveUser(
        new UserIdentityCreateInput("owner@example.com", "Owner", "{noop}owner", "",
            Set.of(Role.OWNER), true));
    userIdentityService.saveUser(
        new UserIdentityCreateInput("admin@example.com", "Admin", "{noop}admin", "",
            Set.of(Role.ADMIN), true));
    userIdentityService.saveUser(
        new UserIdentityCreateInput("staff@example.com", "Staff", "{noop}staff", "",
            Set.of(Role.STAFF), true));
    userIdentityService.saveUser(
        new UserIdentityCreateInput("user@example.com", "User", "{noop}user", "",
            Set.of(Role.BASIC_USER), false));
  }
}
