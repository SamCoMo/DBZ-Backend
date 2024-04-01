//package com.samcomo.dbz.member.service.impl;
//
//import static com.samcomo.dbz.member.model.constants.LoginType.GOOGLE;
//import static com.samcomo.dbz.member.model.constants.MemberRole.MEMBER;
//import static com.samcomo.dbz.member.model.constants.MemberStatus.ACTIVE;
//
//import com.samcomo.dbz.member.model.dto.oauth2.GoogleResponse;
//import com.samcomo.dbz.member.model.dto.oauth2.Oauth2MemberDetails;
//import com.samcomo.dbz.member.model.dto.oauth2.Oauth2Response;
//import com.samcomo.dbz.member.model.entity.Member;
//import com.samcomo.dbz.member.model.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class Oauth2MemberServiceImpl extends DefaultOAuth2UserService {
//
//  private final MemberRepository memberRepository;
//
//  @Override
//  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
//    OAuth2User oAuth2User = super.loadUser(userRequest);
//    log.info("[소셜로그인 정보]: {}", oAuth2User);
//
//    String loginType = userRequest.getClientRegistration().getRegistrationId();
//
//    Oauth2Response oAuth2Response = null;
//    if (loginType.equals(GOOGLE.getKey())) {
//      oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
//    }
//    else {
//      return null;
//    }
//
//    Member member = memberRepository.findByEmail(oAuth2Response.getEmail())
//        .orElse(Member.builder()
//            .loginType(GOOGLE)
//            .status(ACTIVE)
//            .role(MEMBER)
//            .build());
//
//    member.updateEmailAndNickname(oAuth2Response.getEmail(), getNickname(oAuth2Response));
//    member.setProfileImageUrl(oAuth2Response.getProfileImageUrl());
//
//    return new Oauth2MemberDetails(memberRepository.save(member));
//  }
//
//  @NotNull
//  private static String getNickname(Oauth2Response oAuth2Response) {
//    return oAuth2Response.getNickname()
//        + "[" + oAuth2Response.getLoginType().getKey() + "]";
//  }
//}