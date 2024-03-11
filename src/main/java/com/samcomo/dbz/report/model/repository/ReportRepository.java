package com.samcomo.dbz.report.model.repository;

import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.entity.Report;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {

  @Lock(value = LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from Report r where r.id = :id")
  Optional<Report> findByIdWithPessimistic(Long id);

  @Query(value = "select r "
      + "from Report r "
      + "where ST_Distance_Sphere(Point(r.longitude, r.latitude), Point(:curLongitude, :curLatitude)) > "
      + " ST_Distance_Sphere(Point(:lastLongitude, :lastLatitude), Point(:curLongitude, :curLatitude)) "
      + "order by ST_Distance_Sphere(Point(r.longitude, r.latitude), Point(:curLongitude, :curLatitude)) ")
  Slice<Report> findAllOrderByDistance(
      @Param("lastLatitude") double lastLatitude, @Param("lastLongitude") double lastLongitude,
      @Param("curLatitude") double curLatitude, @Param("curLongitude") double curLongitude,
      Pageable pageable);

  @Query(value = "select r "
      + "from Report r "
      + "where ST_Distance_Sphere(Point(r.longitude, r.latitude), Point(:curLongitude, :curLatitude)) > "
      + " ST_Distance_Sphere(Point(r.longitude, r.latitude), Point(:curLongitude, :curLatitude)) and r.reportStatus = 'PUBLISHED'"
      + "order by ST_Distance_Sphere(Point(:lastLongitude, :lastLatitude), Point(:curLongitude, :curLatitude)) ")
  Slice<Report> findAllInProcessOrderByDistance(
      @Param("lastLatitude") double lastLatitude, @Param("lastLongitude") double lastLongitude,
      @Param("curLatitude") double curLatitude, @Param("curLongitude") double curLongitude,
      Pageable pageable);


  Slice<Report> findAllByTitleContainsOrPetNameContainsOrSpeciesContains(String title, String petName, String species, Pageable pageable);

  Slice<Report> findAllByTitleContainsOrPetNameContainsOrSpeciesContainsAndReportStatus(String title, String petName, String species, ReportStatus reportStatus, Pageable pageable);
}
