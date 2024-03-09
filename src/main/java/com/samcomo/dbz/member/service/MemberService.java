package com.samcomo.dbz.member.service;

import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
import com.samcomo.dbz.member.model.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  void register(RegisterRequestDto request);

  Member getMemberByAuthentication(Authentication authentication);

  void validateDuplicateMember(String email, String nickname);
}
