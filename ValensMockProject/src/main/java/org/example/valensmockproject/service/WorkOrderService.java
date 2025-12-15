package org.example.valensmockproject.service;

import org.example.valensmockproject.domain.Technician;
import org.example.valensmockproject.domain.WorkOrder;
import org.example.valensmockproject.domain.WorkOrderStatus;
import org.example.valensmockproject.repository.TechnicianRepository;
import org.example.valensmockproject.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
public class WorkOrderService {
    private final WorkOrderRepository workOrderRepository;
    private final TechnicianRepository technicianRepository;

    public WorkOrderService(WorkOrderRepository workOrderRepository, TechnicianRepository technicianRepository){
        this.workOrderRepository = workOrderRepository;
        this.technicianRepository = technicianRepository;
    }

    @Transactional
    public WorkOrder create(String title, String description, String siteName, String siteAddress){
        WorkOrder workOrder = new WorkOrder();
        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setSiteName(siteName);
        workOrder.setSiteAddress(siteAddress);

        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public WorkOrder assignTechnician(UUID workOrderId, UUID technicianId){
        WorkOrder workOrder = getRequiredWorkOrder(workOrderId);
        ensureNotTerminal(workOrder);

        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(()-> new NotFoundException("Technician not found: " + technicianId));

        workOrder.setAssignedTechnician(technician);

        if(workOrder.getStatus() == WorkOrderStatus.CREATED){
            workOrder.setStatus(WorkOrderStatus.SCHEDULED);
        }
        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public WorkOrder updateStatus(UUID workOrderId, WorkOrderStatus next) {
        WorkOrder wo = getRequiredWorkOrder(workOrderId);

        WorkOrderStatus current = wo.getStatus();
        if (!isValidTransition(current, next)) {
            throw new BadRequestException("Invalid status transition: " + current + " -> " + next);
        }

        wo.setStatus(next);

        // set completedAt when completing
        if (next == WorkOrderStatus.COMPLETED) {
            wo.setCompletedAt(Instant.now());
        }

        return workOrderRepository.save(wo);
    }

    @Transactional(readOnly = true)
    public WorkOrder get(UUID workOrderId){
        return getRequiredWorkOrder((workOrderId));
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> list(WorkOrderStatus workOrderStatus) {
        return (workOrderStatus == null) ? workOrderRepository.findAll() : workOrderRepository.findByStatus(workOrderStatus);
    }

    private WorkOrder getRequiredWorkOrder(UUID workOrderId){
        return workOrderRepository.findById(workOrderId)
                .orElseThrow(()-> new NotFoundException("Work Order not found: " + workOrderId));
    }

    private void ensureNotTerminal(WorkOrder workOrder){
        if(workOrder.getStatus() == WorkOrderStatus.COMPLETED || workOrder.getStatus() == WorkOrderStatus.CANCELLED){
            throw new BadRequestException("Work Order is in terminal status: " + workOrder.getStatus());
        }
    }
    private boolean isValidTransition(WorkOrderStatus from, WorkOrderStatus to) {
        if (from == to) return true;

        // terminal states: no leaving
        if (from == WorkOrderStatus.COMPLETED || from == WorkOrderStatus.CANCELLED) return false;

        // allow cancel from most states
        if (to == WorkOrderStatus.CANCELLED) return true;

        return switch (from) {
            case CREATED -> to == WorkOrderStatus.SCHEDULED;
            case SCHEDULED -> to == WorkOrderStatus.IN_PROGRESS;
            case IN_PROGRESS -> to == WorkOrderStatus.COMPLETED;
            default -> false;
        };
    }


    // Minimal exceptions (keep these in service package for now)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String msg) { super(msg); }
    }
}
