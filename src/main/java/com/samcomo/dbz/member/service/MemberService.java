package com.samcomo.dbz.member.service;

import com.samcomo.dbz.member.model.dto.RegisterDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  RegisterDto.Response register(RegisterDto.Request request);

  void validateDuplicateMember(String email, String nickname);

  Long getMemberIdByAuthentication(Authentication authentication);
}
