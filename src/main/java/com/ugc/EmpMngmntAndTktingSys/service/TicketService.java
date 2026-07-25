package com.ugc.EmpMngmntAndTktingSys.service;

import com.ugc.EmpMngmntAndTktingSys.DTO.CreateTicketRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.TicketResponse;
import com.ugc.EmpMngmntAndTktingSys.exception.*;
import com.ugc.EmpMngmntAndTktingSys.kafka.event.TicketCreatedEvent;
import com.ugc.EmpMngmntAndTktingSys.kafka.producer.KafkaProducerService;
import com.ugc.EmpMngmntAndTktingSys.mapper.TicketMapper;
import com.ugc.EmpMngmntAndTktingSys.model.*;
import com.ugc.EmpMngmntAndTktingSys.repo.TicketRepo;
import com.ugc.EmpMngmntAndTktingSys.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {
    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    TicketMapper ticketMapper;

    @Autowired
    KafkaProducerService kafkaProducerService;

    private User getUser(String username){
        return userRepo.findByUserName(username)
                .orElseThrow(()-> new UserNotFoundException("User not found!"));
    }

    public TicketResponse createTicket(CreateTicketRequest ticketRequest, String username) {

        Ticket ticket = new Ticket();

        ticket.setTitle(ticketRequest.getTitle());
        ticket.setTicketDesc(ticketRequest.getTicketDesc());
        ticket.setPriority(ticketRequest.getPriority());

        ticket.setCreatedBy(getUser(username));
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignedTo(null);
        Ticket savedTicket = ticketRepo.save(ticket);
        TicketCreatedEvent event = new TicketCreatedEvent(
                savedTicket.getTicketId(),
                savedTicket.getTitle(),
                username,
                savedTicket.getPriority());
        kafkaProducerService.publishTicketCreatedEvent(event);
        return ticketMapper.mapToTicketResponse(savedTicket);
    }


    public List<TicketResponse> getAssignedTickets(String username) {
//        return ticketRepo.findByAssignedTo(getUser(username));
        return  ticketRepo.findByAssignedToAndStatusIn(getUser(username),
                List.of(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED)).stream()
                .map(ticketMapper :: mapToTicketResponse).toList();
    }

    public Page<TicketResponse> getCreatedTickets(String username, Pageable pageable){
        return ticketRepo.findByCreatedBy(getUser(username),pageable)
                .map(ticketMapper::mapToTicketResponse);
    }

    public List<TicketResponse> getCreatedTicketsByStatus(String username, TicketStatus status) {
        return ticketRepo.findByCreatedByAndStatus(getUser(username),status).stream()
                .map(ticketMapper::mapToTicketResponse).toList();
    }

    public List<TicketResponse> getCreatedTicketsByPriority(String username, Priority priority) {
        return ticketRepo.findByCreatedByAndPriority(getUser(username),priority)
                .stream().map(ticketMapper :: mapToTicketResponse).toList();
    }

    public TicketResponse assignTicket(Long ticketId, Long employeeId) {
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(()-> new TicketNotFoundException("Ticket not found!"));
        User user = userRepo.findById(employeeId)
                .orElseThrow(()-> new UserNotFoundException("User not found!"));
        if( ticket.getAssignedTo() != null ){
            throw new TicketAlreadyAssignedException("Ticket is already assigned!");
        }
        if(ticket.getStatus()!=TicketStatus.OPEN){
            throw new InvalidTicketStateException("Only OPEN tickets can be assigned.");
        }
        boolean isEmployee = user.getRoles()
                .stream()
                .anyMatch(role -> role.getRoleName() == RoleType.ROLE_EMP);

        if (!isEmployee) {
            throw new InvalidAssignmentException("Only employees can be assigned.");
        }
//        if(user.getRoles() != RoleType.ROLE_EMP) throw new RuntimeException("Ticket is already assigned.");
        ticket.setAssignedTo(user);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setUpdatedAt(LocalDateTime.now());
        TicketResponse ticketResponse = ticketMapper.mapToTicketResponse(ticketRepo.save(ticket));
        return ticketResponse;
    }

    public List<TicketResponse> getUnassignedTickets() {
//        List<Ticket> unassigned = new ArrayList<>();
//        List<Ticket> tickets = ticketRepo.findAll();
//        for( Ticket ticket : tickets){
//            if(ticket.getAssignedTo()==null){
//                unassigned.add(ticket);
//            }
//        }
        List<TicketResponse> ticketResponse = ticketRepo.findByAssignedToIsNull().stream()
                .map(ticketMapper ::mapToTicketResponse).toList();
        return ticketResponse;
    }

    public TicketResponse resolveTicket(Long tktId, String username) {
        User user = getUser(username);
        Ticket ticket = ticketRepo.findById(tktId)
                .orElseThrow(()-> new TicketNotFoundException("Ticket not found!"));
        if (ticket.getAssignedTo() == null ||
                !ticket.getAssignedTo().equals(user)) {
            throw new UnauthorizedActionException("Access denied!");
        }
        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            throw new InvalidTicketStateException("Only IN_PROGRESS tickets can be resolved.");
        }
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketMapper.mapToTicketResponse(ticketRepo.save(ticket));
    }

    public List<TicketResponse> getResolvedTickets() {
        return ticketRepo.findByStatus(TicketStatus.RESOLVED).stream()
                .map(ticketMapper :: mapToTicketResponse).toList();
    }

    public TicketResponse closeTicket(Long tktId) {
        Ticket tkt = ticketRepo.findById(tktId)
                .orElseThrow(()-> new TicketNotFoundException("Ticket not found!"));
        if (tkt.getStatus() != TicketStatus.RESOLVED) {
            throw new InvalidTicketStateException("Only RESOLVED tickets can be CLOSED.");
        }
        tkt.setStatus(TicketStatus.CLOSED);
        tkt.setUpdatedAt(LocalDateTime.now());
        return ticketMapper.mapToTicketResponse(ticketRepo.save(tkt));
    }

//    @Cacheable(value = "tickets", key = "#tktId")
    public TicketResponse getTicketById(Long tktId) {

//        System.out.println("Fetching from MySQL...");

        Ticket ticket = ticketRepo.findById(tktId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return ticketMapper.mapToTicketResponse(ticket);
    }
}
