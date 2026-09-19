import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    getUnassignedTickets,
    assignTicket,
    getResolvedTickets,
    closeTicket,
} from "../services/ticketService";
import { getEmployees, getUserByUsername } from "../services/userService";
import { getCurrentUsername } from "../services/authService";
import Sidebar from "../components/Sidebar";

function ManagerDashboard() {
    const navigate = useNavigate();

    const [user, setUser] = useState(null);
    const [tickets, setTickets] = useState([]);
    const [resolvedTickets, setResolvedTickets] = useState([]);
    const [employees, setEmployees] = useState([]);

    const [loading, setLoading] = useState(true);
    const [resolvedLoading, setResolvedLoading] = useState(true);

    const [error, setError] = useState("");

    const [selectedTicket, setSelectedTicket] = useState(null);
    const [employeeId, setEmployeeId] = useState("");
    const [assigning, setAssigning] = useState(false);
    const [closingTicketId, setClosingTicketId] = useState(null);

    useEffect(() => {
        loadManager();
        loadTickets();
        loadResolvedTickets();
        loadEmployees();
    }, []);

    const loadManager = async () => {
        try {
            const username = getCurrentUsername();

            if (!username) {
                navigate("/login");
                return;
            }

            const userData = await getUserByUsername(username);
            setUser(userData);

        } catch (error) {
            console.error("Failed to load manager:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
            }
        }
    };

    const loadTickets = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await getUnassignedTickets();
            setTickets(data || []);

        } catch (error) {
            console.error("Failed to load unassigned tickets:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            if (error.response?.status === 403) {
                setError(
                    "Access denied. Only managers and administrators can access this page."
                );
                return;
            }

            setError("Unable to load unassigned tickets.");

        } finally {
            setLoading(false);
        }
    };

    const loadResolvedTickets = async () => {
        try {
            setResolvedLoading(true);

            const data = await getResolvedTickets();
            setResolvedTickets(data || []);

        } catch (error) {
            console.error("Failed to load resolved tickets:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            if (error.response?.status === 403) {
                setError(
                    "You don't have permission to view resolved tickets."
                );
                return;
            }

            setError("Unable to load resolved tickets.");

        } finally {
            setResolvedLoading(false);
        }
    };

    const loadEmployees = async () => {
        try {
            const data = await getEmployees();
            setEmployees(data || []);
        } catch (error) {
            console.error("Failed to load employees:", error);
        }
    };

    const handleAssign = async () => {
        if (!employeeId.trim()) {
            return;
        }

        try {
            setAssigning(true);
            setError("");

            await assignTicket(
                selectedTicket.ticketId,
                employeeId.trim()
            );

            setSelectedTicket(null);
            setEmployeeId("");

            await loadTickets();

        } catch (error) {
            console.error("Failed to assign ticket:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            setError(
                error.response?.data?.message ||
                "Unable to assign ticket."
            );

        } finally {
            setAssigning(false);
        }
    };

    const handleCloseTicket = async (ticketId) => {
        const confirmed = window.confirm(
            "Are you sure you want to close this ticket?"
        );

        if (!confirmed) {
            return;
        }

        try {
            setClosingTicketId(ticketId);
            setError("");

            await closeTicket(ticketId);
            await loadResolvedTickets();

        } catch (error) {
            console.error("Failed to close ticket:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            setError(
                error.response?.data?.message ||
                "Unable to close ticket."
            );

        } finally {
            setClosingTicketId(null);
        }
    };

    const formatDate = (date) => {
        if (!date) {
            return "-";
        }

        return new Date(date).toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
        });
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
        <div className="app-layout">

            {/* SIDEBAR */}
            <Sidebar user={user} />

            {/* MAIN CONTENT */}
            <main className="main-content">

                {/* TOP BAR */}
                <header className="topbar">

                    <div>
                        <h2>Manager Dashboard</h2>

                        <p>
                            Manage, assign and close employee tickets.
                        </p>
                    </div>

                    <div className="user-profile">

                        <div className="avatar">
                            {user?.userName
                                ?.charAt(0)
                                .toUpperCase()}
                        </div>

                        <div>
                            <strong>
                                {user?.userName}
                            </strong>

                            <small>
                                {user?.roles?.join(", ") || "MANAGER"}
                            </small>
                        </div>

                    </div>

                </header>


                {/* ERROR */}
                {error && (
                    <div className="dashboard-error">
                        {error}
                    </div>
                )}


                {/* UNASSIGNED */}
                <div className="manager-section-header">

                    <div>
                        <h2>Unassigned Tickets</h2>

                        <p>
                            {tickets.length} ticket
                            {tickets.length !== 1 ? "s" : ""}
                            {" "}waiting for assignment
                        </p>
                    </div>

                </div>


                {loading ? (

                    <div className="loading-page">
                        <div className="loading-spinner"></div>
                        <p>Loading unassigned tickets...</p>
                    </div>

                ) : tickets.length === 0 ? (

                    <div className="empty-state">
                        <h3>No unassigned tickets</h3>
                        <p>
                            All tickets have currently been assigned.
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
                                <th>Created</th>
                                <th>Action</th>
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
                                        {formatDate(ticket.createdAt)}
                                    </td>

                                    <td>
                                        <button
                                            className="primary-button"
                                            onClick={() => {
                                                setSelectedTicket(ticket);
                                                setEmployeeId("");
                                            }}
                                        >
                                            Assign
                                        </button>
                                    </td>

                                </tr>

                            ))}

                            </tbody>

                        </table>

                    </div>
                )}


                {/* RESOLVED */}
                <div className="manager-section-header resolved-section-header">

                    <div>
                        <h2>Resolved Tickets</h2>

                        <p>
                            Review resolved tickets and close them.
                        </p>
                    </div>

                    <span className="assigned-count">
                        {resolvedTickets.length} resolved
                    </span>

                </div>


                {resolvedLoading ? (

                    <div className="loading-page">
                        <div className="loading-spinner"></div>
                        <p>Loading resolved tickets...</p>
                    </div>

                ) : resolvedTickets.length === 0 ? (

                    <div className="empty-state">
                        <h3>No resolved tickets</h3>
                        <p>
                            Resolved tickets will appear here.
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
                                <th>Updated</th>
                                <th>Action</th>
                            </tr>
                            </thead>

                            <tbody>

                            {resolvedTickets.map((ticket) => (

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
                                        {formatDate(ticket.updatedAt)}
                                    </td>

                                    <td>

                                        <div className="manager-ticket-actions">

                                            <button
                                                className="view-button"
                                                onClick={() =>
                                                    navigate(
                                                        `/tickets/${ticket.ticketId}`
                                                    )
                                                }
                                            >
                                                View
                                            </button>

                                            <button
                                                className="close-ticket-button"
                                                onClick={() =>
                                                    handleCloseTicket(
                                                        ticket.ticketId
                                                    )
                                                }
                                                disabled={
                                                    closingTicketId ===
                                                    ticket.ticketId
                                                }
                                            >
                                                {closingTicketId ===
                                                ticket.ticketId
                                                    ? "Closing..."
                                                    : "Close"}
                                            </button>

                                        </div>

                                    </td>

                                </tr>

                            ))}

                            </tbody>

                        </table>

                    </div>
                )}


                {/* ASSIGN MODAL */}
                {selectedTicket && (

                    <div className="modal-overlay">

                        <div className="assign-modal">

                            <div className="assign-modal-header">

                                <div>
                                    <h2>Assign Ticket</h2>

                                    <p>
                                        Ticket #{selectedTicket.ticketId}
                                    </p>
                                </div>

                                <button
                                    className="modal-close"
                                    onClick={() =>
                                        setSelectedTicket(null)
                                    }
                                >
                                    ×
                                </button>

                            </div>


                            <div className="assign-ticket-summary">

                                <strong>
                                    {selectedTicket.title}
                                </strong>

                                <p>
                                    {selectedTicket.ticketDesc}
                                </p>

                            </div>


                            <div className="form-group">

                                <label>
                                    Assign To
                                </label>

                                <select
                                    value={employeeId}
                                    onChange={(event) =>
                                        setEmployeeId(
                                            event.target.value
                                        )
                                    }
                                    disabled={assigning}
                                >

                                    <option value="">
                                        Select employee
                                    </option>

                                    {employees.map((employee) => (

                                        <option
                                            key={employee.userId}
                                            value={employee.userId}
                                        >
                                            {employee.userName} —{" "}
                                            {employee.department}
                                        </option>

                                    ))}

                                </select>

                            </div>


                            <div className="form-actions">

                                <button
                                    className="secondary-button"
                                    onClick={() =>
                                        setSelectedTicket(null)
                                    }
                                    disabled={assigning}
                                >
                                    Cancel
                                </button>

                                <button
                                    className="primary-button"
                                    onClick={handleAssign}
                                    disabled={
                                        assigning ||
                                        !employeeId
                                    }
                                >
                                    {assigning
                                        ? "Assigning..."
                                        : "Assign Ticket"}
                                </button>

                            </div>

                        </div>

                    </div>

                )}

            </main>

        </div>
    );
}

export default ManagerDashboard;