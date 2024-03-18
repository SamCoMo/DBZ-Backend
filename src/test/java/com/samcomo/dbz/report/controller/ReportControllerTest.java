package com.samcomo.dbz.report.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.service.MemberService;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.dto.CustomPageable;
import com.samcomo.dbz.report.model.dto.CustomSlice;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportDto.Form;
import com.samcomo.dbz.report.model.dto.ReportDto.Response;
import com.samcomo.dbz.report.model.dto.ReportStateDto;
import com.samcomo.dbz.report.model.dto.ReportSummaryDto;
import com.samcomo.dbz.report.service.ReportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(ReportController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ReportControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ReportService reportService;

  @MockBean
  private MemberService memberService;

  @Autowired
  private ObjectMapper objectMapper;

  private Member member;
  private ReportDto.Form reportForm;
  private  MockMultipartFile image1;
  private  MockMultipartFile image2;
  private MockMultipartFile reportFormFile;
  private ReportDto.Response response;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    member = Member.builder()
        .email("test@gmail.com")
        .build();

    reportForm = Form.builder()
        .showsPhone(true)
        .title("test title")
        .latitude(37.12345)
        .longitude(127.12345)
        .build();

    image1 = new MockMultipartFile(
        "imageList",
        "test1.PNG",
        MediaType.IMAGE_PNG_VALUE,
        "test1".getBytes());
    image2 = new MockMultipartFile(
        "imageList",
        "test2.PNG",
        MediaType.IMAGE_PNG_VALUE,
        "test2".getBytes());

    reportFormFile = new MockMultipartFile(
        "reportForm",
        null,
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(reportForm)
    );

    response = Response.builder()
        .reportId(1L)
        .memberId(1L)
        .title("test title")
        .isWriter(true)
        .latitude(37.12345)
        .longitude(127.12345)
        .build();
  }

  @Test
  @WithMockUser(username = "test@gmail.com", roles = {"MEMBER"})
  @DisplayName("게시글 등록 성공")
  void registerReportSuccess() throws Exception {
    //given
    Mockito.when(memberService.getMemberByAuthentication(Mockito.any()))
        .thenReturn(member);

    Mockito
        .when(reportService.uploadReport(Mockito.any(Member.class), Mockito.any(ReportDto.Form.class), Mockito.any()))
        .thenReturn(response);

    //when
    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.multipart(HttpMethod.POST, "/report")
            .file(reportFormFile)
            .file(image1)
            .file(image2)
            .with(csrf())
    );

    //then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reportId").value(1));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", roles = {"MEMBER"})
  @DisplayName("특정 게시글 가져오기 성공 ")
  void getReportSuccess() throws Exception {
    // given
    Long reportId = 1L;
    Response response = Response.builder()
        .reportId(1L)
        .memberId(1L)
        .isWriter(true)
        .latitude(37.12345)
        .longitude(127.12345)
        .build();

    Mockito.when(memberService.getMemberByAuthentication(Mockito.any()))
        .thenReturn(member);
    Mockito.when(reportService.getReport(reportId, member))
        .thenReturn(response);
    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/report/" + reportId)
        .with(csrf()));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reportId").value(1L));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", roles = {"MEMBER"})
  @DisplayName("게시글 목록 가져오기 성공 - \"PUBLISHED\" 상태")
  void getReportListSuccess() throws Exception {

    // given
    double lastLatitude = 37.12345;
    double lastLongitude = 127.12345;
    double curLatitude = 37.12345;
    double curLongitude = 37.12345;
    boolean showsInProcessOnly = true;
    Pageable pageable = PageRequest.of(1, 10);

    ReportSummaryDto reportSummaryDto = ReportSummaryDto.builder()
        .reportId(1L)
        .title("test title")
        .build();

    CustomPageable customPageable = new CustomPageable(pageable.getPageNumber(),
        pageable.getPageSize());
    CustomSlice<ReportSummaryDto> reportListCustomSlice = CustomSlice.<ReportSummaryDto>builder()
        .content(List.of(reportSummaryDto))
        .pageable(customPageable)
        .first(true)
        .last(false)
        .number(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .numberOfElements(pageable.getPageSize())
        .build();

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("lastLatitude", String.valueOf(lastLatitude));
    params.add("lastLongitude", String.valueOf(lastLongitude));
    params.add("curLatitude", String.valueOf(curLatitude));
    params.add("curLongitude", String.valueOf(curLongitude));
    params.add("showsInProcessOnly", String.valueOf(showsInProcessOnly));
    params.add("page", String.valueOf(pageable.getPageNumber()));
    params.add("size", String.valueOf(pageable.getPageSize()));

    Mockito.when(reportService.getReportList(
            Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(),
            Mockito.anyBoolean(), Mockito.any(Pageable.class)))
        .thenReturn(reportListCustomSlice);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/report/list")
        .params(params)
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf()));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].reportId").value(1L))
        .andExpect(jsonPath("$.content[0].title").value("test title"));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", roles = {"MEMBER"})
  @DisplayName("게시글 목록 가져오기 성공 - \"PUBLISHED\" 상태")
  void updateReportSuccess() throws Exception {
    // given
    Long reportId = 1L;
    Mockito.when(memberService.getMemberByAuthentication(Mockito.any()))
        .thenReturn(member);
    Mockito.when(
        reportService.updateReport(Mockito.anyLong(), Mockito.any(ReportDto.Form.class), Mockito.any(), Mockito.any()))
        .thenReturn(response);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/report/" + reportId)
        .file(image1)
        .file(image2)
        .file(reportFormFile)
        .with(csrf()));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reportId").value(1L))
        .andExpect(jsonPath("$.title").value("test title"))
        .andDo(print());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", roles = {"MEMBER"})
  @DisplayName("게시글 삭제 성공")
  void deleteReportSuccess() throws Exception {
    // given
    long reportId = 1L;

    ReportStateDto.Response deleteResponse = ReportStateDto.Response.builder()
        .reportId(reportId)
        .status(ReportStatus.DELETED.toString())
        .build();

    Mockito.when(memberService.getMemberByAuthentication(Mockito.any()))
        .thenReturn(member);

    Mockito.when(reportService.deleteReport(member, reportId))
        .thenReturn(deleteResponse);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/report/" + reportId)
        .with(csrf()));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reportId").value(1L))
        .andExpect(jsonPath("$.status").value(ReportStatus.DELETED.toString()))
        .andDo(print());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", roles = {"MEMBER"})
  @DisplayName("게시글 상태 \"PUBLISHED\"로 변경 성공")
  void completeProcessSuccess() throws Exception {
    // given
    long reportId = 1L;

    ReportStateDto.Response foundResponse = ReportStateDto.Response.builder()
        .reportId(reportId)
        .status(ReportStatus.FOUND.toString())
        .build();

    Mockito.when(memberService.getMemberByAuthentication(Mockito.any()))
        .thenReturn(member);

    Mockito.when(reportService.changeStatusToFound(member, reportId))
        .thenReturn(foundResponse);
    // when
    ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.put("/report/" + reportId + "/complete")
            .with(csrf()));
    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reportId").value(1L))
        .andExpect(jsonPath("$.status").value(ReportStatus.FOUND.toString()));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", roles = {"MEMBER"})
  @DisplayName("게시글 검색 성공")
  void searchReport() throws Exception {
    // given
    String object = "test";
    boolean showsInProgressOnly = true;
    Pageable pageable = PageRequest.of(1, 10);

    ReportSummaryDto reportSummaryDto = ReportSummaryDto.builder()
        .reportId(1L)
        .title("test title")
        .build();

    CustomPageable customPageable = new CustomPageable(pageable.getPageNumber(), pageable.getPageSize());
    CustomSlice<ReportSummaryDto> reportListSlice = CustomSlice.<ReportSummaryDto>builder()
        .content(List.of(reportSummaryDto))
        .pageable(customPageable)
        .first(true)
        .last(false)
        .number(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .numberOfElements(pageable.getPageSize())
        .build();

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("showsInProgressOnly", String.valueOf(showsInProgressOnly));
    params.add("object", object);
    params.add("page", String.valueOf(pageable.getPageNumber()));
    params.add("size", String.valueOf(pageable.getPageSize()));

    Mockito.when(memberService.getMemberByAuthentication(Mockito.any()))
        .thenReturn(member);

    Mockito.when(reportService.searchReport(object, showsInProgressOnly, pageable))
        .thenReturn(reportListSlice);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/report/search")
            .params(params)
            .with(csrf())
        );

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].reportId").value(1L))
        .andExpect(jsonPath("$.content[0].title").value("test title"));

  }
}
