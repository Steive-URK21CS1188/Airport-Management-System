package com.airport_management_system.MNG.models.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class PlaneAllocationId implements Serializable {
    private Long planeId;
    private Long pilotId;
    private LocalDateTime fromDate;

    public PlaneAllocationId() {}

    public PlaneAllocationId(Long planeId, Long pilotId, LocalDateTime fromDate) {
        this.planeId = planeId;
        this.pilotId = pilotId;
        this.fromDate = fromDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaneAllocationId that = (PlaneAllocationId) o;
        return Objects.equals(planeId, that.planeId) &&
               Objects.equals(pilotId, that.pilotId) &&
               Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planeId, pilotId, fromDate);
    }
}
