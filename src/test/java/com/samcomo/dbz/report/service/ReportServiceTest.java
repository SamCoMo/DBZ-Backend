package com.samcomo.dbz.report.service;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.global.s3.ImageUploadState;
import com.samcomo.dbz.global.s3.S3Service;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.constants.ReportStatus;
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
  }

  @Test
  @DisplayName("게시글 업로드 성공")
  void uploadReport(){
    //given
    ReportImage reportImage = ReportImage.builder()
        .imageUrl(imageUploadState.getImageUrl())
        .build();
    Report newReport = Report.from(reportForm, member);

    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    Mockito.when(s3Service.uploadAll(multipartFileList))
        .thenReturn(List.of(reportImage));

    newReport.setId(1L);
    Mockito.when(reportRepository.save(Mockito.any()))
        .thenReturn(newReport);
    Mockito.when(reportImageRepository.saveAll(Mockito.any()))
        .thenReturn(List.of(reportImage, reportImage));

    //when
    Response response = reportService.uploadReport(1L, reportForm, multipartFileList);

    //then
    Assertions.assertEquals(newReport.getId() ,response.getReportId());
    Assertions.assertEquals(reportForm.getTitle(), response.getTitle());
    Assertions.assertEquals(reportForm.getPetType(), response.getPetType());
  }

  @Test
  @DisplayName("게시글 업로드 실패 - 멤버 정보 없음")
  void uploadReportFail1(){
    //given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    //when
    Throwable exception = Assertions.assertThrows(MemberException.class,
        () -> reportService.uploadReport(1L, reportForm, multipartFileList));

    //then
    Assertions.assertEquals(ErrorCode.MEMBER_NOT_FOUND.getMessage(), exception.getMessage());

  }

  @Test
  @DisplayName("게시글 가져오기 성공")
  void getReportSuccess(){
    //given
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(report));

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
    ReportDto.Response response = reportService.getReport(1L);

    //then
    Assertions.assertEquals(report.getId(), response.getReportId());
    Assertions.assertEquals(report.getViews(), response.getViews());
    Assertions.assertEquals("http://testUpload/test.png", response.getImageList().get(0).getUrl());

  }

  @Test
  @DisplayName("게시글 가져오기 실패 - 게시글 정보 없음")
  void getReportFail1(){
    //given
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    //when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        () -> reportService.getReport(1L));

    //then
    Assertions.assertEquals(ErrorCode.REPORT_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 목록 가져오기 성공")
  void getReportList(){
    //given

    Pageable pageable = PageRequest.of(1, 10);
    int cursorId = 0;
    double latitude = 37.1234;
    double longitude = 127.1234;

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
            Mockito.anyLong(),
            Mockito.anyDouble(),
            Mockito.anyDouble(),
            Mockito.any(Pageable.class)))
        .thenReturn(reportSlice);

    //when
    Slice<ReportList> reportListSlice = reportService.getReportList(0, latitude, longitude, false,
        pageable);
    //then
    int  sliceSize = reportListSlice.getContent().size();
    System.out.println(sliceSize);
    Assertions.assertEquals(1L, reportListSlice.getContent().get(0).getReportId());
    Assertions.assertEquals(10L, reportListSlice.getContent().get(9).getReportId());
    Assertions.assertTrue(reportListSlice.hasNext());
    Assertions.assertEquals(pageable.getPageSize(), reportListSlice.getSize());
  }

  @Test
  @DisplayName("게시글 목록 가져오기 성공 - \"진행중\"상태인 게시글만 가져오기")
  void getReportListSuccess2(){
    //given

    Pageable pageable = PageRequest.of(1, 10);
    int cursorId = 0;
    double latitude = 37.1234;
    double longitude = 127.1234;

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
            Mockito.anyLong(),
            Mockito.anyDouble(),
            Mockito.anyDouble(),
            Mockito.any(Pageable.class)))
        .thenReturn(reportSlice);

    //when
    Slice<ReportList> reportListSlice = reportService.getReportList(0, latitude, longitude, true,
        pageable);
    //then
    int  sliceSize = reportListSlice.getContent().size();
    System.out.println(sliceSize);
    Assertions.assertEquals(1L, reportListSlice.getContent().get(0).getReportId());
    Assertions.assertEquals(10L, reportListSlice.getContent().get(9).getReportId());
    Assertions.assertTrue(reportListSlice.hasNext());
    Assertions.assertEquals(pageable.getPageSize(), reportListSlice.getSize());
    Mockito.verify(reportRepository, Mockito.times(0))
        .findAllOrderByDistance(Mockito.anyLong(), Mockito.anyDouble() ,Mockito.anyDouble() , Mockito.any(Pageable.class));
  }

  @Test
  @DisplayName("게시글 수정 성공")
  void updateReport(){
    //given

    List<ReportImage> reportImageList = List.of(ReportImage.builder()
            .id(1L)
            .imageUrl("http://testUpload/test.png")
        .build());

    ReportImage reportImage = ReportImage.builder()
        .imageUrl(imageUploadState.getImageUrl())
        .build();


    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(report));
    Mockito.when(reportImageRepository.findAllByReport(Mockito.any(Report.class)))
        .thenReturn(reportImageList);

    Mockito.when(s3Service.uploadAll(Mockito.any()))
        .thenReturn(List.of(reportImage));


    Mockito.when(reportRepository.save(report))
        .thenReturn(report);
    Mockito.when(reportImageRepository.saveAll(Mockito.any()))
        .thenReturn(List.of(reportImage, reportImage));

    ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);

    //when
    Response response = reportService.updateReport(1L, reportForm, multipartFileList, 1L);

    //then
    Mockito.verify(s3Service).delete(fileNameCaptor.capture());
    String fileName = fileNameCaptor.getValue();

    Assertions.assertEquals("test.png", fileName);
    Assertions.assertEquals(report.getId(), response.getReportId());
  }

  @Test
  @DisplayName("게시글 수정 실패 - 멤버 정보 없음")
  void updateReportFail1(){
    //given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.empty());
    //when
    Throwable exception = Assertions.assertThrows(MemberException.class,
        ()-> reportService.updateReport(1L, reportForm, multipartFileList, 1L));

    //then
    Assertions.assertEquals(ErrorCode.MEMBER_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 수정 실패 - 게시글 정보 없음")
  void updateReportFail2(){
    //given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.empty());
    //when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        ()-> reportService.updateReport(1L, reportForm, multipartFileList, 1L));

    //then
    Assertions.assertEquals(ErrorCode.REPORT_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 수정 실패 - 게시글 작성자와 수정 유청자가 다름")
  void updateReportFail3(){
    //given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    report.setMember(Member.builder().id(2L).build());
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(report));

    //when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        ()-> reportService.updateReport(1L, reportForm, multipartFileList, 1L));

    //then
    Assertions.assertEquals(ErrorCode.NOT_SAME_MEMBER.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 삭제 성공")
  void deleteReportSuccess(){
    // given
    List<ReportImage> reportImageList = List.of(ReportImage.builder()
        .id(1L)
        .imageUrl("http://testUpload/test.png")
        .build());

    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(report));
    Mockito.when(reportImageRepository.findAllByReport(Mockito.any(Report.class)))
        .thenReturn(reportImageList);

    ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
    // when
    ReportStateDto.Response response = reportService.deleteReport(1L, 1L);

    // then
    Mockito.verify(s3Service).delete(fileNameCaptor.capture());
    String deletedFileName = fileNameCaptor.getValue();

    Assertions.assertEquals("test.png", deletedFileName);
    Assertions.assertEquals(1L, response.getReportId());
    Assertions.assertEquals("DELETED", response.getStatus());
  }

  @Test
  @DisplayName("게시글 삭제 실패 - 멤버 정보 없음")
  void deleteReportFail1(){
    // given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    // when
    Throwable exception = Assertions.assertThrows(MemberException.class,
        () -> reportService.deleteReport(1L, 1L));

    // then

    Assertions.assertEquals(ErrorCode.MEMBER_NOT_FOUND.getMessage() ,exception.getMessage());
  }

  @Test
  @DisplayName("게시글 삭제 실패 - 게시글 정보 없음")
  void deleteReportFail2(){
    // given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    // when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        () -> reportService.deleteReport(1L, 1L));

    // then
    Assertions.assertEquals(ErrorCode.REPORT_NOT_FOUND.getMessage() ,exception.getMessage());
  }

  @Test
  @DisplayName("게시글 삭제 실패 - 게시글 작성자와 삭제 요청자 불일치")
  void deleteReportFail3(){
    // given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    report.setMember(Member.builder().id(2L).build());
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(report));

    // when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        () -> reportService.deleteReport(2L, 1L));

    // then
    Assertions.assertEquals(ErrorCode.NOT_SAME_MEMBER.getMessage() ,exception.getMessage());
  }

  @Test
  @DisplayName("게시글 상태 '찾음'으로 변경 성공")
  void changeStatusToFoundSuccess(){
    // given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(report));
    report.setReportStatus(ReportStatus.FOUND);
    Mockito.when(reportRepository.save(Mockito.any(Report.class)))
        .thenReturn(report);

    // when
    ReportStateDto.Response response = reportService.changeStatusToFound(1L, 1L);

    // then
    Assertions.assertEquals(report.getId(), response.getReportId());
    Assertions.assertEquals(ReportStatus.FOUND.toString(), response.getStatus());
  }

  @Test
  @DisplayName("게시글 상태 '찾음'으로 변경 실패 - 멤버 정보 없음")
  void changeStatusToFoundFail1(){
    // given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    // when
    Throwable exception = Assertions.assertThrows(MemberException.class,
        () -> reportService.changeStatusToFound(1L, 1L));

    // then

    Assertions.assertEquals(ErrorCode.MEMBER_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 상태 '찾음'으로 변경 실패 - 게시글 정보 없음")
  void changeStatusToFoundFail2(){
    // given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    // when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        () -> reportService.changeStatusToFound(1L, 1L));

    // then

    Assertions.assertEquals(ErrorCode.REPORT_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("게시글 상태 '찾음'으로 변경 실패 - 상태 변경 요청자와 게시글 작성자 불일치")
  void changeStatusToFoundFail3(){
    // given
    Mockito.when(memberRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(member));
    report.setMember(Member.builder().id(2L).build());
    Mockito.when(reportRepository.findById(Mockito.anyLong()))
        .thenReturn(Optional.of(report));

    // when
    Throwable exception = Assertions.assertThrows(ReportException.class,
        () -> reportService.changeStatusToFound(1L, 1L));

    // then

    Assertions.assertEquals(ErrorCode.NOT_SAME_MEMBER.getMessage(), exception.getMessage());
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
    Slice<ReportList> reportListSlice = reportService.searchReport("test", false, pageable);

    // then
    Assertions.assertTrue(reportListSlice.hasNext());
    Assertions.assertEquals(0L, reportListSlice.getContent().get(0).getReportId());
    Assertions.assertEquals(pageable.getPageNumber(), reportListSlice.getPageable().getPageNumber());
  }

  @Test
  @DisplayName("게시글 검색 성공 - \"PUBLISHED\" 상태인 게시글만 검색")
  void searchReportSuccess2(){
    //given
    Pageable pageable = PageRequest.of(1, 10);

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
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(ReportStatus.class),
            Mockito.any(Pageable.class)
        ))
        .thenReturn(reportSlice);
    Mockito.when(reportImageRepository.findFirstByReport(Mockito.any(Report.class)))
        .thenReturn(Optional.of(ReportImage.builder()
            .imageUrl("http://testUpload/test.png")
            .build()));

    // when
    Slice<ReportList> reportListSlice = reportService.searchReport("test", true, pageable);

    // then
    Assertions.assertTrue(reportListSlice.hasNext());
    Assertions.assertEquals(0L, reportListSlice.getContent().get(0).getReportId());
    Assertions.assertEquals(pageable.getPageNumber(), reportListSlice.getPageable().getPageNumber());
    Assertions.assertEquals(reportSlice.getContent().get(0).getReportStatus(), reportListSlice.getContent().get(0).getReportStatus());
  }
}
