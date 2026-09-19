package com.ugc.audit_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "audit_records",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_audit_ticket_action",
                columnNames = {"ticketId","action"}
        )
        }
      )
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ticketId;

    private String action;

    private String createdBy;

    private LocalDateTime createdAt;
}