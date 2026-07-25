package com.ugc.EmpMngmntAndTktingSys.controller;

import com.ugc.EmpMngmntAndTktingSys.DTO.TicketResponse;
import com.ugc.EmpMngmntAndTktingSys.mapper.TicketMapper;
import com.ugc.EmpMngmntAndTktingSys.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets/manager")
public class ManagerTicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketMapper ticketMapper;

    @GetMapping("/unassigned")
    public ResponseEntity<List<TicketResponse>> getUnassignedTickets(){
        return ResponseEntity.ok(ticketService.getUnassignedTickets());
    }

    @PutMapping("/{ticketId}/assign/{employeeId}")
    public ResponseEntity<TicketResponse> assignTicket(@PathVariable Long ticketId, @PathVariable Long employeeId){
        return ResponseEntity.ok(ticketService.assignTicket(ticketId,employeeId));
    }

    @GetMapping("/resolved")
    public ResponseEntity<List<TicketResponse>> getResolvedTickets(){
        return ResponseEntity.ok(ticketService.getResolvedTickets());
    }

    @PostMapping("/closeticket/{tktId}")
    public ResponseEntity<TicketResponse> closeTicket(@PathVariable Long tktId){
        return ResponseEntity.ok(ticketService.closeTicket(tktId));
    }

}
