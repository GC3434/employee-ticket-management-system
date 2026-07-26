package com.ugc.EmpMngmntAndTktingSys.kafka.consumer;

import com.ugc.EmpMngmntAndTktingSys.kafka.KafkaTopics;
import com.ugc.EmpMngmntAndTktingSys.kafka.event.TicketCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuditConsumerService {
    @KafkaListener(topics = KafkaTopics.TICKET_EVENTS,
                    groupId = "audit-group")
    public void audit(TicketCreatedEvent event){
        System.out.println("========== AUDIT ==========");
        System.out.println("Audit Saved : " + event);
        System.out.println("===========================");
    }
}
