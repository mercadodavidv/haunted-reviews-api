package com.mercadodavidv.hauntedreviews.auth;

import com.mercadodavidv.hauntedreviews.user.validation.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@AllArgsConstructor
class UserRegistrationForm {

  @NotBlank(message = "{validator.email.notblank.message}")
  @Email(message = "{validator.email.pattern.message}")
  private final String email;

  @Username
  private final String username;

  // TODO Password requirements
  private final String password;

}
