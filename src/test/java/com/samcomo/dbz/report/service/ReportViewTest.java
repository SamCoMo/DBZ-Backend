package com.samcomo.dbz.report.service;

import com.samcomo.dbz.report.model.entity.Report;
import com.samcomo.dbz.report.model.repository.ReportRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReportViewTest {

  @Autowired
  private ReportRepository reportRepository;
  @Autowired
  private ReportService reportService;

  @AfterEach
  void reset(){
    Report report = reportRepository.findById(1L).get();
    report.setViews(0L);
    reportRepository.save(report);
  }

  @Test
  void 게시글_조회수_동시성테스트_요청_100개() throws InterruptedException {
    int threadCount = 100;

    // 비동기로 실행하는 작업을 단순화 하여 사용할 수 있게 도와주는 자바의 API
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    // 다른 스레드에서 수행 중인 작업이 완료될 때까지 대기할 수 있도록 도와주는 클래스
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          reportService.getReport(1L, "test@gmail.com");
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    Report report = reportRepository.findById(1L).orElseThrow();
    Assertions.assertEquals(100L, report.getViews());
  }

}
