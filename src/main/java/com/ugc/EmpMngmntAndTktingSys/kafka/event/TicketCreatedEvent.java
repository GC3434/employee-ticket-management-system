package com.ugc.EmpMngmntAndTktingSys.kafka.event;

import com.ugc.EmpMngmntAndTktingSys.model.Priority;
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
