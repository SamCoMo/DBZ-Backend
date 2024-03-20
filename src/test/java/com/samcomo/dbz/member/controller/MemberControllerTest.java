package com.samcomo.dbz.member.controller;

import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.filter.RefreshTokenFilter;
import com.samcomo.dbz.member.model.dto.MemberMyInfo;
import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.service.MemberService;
import com.samcomo.dbz.utils.annotation.WithMockMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {

  @MockBean
  private MemberService memberService;
  @MockBean
  private RefreshTokenFilter refreshTokenFilter;

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;

  private RegisterRequestDto request;
  private String validEmail;
  private String validNickname;
  private String validPhone;
  private String validPassword;
  private String validProfileImageUrl;
  private MemberMyInfo myInfo;
  private Member member;
  private static final String REQUIRED_FIELD_MESSAGE = "은(는) 필수 항목입니다.";

  @BeforeEach
  void init() {

    validEmail = "samcomo@gmail.com";
    validPhone = "010-1234-5678";
    validNickname = "삼코모";
    validPassword = "abcd1234!";
    validProfileImageUrl = "image.jpg";

    member = Member.builder()
        .id(1L)
        .email(validEmail)
        .nickname(validNickname)
        .profileImageUrl(validProfileImageUrl)
        .phone(validPhone)
        .build();

    request = RegisterRequestDto.builder()
        .email(validEmail)
        .nickname(validNickname)
        .phone(validPhone)
        .password(validPassword)
        .build();

    myInfo = MemberMyInfo.from(member);
  }

  @Test
  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
  @DisplayName(value = "회원가입[성공]")
  void successRegister() throws Exception {

    mockMvc.perform(
            post("/member/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
  @DisplayName(value = "회원가입[실패] - 유효성검증(이메일)")
  void failRegisterEmail() throws Exception {

    RegisterRequestDto request = RegisterRequestDto.builder()
        .email("x")
        .nickname(validNickname)
        .phone(validPhone)
        .password(validPassword)
        .build();

    mockMvc.perform(
            post("/member/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("올바르지 않은 이메일 형식입니다."))
        .andExpect(jsonPath("$.nickname").doesNotExist())
        .andExpect(jsonPath("$.phone").doesNotExist())
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
  @DisplayName(value = "회원가입[실패] - 유효성검증(닉네임)")
  void failRegisterNickname() throws Exception {

    RegisterRequestDto request = RegisterRequestDto.builder()
        .email(validEmail)
        .nickname("x")
        .phone(validPhone)
        .password(validPassword)
        .build();

    mockMvc.perform(
            post("/member/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").doesNotExist())
        .andExpect(jsonPath("$.nickname").value("특수문자를 제외한 2~10자 사이로 입력해주세요."))
        .andExpect(jsonPath("$.phone").doesNotExist())
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
  @DisplayName(value = "회원가입[실패] - 유효성검증(전화번호)")
  void failRegisterPhone() throws Exception {

    RegisterRequestDto request = RegisterRequestDto.builder()
        .email(validEmail)
        .nickname(validNickname)
        .phone("x")
        .password(validPassword)
        .build();

    mockMvc.perform(
            post("/member/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").doesNotExist())
        .andExpect(jsonPath("$.nickname").doesNotExist())
        .andExpect(jsonPath("$.phone").value("올바르지 않은 전화번호 형식입니다."))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
  @DisplayName(value = "회원가입[실패] - 유효성검증(비밀번호)")
  void failRegisterPassword() throws Exception {

    RegisterRequestDto request = RegisterRequestDto.builder()
        .email(validEmail)
        .nickname(validNickname)
        .phone(validPhone)
        .password("x")
        .build();

    mockMvc.perform(
            post("/member/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").doesNotExist())
        .andExpect(jsonPath("$.nickname").doesNotExist())
        .andExpect(jsonPath("$.phone").doesNotExist())
        .andExpect(jsonPath("$.password").value("영문자+특수문자+숫자를 포함하여 8자 이상 입력해주세요."));
  }

  @Test
  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
  @DisplayName(value = "회원가입[실패] - 유효성검증(all)")
  void failRegisterAll() throws Exception {

    RegisterRequestDto request = RegisterRequestDto.builder()
        .email("x")
        .nickname("x")
        .phone("x")
        .password("x")
        .build();

    mockMvc.perform(
            post("/member/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("올바르지 않은 이메일 형식입니다."))
        .andExpect(jsonPath("$.nickname").value("특수문자를 제외한 2~10자 사이로 입력해주세요."))
        .andExpect(jsonPath("$.phone").value("올바르지 않은 전화번호 형식입니다."))
        .andExpect(jsonPath("$.password").value("영문자+특수문자+숫자를 포함하여 8자 이상 입력해주세요."));
  }

  @Test
  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
  @DisplayName(value = "회원가입[실패] - null체크(all)")
  void failRegisterNull() throws Exception {

    RegisterRequestDto request = RegisterRequestDto.builder()
        .build();

    mockMvc.perform(
            post("/member/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("이메일" + REQUIRED_FIELD_MESSAGE))
        .andExpect(jsonPath("$.nickname").value("닉네임" + REQUIRED_FIELD_MESSAGE))
        .andExpect(jsonPath("$.phone").value("전화번호" + REQUIRED_FIELD_MESSAGE))
        .andExpect(jsonPath("$.password").value("비밀번호" + REQUIRED_FIELD_MESSAGE));
  }

  @Test
  @WithMockMember
  @DisplayName(value = "마이페이지[성공]")
  void successGetMyPage() throws Exception {

    given(memberService.getMyInfo(anyLong()))
        .willReturn(myInfo);

    mockMvc.perform(
            get("/member/my")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(validEmail))
        .andExpect(jsonPath("$.nickname").value(validNickname))
        .andExpect(jsonPath("$.profileImageUrl").value(validProfileImageUrl))
        .andExpect(jsonPath("$.phone").value(validPhone));
  }

  @Test
//  @WithMockMember
  @DisplayName(value = "마이페이지[실패] - 인가 실패")
  void failGetMyPage1() throws Exception {

    mockMvc.perform(
            get("/member/my")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockMember
  @DisplayName(value = "마이페이지[실패] - DB 조회 실패")
  void failGetMyPage2() throws Exception {

    given(memberService.getMyInfo(member.getId()))
        .willThrow(new MemberException(MEMBER_NOT_FOUND));

    mockMvc.perform(
            get("/member/my")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}