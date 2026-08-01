package com.ugc.EmpMngmntAndTktingSys.controller;

import com.ugc.common.event.TicketCreatedEvent;
import com.ugc.EmpMngmntAndTktingSys.kafka.producer.KafkaProducerService;
import com.ugc.common.model.Priority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;

    public KafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/kafka/send")
    public String sendMessage() {

        TicketCreatedEvent event =
                new TicketCreatedEvent(
                        1L,
                        "Login Issue",
                        "Abhi",
                        Priority.HIGH
                );

        kafkaProducerService.publishTicketCreatedEvent(event);

        return "Event Published";

    }
}