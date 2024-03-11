package com.samcomo.dbz.member.utils.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.samcomo.dbz.member.utils.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface EmailCheck {

  String message() default "올바르지 않은 이메일 형식입니다.";

  Class[] groups() default {};

  Class<Payload>[] payload() default {};
}
