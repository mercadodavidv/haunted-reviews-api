package com.mercadodavidv.hauntedreviews.user.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Size(min = 4, max = 20, message = "{validator.username.size.message}")
@Pattern(regexp = "^[A-Za-z0-9]\\w*$", message = "{validator.username.alphanumeric.message}")
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface Username {

  String message() default "{validator.username.general.message}";

  Class<?>[] groups() default {};

  Class<?>[] payload() default {};

}
