package com.samcomo.dbz.pin.service.impl;


import com.samcomo.dbz.global.s3.constants.ImageCategory;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.pin.dto.CreatePinDto;
import com.samcomo.dbz.pin.dto.CreatePinDto.Request;
import com.samcomo.dbz.pin.dto.CreatePinDto.Response;
import com.samcomo.dbz.pin.dto.UpdatePinAddressDto;
import com.samcomo.dbz.pin.dto.UpdatePinDataDto;
import com.samcomo.dbz.pin.model.entity.Pin;
import com.samcomo.dbz.pin.model.entity.PinImage;
import com.samcomo.dbz.pin.model.repository.PinImageRepository;
import com.samcomo.dbz.pin.model.repository.PinRepository;
import com.samcomo.dbz.pin.service.PinService;
import com.samcomo.dbz.pin.util.PinUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {

  private final PinRepository pinRepository;
  private final PinImageRepository pinImageRepository;
  private final PinUtils pinUtils;
  private final S3Service s3Service;

  @Override
  @Transactional
  public Response createPin(String memberEmail, Long reportId, Request request) {

    // 핀 생성 검증 및 저장
    Pin newPin = pinRepository.save(
        Pin.builder()
        .report(pinUtils.verifyReportById(reportId)) // report 검증
        .member(pinUtils.verifyMemberByEmail(memberEmail)) // member 검증
        .description(request.getDescription())
        .foundAt(request.getFoundAt())
        .streetAddress(null)
        .roadAddress(null)
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .build());

    // MultipartFile 리스트 S3 업로드
    List<String> imageUrlList = s3Service.uploadImageList(request.getMultipartFileList(), ImageCategory.PIN);

    // PinImage 객체 생성
    List<PinImage> tempPinImageList = new ArrayList<>();

    for (String imageUrl : imageUrlList) {
      tempPinImageList.add(PinImage.builder()
          .imageUrl(imageUrl)
          .pin(newPin)
          .build());
    }

    // PinImage 객체 리스트 저장 후 반환
    return CreatePinDto.Response.from(
        newPin,
        pinImageRepository.saveAll(tempPinImageList));
  }

  @Override
  @Transactional
  public UpdatePinAddressDto.Response updatePinAddress(String memberEmail, Long pinId, UpdatePinAddressDto.Request request) {
    // 핀 검증 + 회원 접근 검증
    Pin pin = pinUtils.verifyPinByIdAndMemberEmail(memberEmail,pinId);

    // 핀 주소 업데이트
    pin.setStreetAddress(request.getStreetAddress());
    pin.setRoadAddress(request.getRoadAddress());

    // 핀 저장
    return UpdatePinAddressDto.Response.from(pinRepository.save(pin));
  }

  @Override
  @Transactional
  public UpdatePinDataDto.Response updatePinData(String memberEmail, Long pinId, UpdatePinDataDto.Response updatePinResponseDto) {
    // 핀 검증 + 회원 접근 검증
    Pin pin = pinUtils.verifyPinByIdAndMemberEmail(memberEmail,pinId);

    // 핀 Data 업데이트 ( 발견시각, 내용 )
    pin.setDescription(updatePinResponseDto.getDescription());
    pin.setFoundAt(updatePinResponseDto.getFoundAt());

    // 핀 저장
    return UpdatePinDataDto.Response.from(pinRepository.save(pin));
  }

  @Override
  @Transactional
  public void deletePin(String memberEmail, Long pinId) {
    // 핀 검증 + 회원 접근 검증 -> 삭제
    pinRepository.delete(
        pinUtils.verifyPinByIdAndMemberEmail(memberEmail,pinId));
  }
}
