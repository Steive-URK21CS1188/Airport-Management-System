package com.airport_management_system.MNG.models.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PlaneAllocationRequest {

    @NotNull(message = "Plane ID is required")
    private Long planeId;

    @NotNull(message = "Pilot ID is required")
    private Long pilotId;

    @NotNull(message = "From date is required")
    private LocalDateTime fromDate;

    @NotNull(message = "To date is required")
    @Future(message = "To date must be in the future")
    private LocalDateTime toDate;

    @NotNull(message = "Manager user ID is required")
    private Long managerUserId;

	public Long getPlaneId() {
		return planeId;
	}

	public void setPlaneId(Long planeId) {
		this.planeId = planeId;
	}

	public Long getPilotId() {
		return pilotId;
	}

	public void setPilotId(Long pilotId) {
		this.pilotId = pilotId;
	}

	public LocalDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	public Long getManagerUserId() {
		return managerUserId;
	}

	public void setManagerUserId(Long managerUserId) {
		this.managerUserId = managerUserId;
	}

    
}
