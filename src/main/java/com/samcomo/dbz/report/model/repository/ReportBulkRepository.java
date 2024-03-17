package com.samcomo.dbz.report.model.repository;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.report.exception.BulkException;
import com.samcomo.dbz.report.model.entity.ReportImage;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ReportBulkRepository {

  private final JdbcTemplate jdbcTemplate;

  @Transactional
  public List<ReportImage> saveAllWithBulk(List<ReportImage> reportImageList){
    String sql =
        "insert into report_image (image_url, report_id, created_at, updated_at) "
            + "values(?, ?, now(), now());";

    jdbcTemplate.batchUpdate(
        sql,
        reportImageList,
        reportImageList.size(),
        (PreparedStatement ps, ReportImage reportImage)-> {
          ps.setString(1, reportImage.getImageUrl());
          ps.setLong(2, reportImage.getReport().getId());
        }
    );

    Long firstPk = jdbcTemplate.queryForObject("select last_insert_id();", Long.class);

    for (int i = 0; i < reportImageList.size(); i++) {
      if (firstPk == null){
        throw new BulkException(ErrorCode.REPORT_IMAGE_ID_NOT_CHECKED);
      }
      reportImageList.get(i).setId(firstPk + i);
    }

    return reportImageList;

  }


}
