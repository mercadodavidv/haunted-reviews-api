package com.mercadodavidv.hauntedreviews.user.validation;

import com.mercadodavidv.hauntedreviews.config.DelegatingPasswordEncoderConfigProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

public class EncodedPasswordValidator implements ConstraintValidator<EncodedPassword, String> {

  Logger logger = Logger.getLogger(getClass().getName());

  private final DelegatingPasswordEncoderConfigProperties delegatingPasswordEncoderConfigProperties;

  public EncodedPasswordValidator(
      DelegatingPasswordEncoderConfigProperties delegatingPasswordEncoderConfigProperties) {
    this.delegatingPasswordEncoderConfigProperties = delegatingPasswordEncoderConfigProperties;
  }

  @Override
  public boolean isValid(String object, ConstraintValidatorContext context) {

    if (object == null) {
      return true;
    }

    //noinspection RegExpSimplifiable - The character class intersection is intentional :)
    if (!StringUtils.startsWithAny(object, //
        delegatingPasswordEncoderConfigProperties.getAcceptedIds().stream() //
            .map(id -> "{" + id + "}").toArray(String[]::new)) //
        || object.matches("^\\{[\\w&&[^}]]*}$") //
    ) {
      logger.warning(
          "ERROR - TRIED TO SAVE A PASSWORD WITHOUT ENCODING OR WITH AN INVALID ENCODING ID!");
      return false;
    }

    return true;
  }
}
