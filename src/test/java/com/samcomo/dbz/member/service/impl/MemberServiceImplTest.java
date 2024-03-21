package com.samcomo.dbz.member.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
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
import com.samcomo.dbz.member.model.dto.LocationUpdateRequest;
import com.samcomo.dbz.member.model.dto.MyPageResponse;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
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

  private RegisterRequest request;
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
    rawPassword = "abcd123!";

    request = RegisterRequest.builder()
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
  @DisplayName(value = "회원가입[성공]")
  void successRegister() {
    // given
    given(memberRepository.existsByEmail(any())).willReturn(false);
    given(memberRepository.existsByNickname(any())).willReturn(false);

    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

    // when
    memberService.register(request);

    // then
    verify(memberRepository, times(1)).existsByEmail(request.getEmail());
    verify(memberRepository, times(1)).existsByNickname(request.getNickname());
    verify(memberRepository, times(1)).save(captor.capture());

    assertEquals(request.getEmail(), captor.getValue().getEmail());
    assertEquals(request.getNickname(), captor.getValue().getNickname());
    assertEquals(request.getPhone(), captor.getValue().getPhone());

    assertTrue(passwordEncoder.matches(rawPassword, savedMember.getPassword()));
  }

  @Test
  @DisplayName(value = "회원가입[실패] : 이메일 중복")
  void failValidateDuplicateMember_EmailException() {
    // given
    given(memberRepository.existsByEmail(any())).willReturn(true);

    // when
    MemberException memberException = assertThrows(MemberException.class,
        () -> memberService.register(request));

    // then
    assertEquals(EMAIL_ALREADY_EXISTS, memberException.getErrorCode());
  }

  @Test
  @DisplayName(value = "회원가입[실패] : 닉네임 중복")
  void failValidateDuplicateMember_NicknameException() {
    // given
    given(memberRepository.existsByEmail(any())).willReturn(false);
    given(memberRepository.existsByNickname(any())).willReturn(true);

    // when
    MemberException memberException = assertThrows(MemberException.class,
        () -> memberService.register(request));

    // then
    assertEquals(NICKNAME_ALREADY_EXISTS, memberException.getErrorCode());
  }

  @Test
  @DisplayName("마이페이지[성공]")
  void successGetMyInfo() {
    // given
    given(memberRepository.findById(any())).willReturn(Optional.of(savedMember));

    // when
    MyPageResponse myPageResponse = memberService.getMyInfo(savedMember.getId());

    // then
    assertEquals(savedMember.getEmail(), myPageResponse.getEmail());
    assertEquals(savedMember.getNickname(), myPageResponse.getNickname());
    assertEquals(savedMember.getPhone(), myPageResponse.getPhone());
  }

  @Test
  @DisplayName("마이페이지[실패] - DB 조회 실패")
  void failGetMyInfo() {
    // given
    given(memberRepository.findById(any())).willReturn(Optional.empty());

    // when
    MemberException e = assertThrows(MemberException.class,
        () -> memberService.getMyInfo(savedMember.getId()));

    // then
    assertEquals(MEMBER_NOT_FOUND, e.getErrorCode());
  }

  @Test
  @DisplayName("위치업데이트[성공]")
  void successUpdateLocation() {
    // given
    LocationUpdateRequest updateRequest = LocationUpdateRequest.builder()
        .address("새로운 주소지")
        .longitude(37.12345)
        .latitude(127.12345)
        .build();
    given(memberRepository.findById(any())).willReturn(Optional.of(savedMember));

    Member updatedMember = savedMember;
    updatedMember.setAddress(updateRequest.getAddress());
    updatedMember.setLatitude(updateRequest.getLatitude());
    updatedMember.setLongitude(updateRequest.getLongitude());
    given(memberRepository.save(any())).willReturn(updatedMember);

    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

    // when
    memberService.updateLocation(savedMember.getId(), updateRequest);

    // then
    System.out.println(captor.getAllValues());
  }

  @Test
  @DisplayName("위치업데이트[실패] - DB 조회 실패")
  void failUpdateLocation() {
    // given
    LocationUpdateRequest updateRequest = LocationUpdateRequest.builder()
        .address("새로운 주소")
        .longitude(37.12345)
        .latitude(127.12345)
        .build();
    given(memberRepository.findById(savedMember.getId())).willReturn(Optional.empty());

    // when
    MemberException e = assertThrows(MemberException.class,
        () -> memberService.updateLocation(savedMember.getId(), updateRequest));

    // then
    assertEquals(MEMBER_NOT_FOUND, e.getErrorCode());
  }
}