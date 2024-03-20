package com.samcomo.dbz.utils.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.samcomo.dbz.utils.WithMemberSecurityContextFactory;
import java.lang.annotation.Retention;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RUNTIME)
@WithSecurityContext(factory = WithMemberSecurityContextFactory.class)
public @interface WithMockMember {

  String value() default "member";

  String[] roles() default { "MEMBER" };
}
