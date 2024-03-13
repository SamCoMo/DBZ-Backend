package com.samcomo.dbz.global.utils;

import com.samcomo.dbz.global.utils.annotation.EmailCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailCheck, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null || value.trim().isEmpty()) {

      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("이메일은(는) 필수 항목입니다.")
                .addConstraintViolation();
      return false;
    }

    return value.matches("^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])"
                                    + "*@[0-9a-zA-z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$");
  }
}
