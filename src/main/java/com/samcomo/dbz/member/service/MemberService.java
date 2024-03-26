package com.samcomo.dbz.member.service;

import com.samcomo.dbz.member.model.dto.LocationRequest;
import com.samcomo.dbz.member.model.dto.MyPageResponse;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
import com.samcomo.dbz.member.model.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface MemberService {

  void register(RegisterRequest request);

  void validateDuplicateMember(String email, String nickname);

  MyPageResponse getMyInfo(long memberId);

  void updateLocation(long memberId, LocationRequest request);

  Member getMember(long memberId);

  void updateProfileImage(long memberId, MultipartFile profileImage);
}
