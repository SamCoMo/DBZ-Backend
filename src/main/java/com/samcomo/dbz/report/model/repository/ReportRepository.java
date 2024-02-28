package com.samcomo.dbz.report.model.repository;

import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.entity.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {

  @Query("select r from Report r "
      + "where abs(r.latitude - :latitude) + abs(r.longitude - :longitude) > :cursor "
      + "order by abs(:latitude - r.latitude) + abs(:longitude - r.longitude) ")
  Slice<Report> findAllOrderByDistance(@Param("cursor") long cursor, @Param("latitude") double latitude, @Param("longitude") double longitude, Pageable pageable);

  @Query("select r from Report r "
      + "where r.reportStatus = 'PUBLISHED' "
      + "and abs(r.latitude - :latitude) + abs(r.longitude - :longitude) > :cursor "
      + "order by abs(:latitude - r.latitude) + abs(:longitude - r.longitude) ")
  Slice<Report> findAllInProcessOrderByDistance(@Param("cursor") long cursor, @Param("latitude") double latitude, @Param("longitude") double longitude, Pageable pageable);

  Slice<Report> findAllByTitleContainsOrPetNameContainsOrSpeciesContains(String title, String petName, String species, Pageable pageable);

  Slice<Report> findAllByTitleContainsOrPetNameContainsOrSpeciesContainsAndReportStatus(String title, String petName, String species, ReportStatus reportStatus, Pageable pageable);
}
