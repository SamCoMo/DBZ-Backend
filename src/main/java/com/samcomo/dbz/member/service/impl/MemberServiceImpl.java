package com.samcomo.dbz.member.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.samcomo.dbz.global.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.dto.LocationUpdateRequest;
import com.samcomo.dbz.member.model.dto.MyPageResponse;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void register(RegisterRequest request) {
    validateDuplicateMember(request.getEmail(), request.getNickname());

    Member member = Member.from(request);
    member.encodePassword(passwordEncoder, request.getPassword());

    memberRepository.save(member);
  }

  @Override
  public MyPageResponse getMyInfo(long memberId) {
    return MyPageResponse.from(getMember(memberId));
  }

  @Override
  public void updateLocation(long memberId, LocationUpdateRequest request) {
    Member member = getMember(memberId);
    member.setAddress(request.getAddress());
    member.setLatitude(request.getLatitude());
    member.setLongitude(request.getLongitude());
    memberRepository.save(member);
  }

  @Override
  public Member getMember(long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  @Override
  public void validateDuplicateMember(String email, String nickname) {
    if (memberRepository.existsByEmail(email)) {
      throw new MemberException(EMAIL_ALREADY_EXISTS);
    }
    if (memberRepository.existsByNickname(nickname)) {
      throw new MemberException(NICKNAME_ALREADY_EXISTS);
    }
  }
}
