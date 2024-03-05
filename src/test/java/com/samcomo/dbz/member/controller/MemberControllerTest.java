package com.samcomo.dbz.member.controller;

import static com.samcomo.dbz.member.model.constants.MemberRole.MEMBER;
import static com.samcomo.dbz.member.model.constants.MemberStatus.ACTIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcomo.dbz.member.model.dto.RegisterDto;
import com.samcomo.dbz.member.model.dto.RegisterDto.Response.MemberInfo;
import com.samcomo.dbz.member.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

  @MockBean
  private MemberServiceImpl memberService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  private RegisterDto.Request request;
  private RegisterDto.Response response;
  private String rawEmail;
  private String rawNickname;
  private String rawPhone;

  @BeforeEach
  void init() {

    rawEmail = "samcomo@gmail.com";
    rawPhone = "010-1234-5678";
    rawNickname = "삼코모";

    request = RegisterDto.Request.builder()
        .email(rawEmail)
        .nickname(rawNickname)
        .phone(rawPhone)
        .build();

    response = RegisterDto.Response.builder()
        .memberInfo(
            MemberInfo.builder()
                .email("samcomo@gmail.com")
                .nickname("삼코모")
                .phone("010-1234-5678")
                .build())
        .status(ACTIVE)
        .role(MEMBER)
        .build();
  }

//  @Test
//  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
//  @DisplayName(value = "[성공] 회원가입")
//  void successRegister() throws Exception {
//    // given
//    given(memberService.register(any(RegisterDto.Request.class)))
//        .willReturn(response);
//
//    // when
//    // then
//    mockMvc.perform(
//            post("/member/register")
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(
//                    objectMapper.writeValueAsString(request)))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("memberInfo.email").value(rawEmail))
//        .andExpect(jsonPath("memberInfo.nickname").value(rawNickname))
//        .andExpect(jsonPath("memberInfo.phone").value(rawPhone))
//        .andExpect(jsonPath("status").value("ACTIVE"))
//        .andExpect(jsonPath("role").value("MEMBER"))
//        .andDo(print());
//  }
}