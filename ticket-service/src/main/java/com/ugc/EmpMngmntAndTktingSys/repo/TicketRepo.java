package com.ugc.EmpMngmntAndTktingSys.repo;

import com.ugc.EmpMngmntAndTktingSys.model.Priority;
import com.ugc.EmpMngmntAndTktingSys.model.Ticket;
import com.ugc.EmpMngmntAndTktingSys.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    List<Ticket> findByAssignedToUserId(Long userId);

    Page<Ticket> findByCreatedByUserId(Long userId, Pageable pageable);

    List<Ticket> findByAssignedToUserIdAndStatus(Long userId, TicketStatus status);

    List<Ticket> findByAssignedToUserIdAndPriority(Long userId, Priority priority);

    List<Ticket> findByCreatedByUserIdAndStatus(Long userId, TicketStatus status);

    List<Ticket> findByCreatedByUserIdAndPriority(Long userId, Priority priority);

    List<Ticket> findByAssignedToUserIdAndStatusIn(Long userId,
                                                   List<TicketStatus> statuses);

    List<Ticket> findByAssignedToUserIdIsNull();

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByAssignedToUserIdIsNullAndStatus(TicketStatus ticketStatus);
}
