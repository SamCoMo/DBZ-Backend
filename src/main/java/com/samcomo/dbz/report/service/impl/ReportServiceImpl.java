package com.samcomo.dbz.report.service.impl;

import com.samcomo.dbz.global.config.s3.ImageUploadState;
import com.samcomo.dbz.global.config.s3.S3ServiceImpl;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.dto.ReportForm;
import com.samcomo.dbz.report.model.dto.ReportImageResponse;
import com.samcomo.dbz.report.model.dto.ReportResponse;
import com.samcomo.dbz.report.model.entity.Report;
import com.samcomo.dbz.report.model.entity.ReportImage;
import com.samcomo.dbz.report.model.repository.ReportImageRepository;
import com.samcomo.dbz.report.model.repository.ReportRepository;
import com.samcomo.dbz.report.service.ReportService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Repository
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final ReportImageRepository reportImageRepository;
  private final MemberRepository memberRepository;
  private final S3ServiceImpl s3Service;

  @Override
  @Transactional
  public ReportResponse uploadReport(long memberId, ReportForm reportForm, List<MultipartFile> multipartFileList) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    // 게시글 내용 저장
    Report newReport = reportRepository.save(Report.from(reportForm, member));

    List<ReportImageResponse> reportImageResponseList = new ArrayList<>();
    List<ReportImage> imageList = new ArrayList<>();
    if (!multipartFileList.isEmpty()) {
      for (MultipartFile image : multipartFileList) {
        ImageUploadState imageUploadState = s3Service.upload(image);
        checkUploadState(imageUploadState);
        String imageUrl = imageUploadState.getImageUrl();
        ReportImage reportImage = ReportImage.builder()
            .report(newReport)
            .imageUrl(imageUrl)
            .build();
        imageList.add(reportImage);
        reportImageResponseList.add(ReportImageResponse.from(reportImage));
      }
    }

    reportImageRepository.saveAll(imageList);

    return ReportResponse.from(newReport, reportImageResponseList);
  }

  private void checkUploadState(ImageUploadState imageUploadState) {

    String imageUrl = imageUploadState.getImageUrl();

    if (!imageUploadState.isSuccess()) {
      int idx = imageUrl.lastIndexOf("/");
      String fileName = imageUrl.substring(idx + 1);
      s3Service.delete(fileName);

      throw new ReportException(ErrorCode.IMAGE_UPLOAD_FAIL);
    }
  }
}
