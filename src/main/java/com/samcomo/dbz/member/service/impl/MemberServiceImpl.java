package com.samcomo.dbz.member.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.samcomo.dbz.global.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.dto.MemberMyInfo;
import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
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
  public void register(RegisterRequestDto request) {

    validateDuplicateMember(request.getEmail(), request.getNickname());

    Member member = Member.from(request);
    member.encodePassword(passwordEncoder, request.getPassword());

    memberRepository.save(member);
  }

  @Override
  public MemberMyInfo getMyInfo(Long memberId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    return MemberMyInfo.from(member);
  }

  @Override
  public void validateDuplicateMember(String email, String nickname) {

    if (memberRepository.findByEmail(email).isPresent()) {
      throw new MemberException(EMAIL_ALREADY_EXISTS);
    }
    if (memberRepository.findByNickname(nickname).isPresent()) {
      throw new MemberException(NICKNAME_ALREADY_EXISTS);
    }
  }
}
