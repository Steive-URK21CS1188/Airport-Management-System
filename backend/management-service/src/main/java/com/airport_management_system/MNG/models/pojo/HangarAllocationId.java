package com.airport_management_system.MNG.models.pojo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class HangarAllocationId implements Serializable {

    private Long planeId;
    private Long hangarId;
    private Timestamp fromDate;

    // Constructors
    public HangarAllocationId() {}

    public HangarAllocationId(Long planeId, Long hangarId, Timestamp fromDate) {
        this.planeId = planeId;
        this.hangarId = hangarId;
        this.fromDate = fromDate;
    }

    // Getters and Setters 
    public Long getPlaneId() {
        return planeId;
    }

    public void setPlaneId(Long planeId) {
        this.planeId = planeId;
    }

    public Long getHangarId() {
        return hangarId;
    }

    public void setHangarId(Long hangarId) {
        this.hangarId = hangarId;
    }

    public Timestamp getFromDate() {
        return fromDate;
    }

    public void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    // Equals and HashCode 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HangarAllocationId)) return false;
        HangarAllocationId that = (HangarAllocationId) o;
        return Objects.equals(planeId, that.planeId) &&
               Objects.equals(hangarId, that.hangarId) &&
               Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planeId, hangarId, fromDate);
    }
}
