package com.ugc.email_service.kafka.event;

import com.ugc.email_service.model.Priority;
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
