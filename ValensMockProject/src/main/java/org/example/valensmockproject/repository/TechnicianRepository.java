package org.example.valensmockproject.repository;

import org.example.valensmockproject.domain.Technician;
import org.example.valensmockproject.domain.TechnicianStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TechnicianRepository extends JpaRepository<Technician, UUID>{
    List<Technician> findByStatus(TechnicianStatus status);
}
