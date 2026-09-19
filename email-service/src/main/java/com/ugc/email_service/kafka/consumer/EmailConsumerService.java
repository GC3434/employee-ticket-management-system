package com.ugc.email_service.kafka.consumer;

import com.ugc.common.kafka.KafkaTopics;
import com.ugc.common.event.TicketCreatedEvent;
import com.ugc.email_service.service.EmailService;
import com.ugc.email_service.service.ProcessedEventService;
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
    private final ProcessedEventService processedEventService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000)
    )
    @KafkaListener(topics = KafkaTopics.TICKET_EVENTS)
    public void consume(TicketCreatedEvent event) {

        System.out.println("Received Event from Kafka...");

        //Check if event was already processed.
        if(processedEventService.isAlreadyProcessed(event.getTicketId())){
            System.out.println("Duplicate event detected for ticket " + event.getTicketId()
                    + ". Skipping email.");

            return;
        }
        //Process Event
        emailService.sendTicketCreatedEmail(event);

        //Mark Ticked as Processed
        processedEventService.markAsProcessed(event.getTicketId());
        System.out.println(
                "Email processed successfully for ticket " + event.getTicketId());
    }

    @DltHandler
    public void dltHandler(TicketCreatedEvent event) {

        System.out.println("================================");
        System.out.println("Message reached Dead Letter Topic");
        System.out.println(event);
        System.out.println("================================");
    }
}