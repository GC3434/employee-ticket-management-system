import api from "./api";

export async function getCreatedTickets() {
    const response = await api.get("/tickets/employee/created");
    return response.data;
}

export async function getAssignedTickets() {
    const response = await api.get("/tickets/employee/assigned");
    return response.data;
}

export async function getCreatedTicketsByStatus(status) {
    const response = await api.get(
        `/tickets/employee/created/status/${status}`
    );

    return response.data;
}

export async function getCreatedTicketsByPriority(priority) {
    const response = await api.get(
        `/tickets/employee/created/priority/${priority}`
    );

    return response.data;
}

export async function getTicketById(ticketId) {
    const response = await api.get(
        `/tickets/employee/ticket/${ticketId}`
    );

    return response.data;
}

export async function createTicket(ticket) {
    const response = await api.post(
        "/tickets/employee/create",
        ticket
    );

    return response.data;
}

export async function resolveTicket(ticketId) {
    const response = await api.put(
        `/tickets/employee/resolve/${ticketId}`
    );

    return response.data;
}

export async function getUnassignedTickets() {
    const response = await api.get("/tickets/manager/unassigned");
    return response.data;
}

export async function assignTicket(ticketId, employeeId) {
    const response = await api.put(
        `/tickets/manager/${ticketId}/assign/${employeeId}`
    );

    return response.data;
}

export async function getResolvedTickets() {
    const response = await api.get("/tickets/manager/resolved");
    return response.data;
}

export async function closeTicket(ticketId) {
    const response = await api.post(
        `/tickets/manager/closeticket/${ticketId}`
    );

    return response.data;
}