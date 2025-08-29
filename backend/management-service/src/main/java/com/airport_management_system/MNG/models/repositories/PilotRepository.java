package com.airport_management_system.MNG.models.repositories;

import com.airport_management_system.MNG.models.pojo.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {
}
