package org.example.valensmockproject.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class WorkOrder {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;

    private Instant createdAt;
    private Instant scheduledStart;
    private Instant completedAt;

    private String siteName;
    private String siteAddress;

    @Enumerated(EnumType.STRING)
    private WorkOrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician_id")
    private Technician assignedTechnician;

    @PrePersist
    void onCreate(){
        this.createdAt = Instant.now();
        if(this.status == null){
            this.status = WorkOrderStatus.CREATED;
        }
    }
}
