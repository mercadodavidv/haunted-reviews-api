package com.mercadodavidv.hauntedreviews.config;

import com.mercadodavidv.hauntedreviews.user.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
@SuppressWarnings("java:S1118") // This is not a "utility class".
class RoleHierarchyConfig {

  @Bean
  static RoleHierarchy roleHierarchy() {
    // @formatter:off
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role(Role.OWNER.name()).implies(Role.ADMIN.name(), Role.STAFF.name())
        .role(Role.ADMIN.name()).implies(Role.BASIC_USER.name())
        .role(Role.STAFF.name()).implies(Role.BASIC_USER.name())
        .role(Role.BASIC_USER.name()).implies("ANONYMOUS")
        .build();
    // @formatter:on
  }

  // Support method security
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setRoleHierarchy(roleHierarchy);
    return expressionHandler;
  }
}
