package com.ugc.EmpMngmntAndTktingSys.service;

import com.ugc.EmpMngmntAndTktingSys.DTO.CreateTicketRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.TicketResponse;
import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import com.ugc.EmpMngmntAndTktingSys.feign.UserClient;
import com.ugc.EmpMngmntAndTktingSys.exception.*;
import com.ugc.common.event.TicketCreatedEvent;
import com.ugc.EmpMngmntAndTktingSys.kafka.producer.KafkaProducerService;
import com.ugc.EmpMngmntAndTktingSys.mapper.TicketMapper;
import com.ugc.EmpMngmntAndTktingSys.model.*;
import com.ugc.EmpMngmntAndTktingSys.repo.TicketRepo;
import com.ugc.common.model.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepo ticketRepo;
    private final TicketMapper ticketMapper;
    private  final UserClient userClient;
    private final KafkaProducerService kafkaProducerService;

    public TicketResponse createTicket(CreateTicketRequest ticketRequest, String username) {

        log.info("Creating ticket for user {}", username);

        UserResponse user = userClient.getUserByUsername(username);

        Ticket ticket = new Ticket();
        ticket.setTitle(ticketRequest.getTitle());
        ticket.setTicketDesc(ticketRequest.getTicketDesc());
        ticket.setPriority(ticketRequest.getPriority());
        ticket.setCreatedByUserId(user.getUserId());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignedToUserId(null);

        Ticket savedTicket = ticketRepo.save(ticket);

        log.info("Ticket {} created successfully by user {}",
                savedTicket.getTicketId(),
                username);

        TicketCreatedEvent event = new TicketCreatedEvent(
                savedTicket.getTicketId(),
                savedTicket.getTitle(),
                username,
                savedTicket.getPriority());

        kafkaProducerService.publishTicketCreatedEvent(event);

        log.info("TicketCreatedEvent published for ticket {}",
                savedTicket.getTicketId());

        return ticketMapper.mapToTicketResponse(savedTicket);
    }

    public List<TicketResponse> getAssignedTickets(String userName) {

        log.info("Fetching assigned tickets for user {}", userName);

        UserResponse user = userClient.getUserByUsername(userName);

        List<TicketResponse> tickets = ticketRepo
                .findByAssignedToUserIdAndStatusIn(
                        user.getUserId(),
                        List.of(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED))
                .stream()
                .map(ticketMapper::mapToTicketResponse)
                .toList();

        log.info("Found {} assigned tickets for user {}",
                tickets.size(),
                userName);

        return tickets;
    }

    public Page<TicketResponse> getCreatedTickets(String userName,
                                                  Pageable pageable) {

        log.info("Fetching created tickets for user {}", userName);

        UserResponse user = userClient.getUserByUsername(userName);

        Page<TicketResponse> tickets = ticketRepo
                .findByCreatedByUserId(user.getUserId(), pageable)
                .map(ticketMapper::mapToTicketResponse);

        log.info("Fetched page {} containing {} tickets for user {}",
                pageable.getPageNumber(),
                tickets.getNumberOfElements(),
                userName);

        return tickets;
    }

    public List<TicketResponse> getCreatedTicketsByStatus(String userName,
                                                          TicketStatus status) {

        log.info("Fetching {} tickets created by {}",
                status,
                userName);

        UserResponse user = userClient.getUserByUsername(userName);

        List<TicketResponse> tickets = ticketRepo
                .findByCreatedByUserIdAndStatus(user.getUserId(), status)
                .stream()
                .map(ticketMapper::mapToTicketResponse)
                .toList();

        log.info("Found {} {} tickets",
                tickets.size(),
                status);

        return tickets;
    }

    public List<TicketResponse> getCreatedTicketsByPriority(String userName,
                                                            Priority priority) {

        log.info("Fetching {} priority tickets created by {}",
                priority,
                userName);

        UserResponse user = userClient.getUserByUsername(userName);

        List<TicketResponse> tickets = ticketRepo
                .findByCreatedByUserIdAndPriority(user.getUserId(), priority)
                .stream()
                .map(ticketMapper::mapToTicketResponse)
                .toList();

        log.info("Found {} {} priority tickets",
                tickets.size(),
                priority);

        return tickets;
    }

    public TicketResponse assignTicket(Long ticketId, Long employeeId) {

        log.info("Assigning ticket {} to employee {}", ticketId, employeeId);

        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> {
                    log.error("Ticket {} not found", ticketId);
                    return new TicketNotFoundException("Ticket not found!");
                });

        if (ticket.getAssignedToUserId() != null) {
            log.warn("Ticket {} is already assigned to employee {}",
                    ticketId,
                    ticket.getAssignedToUserId());

            throw new TicketAlreadyAssignedException(
                    "Ticket is already assigned!");
        }

        if (ticket.getStatus() != TicketStatus.OPEN) {

            log.warn("Attempt to assign ticket {} with status {}",
                    ticketId,
                    ticket.getStatus());

            throw new InvalidTicketStateException(
                    "Only OPEN tickets can be assigned.");
        }

        UserResponse employee = userClient.getUserById(employeeId);

        if (!employee.getRoles().contains("ROLE_EMP")) {

            log.warn("User {} is not an employee. Roles: {}",
                    employeeId,
                    employee.getRoles());

            throw new UnauthorizedActionException(
                    "User is not an employee.");
        }

        ticket.setAssignedToUserId(employeeId);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketRepo.save(ticket);

        log.info("Ticket {} assigned successfully to employee {}",
                ticketId,
                employeeId);

        return ticketMapper.mapToTicketResponse(updatedTicket);
    }

    public List<TicketResponse> getUnassignedTickets() {

        log.info("Fetching unassigned OPEN tickets");

        List<TicketResponse> tickets = ticketRepo
                .findByAssignedToUserIdIsNullAndStatus(TicketStatus.OPEN)
                .stream()
                .map(ticketMapper::mapToTicketResponse)
                .toList();

        log.info("Found {} unassigned OPEN tickets",
                tickets.size());

        return tickets;
    }

    public TicketResponse resolveTicket(Long ticketId, String userName) {

        log.info("User {} is attempting to resolve ticket {}", userName, ticketId);

        UserResponse user = userClient.getUserByUsername(userName);
        Long employeeId = user.getUserId();

        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> {
                    log.error("Ticket {} not found", ticketId);
                    return new TicketNotFoundException("Ticket not found!");
                });

        if (ticket.getAssignedToUserId() == null ||
                !ticket.getAssignedToUserId().equals(employeeId)) {

            log.warn("User {} is not assigned to ticket {}",
                    userName,
                    ticketId);

            throw new UnauthorizedActionException("Access denied!");
        }

        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {

            log.warn("Attempt to resolve ticket {} with status {}",
                    ticketId,
                    ticket.getStatus());

            throw new InvalidTicketStateException(
                    "Only IN_PROGRESS tickets can be resolved.");
        }

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketRepo.save(ticket);

        log.info("Ticket {} resolved successfully by user {}",
                ticketId,
                userName);

        return ticketMapper.mapToTicketResponse(updatedTicket);
    }

    public List<TicketResponse> getResolvedTickets() {

        log.info("Fetching resolved tickets");

        List<TicketResponse> tickets = ticketRepo
                .findByStatus(TicketStatus.RESOLVED)
                .stream()
                .map(ticketMapper::mapToTicketResponse)
                .toList();

        log.info("Found {} resolved tickets",
                tickets.size());

        return tickets;
    }

    public TicketResponse closeTicket(Long ticketId) {

        log.info("Attempting to close ticket {}", ticketId);

        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> {
                    log.error("Ticket {} not found", ticketId);
                    return new TicketNotFoundException("Ticket not found!");
                });

        if (ticket.getStatus() != TicketStatus.RESOLVED) {

            log.warn("Attempt to close ticket {} with status {}",
                    ticketId,
                    ticket.getStatus());

            throw new InvalidTicketStateException(
                    "Only RESOLVED tickets can be CLOSED.");
        }

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketRepo.save(ticket);

        log.info("Ticket {} closed successfully", ticketId);

        return ticketMapper.mapToTicketResponse(updatedTicket);
    }

//    @Cacheable(value = "tickets", key = "#tktId")
    public TicketResponse getTicketById(Long ticketId) {

        log.info("Fetching ticket {}", ticketId);

        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> {
                    log.error("Ticket {} not found", ticketId);
                    return new TicketNotFoundException("Ticket not found!");
                });

        log.info("Ticket {} fetched successfully", ticketId);

        return ticketMapper.mapToTicketResponse(ticket);
    }
}
