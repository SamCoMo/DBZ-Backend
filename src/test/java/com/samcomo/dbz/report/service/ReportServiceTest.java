package com.samcomo.dbz.report.service;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.global.s3.constants.ImageCategory;
import com.samcomo.dbz.global.s3.constants.ImageUploadState;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportDto.Response;
import com.samcomo.dbz.report.model.dto.ReportList;
import com.samcomo.dbz.report.model.dto.ReportStateDto;
import com.samcomo.dbz.report.model.entity.Report;
import com.samcomo.dbz.report.model.entity.ReportImage;
import com.samcomo.dbz.report.model.repository.ReportImageRepository;
import com.samcomo.dbz.report.model.repository.ReportRepository;
import com.samcomo.dbz.report.service.impl.ReportServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

  @Mock
  private ReportRepository reportRepository;
  @Mock
  private ReportImageRepository reportImageRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private S3Service s3Service;
  @InjectMocks
  private ReportServiceImpl reportService;

  private Member member;
  private ReportDto.Form reportForm;
  private List<MultipartFile> multipartFileList;
  private ImageUploadState imageUploadState;
  private Report report;
  private String memberEmail;


  @BeforeEach
  void setUp(){
      member = Member.builder()
        .id(1L)
        .email("test@gmail.com")
        .build();

    reportForm = ReportDto.Form.builder()
        .title("test title")
        .petType(PetType.DOG)
        .build();

    multipartFileList = List.of(
        new MockMultipartFile("test1","test1.PNG", MediaType.IMAGE_PNG_VALUE,"test1".getBytes()),
        new MockMultipartFile("test2","test2.PNG", MediaType.IMAGE_PNG_VALUE,"test2".getBytes())
    );
    imageUploadState = ImageUploadState.builder()
        .success(true)
        .imageUrl("http://testUpload/test.png")
        .build();
    report = Report.builder()
        .id(1L)
        .member(member)
        .showsPhone(true)
        .views(0L)
        .build();

    memberEmail = "test@gmail.com";
  }

  @Test
  @DisplayName("게시글 업로드 성공")
  void uploadReport(){
    //given
    ReportImage reportImage = ReportImage.builder()
        .imageUrl(imageUploadState.getImageUrl())
        .build();
    String imageUrl = imageUploadState.getImageUrl();

    Report newReport = Report.from(reportForm, member);

    Mockito.when(memberRepository.findByEmail(memberEmail))
        .thenReturn(Optional.of(member));

    Mockito.when(s3Service.uploadImageList(multipartFileList, ImageCategory.REPORT))
        .thenReturn(List.of(imageUrl));

    newReport.setId(1L);
    Mockito.when(reportRepository.save(Mockito.any(Report.class)))
        .thenReturn(newReport);
    Mockito.when(reportImageRepository.saveAll(Mockito.any(List.class)))
        .thenReturn(List.of(reportImage, reportImage));

    //when
    Response response = reportService.uploadReport("test@gmail.com", reportForm, multipartFileList);

    //then
    Assertions.assertEquals(newReport.getId() ,response.getReportId());
    Assertions.assertEquals(reportForm.getTitle(), response.getTitle());
    Assertions.assertEquals(reportForm.getPetType(), response.getPetType());
  }

  @Test
  @DisplayName("게시글 가져오기 성공")
  void getReportSuccess(){
    //given
    Mockito.when(reportRepository.findById(1L))
        .thenReturn(Optional.of(report));
    Mockito.when(memberRepository.findByEmail(memberEmail))
        .thenReturn(Optional.of(member));

    report.setViews(report.getViews() + 1);
    Mockito.when(reportRepository.save(Mockito.any(Report.class)))
        .thenReturn(report);

    Mockito.when(reportImageRepository.findAllByReport(Mockito.any(Report.class)))
        .thenReturn(List.of(
            ReportImage.builder()
                .id(1L)
                .imageUrl("http://testUpload/test.png")
                .report(report)
                .build(),
            ReportImage.builder()
                .id(2L)
                .imageUrl("http://testUpload/test.png")
                .report(report)
                .build()
        ));

    //when
    ReportDto.Response response = reportService.getReport(1L, "test@gmail.com");

    //then
    Assertions.assertEquals(report.getId(), response.getReportId());
    Assertions.assertEquals(report.getViews(), response.getViews());
    Assertions.assertEquals("http://testUpload/test.png", response.getImageList().get(0).getUrl());

  }

  @Test
  @DisplayName("게시글 가져오기 실패 - 게시글 정보 없음")
  void getReportFail1(){
    //given
    Mockito.when(reportRepository.findById(1L))
        .thenReturn(Optional.empty());

    //when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        () -> reportService.getReport(1L, "test@gmail.com"));

    //then
    Assertions.assertEquals(ErrorCode.REPORT_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 목록 가져오기 성공")
  void getReportList(){
    //given

    Pageable pageable = PageRequest.of(1, 10);
    double lastLatitude = 37.1111;
    double lastLongitude = 127.1111;
    double curLatitude = 37.1234;
    double curLongitude = 127.1234;

    List<Report> reportList = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      reportList.add(
          Report.builder()
              .id((long) i)
              .build()
      );
    }

    Slice<Report> reportSlice = new SliceImpl<>(reportList, pageable, true);
    Mockito.when(reportRepository.findAllOrderByDistance(
            lastLatitude, lastLongitude,
            curLatitude, curLongitude,
            pageable))
        .thenReturn(reportSlice);

    //when
    CustomSlice<ReportList> reportListSlice = reportService.getReportList(
        lastLatitude, lastLongitude,
        curLatitude, curLongitude,
        false,
        pageable);
    //then
    int  sliceSize = reportListSlice.getContent().size();
    System.out.println(sliceSize);
    Assertions.assertEquals(1L, reportListSlice.getContent().get(0).getReportId());
    Assertions.assertEquals(10L, reportListSlice.getContent().get(9).getReportId());
    Assertions.assertFalse(reportListSlice.isLast());
    Assertions.assertEquals(pageable.getPageSize(), reportListSlice.getSize());
  }

  @Test
  @DisplayName("게시글 목록 가져오기 성공 - \"진행중\"상태인 게시글만 가져오기")
  void getReportListSuccess2(){
    //given

    Pageable pageable = PageRequest.of(1, 10);
    double lastLatitude = 37.1111;
    double lastLongitude = 127.1111;
    double curLatitude = 37.1234;
    double curLongitude = 127.1234;

    List<Report> reportList = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      reportList.add(
          Report.builder()
              .id((long)i)
              .build()
      );
    }

    Slice<Report> reportSlice = new SliceImpl<>(reportList, pageable, true);
    Mockito.when(reportRepository.findAllInProcessOrderByDistance(
            lastLatitude, lastLongitude,
            curLatitude, curLongitude,
            pageable))
        .thenReturn(reportSlice);

    //when
    CustomSlice<ReportList> reportListSlice = reportService.getReportList(
        lastLatitude, lastLongitude,
        curLatitude, curLongitude,
        true,
        pageable);

    //then
    int  sliceSize = reportListSlice.getContent().size();
    System.out.println(sliceSize);
    Assertions.assertEquals(1L, reportListSlice.getContent().get(0).getReportId());
    Assertions.assertEquals(10L, reportListSlice.getContent().get(9).getReportId());
    Assertions.assertFalse(reportListSlice.isLast());
    Assertions.assertEquals(pageable.getPageSize(), reportListSlice.getSize());
    Mockito.verify(reportRepository, Mockito.times(0))
        .findAllOrderByDistance(
            Mockito.anyDouble(), Mockito.anyDouble(),
            Mockito.anyDouble() ,Mockito.anyDouble(),
            Mockito.any(Pageable.class));
  }

  @Test
  @DisplayName("게시글 수정 성공")
  void updateReport(){
    //given

    List<ReportImage> reportImageList = List.of(ReportImage.builder()
            .id(1L)
            .imageUrl("http://testUpload/test.png")
        .build());

    ReportImage reportImage1 = ReportImage.builder()
        .id(1L)
        .imageUrl(imageUploadState.getImageUrl())
        .build();
    ReportImage reportImage2 = ReportImage.builder()
        .id(2L)
        .imageUrl(imageUploadState.getImageUrl())
        .build();
    String imageUrl = imageUploadState.getImageUrl();

    Mockito.when(reportRepository.findByIdAndMember_Email(1L, "test@gmail.com"))
        .thenReturn(Optional.of(report));
    Mockito.when(reportImageRepository.findAllByReport(report))
        .thenReturn(reportImageList);

    Mockito.when(s3Service.uploadImageList(multipartFileList, ImageCategory.REPORT))
        .thenReturn(List.of(imageUrl));


    Mockito.when(reportRepository.save(report))
        .thenReturn(report);
    Mockito.when(reportImageRepository.saveAll(Mockito.any(List.class)))
        .thenReturn(List.of(reportImage1, reportImage2));

    ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);

    //when
    Response response = reportService.updateReport(1L, reportForm, multipartFileList, "test@gmail.com");

    //then
    Mockito.verify(s3Service).deleteFile(fileNameCaptor.capture());
    String fileName = fileNameCaptor.getValue();

    Assertions.assertEquals("test.png", fileName);
    Assertions.assertEquals(report.getId(), response.getReportId());
  }

  @Test
  @DisplayName("게시글 수정 실패 - 게시글 정보 없음")
  void updateReportFail1(){
    //given
    Mockito.when(reportRepository.findByIdAndMember_Email(1L, "test@gmail.com"))
        .thenReturn(Optional.empty());
    //when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        ()-> reportService.updateReport(1L, reportForm, multipartFileList, "test@gmail.com"));

    //then
    Assertions.assertEquals(ErrorCode.REPORT_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 삭제 성공")
  void deleteReportSuccess(){
    // given
    List<ReportImage> reportImageList = List.of(ReportImage.builder()
        .id(1L)
        .imageUrl("http://testUpload/test.png")
        .build());

    Mockito.when(reportRepository.findByIdAndMember_Email(1L, "test@gmail.com"))
        .thenReturn(Optional.of(report));
    Mockito.when(reportImageRepository.findAllByReport(report))
        .thenReturn(reportImageList);

    ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
    // when
    ReportStateDto.Response response = reportService.deleteReport("test@gmail.com", 1L);

    // then
    Mockito.verify(s3Service).deleteFile(fileNameCaptor.capture());
    String deletedFileName = fileNameCaptor.getValue();

    Assertions.assertEquals("test.png", deletedFileName);
    Assertions.assertEquals(1L, response.getReportId());
    Assertions.assertEquals("DELETED", response.getStatus());
  }

  @Test
  @DisplayName("게시글 삭제 실패 - 게시글 정보 없음")
  void deleteReportFail2(){
    // given
    Mockito.when(reportRepository.findByIdAndMember_Email(1L, "test@gmail.com"))
        .thenReturn(Optional.empty());

    // when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        () -> reportService.deleteReport("test@gmail.com", 1L));

    // then
    Assertions.assertEquals(ErrorCode.REPORT_NOT_FOUND.getMessage() ,exception.getMessage());
  }

  @Test
  @DisplayName("게시글 상태 '찾음' 으로 변경 성공")
  void changeStatusToFoundSuccess(){
    // given
    Mockito.when(reportRepository.findByIdAndMember_Email(1L, "test@gmail.com"))
        .thenReturn(Optional.of(report));
    report.setReportStatus(ReportStatus.FOUND);
    Mockito.when(reportRepository.save(report))
        .thenReturn(report);

    // when
    ReportStateDto.Response response = reportService.changeStatusToFound("test@gmail.com", 1L);

    // then
    Assertions.assertEquals(report.getId(), response.getReportId());
    Assertions.assertEquals(ReportStatus.FOUND.toString(), response.getStatus());
  }

  @Test
  @DisplayName("게시글 상태 '찾음'으로 변경 실패 - 게시글 정보 없음")
  void changeStatusToFoundFail2(){
    // given
    Mockito.when(reportRepository.findByIdAndMember_Email(1L, "test@gmail.com"))
        .thenReturn(Optional.empty());

    // when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        () -> reportService.changeStatusToFound("test@gmail.com", 1L));

    // then

    Assertions.assertEquals(ErrorCode.REPORT_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 검색 성공 - 게시글 상태에 상관 없이 검색")
  void searchReportSuccess1(){
    // given
    Pageable pageable = PageRequest.of(1, 10);

    List<Report> reportList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      reportList.add(
          Report.builder()
              .id((long) i)
              .build()
      );
    }
    Slice<Report> reportSlice = new SliceImpl<>(reportList, pageable, true);

    Mockito.when(reportRepository.findAllByTitleContainsOrPetNameContainsOrSpeciesContains(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
        .thenReturn(reportSlice);
    Mockito.when(reportImageRepository.findFirstByReport(Mockito.any(Report.class)))
        .thenReturn(Optional.of(ReportImage.builder()
                .imageUrl("http://testUpload/test.png")
            .build()));

    // when
    CustomSlice<ReportList> reportListSlice = reportService.searchReport("test", false, pageable);

    // then
    Assertions.assertFalse(reportListSlice.isLast());
    Assertions.assertEquals(0L, reportListSlice.getContent().get(0).getReportId());
    Assertions.assertEquals(pageable.getPageNumber(), reportListSlice.getPageable().getPageNumber());
  }

  @Test
  @DisplayName("게시글 검색 성공 - \"PUBLISHED\" 상태인 게시글만 검색")
  void searchReportSuccess2(){
    //given
    Pageable pageable = PageRequest.of(1, 10);
    String object = "test";

    List<Report> reportList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      reportList.add(
          Report.builder()
              .id((long) i)
              .reportStatus(ReportStatus.PUBLISHED)
              .build()
      );
    }

    Slice<Report> reportSlice = new SliceImpl<>(reportList, pageable, true);

    Mockito.when(reportRepository
        .findAllByTitleContainsOrPetNameContainsOrSpeciesContainsAndReportStatus(
            object,
            object,
            object,
            ReportStatus.PUBLISHED,
            pageable
        ))
        .thenReturn(reportSlice);
    Mockito.when(reportImageRepository.findFirstByReport(Mockito.any(Report.class)))
        .thenReturn(Optional.of(ReportImage.builder()
            .imageUrl("http://testUpload/test.png")
            .build()));

    // when
    CustomSlice<ReportList> reportListSlice = reportService.searchReport("test", true, pageable);

    // then
    Assertions.assertFalse(reportListSlice.isLast());
    Assertions.assertEquals(0L, reportListSlice.getContent().get(0).getReportId());
    Assertions.assertEquals(pageable.getPageNumber(), reportListSlice.getPageable().getPageNumber());
    Assertions.assertEquals(reportSlice.getContent().get(0).getReportStatus(), reportListSlice.getContent().get(0).getReportStatus());
  }
}
