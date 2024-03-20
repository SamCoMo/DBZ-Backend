package com.samcomo.dbz.member.service;

import com.samcomo.dbz.member.model.dto.MemberMyInfo;
import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  void register(RegisterRequestDto request);

  void validateDuplicateMember(String email, String nickname);

  MemberMyInfo getMyInfo(Long memberId);
}
