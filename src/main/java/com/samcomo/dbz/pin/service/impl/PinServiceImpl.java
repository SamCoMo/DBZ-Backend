package com.samcomo.dbz.pin.service.impl;


import com.samcomo.dbz.global.s3.constants.ImageCategory;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.notification.service.NotificationService;
import com.samcomo.dbz.pin.dto.RegisterPinDto;
import com.samcomo.dbz.pin.dto.RegisterPinDto.Response;
import com.samcomo.dbz.pin.dto.PinDto;
import com.samcomo.dbz.pin.dto.PinListDto;
import com.samcomo.dbz.pin.dto.UpdatePinDto;
import com.samcomo.dbz.pin.model.entity.Pin;
import com.samcomo.dbz.pin.model.entity.PinImage;
import com.samcomo.dbz.pin.model.repository.PinImageRepository;
import com.samcomo.dbz.pin.model.repository.PinRepository;
import com.samcomo.dbz.pin.service.PinService;
import com.samcomo.dbz.pin.util.PinUtils;
import java.util.List;
import java.util.stream.Collectors;
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
  private final NotificationService notificationService;

  @Override
  @Transactional
  public Response registerPin(Long memberId, Long reportId,
      RegisterPinDto.Request request) {
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
        s3Service.uploadImageList(request.getMultipartFileList(),
            ImageCategory.PIN);

    // PinImage 리스트 객체 생성
    List<PinImage> newPinImageList =
        pinImageRepository.saveAll(
            imageUrlList.stream()
                .map(imageUrl -> PinImage.builder()
                    .imageUrl(imageUrl)
                    .pin(newPin)
                    .build())
                .toList());

    notificationService.sendPinNotification(memberId);

    return RegisterPinDto.Response.from(
        newPin,
        newPinImageList);
  }

  @Override
  @Transactional
  public PinDto updatePin(
      Long memberId, Long pinId, UpdatePinDto.Request request) {

    // 핀 검증 + 회원 접근 검증
    Pin pin = pinUtils.verifyUpdateMemberByPinId(memberId, pinId);

    List<PinImage> pinImageListToDelete = pinImageRepository.findAllByPin_PinId(
        pinId);

    // S3 사진 삭제
    pinImageListToDelete.stream()
        .map(PinImage::getImageUrl)
        .map(url -> url.substring(url.lastIndexOf("/") + 1)) // 파일이름
        .forEach(s3Service::deleteFile); // S3 서비스 각 파일이름 삭제

    // PinImage 리스트 삭제
    pinImageRepository.deleteAll(pinImageListToDelete);

    // S3 사진 업로드 , PinImage 리스트 생성
    List<String> imageUrlList = s3Service.uploadImageList(
        request.getMultipartFileList(), ImageCategory.PIN);

    // PinImage 생성 후 저장
    List<PinImage> updatedPinImageList =
        pinImageRepository.saveAll(
            imageUrlList.stream()
                .map(imageUrl -> PinImage.builder()
                    .imageUrl(imageUrl)
                    .pin(pin)
                    .build())
                .toList());

    // 핀 주소 업데이트
    pin.setDescription(request.getDescription());
    pin.setFoundAt(request.getFoundAt());
    pin.setAddress(request.getAddress());
    pin.setLatitude(request.getLatitude());
    pin.setLongitude(request.getLongitude());

    // 핀 저장
    return PinDto.from(pinRepository.save(pin), updatedPinImageList);
  }

  @Override
  @Transactional
  public void deletePin(Long memberId, Long pinId) {
    // 핀 검증 + 회원 접근 검증 -> 삭제
    pinRepository.delete(
        pinUtils.verifyDeleteMemberByPinId(pinId, memberId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<PinListDto> getPinList(Long memberId, Long reportId) {
    // 레포트 검증 후 핀 리스트 반환
    List<Pin> pinList = pinRepository.findByReport(
        pinUtils.verifyReportById(reportId));
    return pinList.stream().map(PinListDto::from).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public PinDto getPin(Long memberId, Long pinId) {
    // 핀 검증 , 핀 사진 검증 후 반환
    Pin pin = pinUtils.verifyPinById(pinId);
    return PinDto.from(pin, pinUtils.getPinImageListByPinId(pinId));
  }
}