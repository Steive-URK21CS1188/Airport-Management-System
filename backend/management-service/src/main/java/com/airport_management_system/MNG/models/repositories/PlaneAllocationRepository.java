package com.airport_management_system.MNG.models.repositories;

import com.airport_management_system.MNG.models.pojo.PlaneAllocation;
import com.airport_management_system.MNG.models.pojo.PlaneAllocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlaneAllocationRepository extends JpaRepository<PlaneAllocation, PlaneAllocationId> {

    @Query("""
           SELECT CASE WHEN COUNT(pa) > 0 THEN true ELSE false END
           FROM PlaneAllocation pa
           WHERE pa.pilotId = :pilotId
           AND pa.fromDate < :toDate
           AND (pa.toDate IS NULL OR pa.toDate > :fromDate)
           """)
    boolean existsOverlappingAllocation(
            @Param("pilotId") Long pilotId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    List<PlaneAllocation> findByUserId(Long userId);
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM PlaneAllocation p " +
            "WHERE p.planeId = :planeId " +
            "AND p.fromDate < :toDate " +
            "AND p.toDate > :fromDate")
     boolean existsOverlappingAllocationForPlane(@Param("planeId") Long planeId,
                                                 @Param("fromDate") LocalDateTime fromDate,
                                                 @Param("toDate") LocalDateTime toDate);
 }
