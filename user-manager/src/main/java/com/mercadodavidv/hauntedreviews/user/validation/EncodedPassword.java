package com.mercadodavidv.hauntedreviews.user.validation;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EncodedPasswordValidator.class)
public @interface EncodedPassword {

  String message() default "{validator.password.internal-error.message}";

  Class<?>[] groups() default {};

  Class<?>[] payload() default {};

}
