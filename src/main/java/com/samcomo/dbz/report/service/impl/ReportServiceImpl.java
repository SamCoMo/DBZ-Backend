package com.samcomo.dbz.report.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.global.redis.LockType;
import com.samcomo.dbz.global.redis.aop.DistributedLock;
import com.samcomo.dbz.global.s3.constants.ImageCategory;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.notification.service.NotificationService;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.dto.CustomPageable;
import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportImageDto;
import com.samcomo.dbz.report.model.dto.ReportSearchSummaryDto;
import com.samcomo.dbz.report.model.dto.ReportStateDto;
import com.samcomo.dbz.report.model.dto.ReportSummaryDto;
import com.samcomo.dbz.report.model.dto.ReportWithUrl;
import com.samcomo.dbz.report.model.entity.Report;
import com.samcomo.dbz.report.model.entity.ReportImage;
import com.samcomo.dbz.report.model.repository.ReportBulkRepository;
import com.samcomo.dbz.report.model.repository.ReportImageRepository;
import com.samcomo.dbz.report.model.repository.ReportRepository;
import com.samcomo.dbz.report.service.ReportService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final ReportImageRepository reportImageRepository;
  private final MemberRepository memberRepository;
  private final S3Service s3Service;
  private final ReportBulkRepository reportBulkRepository;

  private final NotificationService notificationService;

  @Override
  public ReportDto.Response uploadReport(long memberId, ReportDto.Form reportForm) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    // S3 이미지 저장
    List<String> imageUrlList = s3Service.uploadImageList(reportForm.getImageList(), ImageCategory.REPORT);

    // 게시글 저장
    Report newReport = reportRepository.save(Report.from(reportForm, member));

    //게시글 이미지 저장
    List<ReportImage> reportImageList = imageUrlList.stream()
        .map(imageUrl ->
            ReportImage.builder()
                .imageUrl(imageUrl)
                .report(newReport)
                .build()
        )
        .toList();

    List<ReportImage> savedImageList = reportBulkRepository.saveAllWithBulk(reportImageList);

    notificationService.sendReportNotification(reportForm);

    return ReportDto.Response.from(newReport, savedImageList.stream()
            .map(ReportImageDto.Response::from)
            .collect(Collectors.toList())
        , memberId
    );
  }

  @Override
  @DistributedLock(lockType = LockType.REPORT ,key = "redisLock", waitTime = 3L, leaseTime = 5L)
  public ReportDto.Response getReport(long reportId, long memberId) {

    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new ReportException(ErrorCode.REPORT_NOT_FOUND));

    log.info("현재 조회수 : {}", report.getViews());

    report.setViews(report.getViews() + 1);
    Report newReport = reportRepository.save(report);

    List<ReportImage> reportImageList = reportImageRepository.findAllByReport(newReport);
    List<ReportImageDto.Response> reportImageResponseList = new ArrayList<>();

    for (ReportImage reportImage : reportImageList) {
      reportImageResponseList.add(ReportImageDto.Response.from(reportImage));
    }

    log.info("================Finish 조회수 : {}===================", newReport.getViews());
    return ReportDto.Response.from(newReport, reportImageResponseList, report.getMember().getId());
  }

  @Override
  public CustomSlice<ReportSummaryDto> getReportList(
      double lastLatitude,
      double lastLongitude,
      double curLatitude,
      double curLongitude,
      boolean showsInProcessOnly,
      Pageable pageable
  ) {

    //TODO: 마지막 데이터에 대한 정보 필요 >> cursorId(마지막 데이터의 절대값)
    log.info("showInProcessOnly : {}", showsInProcessOnly);
    Slice<ReportWithUrl> reportSlice =
        showsInProcessOnly ?
            reportRepository.findAllInProcessOrderByDistance(
                lastLatitude, lastLongitude,
                curLatitude, curLongitude,
                pageable)
            :
                reportRepository.findAllOrderByDistance(
                    lastLatitude, lastLongitude,
                    curLatitude, curLongitude,
                    pageable);

    return getCustomSlice(reportSlice, pageable);
  }

  @Override
  @Transactional
  public ReportDto.Response updateReport(
      long reportId, long memberId, ReportDto.Form reportForm
  ) {

    Report report = reportRepository.findByIdAndMember_Id(reportId, memberId)
        .orElseThrow(() -> new ReportException(ErrorCode.REPORT_NOT_FOUND));

    report.setTitle(reportForm.getTitle());
    report.setPetName(reportForm.getPetName());
    report.setPetType(reportForm.getPetType());
    report.setSpecies(reportForm.getSpecies());
    report.setDescription(reportForm.getDescriptions());
    report.setStreetAddress(reportForm.getStreetAddress());
    report.setRoadAddress(reportForm.getRoadAddress());
    report.setLatitude(reportForm.getLatitude());
    report.setLongitude(report.getLongitude());
    report.setShowsPhone(reportForm.getShowsPhone());

    // s3이미지 삭제
    List<ReportImage> reportImageList = reportImageRepository.findAllByReport(report);
    for (ReportImage reportImage : reportImageList) {
      String url = reportImage.getImageUrl();
      int idx = url.lastIndexOf("/");

      s3Service.deleteFile(url.substring(idx + 1));
    }

    // ReportImage 에서 기존 이미지 삭제
    reportImageRepository.deleteAll(reportImageList);

    // 변경된 이미지 S3 저장
    List<String> imageUrlList = s3Service.uploadImageList(reportForm.getImageList(), ImageCategory.REPORT);

    // 게시글 저장
    Report newReport = reportRepository.save(report);

    //게시글 이미지 저장
    List<ReportImage> reportImages = imageUrlList.stream()
        .map(imageUrl -> ReportImage.builder()
            .imageUrl(imageUrl)
            .report(newReport)
            .build())
        .toList();

    List<ReportImage> updatedReportImageList = reportBulkRepository.saveAllWithBulk(reportImages);

    return ReportDto.Response.from(
        newReport,
        updatedReportImageList.stream()
            .map(ReportImageDto.Response::from)
            .collect(Collectors.toList()), memberId);
  }

  @Override
  @Transactional
  public ReportStateDto.Response deleteReport(long reportId, long memberId) {

    Report report = reportRepository.findByIdAndMember_Id(reportId, memberId)
        .orElseThrow(() -> new ReportException(ErrorCode.REPORT_NOT_FOUND));

    List<ReportImage> reportImageList = reportImageRepository.findAllByReport(report);
    for (ReportImage reportImage : reportImageList) {
      String url = reportImage.getImageUrl();
      int idx = url.lastIndexOf("/");

      s3Service.deleteFile(url.substring(idx + 1));
    }

    reportImageRepository.deleteAll(reportImageList);
    reportRepository.delete(report);

    return ReportStateDto.Response.builder()
        .reportId(report.getId())
        .status(ReportStatus.DELETED.toString())
        .build();
  }

  @Override
  @Transactional
  public ReportStateDto.Response changeStatusToFound(long reportId, long memberId) {

    Report report = reportRepository.findByIdAndMember_Id(reportId, memberId)
        .orElseThrow(() -> new ReportException(ErrorCode.REPORT_NOT_FOUND));

    report.setReportStatus(ReportStatus.FOUND);
    Report savedReport = reportRepository.save(report);

    return ReportStateDto.Response.builder()
        .reportId(savedReport.getId())
        .status(ReportStatus.FOUND.toString())
        .build();
  }

  @Override
  public CustomSlice<ReportSearchSummaryDto> searchReport(String object, boolean showsInProgressOnly,
      Pageable pageable) {

    Slice<Report> reportSlice = showsInProgressOnly ?
        reportRepository
            .findAllByTitleContainsOrPetNameContainsOrSpeciesContainsAndReportStatus(
                object, object, object, ReportStatus.PUBLISHED, pageable)
        :
            reportRepository
                .findAllByTitleContainsOrPetNameContainsOrSpeciesContains(
                    object, object, object, pageable);

    return getCustomSliceForSearch(reportSlice, pageable);
  }

  private CustomSlice<ReportSearchSummaryDto> getCustomSliceForSearch(Slice<Report> reportSlice,
      Pageable pageable) {

    CustomPageable customPageable = new CustomPageable(pageable.getPageNumber(),
        pageable.getPageSize());

    List<ReportSearchSummaryDto> ReportSummaryList = reportSlice.getContent().stream()
        .map(report -> {
          ReportSearchSummaryDto reportSummary = ReportSearchSummaryDto.from(report);
          reportImageRepository.findFirstByReport(report)
              .ifPresent(reportImage -> reportSummary.setImageUrl(reportImage.getImageUrl()));
          return reportSummary;
        })
        .toList();

    return CustomSlice.<ReportSearchSummaryDto>builder()
        .content(ReportSummaryList)
        .pageable(customPageable)
        .first(reportSlice.isFirst())
        .last(reportSlice.isLast())
        .number(reportSlice.getNumber())
        .size(reportSlice.getSize())
        .numberOfElements(reportSlice.getContent().size())
        .build();
  }

  private CustomSlice<ReportSummaryDto> getCustomSlice(Slice<ReportWithUrl> reportSlice,
      Pageable pageable) {

    CustomPageable customPageable = new CustomPageable(pageable.getPageNumber(),
        pageable.getPageSize());

    List<Long> reportImageMap = new ArrayList<>();
    List<ReportSummaryDto> reportSummaryDtoList = new ArrayList<>();

    for (ReportWithUrl reportWithUrl : reportSlice) {
      if (reportImageMap.contains(reportWithUrl.getId())) {
        continue;
      }

      reportImageMap.add(reportWithUrl.getId());
      reportSummaryDtoList.add(ReportSummaryDto.from(reportWithUrl));
    }

    return CustomSlice.<ReportSummaryDto>builder()
        .content(reportSummaryDtoList)
        .pageable(customPageable)
        .first(reportSlice.isFirst())
        .last(reportSlice.isLast())
        .number(reportSlice.getNumber())
        .size(reportSlice.getSize())
        .numberOfElements(reportSlice.getContent().size())
        .build();
  }

}
