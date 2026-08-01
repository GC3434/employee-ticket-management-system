package com.ugc.audit_service.service;

import com.ugc.common.event.TicketCreatedEvent;
import com.ugc.audit_service.model.AuditRecord;
import com.ugc.audit_service.repo.AuditRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepo auditRepo;

    public void saveAudit(TicketCreatedEvent event){
        AuditRecord auditRecord = AuditRecord.builder()
                .ticketId(event.getTicketId())
                .action("CREATED")
                .createdBy(event.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();
        auditRepo.save(auditRecord);
    }
}
