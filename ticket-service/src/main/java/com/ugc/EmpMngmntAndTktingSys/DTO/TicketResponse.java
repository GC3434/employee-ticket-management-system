package com.ugc.EmpMngmntAndTktingSys.DTO;

import com.ugc.EmpMngmntAndTktingSys.model.Priority;
import com.ugc.EmpMngmntAndTktingSys.model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

    private Long ticketId;
    private String title;
    private String ticketDesc;
    private TicketStatus status;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long createdByUserId;
    private Long assignedToUserId;
}