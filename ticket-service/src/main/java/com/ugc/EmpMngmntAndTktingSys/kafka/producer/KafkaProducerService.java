package com.ugc.EmpMngmntAndTktingSys.kafka.producer;

import com.ugc.common.event.TicketCreatedEvent;
import com.ugc.common.kafka.KafkaTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, TicketCreatedEvent> kafkaTemplate;


    public void publishTicketCreatedEvent(TicketCreatedEvent event) {

        kafkaTemplate.send(KafkaTopics.TICKET_EVENTS, event);

        System.out.println("Published Event : " + event);
    }
}