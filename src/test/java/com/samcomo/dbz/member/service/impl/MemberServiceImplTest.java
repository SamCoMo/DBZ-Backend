package com.samcomo.dbz.member.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.samcomo.dbz.global.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;
import static com.samcomo.dbz.member.model.constants.MemberRole.MEMBER;
import static com.samcomo.dbz.member.model.constants.MemberStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.dto.RegisterDto;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

  @InjectMocks
  private MemberServiceImpl memberService;

  @Mock
  private MemberRepository memberRepository;

  @Spy
  private PasswordEncoder passwordEncoder;

  private RegisterDto.Request request;
  private Member savedMember;
  private String rawEmail;
  private String rawNickname;
  private String rawPhone;
  private String rawPassword;

  @BeforeEach
  void init() {

    passwordEncoder = new BCryptPasswordEncoder();

    rawEmail = "samcomo@gmail.com";
    rawPhone = "010-1234-5678";
    rawNickname = "삼코모";
    rawPassword = "123";

    request = RegisterDto.Request.builder()
        .email(rawEmail)
        .nickname(rawNickname)
        .phone(rawPhone)
        .password(rawPassword)
        .build();

    savedMember = Member.builder()
        .id(1L)
        .email(rawEmail)
        .nickname(rawNickname)
        .phone(rawPhone)
        .password(passwordEncoder.encode(rawPassword))
        .role(MEMBER)
        .status(ACTIVE)
        .build();
  }

  @Test
  @DisplayName(value = "[성공] 회원가입")
  void successRegister() {
    // given
    given(memberRepository.save(any()))
        .willReturn(savedMember);

    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

    // when
    RegisterDto.Response response = memberService.register(request);

    // then
    verify(memberRepository, times(1)).save(captor.capture());

    assertEquals(1L, response.getMemberId());
    assertEquals(rawEmail, response.getMemberInfo().getEmail());
    assertEquals(rawNickname, response.getMemberInfo().getNickname());
    assertEquals(rawPhone, response.getMemberInfo().getPhone());

    assertEquals(ACTIVE, response.getStatus());
    assertEquals(MEMBER, response.getRole());

    assertTrue(passwordEncoder.matches(rawPassword, savedMember.getPassword()));
  }

  @Test
  @DisplayName(value = "[실패] 회원가입-유효성검사 : 이메일 중복")
  void failValidateDuplicateMember_EmailException() {
    // given
    given(memberRepository.findByEmail(any()))
        .willReturn(Optional.of(savedMember));

    // when
    MemberException memberException = assertThrows(MemberException.class,
        () -> memberService.register(request));

    // then
    assertEquals(EMAIL_ALREADY_EXISTS, memberException.getErrorCode());
  }

  @Test
  @DisplayName(value = "[실패] 회원가입-유효성검사 : 닉네임 중복")
  void failValidateDuplicateMember_NicknameException() {
    // given
    given(memberRepository.findByNickname(any()))
        .willReturn(Optional.of(savedMember));

    // when
    MemberException memberException = assertThrows(MemberException.class,
        () -> memberService.register(request));

    // then
    assertEquals(NICKNAME_ALREADY_EXISTS, memberException.getErrorCode());
  }
}