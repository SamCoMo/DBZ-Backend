package com.samcomo.dbz.member.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_SESSION;
import static com.samcomo.dbz.global.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.dto.RegisterDto;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public RegisterDto.Response register(RegisterDto.Request request) {

    validateDuplicateMember(request.getEmail(), request.getNickname());

    Member member = RegisterDto.Request.toEntity(request);
    member.encodePassword(passwordEncoder, request.getPassword());

    member = memberRepository.save(member);

    return RegisterDto.Response.fromEntity(member);
  }

  @Override
  public Long getMemberIdByAuthentication(Authentication authentication) {

    Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(() ->
        new MemberException(INVALID_SESSION));

    return member.getId();
  }

  public void validateDuplicateMember(String email, String nickname) {

    if (memberRepository.findByEmail(email).isPresent()) {
      throw new MemberException(EMAIL_ALREADY_EXISTS);
    }
    if (memberRepository.findByNickname(nickname).isPresent()) {
      throw new MemberException(NICKNAME_ALREADY_EXISTS);
    }
  }
}
