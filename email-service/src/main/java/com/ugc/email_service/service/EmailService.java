package com.ugc.email_service.service;

import com.ugc.common.event.TicketCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor
@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendTicketCreatedEmail(TicketCreatedEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("lonely3434r@gmail.com"); //message.setTo(event.getEmail());

        message.setFrom(fromEmail);

        message.setSubject("Ticket Created Successfully");

        message.setText(buildTicketCreatedEmail(event));

        log.info("Sending Email for ticket {}",event.getTicketId());

        try {
            mailSender.send(message);
            log.info("Email Sent Successfully for ticket {}",event.getTicketId());
        }catch (Exception ex){
            log.error("Failed to send email for ticket {}", event.getTicketId(), ex);
            throw ex;
        }
    }

    private String buildTicketCreatedEmail(TicketCreatedEvent event){
        return "Hello,\n\n" +
                "Your ticket has been created successfully.\n\n" +
                "Ticket ID : " + event.getTicketId() + "\n" +
                "Title     : " + event.getTitle() + "\n" +
                "Priority  : " + event.getPriority() + "\n\n" +
                "Regards,\nSupport Team";
    }
}