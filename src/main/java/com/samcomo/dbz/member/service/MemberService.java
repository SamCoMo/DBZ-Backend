package com.samcomo.dbz.member.service;

import com.samcomo.dbz.member.model.dto.RegisterDto;
import com.samcomo.dbz.member.model.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  RegisterDto.Response register(RegisterDto.Request request);

  Member getMemberIdByAuthentication(Authentication authentication);

  void validateDuplicateMember(String email, String nickname);
}
