package com.airport_management_system.MNG.models.pojo;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "hangar_allocation")
@IdClass(HangarAllocationId.class)
public class HangarAllocation {

    @Id
    @Column(name = "plane_id")
    private Long planeId;

    @Id
    @Column(name = "hangar_id")
    private Long hangarId;

    @Id
    @Column(name = "from_date")
    private Timestamp fromDate;

    @Column(name = "to_date")
    private Timestamp toDate;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;


    // Constructors
    public HangarAllocation() {}

    public HangarAllocation(Long planeId, Long hangarId, Timestamp fromDate,
                            Timestamp toDate, Long userId) {
        this.planeId = planeId;
        this.hangarId = hangarId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.userId = userId;
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

    public Timestamp getToDate() {
        return toDate;
    }

    public void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
