package com.mercadodavidv.hauntedreviews.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Size(min = 12, message = "{validator.password.size.min.message}")
@Size(max = 100, message = "{validator.password.size.max.message}")
@Pattern(regexp = "[a-z]+", message = "{validator.password.required-characters.lowercase.message}")
@Pattern(regexp = "[A-Z]+", message = "{validator.password.required-characters.capital.message}")
@Pattern(regexp = "\\d+", message = "{validator.password.required-characters.number.message}")
// ~`!@#$%^&*()-_+={}[]|\;:"<>,./?
@Pattern(regexp = "[~`!@#$%^&*()\\-_+={}\\[\\]|\\\\;:\"<>,./?]+", message = "{validator.password.required-characters.special.message}")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface UserPassword {

  String message() default "{validator.password.general.message}";

  Class<?>[] groups() default {};

  Class<?>[] payload() default {};

}
