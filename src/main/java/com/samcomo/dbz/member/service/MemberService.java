package com.samcomo.dbz.member.service;

import com.samcomo.dbz.member.model.dto.LocationUpdateRequest;
import com.samcomo.dbz.member.model.dto.MyPageResponse;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
import com.samcomo.dbz.member.model.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  void register(RegisterRequest request);

  void validateDuplicateMember(String email, String nickname);

  MyPageResponse getMyInfo(long memberId);

  void updateLocation(long memberId, LocationUpdateRequest request);

  Member getMember(long memberId);
}
