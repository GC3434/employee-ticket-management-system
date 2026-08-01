package com.ugc.common.event;

import com.ugc.common.model.Priority;
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
