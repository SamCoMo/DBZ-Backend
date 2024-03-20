package com.samcomo.dbz.utils;

import static com.samcomo.dbz.member.model.constants.MemberRole.MEMBER;

import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.utils.annotation.WithMockMember;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockMember> {

  @Override
  public SecurityContext createSecurityContext(WithMockMember annotation) {

    SecurityContext context = SecurityContextHolder.createEmptyContext();

    MemberDetails details = new MemberDetails(Member.builder().id(1L).role(MEMBER).build());

    Authentication auth = new UsernamePasswordAuthenticationToken(
        details, null, details.getAuthorities());

    context.setAuthentication(auth);
    return context;
  }
}
