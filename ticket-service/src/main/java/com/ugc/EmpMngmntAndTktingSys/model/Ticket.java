package com.ugc.EmpMngmntAndTktingSys.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.ugc.common.model.Priority;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;
    private String title;
    @Column(length = 3000)
    private String ticketDesc;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdByUserId;
    private Long assignedToUserId;
}