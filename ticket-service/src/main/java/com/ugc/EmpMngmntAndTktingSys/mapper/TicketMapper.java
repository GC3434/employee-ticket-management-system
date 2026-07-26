package com.ugc.EmpMngmntAndTktingSys.mapper;

import com.ugc.EmpMngmntAndTktingSys.DTO.TicketResponse;
import com.ugc.EmpMngmntAndTktingSys.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponse mapToTicketResponse(Ticket ticket) {

        return new TicketResponse(
                ticket.getTicketId(),
                ticket.getTitle(),
                ticket.getTicketDesc(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getCreatedByUserId(),
                ticket.getAssignedToUserId()
        );
    }
}
