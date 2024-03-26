package com.samcomo.dbz.member.controller;

import static com.google.api.client.http.HttpMethods.PATCH;
import static com.google.api.client.http.HttpMethods.PUT;
import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.filter.RefreshTokenFilter;
import com.samcomo.dbz.member.model.dto.LocationRequest;
import com.samcomo.dbz.member.model.dto.MyPageResponse;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {

  @MockBean
  private MemberService memberService;
  @MockBean
  private RefreshTokenFilter refreshTokenFilter;
  @MockBean
  private S3Service s3Service;

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;

  private RegisterRequest request;
  private String validEmail;
  private String validNickname;
  private String validPhone;
  private String validPassword;
  private String validProfileImageUrl;
  private MyPageResponse myPageResponse;
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

    request = RegisterRequest.builder()
        .email(validEmail)
        .nickname(validNickname)
        .phone(validPhone)
        .password(validPassword)
        .build();

    myPageResponse = MyPageResponse.from(member);
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

    RegisterRequest request = RegisterRequest.builder()
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

    RegisterRequest request = RegisterRequest.builder()
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

    RegisterRequest request = RegisterRequest.builder()
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

    RegisterRequest request = RegisterRequest.builder()
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

    RegisterRequest request = RegisterRequest.builder()
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

    RegisterRequest request = RegisterRequest.builder()
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
        .willReturn(myPageResponse);

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

  @Test
  @WithMockMember
  @DisplayName(value = "위치업데이트[성공]")
  void successUpdateLocation() throws Exception {
    LocationRequest locationRequest = LocationRequest.builder()
        .address("새로운 주소")
        .latitude(37.12345)
        .longitude(127.12345)
        .build();

    mockMvc.perform(
            patch("/member/location")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(locationRequest)))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockMember
  @DisplayName(value = "위치업데이트[실패] - address null")
  void failUpdateLocation() throws Exception {
    LocationRequest locationRequest = LocationRequest.builder()
        .latitude(37.12345)
        .longitude(127.12345)
        .build();

    mockMvc.perform(
            patch("/member/location")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(locationRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.address").value("주소는 필수 항목입니다."))
        .andExpect(jsonPath("$.latitude").doesNotExist())
        .andExpect(jsonPath("$.longitude").doesNotExist());
  }

  @Test
  @WithMockMember
  @DisplayName(value = "위치업데이트[실패] - address blank/whiteSpace")
  void failUpdateLocation2() throws Exception {
    LocationRequest locationRequest = LocationRequest.builder()
        .address(" ")
        .latitude(37.12345)
        .longitude(127.12345)
        .build();

    mockMvc.perform(
            patch("/member/location")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(locationRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.address").value("주소는 필수 항목입니다."))
        .andExpect(jsonPath("$.latitude").doesNotExist())
        .andExpect(jsonPath("$.longitude").doesNotExist());
  }

  @Test
  @WithMockMember
  @DisplayName(value = "위치업데이트[실패] - latitude null")
  void failUpdateLocation3() throws Exception {
    LocationRequest locationRequest = LocationRequest.builder()
        .address("새로운 주소")
        .longitude(127.12345)
        .build();

    mockMvc.perform(
            patch("/member/location")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(locationRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.address").doesNotExist())
        .andExpect(jsonPath("$.latitude").value("위도는 필수 항목입니다."))
        .andExpect(jsonPath("$.longitude").doesNotExist());
  }

  @Test
  @WithMockMember
  @DisplayName(value = "위치업데이트[실패] - longitude null")
  void failUpdateLocation4() throws Exception {
    LocationRequest locationRequest = LocationRequest.builder()
        .address("새로운 주소")
        .latitude(37.12345)
        .build();

    mockMvc.perform(
            patch("/member/location")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(locationRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.address").doesNotExist())
        .andExpect(jsonPath("$.latitude").doesNotExist())
        .andExpect(jsonPath("$.longitude").value("경도는 필수 항목입니다."));
  }

  @Test
  @WithMockMember
  @DisplayName("프로필이미지업데이트[성공]")
  void successUpdateProfileImage() throws Exception {
    // given
    MockMultipartFile profileImage = new MockMultipartFile(
        "profileImage", "image.png", IMAGE_PNG_VALUE, "image".getBytes());

    MockMultipartHttpServletRequestBuilder request1 = MockMvcRequestBuilders
        .multipart("/member/profile-image").file(profileImage);
    request1.with(new RequestPostProcessor() {
      @Override
      public MockHttpServletRequest postProcessRequest(MockHttpServletRequest rq) {
        rq.setMethod(PATCH);
        return rq;
      }
    });
    // when
    // then
    mockMvc.perform(request1
            .with(csrf()))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @WithMockMember
  @DisplayName("프로필이미지업데이트[실패] - 이미지 null")
  void failUpdateProfileImage() throws Exception {
    // given
    MockMultipartHttpServletRequestBuilder request1 = MockMvcRequestBuilders
        .multipart("/member/profile-image");
    request1.with(new RequestPostProcessor() {
      @Override
      public MockHttpServletRequest postProcessRequest(MockHttpServletRequest rq) {
        rq.setMethod(PATCH);
        return rq;
      }
    });
    // when
    // then
    mockMvc.perform(request1
            .with(csrf()))
        .andExpect(status().isInternalServerError())
        .andDo(print());
  }

  @Test
  @WithMockMember
  @DisplayName("프로필이미지업데이트[실패] - 요청 메서드 다름")
  void failUpdateProfileImage2() throws Exception {
    // given
    MockMultipartFile profileImage = new MockMultipartFile(
        "profileImage", "image.png", IMAGE_PNG_VALUE, "image".getBytes());

    MockMultipartHttpServletRequestBuilder request1 = MockMvcRequestBuilders
        .multipart("/member/profile-image").file(profileImage);
    request1.with(new RequestPostProcessor() {
      @Override
      public MockHttpServletRequest postProcessRequest(MockHttpServletRequest rq) {
        rq.setMethod(PUT);
        return rq;
      }
    });
    // when
    // then
    mockMvc.perform(request1
            .with(csrf()))
        .andExpect(status().isInternalServerError())
        .andDo(print());
  }
}