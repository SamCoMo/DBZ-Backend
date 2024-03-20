package com.samcomo.dbz.member.service;

import com.samcomo.dbz.member.model.dto.LocationInfo;
import com.samcomo.dbz.member.model.dto.MyInfo;
import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
import com.samcomo.dbz.member.model.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  void register(RegisterRequestDto request);

  void validateDuplicateMember(String email, String nickname);

  MyInfo getMyInfo(long memberId);

  LocationInfo.Response updateLocation(long memberId, LocationInfo.Request request);

  Member getMember(long memberId);
}
