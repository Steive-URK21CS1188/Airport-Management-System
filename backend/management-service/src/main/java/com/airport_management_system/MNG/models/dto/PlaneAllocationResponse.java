package com.airport_management_system.MNG.models.dto;

import java.time.LocalDateTime;

public class PlaneAllocationResponse {
    
	public Long planeId;
    public Long pilotId;
    public LocalDateTime fromDate;
    public LocalDateTime toDate;
    public Long managerUserId;
    
    public PlaneAllocationResponse(Long planeId, Long pilotId, LocalDateTime fromDate, LocalDateTime toDate, Long managerUserId) {
        this.planeId = planeId;
        this.pilotId = pilotId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.managerUserId = managerUserId;
    }
    public PlaneAllocationResponse() {}
    public Long getManagerUserId() {
		return managerUserId;
	}

	public void setManagerUserId(Long managerUserId) {
		this.managerUserId = managerUserId;
	}

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
}
