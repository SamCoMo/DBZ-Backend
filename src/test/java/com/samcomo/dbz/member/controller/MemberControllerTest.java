package com.samcomo.dbz.member.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
  private String rawEmail;
  private String rawNickname;
  private String rawPhone;

  @BeforeEach
  void init() {

    rawEmail = "samcomo@gmail.com";
    rawPhone = "010-1234-5678";
    rawNickname = "삼코모";

    request = RegisterRequestDto.builder()
        .email(rawEmail)
        .nickname(rawNickname)
        .phone(rawPhone)
        .password("123")
        .build();
  }

  @Test
  @WithMockUser(username = "테스트 관리자", roles = {"SUPER"})
  @DisplayName(value = "[성공] 회원가입")
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
}