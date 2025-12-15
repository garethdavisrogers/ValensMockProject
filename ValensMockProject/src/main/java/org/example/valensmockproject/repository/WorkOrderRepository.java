package org.example.valensmockproject.repository;

import org.example.valensmockproject.domain.WorkOrder;
import org.example.valensmockproject.domain.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {
    List<WorkOrder> findByStatus(WorkOrderStatus status);
}
