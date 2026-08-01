package com.ugc.audit_service.kafka.consumer;

import com.ugc.common.kafka.KafkaTopics;
import com.ugc.common.event.TicketCreatedEvent;
import com.ugc.audit_service.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(AuditConsumer.class);
    private final AuditService auditService;

    @RetryableTopic(attempts = "3",
                     backOff = @BackOff(delay = 2000))
    @KafkaListener(topics = KafkaTopics.TICKET_EVENTS)
    public void consume(TicketCreatedEvent event){
        System.out.println(">>> AUDIT CONSUMER INVOKED <<<");
        auditService.saveAudit(event);
        log.info("Audit record saved for ticket {}", event.getTicketId());
    }

    @DltHandler
    public void dltHandler(TicketCreatedEvent event){
        System.out.println("================================");
        System.out.println("Audit Event reached DLT");
        System.out.println(event);
        System.out.println("================================");
    }
}
