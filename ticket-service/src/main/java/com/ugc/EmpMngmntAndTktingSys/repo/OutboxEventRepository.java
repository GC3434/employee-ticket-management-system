package com.ugc.EmpMngmntAndTktingSys.repo;

import com.ugc.EmpMngmntAndTktingSys.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent,Long> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(String status);
}
