package com.ugc.EmpMngmntAndTktingSys.repo;

import com.ugc.EmpMngmntAndTktingSys.model.Priority;
import com.ugc.EmpMngmntAndTktingSys.model.Ticket;
import com.ugc.EmpMngmntAndTktingSys.model.TicketStatus;
import com.ugc.EmpMngmntAndTktingSys.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    List<Ticket> findByAssignedTo(User user);

    Page<Ticket> findByCreatedBy(User user, Pageable pageable);

    List<Ticket> findByAssignedToAndStatus(User user, TicketStatus status);

    List<Ticket> findByAssignedToAndPriority(User user, Priority priority);

    List<Ticket> findByCreatedByAndStatus(User user, TicketStatus status);

    List<Ticket> findByCreatedByAndPriority(User user, Priority priority);

    List<Ticket> findByAssignedToIsNull();

    List<Ticket> findByStatus(TicketStatus ticketStatus);

    List<Ticket> findByAssignedToAndStatusIn(User user, List<TicketStatus> open);
}
