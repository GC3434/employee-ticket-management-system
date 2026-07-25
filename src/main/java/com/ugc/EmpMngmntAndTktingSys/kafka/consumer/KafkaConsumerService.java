package com.ugc.EmpMngmntAndTktingSys.kafka.consumer;

import com.ugc.EmpMngmntAndTktingSys.kafka.KafkaTopics;
import com.ugc.EmpMngmntAndTktingSys.kafka.event.TicketCreatedEvent;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000)
    )
    @KafkaListener(
            topics = KafkaTopics.TICKET_EVENTS,
            groupId = "ticket-group"
    )
    public void consume(TicketCreatedEvent event) {

        System.out.println("==============================");
        System.out.println("Ticket Created Event Received");
        //throw new RuntimeException("Email Service Down");
        System.out.println("Ticket Id : " + event.getTicketId());
        System.out.println("Title     : " + event.getTitle());
        System.out.println("CreatedBy : " + event.getCreatedBy());
        System.out.println("Priority  : " + event.getPriority());
        System.out.println("==============================");
    }

    @DltHandler
    public void dltHandler(TicketCreatedEvent event) {

        System.out.println("================================");
        System.out.println("Message reached DLT");
        System.out.println(event);
        System.out.println("================================");

    }
}