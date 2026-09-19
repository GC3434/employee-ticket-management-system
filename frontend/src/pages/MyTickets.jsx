import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    getCreatedTickets,
    getCreatedTicketsByStatus,
    getCreatedTicketsByPriority,
} from "../services/ticketService";

function MyTickets() {
    const navigate = useNavigate();

    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [statusFilter, setStatusFilter] = useState("ALL");
    const [priorityFilter, setPriorityFilter] = useState("ALL");

    useEffect(() => {
        loadTickets();
    }, [statusFilter, priorityFilter]);

    const loadTickets = async () => {
        try {
            setLoading(true);
            setError("");

            let data;

            if (statusFilter !== "ALL") {
                data = await getCreatedTicketsByStatus(statusFilter);
            } else if (priorityFilter !== "ALL") {
                data = await getCreatedTicketsByPriority(priorityFilter);
            } else {
                data = await getCreatedTickets();
            }

            // /created returns a Spring Page
            // status/priority endpoints return Lists
            setTickets(data.content || data || []);

        } catch (error) {
            console.error("Failed to load tickets:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            setError("Unable to load tickets.");
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (date) => {
        if (!date) return "-";

        return new Date(date).toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
        });
    };

    const getStatusClass = (status) => {
        switch (status) {
            case "OPEN":
                return "status-open";
            case "IN_PROGRESS":
                return "status-progress";
            case "RESOLVED":
                return "status-resolved";
            case "CLOSED":
                return "status-closed";
            default:
                return "";
        }
    };

    const getPriorityClass = (priority) => {
        switch (priority) {
            case "HIGH":
                return "priority-high";
            case "MEDIUM":
                return "priority-medium";
            case "LOW":
                return "priority-low";
            default:
                return "";
        }
    };

    return (
        <div className="page-container">

            <div className="page-header">

                <div>
                    <h1>My Tickets</h1>
                    <p>View and manage tickets created by you</p>
                </div>

                <button
                    className="primary-button"
                    onClick={() => navigate("/tickets/create")}
                >
                    + Create Ticket
                </button>

            </div>

            {/* Filters */}

            <div className="ticket-filters">

                <div className="filter-group">
                    <label>Status</label>

                    <select
                        value={statusFilter}
                        onChange={(event) => {
                            setStatusFilter(event.target.value);

                            if (event.target.value !== "ALL") {
                                setPriorityFilter("ALL");
                            }
                        }}
                    >
                        <option value="ALL">All Statuses</option>
                        <option value="OPEN">Open</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="RESOLVED">Resolved</option>
                        <option value="CLOSED">Closed</option>
                    </select>
                </div>

                <div className="filter-group">
                    <label>Priority</label>

                    <select
                        value={priorityFilter}
                        onChange={(event) => {
                            setPriorityFilter(event.target.value);

                            if (event.target.value !== "ALL") {
                                setStatusFilter("ALL");
                            }
                        }}
                    >
                        <option value="ALL">All Priorities</option>
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                    </select>
                </div>

            </div>

            {/* Content */}

            {loading ? (
                <div className="loading-page">
                    <div className="loading-spinner"></div>
                    <p>Loading tickets...</p>
                </div>
            ) : error ? (
                <div className="dashboard-error">
                    {error}
                </div>
            ) : tickets.length === 0 ? (
                <div className="empty-state">
                    <h3>No tickets found</h3>
                    <p>
                        There are no tickets matching the selected filters.
                    </p>
                </div>
            ) : (
                <div className="tickets-table-card">

                    <table className="tickets-table">

                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th></th>
                        </tr>
                        </thead>

                        <tbody>

                        {tickets.map((ticket) => (

                            <tr key={ticket.ticketId}>

                                <td>
                                    #{ticket.ticketId}
                                </td>

                                <td>
                                    <div className="ticket-title">
                                        {ticket.title}
                                    </div>

                                    <div className="ticket-description-preview">
                                        {ticket.ticketDesc}
                                    </div>
                                </td>

                                <td>
                    <span
                        className={`priority-badge ${getPriorityClass(
                            ticket.priority
                        )}`}
                    >
                      {ticket.priority}
                    </span>
                                </td>

                                <td>
                    <span
                        className={`status-badge ${getStatusClass(
                            ticket.status
                        )}`}
                    >
                      {ticket.status?.replace("_", " ")}
                    </span>
                                </td>

                                <td>
                                    {formatDate(ticket.createdAt)}
                                </td>

                                <td>
                                    <button
                                        className="view-button"
                                        onClick={() =>
                                            navigate(`/tickets/${ticket.ticketId}`)
                                        }
                                    >
                                        View
                                    </button>
                                </td>

                            </tr>

                        ))}

                        </tbody>

                    </table>

                </div>
            )}

        </div>
    );
}

export default MyTickets;