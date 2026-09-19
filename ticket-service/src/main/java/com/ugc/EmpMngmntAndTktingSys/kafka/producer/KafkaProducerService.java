package com.ugc.EmpMngmntAndTktingSys.kafka.producer;

import com.ugc.common.event.TicketCreatedEvent;
import com.ugc.common.kafka.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, TicketCreatedEvent> kafkaTemplate;

    public void publishTicketCreatedEvent(TicketCreatedEvent event) {

        kafkaTemplate.send(KafkaTopics.TICKET_EVENTS, event);

        log.info("Published Event: {} ", event);
    }
}