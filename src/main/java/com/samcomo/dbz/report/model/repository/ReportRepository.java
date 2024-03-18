package com.samcomo.dbz.report.model.repository;

import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.dto.ReportWithUrl;
import com.samcomo.dbz.report.model.entity.Report;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {

  Optional<Report> findByIdAndMember_Id(Long id, Long memberId);

  @Query(value = "select "
      + "new com.samcomo.dbz.report.model.dto.ReportWithUrl("
      + "r.id, r.member.id, r.petType, r.reportStatus, r.title, r.petName, r.species, r.streetAddress, r.roadAddress, r.latitude, r.longitude, ri.imageUrl, r.createdAt, r.updatedAt) "
      + "from Report r "
      + "join ReportImage ri on r = ri.report "
      + "where ST_Distance_Sphere(Point(r.longitude, r.latitude), Point(:curLongitude, :curLatitude)) > "
      + " ST_Distance_Sphere(Point(:lastLongitude, :lastLatitude), Point(:curLongitude, :curLatitude)) "
      + "order by ST_Distance_Sphere(Point(r.longitude, r.latitude), Point(:curLongitude, :curLatitude)) ")
  Slice<ReportWithUrl> findAllOrderByDistance(
      @Param("lastLatitude") double lastLatitude, @Param("lastLongitude") double lastLongitude,
      @Param("curLatitude") double curLatitude, @Param("curLongitude") double curLongitude,
      Pageable pageable);

  @Query(value = "select "
      + "new com.samcomo.dbz.report.model.dto.ReportWithUrl("
      + "r.id, r.member.id, r.petType, r.reportStatus, r.title, r.petName, r.species, r.streetAddress, r.roadAddress, r.latitude, r.longitude, ri.imageUrl, r.createdAt, r.updatedAt) "
      + "from Report r "
      + "join ReportImage ri on r = ri.report "
      + "where ST_Distance_Sphere(Point(r.longitude, r.latitude), Point(:curLongitude, :curLatitude)) > "
      + " ST_Distance_Sphere(Point(r.longitude, r.latitude), Point(:curLongitude, :curLatitude)) "
      + "and r.reportStatus = 'PUPLISHED' "
      + "order by ST_Distance_Sphere(Point(:lastLongitude, :lastLatitude), Point(:curLongitude, :curLatitude)) ")
  Slice<ReportWithUrl> findAllInProcessOrderByDistance(
      @Param("lastLatitude") double lastLatitude, @Param("lastLongitude") double lastLongitude,
      @Param("curLatitude") double curLatitude, @Param("curLongitude") double curLongitude,
      Pageable pageable);

  Slice<Report> findAllByTitleContainsOrPetNameContainsOrSpeciesContains(String title, String petName, String species, Pageable pageable);

  Slice<Report> findAllByTitleContainsOrPetNameContainsOrSpeciesContainsAndReportStatus(String title, String petName, String species, ReportStatus reportStatus, Pageable pageable);
}
