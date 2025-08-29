package com.airport_management_system.MNG.models.pojo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plane_allocation")
@IdClass(PlaneAllocationId.class)
public class PlaneAllocation {

    @Id
    @Column(name = "plane_id")
    private Long planeId;

    @Id
    @Column(name = "pilot_id")
    private Long pilotId;

    @Id
    @Column(name = "from_date")
    private LocalDateTime fromDate;

    @Column(name = "to_date")
    private LocalDateTime toDate;

    @Column(name = "user_id")
    private Long userId;

    public PlaneAllocation() {}

    public PlaneAllocation(Long planeId, Long pilotId, LocalDateTime fromDate, LocalDateTime toDate, Long userId) {
        this.planeId = planeId;
        this.pilotId = pilotId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.userId = userId;
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

public Long getUserId() {
    return userId;
}

public void setUserId(Long userId) {
    this.userId = userId;
}

}

