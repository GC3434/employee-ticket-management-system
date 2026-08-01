package com.ugc.audit_service.kafka.event;

import com.ugc.audit_service.model.Priority;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketCreatedEvent {
    private Long ticketId;
    private String title;
    private String createdBy;
    private Priority priority;
}
