package com.ugc.EmpMngmntAndTktingSys.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugc.EmpMngmntAndTktingSys.kafka.producer.KafkaProducerService;
import com.ugc.EmpMngmntAndTktingSys.model.OutboxEvent;
import com.ugc.EmpMngmntAndTktingSys.repo.OutboxEventRepository;
import com.ugc.common.event.TicketCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findByStatusOrderByCreatedAtAsc("PENDING");

        for (OutboxEvent event : events) {

            try {

                TicketCreatedEvent ticketCreatedEvent =
                        objectMapper.readValue(
                                event.getPayload(),
                                TicketCreatedEvent.class
                        );

                kafkaProducerService.publishTicketCreatedEvent(
                        ticketCreatedEvent
                );

                event.setStatus("PUBLISHED");
                event.setPublishedAt(LocalDateTime.now());

                outboxEventRepository.save(event);

                System.out.println(
                        "Outbox event published successfully: "
                                + event.getId()
                );

            } catch (Exception ex) {

                System.out.println(
                        "Failed to publish outbox event: "
                                + event.getId()
                );

                ex.printStackTrace();
            }
        }
    }
}