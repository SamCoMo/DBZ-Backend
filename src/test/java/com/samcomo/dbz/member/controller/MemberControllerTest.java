package com.samcomo.dbz.member.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
import com.samcomo.dbz.member.service.impl.MemberServiceImpl;
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
  private MemberServiceImpl memberService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  private RegisterRequestDto request;
  private String validEmail;
  private String validNickname;
  private String validPhone;
  private String validPassword;

  @BeforeEach
  void init() {

    validEmail = "samcomo@gmail.com";
    validPhone = "010-1234-5678";
    validNickname = "삼코모";
    validPassword = "abcd1234!";

    request = RegisterRequestDto.builder()
        .email(validEmail)
        .nickname(validNickname)
        .phone(validPhone)
        .password(validPassword)
        .build();
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
        .andExpect(status().isCreated())
        .andDo(print());
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
        .andExpect(jsonPath("$.email").value("올바르지 않은 이메일 형식 입니다."))
        .andExpect(jsonPath("$.nickname").doesNotExist())
        .andExpect(jsonPath("$.phone").doesNotExist())
        .andExpect(jsonPath("$.password").doesNotExist())
        .andDo(print());
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
        .andExpect(jsonPath("$.password").doesNotExist())
        .andDo(print());
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
        .andExpect(jsonPath("$.phone").value("올바르지 않은 전화번호 형식 입니다."))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andDo(print());
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
        .andExpect(jsonPath("$.password").value("영문자+특수문자+숫자를 포함하여 8자 이상 입력해주세요."))
        .andDo(print());
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
        .andExpect(jsonPath("$.email").value("올바르지 않은 이메일 형식 입니다."))
        .andExpect(jsonPath("$.nickname").value("특수문자를 제외한 2~10자 사이로 입력해주세요."))
        .andExpect(jsonPath("$.phone").value("올바르지 않은 전화번호 형식 입니다."))
        .andExpect(jsonPath("$.password").value("영문자+특수문자+숫자를 포함하여 8자 이상 입력해주세요."))
        .andDo(print());
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
        .andExpect(jsonPath("$.email").value("이메일은 필수 항목 입니다."))
        .andExpect(jsonPath("$.nickname").value("닉네임은 필수 항목 입니다."))
        .andExpect(jsonPath("$.phone").value("전화번호는 필수 항목 입니다."))
        .andExpect(jsonPath("$.password").value("비밀번호는 필수 항목 입니다."))
        .andDo(print());
  }
}