package org.example.valensmockproject.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Technician {
    @Id
    @GeneratedValue
    private UUID id;

    private String lastName;
    private String firstName;

    @Enumerated(EnumType.STRING)
    private TechnicianStatus status;

    @Enumerated(EnumType.STRING)
    private TechnicianRole role;

    private String email;
}
