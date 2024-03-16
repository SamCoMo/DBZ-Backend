package com.samcomo.dbz.pin.service.impl;


import com.samcomo.dbz.global.s3.constants.ImageCategory;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.pin.dto.RegisterPinDto;
import com.samcomo.dbz.pin.dto.RegisterPinDto.Response;
import com.samcomo.dbz.pin.dto.UpdatePinDto;
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
  public Response registerPin(Long memberId, Long reportId, RegisterPinDto.Request request) {

    // 핀 생성 검증 및 저장
    Pin newPin = pinRepository.save(
        Pin.builder()
        .report(pinUtils.verifyReportById(reportId)) // report 검증
        .member(pinUtils.verifyMemberById(memberId)) // member 검증
        .description(request.getDescription())
        .foundAt(request.getFoundAt())
        .address(request.getAddress())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .build());

    // MultipartFile 리스트 S3 업로드
    List<String> imageUrlList =
        s3Service.uploadImageList(request.getMultipartFileList(), ImageCategory.PIN);

    // PinImage 객체 생성
    List<PinImage> tempPinImageList = new ArrayList<>();

    for (String imageUrl : imageUrlList) {
      tempPinImageList.add(new PinImage(imageUrl, newPin));
    }

    // PinImage 객체 리스트 저장 후 반환
    return RegisterPinDto.Response.from(
        newPin,
        pinImageRepository.saveAll(tempPinImageList));
  }

  @Override
  public UpdatePinDto.Response updatePin(
      Long memberId, Long pinId, UpdatePinDto.Request request) {

    // 핀 검증 + 회원 접근 검증
    Pin pin = pinUtils.verifyPinByIdAndMemberId(pinId, memberId);

    // 핀 Data 업데이트 ( 발견시각, 내용 )
    pin.setDescription(request.getDescription());
    pin.setFoundAt(request.getFoundAt());

    // 핀 저장
    return UpdatePinDto.Response.from(pinRepository.save(pin));
  }

  @Override
  @Transactional
  public void deletePin(Long memberId, Long pinId) {
    // 핀 검증 + 회원 접근 검증 -> 삭제
    // TODO : 테스트 필요
    pinRepository.delete(pinUtils.verifyPinByIdAndMemberId(pinId, memberId));
  }
}
