package com.ugc.audit_service.repo;

import com.ugc.audit_service.model.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepo extends JpaRepository<AuditRecord,Integer> {

}
