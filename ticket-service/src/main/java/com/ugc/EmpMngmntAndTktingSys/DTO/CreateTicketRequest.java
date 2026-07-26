package com.ugc.EmpMngmntAndTktingSys.DTO;

import com.ugc.EmpMngmntAndTktingSys.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Ticket description is required")
    private String ticketDesc;

    @NotNull(message = "Priority is required")
    private Priority priority;
}