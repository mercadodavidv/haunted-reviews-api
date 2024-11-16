package com.mercadodavidv.hauntedreviews.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mercadodavidv.hauntedreviews.user.Role;
import java.util.Set;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UserIdentityTest {

  @Test
  void testSerialization() {

    UserIdentity userIdentity = new UserIdentity(1L, null, null, "email@example.com", "", null, null, "",
        Set.of(Role.BASIC_USER));
    UserIdentity deserializedObject = SerializationUtils.deserialize(
        SerializationUtils.serialize(userIdentity));

    assertEquals(userIdentity, deserializedObject);
  }

  @Test
  void testRolesToAuthorities() {

    UserIdentity userIdentity = new UserIdentity(1L, null, null, "email@example.com", "", null, null, "",
        roleSetBasicUserAndStaff());

    assertThat(userIdentity.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsOnly(
        roleSetBasicUserAndStaff().stream().map(this::appendRoleToAuthority).toArray(String[]::new));
  }

  private Set<Role> roleSetBasicUserAndStaff() {
    return Set.of(Role.BASIC_USER, Role.STAFF);
  }

  private String appendRoleToAuthority(Role role) {
    return "ROLE_" + role.name();
  }
}