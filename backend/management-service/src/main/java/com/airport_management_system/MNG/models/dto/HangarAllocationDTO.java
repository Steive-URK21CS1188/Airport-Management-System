package com.airport_management_system.MNG.models.dto;

import java.sql.Timestamp;

public class HangarAllocationDTO {
    private Long planeId;
    private Long hangarId;
    private Timestamp fromDate;
    private Timestamp toDate;
    private Long userId;

    public HangarAllocationDTO() {}

    public HangarAllocationDTO(Long planeId, Long hangarId,
        Timestamp fromDate, Timestamp toDate, Long userId) {
        this.planeId = planeId;
        this.hangarId = hangarId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.userId = userId;
    }

    // getters & setters
    public Long getPlaneId() { return planeId; }
    public void setPlaneId(Long planeId) { this.planeId = planeId; }

    public Long getHangarId() { return hangarId; }
    public void setHangarId(Long hangarId) { this.hangarId = hangarId; }

    public Timestamp getFromDate() { return fromDate; }
    public void setFromDate(Timestamp fromDate) { this.fromDate = fromDate; }

    public Timestamp getToDate() { return toDate; }
    public void setToDate(Timestamp toDate) { this.toDate = toDate; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
