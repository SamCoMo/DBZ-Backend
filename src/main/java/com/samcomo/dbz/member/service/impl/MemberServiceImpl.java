package com.samcomo.dbz.member.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.samcomo.dbz.global.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;
import static com.samcomo.dbz.global.exception.ErrorCode.PROFILE_IMAGE_NOT_UPLOADED;
import static com.samcomo.dbz.global.s3.constants.ImageCategory.MEMBER;

import com.samcomo.dbz.global.s3.constants.ImageUploadState;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.dto.LocationRequest;
import com.samcomo.dbz.member.model.dto.MyPageResponse;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final S3Service s3Service;

  @Override
  public void register(RegisterRequest request) {
    validateDuplicateMember(request.getEmail(), request.getNickname());

    Member member = Member.from(request);
    member.encodePassword(passwordEncoder, request.getPassword());

    memberRepository.save(member);
  }

  @Override
  public MyPageResponse getMyInfo(long memberId) {
    return MyPageResponse.from(getMember(memberId));
  }

  @Override
  public void updateLocation(long memberId, LocationRequest request) {
    Member member = getMember(memberId);
    member.setAddress(request.getAddress());
    member.setLatitude(request.getLatitude());
    member.setLongitude(request.getLongitude());
    memberRepository.save(member);
  }

  @Override
  public void updateProfileImage(long memberId, MultipartFile profileImage) {
    Member member = getMember(memberId);

    ImageUploadState imageUploadState =
        s3Service.uploadMultipartFileByStream(profileImage, MEMBER);

    if (!imageUploadState.isSuccess()) {
      throw new MemberException(PROFILE_IMAGE_NOT_UPLOADED);
    }

    String oldProfileImageUrl = member.getProfileImageUrl();
    if (oldProfileImageUrl != null) {
      s3Service.deleteFile(s3Service.getFileName(oldProfileImageUrl));
    }

    member.setProfileImageUrl(imageUploadState.getImageUrl());
    memberRepository.save(member);
  }

  @Override
  public Member getMember(long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  @Override
  public void validateDuplicateMember(String email, String nickname) {
    if (memberRepository.existsByEmail(email)) {
      throw new MemberException(EMAIL_ALREADY_EXISTS);
    }
    if (memberRepository.existsByNickname(nickname)) {
      throw new MemberException(NICKNAME_ALREADY_EXISTS);
    }
  }
}
