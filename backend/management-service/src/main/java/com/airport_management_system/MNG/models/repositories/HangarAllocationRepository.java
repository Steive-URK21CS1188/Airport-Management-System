package com.airport_management_system.MNG.models.repositories;

import com.airport_management_system.MNG.models.pojo.HangarAllocation;
import com.airport_management_system.MNG.models.pojo.HangarAllocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.sql.Timestamp;
public interface HangarAllocationRepository extends JpaRepository<HangarAllocation, HangarAllocationId> {

   
	List<HangarAllocation> findByHangarIdAndFromDateLessThanAndToDateGreaterThan(Long hangarId, Timestamp to, Timestamp from);
	@Query(value = "SELECT COUNT(*) FROM hangar_details WHERE hangar_id = :hangarId", nativeQuery = true)
	Long countHangarById(@Param("hangarId") Long hangarId);
	List<HangarAllocation> findByPlaneIdAndFromDateLessThanAndToDateGreaterThan(
		    Long planeId,
		    Timestamp to,
		    Timestamp from
		);

}
