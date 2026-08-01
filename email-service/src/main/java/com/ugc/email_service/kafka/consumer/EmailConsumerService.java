package com.ugc.email_service.kafka.consumer;

import com.ugc.email_service.kafka.KafkaTopics;
import com.ugc.email_service.kafka.event.TicketCreatedEvent;
import com.ugc.email_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailConsumerService {

    private final EmailService emailService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000)
    )
    @KafkaListener(topics = KafkaTopics.TICKET_EVENTS)
    public void consume(TicketCreatedEvent event) {

        System.out.println("Received Event from Kafka...");

        emailService.sendTicketCreatedEmail(event);
    }

    @DltHandler
    public void dltHandler(TicketCreatedEvent event) {

        System.out.println("================================");
        System.out.println("Message reached Dead Letter Topic");
        System.out.println(event);
        System.out.println("================================");
    }
}