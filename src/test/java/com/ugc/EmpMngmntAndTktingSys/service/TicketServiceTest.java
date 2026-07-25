package com.ugc.EmpMngmntAndTktingSys.service;

import com.ugc.EmpMngmntAndTktingSys.DTO.CreateTicketRequest;
import com.ugc.EmpMngmntAndTktingSys.exception.*;
import com.ugc.EmpMngmntAndTktingSys.model.*;
import com.ugc.EmpMngmntAndTktingSys.repo.TicketRepo;
import com.ugc.EmpMngmntAndTktingSys.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @InjectMocks
    TicketService ticketService;

    @Mock
    TicketRepo ticketRepo;

    @Mock
    UserRepo userRepo;

    //createTicket()
    @Test
    void shouldCreateTicket(){
        //Arrange
        CreateTicketRequest ticketRequest = new CreateTicketRequest(
                "N/w issue",
                "Internet not working",
                Priority.HIGH
        );

        User existingUser = new User(1L,"abhisk","abhi@gmail.com","3434","IT", Set.of(new Role(1L, RoleType.ROLE_EMP)));

        when(userRepo.findByUserName(existingUser.getUserName()))
                .thenReturn(Optional.of(existingUser));
        when(ticketRepo.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Ticket ticket = ticketService.createTicket(ticketRequest,existingUser.getUserName());

        //Assert
        assertEquals(ticketRequest.getTitle(),ticket.getTitle());
        assertEquals(ticketRequest.getTicketDesc(),ticket.getTicketDesc());
        assertEquals(ticketRequest.getPriority(),ticket.getPriority());
        assertEquals(TicketStatus.OPEN,ticket.getStatus());
        assertEquals(existingUser,ticket.getCreatedBy());
        assertNotNull(ticket.getCreatedAt());
        assertNotNull(ticket.getUpdatedAt());
        assertNull(ticket.getAssignedTo());

        //Verify
        verify(ticketRepo).save(any(Ticket.class));
        verify(userRepo).findByUserName(existingUser.getUserName());
    }
    @Test
    void shouldThrowUserNotFoundException(){
        //Arrange
        CreateTicketRequest ticketRequest = new CreateTicketRequest(
                "N/w issue",
                "Internet not working",
                Priority.HIGH
        );
        User existingUser = new User(1L,"abhisk","abhi@gmail.com","3434","IT", Set.of(new Role(1L,RoleType.ROLE_EMP)));

        when(userRepo.findByUserName(existingUser.getUserName()))
                .thenReturn(Optional.empty());

        //Act+Assert
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                ()->ticketService.createTicket(ticketRequest,existingUser.getUserName()));
        assertEquals("User not found!",ex.getMessage());

        //Verify
        verify(userRepo).findByUserName(existingUser.getUserName());
        verify(ticketRepo,never()).save(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepo, userRepo);
    }

    //assignTicket()
    @Test
    void shouldThrowTicketNotFoundException(){

        //Arrange
        Long ticketId = 1L;
        Long empId = 1L;

        when(ticketRepo.findById(ticketId))
                .thenReturn(Optional.empty());

        //Act+Assert
        TicketNotFoundException ex = assertThrows(TicketNotFoundException.class,
                                    ()->ticketService.assignTicket(ticketId,empId));
        assertEquals("Ticket not found!",ex.getMessage());

        //Verify
        verify(ticketRepo).findById(ticketId);
        verify(ticketRepo,never()).save(any(Ticket.class));
        verify(userRepo,never()).findById(empId);
        verifyNoMoreInteractions(ticketRepo, userRepo);
    }
    @Test
    void shouldThrowUserNotFoundExceptionAssignTkt(){
        //Arrange
        Long tktId = 1L;
        Ticket ticket = new Ticket();
        ticket.setTicketId(tktId);
        Long empId =1L;

        when(ticketRepo.findById(tktId))
                .thenReturn(Optional.of(ticket));
        when(userRepo.findById(empId))
                .thenReturn(Optional.empty());

        //Act+Assert
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                ()->ticketService.assignTicket(tktId,empId));
        assertEquals("User not found!",ex.getMessage());

        //Verify
        verify(ticketRepo).findById(tktId);
        verify(userRepo).findById(empId);
        verify(ticketRepo,never()).save(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepo, userRepo);
    }
    @Test
    void shouldAssignTicketSuccessfully(){
        //Arrange
        User existingUser = new User(1L,"abhisk","abhi@gmail.com","3434","IT",Set.of(new Role(1L,RoleType.ROLE_EMP)) );
        Long tktId = 2L;
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId(tktId);
        ticket1.setStatus(TicketStatus.OPEN);

        when(ticketRepo.findById(tktId))
                .thenReturn(Optional.of(ticket1));
        when(userRepo.findById(existingUser.getUserId()))
                .thenReturn(Optional.of(existingUser));
        when(ticketRepo.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Ticket assignedTkt = ticketService.assignTicket(tktId,existingUser.getUserId());

        //Assert
        assertEquals(existingUser,assignedTkt.getAssignedTo());
        assertEquals(TicketStatus.IN_PROGRESS,assignedTkt.getStatus());
        assertNotNull(assignedTkt.getUpdatedAt());

        //Verify
        verify(ticketRepo).findById(tktId);
        verify(userRepo).findById(existingUser.getUserId());
        verify(ticketRepo).save(any(Ticket.class));

    }
    @Test
    void shouldThrowTicketAlreadyAssignedException(){
        //Arrange
        Long tktId = 1L;
        User existingUser = new User(1L,"abhisk","abhi@gmail.com","3434","IT", Set.of(new Role(1L,RoleType.ROLE_EMP)));
        Ticket ticket = new Ticket();
        ticket.setTicketId(tktId);
        ticket.setAssignedTo(existingUser);

        when(ticketRepo.findById(tktId)).thenReturn(Optional.of(ticket));
        when(userRepo.findById(existingUser.getUserId())).thenReturn(Optional.of(existingUser));

        //Act+Assert
        TicketAlreadyAssignedException ex = assertThrows(TicketAlreadyAssignedException.class,
                ()-> ticketService.assignTicket(tktId,existingUser.getUserId()));
        assertEquals("Ticket is already assigned!",ex.getMessage());

        //Verify
        verify(ticketRepo).findById(tktId);
        verify(userRepo).findById(existingUser.getUserId());
        verify(ticketRepo,never()).save(any(Ticket.class));
    }
    @Test
    void shouldThrowInvalidTicketStateException(){
        //Arrange
        Long tktId = 1L;
        User existingUser = new User(1L,"abhisk","abhi@gmail.com","3434","IT", Set.of(new Role(1L,RoleType.ROLE_EMP)));
        Ticket ticket = new Ticket();
        ticket.setTicketId(tktId);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        when(ticketRepo.findById(tktId)).thenReturn(Optional.of(ticket));
        when(userRepo.findById(existingUser.getUserId())).thenReturn(Optional.of(existingUser));

        //Act+Assert
        InvalidTicketStateException ex = assertThrows(InvalidTicketStateException.class,
                ()-> ticketService.assignTicket(tktId,existingUser.getUserId()));
        assertEquals("Only OPEN tickets can be assigned.",ex.getMessage());

        //Verify
        verify(ticketRepo).findById(tktId);
        verify(userRepo).findById(existingUser.getUserId());
        verify(ticketRepo,never()).save(any(Ticket.class));
    }


    //resolveTicket()
    @Test
    void shouldResolveTicketSuccessfully(){
        //Arrange
        User existingUser = new User(1L,"abhisk","abhi@gmail.com","3434","IT", Set.of(new Role(1L,RoleType.ROLE_EMP)));
        Long tktId = 1L;
        Ticket ticket = new Ticket();
        ticket.setTicketId(tktId);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setAssignedTo(existingUser);


        when(ticketRepo.findById(tktId)).thenReturn(Optional.of(ticket));
        when(userRepo.findByUserName(existingUser.getUserName())).thenReturn(Optional.of(existingUser));
        when(ticketRepo.save(any(Ticket.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        //Act
        Ticket resolvedTkt = ticketService.resolveTicket(ticket.getTicketId(),existingUser.getUserName());

        //Assert
        assertEquals(TicketStatus.RESOLVED,resolvedTkt.getStatus());
        assertNotNull(resolvedTkt.getUpdatedAt());
        assertEquals(existingUser, resolvedTkt.getAssignedTo());

        //verify
        verify(userRepo).findByUserName(existingUser.getUserName());
        verify(ticketRepo).findById(tktId);
        verify(ticketRepo).save(any(Ticket.class));
    }

    //closeTicket
    @Test
    void shouldCloseTicketSuccessfully(){
        //Arrange
        Long tktId = 1L;
        Ticket ticket = new Ticket();
        ticket.setTicketId(tktId);
        ticket.setStatus(TicketStatus.RESOLVED);

        when(ticketRepo.findById(tktId)).thenReturn(Optional.of(ticket));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Ticket closedTkt = ticketService.closeTicket(tktId);

        //Assert
        assertEquals(TicketStatus.CLOSED,closedTkt.getStatus());
        assertNotNull(closedTkt.getUpdatedAt());

        //Verify
        verify(ticketRepo).findById(ticket.getTicketId());
        verify(ticketRepo).save(any(Ticket.class));

    }
}
