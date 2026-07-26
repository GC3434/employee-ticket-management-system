package com.ugc.EmpMngmntAndTktingSys.controller;

import com.ugc.EmpMngmntAndTktingSys.DTO.CreateTicketRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.TicketResponse;
import com.ugc.EmpMngmntAndTktingSys.mapper.TicketMapper;
import com.ugc.EmpMngmntAndTktingSys.model.Priority;
import com.ugc.EmpMngmntAndTktingSys.model.TicketStatus;
import com.ugc.EmpMngmntAndTktingSys.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tickets/employee")
public class EmployeeTicketController {

    private final TicketService ticketService;

    @GetMapping("/created/priority/{priority}")
    public ResponseEntity<List<TicketResponse>> getCreatedTicketsByPriority(Authentication authentication, @PathVariable Priority priority){
        String username = authentication.getName();
        return ResponseEntity.ok(ticketService.getCreatedTicketsByPriority(username,priority));
    }

    @GetMapping("/ticket/{tktId}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long tktId) {
        return ResponseEntity.ok(ticketService.getTicketById(tktId));
    }

    @PutMapping("/resolve/{tktId}")
    public ResponseEntity<TicketResponse> resolveTicket(@PathVariable Long tktId,Authentication authentication){
        return ResponseEntity.ok(ticketService.resolveTicket(tktId,authentication.getName()));
    }

    @PostMapping("/create")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest createTicketRequest, Authentication authentication){
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(createTicketRequest,authentication.getName()));
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TicketResponse>> getAssignedTickets(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(ticketService.getAssignedTickets(username));
    }

    @GetMapping("/created")
    public ResponseEntity<Page<TicketResponse>> getCreatedTickets(Authentication authentication, Pageable pageable){
        String username = authentication.getName();
        return ResponseEntity.ok(ticketService.getCreatedTickets(username,pageable));
    }

    @GetMapping("/created/status/{status}")
    public ResponseEntity<List<TicketResponse>> getCreatedTicketsByStatus(Authentication authentication,@PathVariable TicketStatus status){
        String username = authentication.getName();
        return ResponseEntity.ok(ticketService.getCreatedTicketsByStatus(username,status));

    }

}
