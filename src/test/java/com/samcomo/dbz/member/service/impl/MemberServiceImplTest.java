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
import com.samcomo.dbz.member.model.dto.LocationInfo;
import com.samcomo.dbz.member.model.dto.MyInfo;
import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
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

  private RegisterRequestDto request;
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

    request = RegisterRequestDto.builder()
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
    given(memberRepository.findByEmail(any())).willReturn(Optional.empty());
    given(memberRepository.findByNickname(any())).willReturn(Optional.empty());

    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

    // when
    memberService.register(request);

    // then
    verify(memberRepository, times(1)).findByEmail(request.getEmail());
    verify(memberRepository, times(1)).findByNickname(request.getNickname());
    verify(memberRepository, times(1)).save(captor.capture());

    assertTrue(passwordEncoder.matches(rawPassword, savedMember.getPassword()));
  }

  @Test
  @DisplayName(value = "회원가입[실패] : 이메일 중복")
  void failValidateDuplicateMember_EmailException() {
    // given
    given(memberRepository.findByEmail(request.getEmail())).willReturn(Optional.of(savedMember));

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
    given(memberRepository.findByNickname(request.getNickname())).willReturn(Optional.of(savedMember));

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
    given(memberRepository.findById(savedMember.getId())).willReturn(Optional.of(savedMember));

    // when
    MyInfo myInfo = memberService.getMyInfo(savedMember.getId());

    // then
    assertEquals(myInfo.getEmail(), savedMember.getEmail());
    assertEquals(myInfo.getNickname(), savedMember.getNickname());
    assertEquals(myInfo.getPhone(), savedMember.getPhone());
    assertEquals(myInfo.getProfileImageUrl(), savedMember.getProfileImageUrl());
  }

  @Test
  @DisplayName("마이페이지[실패] - DB 조회 실패")
  void failGetMyInfo() {
    // given
    given(memberRepository.findById(savedMember.getId())).willReturn(Optional.empty());

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
    LocationInfo.Request locationUpdateRequest = LocationInfo.Request.builder()
        .address("새로운 주소지")
        .longitude(37.12345)
        .latitude(127.12345)
        .build();
    given(memberRepository.findById(savedMember.getId())).willReturn(Optional.of(savedMember));

    Member updatedMember = savedMember;
    updatedMember.setAddress(locationUpdateRequest.getAddress());
    updatedMember.setLatitude(locationUpdateRequest.getLatitude());
    updatedMember.setLongitude(locationUpdateRequest.getLongitude());
    given(memberRepository.save(updatedMember)).willReturn(updatedMember);

    // when
    LocationInfo.Response locationInfo =
        memberService.updateLocation(savedMember.getId(), locationUpdateRequest);

    // then
    assertEquals(savedMember.getId(), locationInfo.getMemberId());
    assertEquals(locationUpdateRequest.getAddress(), locationInfo.getAddress());
    assertEquals(locationUpdateRequest.getLatitude(), locationInfo.getLatitude());
    assertEquals(locationUpdateRequest.getLongitude(), locationInfo.getLongitude());
  }

  @Test
  @DisplayName("위치업데이트[실패] - DB 조회 실패")
  void failUpdateLocation() {
    // given
    LocationInfo.Request locationUpdateRequest = LocationInfo.Request.builder()
        .address("새로운 주소")
        .longitude(37.12345)
        .latitude(127.12345)
        .build();
    given(memberRepository.findById(savedMember.getId())).willReturn(Optional.empty());

    // when
    MemberException e = assertThrows(MemberException.class,
        () -> memberService.updateLocation(savedMember.getId(), locationUpdateRequest));

    // then
    assertEquals(MEMBER_NOT_FOUND, e.getErrorCode());
  }
}