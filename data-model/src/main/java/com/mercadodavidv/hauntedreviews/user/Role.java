package com.mercadodavidv.hauntedreviews.user;

import lombok.Getter;

@Getter
public enum Role {

  BASIC_USER,

  // Manage place approvals and edits, users. Moderate all content such as images and review content.
  STAFF,

  // Manage users of lower roles.
  ADMIN,

  // Full control, typically a code owner.
  OWNER;

}
