package com.ugc.EmpMngmntAndTktingSys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugc.EmpMngmntAndTktingSys.DTO.CreateTicketRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.TicketResponse;
import com.ugc.EmpMngmntAndTktingSys.Security.CustomerUserDetailService;
import com.ugc.EmpMngmntAndTktingSys.Security.JwtService;
import com.ugc.EmpMngmntAndTktingSys.mapper.TicketMapper;
import com.ugc.EmpMngmntAndTktingSys.model.Priority;
import com.ugc.EmpMngmntAndTktingSys.model.Ticket;
import com.ugc.EmpMngmntAndTktingSys.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeTicketController.class)   // <-- your controller class
@AutoConfigureMockMvc(addFilters = false)
class EmployeeTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private TicketMapper ticketMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomerUserDetailService customerUserDetailService;

    @Test
    @WithMockUser(username = "abhi", roles = "EMP")
    void shouldCreateTicketSuccessfully() throws Exception {

        // Arrange
        CreateTicketRequest request = new CreateTicketRequest(
                "N/w issue",
                "Internet not working",
                Priority.HIGH
        );

        Ticket ticket = new Ticket();
        TicketResponse response = new TicketResponse();

        when(ticketService.createTicket(eq(request), eq("abhi")))
                .thenReturn(ticket);

        when(ticketMapper.mapToTicketResponse(ticket))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/tickets/employee/create")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify
        verify(ticketService).createTicket(eq(request), eq("abhi"));
        verify(ticketMapper).mapToTicketResponse(ticket);
    }
}