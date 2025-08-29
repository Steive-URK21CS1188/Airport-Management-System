package com.airport_management_system.MNG.models.dao.services;

import com.airport_management_system.MNG.models.pojo.HangarAllocation;
import com.airport_management_system.MNG.models.dto.*;
import java.sql.Timestamp;
import java.util.List;

public interface HangarAllocationService {

	public HangarAllocation allocateHangar(HangarAllocation allocation);
	// HangarAllocationService.java
	public List<HangarAllocation> getAllAllocations();
	public void deleteAllocation(Long planeId, Long hangarId, Timestamp fromDate);
	public List<HangarAllocation> getPlaneAllocations(Long planeId, Timestamp from, Timestamp to);
    List<HangarAllocation> getAvailability(Long hangarId, Timestamp from, Timestamp to);
}
