package com.samcomo.dbz.report.service.impl;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.global.s3.S3Service;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.dto.CustomPageable;
import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportImageDto;
import com.samcomo.dbz.report.model.dto.ReportList;
import com.samcomo.dbz.report.model.dto.ReportStateDto;
import com.samcomo.dbz.report.model.entity.Report;
import com.samcomo.dbz.report.model.entity.ReportImage;
import com.samcomo.dbz.report.model.repository.ReportImageRepository;
import com.samcomo.dbz.report.model.repository.ReportRepository;
import com.samcomo.dbz.report.service.ReportService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final ReportImageRepository reportImageRepository;
  private final MemberRepository memberRepository;
  private final S3Service s3Service;

  @Override
  public ReportDto.Response uploadReport(
      long memberId, ReportDto.Form reportForm, List<MultipartFile> multipartFileList
  ) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    // S3 이미지 저장
    List<ReportImage> imageList = s3Service.uploadAll(multipartFileList);

    // 게시글 저장
    Report newReport = reportRepository.save(Report.from(reportForm, member));

    //게시글 이미지 저장
    imageList.forEach(reportImage -> reportImage.setReport(newReport));
    List<ReportImage> savedImageList = reportImageRepository.saveAll(imageList);

    return ReportDto.Response.from(
        newReport,
        savedImageList.stream()
            .map(ReportImageDto.Response::from)
            .collect(Collectors.toList())
    );
  }

  @Override
  public ReportDto.Response getReport(long reportId) {

    //TODO: 조회수에 대한 동시성 처리 필요

    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new ReportException(ErrorCode.REPORT_NOT_FOUND));

    report.setViews(report.getViews() + 1);
    Report newReport = reportRepository.save(report);

    List<ReportImage> reportImageList = reportImageRepository.findAllByReport(newReport);
    List<ReportImageDto.Response> reportImageResponseList = new ArrayList<>();

    for (ReportImage reportImage : reportImageList) {
      reportImageResponseList.add(ReportImageDto.Response.from(reportImage));
    }

    return ReportDto.Response.from(newReport, reportImageResponseList);
  }

  @Override
  @Cacheable(key = "#lastLatitude +'-'+ #lastLongitude",value = "reportPage")
  public CustomSlice<ReportList> getReportList(
      double lastLatitude,
      double lastLongitude,
      double curLatitude,
      double curLongitude,
      boolean showsInProcessOnly,
      Pageable pageable
  ) {

    //TODO: 마지막 데이터에 대한 정보 필요 >> cursorId(마지막 데이터의 절대값)


    Slice<Report> reportSlice =
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
  public ReportDto.Response updateReport(
      long reportId, ReportDto.Form reportForm, List<MultipartFile> multipartFileList, long userId
  ) {

    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new ReportException(ErrorCode.REPORT_NOT_FOUND));

    // 게시글 작성자와 수정 요청한 유저가 동일한지 검사
    if (!report.getMember().equals(member)) {
      throw new ReportException(ErrorCode.NOT_SAME_MEMBER);
    }

    report.setPetType(reportForm.getPetType());
    report.setTitle(reportForm.getTitle());
    report.setPetName(reportForm.getPetName());
    report.setSpecies(reportForm.getSpecies());
    report.setAge(reportForm.getAge());
    report.setDescription(reportForm.getDescriptions());
    report.setFeature(reportForm.getFeature());
    report.setStreetAddress(reportForm.getStreetAddress());
    report.setRoadAddress(reportForm.getRoadAddress());
    report.setShowsPhone(reportForm.isShowsPhone());

    // s3이미지 삭제
    List<ReportImage> reportImageList = reportImageRepository.findAllByReport(report);
    for (ReportImage reportImage : reportImageList) {
      String url = reportImage.getImageUrl();
      int idx = url.lastIndexOf("/");
      s3Service.delete(url.substring(idx + 1));
    }

    // 변경된 이미지 저장
    List<ReportImage> newImageList = s3Service.uploadAll(multipartFileList);

    // 게시글 저장
    Report newReport = reportRepository.save(report);

    //게시글 이미지 저장
    newImageList.forEach(reportImage -> reportImage.setReport(newReport));
    List<ReportImage> savedImageList = reportImageRepository.saveAll(newImageList);

    return ReportDto.Response.from(
        newReport,
        savedImageList.stream()
            .map(ReportImageDto.Response::from)
            .collect(Collectors.toList()));
  }

  @Override
  public ReportStateDto.Response deleteReport(long userId, long reportId) {

    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new ReportException(ErrorCode.REPORT_NOT_FOUND));

    if (!report.getMember().equals(member)) {
      throw new ReportException(ErrorCode.NOT_SAME_MEMBER);
    }

    List<ReportImage> reportImageList = reportImageRepository.findAllByReport(report);
    for (ReportImage reportImage : reportImageList) {
      String url = reportImage.getImageUrl();
      int idx = url.lastIndexOf("/");
      s3Service.delete(url.substring(idx + 1));
    }

    reportRepository.delete(report);

    return ReportStateDto.Response.builder()
        .reportId(report.getId())
        .status(ReportStatus.DELETED.toString())
        .build();
  }

  @Override
  public ReportStateDto.Response changeStatusToFound(long userId, long reportId) {

    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new ReportException(ErrorCode.REPORT_NOT_FOUND));

    if (!report.getMember().equals(member)) {
      throw new ReportException(ErrorCode.NOT_SAME_MEMBER);
    }

    report.setReportStatus(ReportStatus.FOUND);
    Report savedReport = reportRepository.save(report);

    return  ReportStateDto.Response.builder()
        .reportId(savedReport.getId())
        .status(ReportStatus.FOUND.toString())
        .build();
  }

  @Override
  public CustomSlice<ReportList> searchReport(String object, boolean showsInProgressOnly, Pageable pageable) {

    Slice<Report> reportSlice = showsInProgressOnly ?
        reportRepository
            .findAllByTitleContainsOrPetNameContainsOrSpeciesContainsAndReportStatus(object, object, object, ReportStatus.PUBLISHED ,pageable)
        :
        reportRepository
            .findAllByTitleContainsOrPetNameContainsOrSpeciesContains(object, object, object, pageable);

    return getCustomSlice(reportSlice, pageable);
  }

  private CustomSlice<ReportList> getCustomSlice(Slice<Report> reportSlice, Pageable pageable) {

    CustomPageable customPageable = new CustomPageable(pageable.getPageNumber(),
        pageable.getPageSize());

    List<ReportList> contentReportList = reportSlice.getContent().stream()
        .map(report -> {
          ReportList reportList = ReportList.from(report);
          reportImageRepository.findFirstByReport(report)
              .ifPresent(reportImage -> reportList.setImageUrl(reportImage.getImageUrl()));
          return reportList;
        })
        .toList();

    return new CustomSlice<>(
        contentReportList,
        customPageable,
        reportSlice.isFirst(),
        reportSlice.isLast(),
        reportSlice.getNumber(),
        reportSlice.getSize(),
        reportSlice.getNumberOfElements()
    );
  }

}
