import { useEffect, useState } from "react";
import { getCurrentUsername } from "../services/authService";
import { getUserByUsername } from "../services/userService";
import {
    getCreatedTickets,
    getAssignedTickets,
    resolveTicket,
} from "../services/ticketService";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";

function Dashboard() {
    const navigate = useNavigate();

    const [user, setUser] = useState(null);
    const [tickets, setTickets] = useState([]);
    const [assignedTickets, setAssignedTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [resolvingTicketId, setResolvingTicketId] = useState(null);

    useEffect(() => {
        loadDashboard();
    }, []);

    const loadDashboard = async () => {
        try {
            setLoading(true);
            setError("");

            const username = getCurrentUsername();

            if (!username) {
                navigate("/login");
                return;
            }

            const [userData, createdData, assignedData] =
                await Promise.all([
                    getUserByUsername(username),
                    getCreatedTickets(),
                    getAssignedTickets(),
                ]);

            setUser(userData);
            setTickets(createdData.content || []);
            setAssignedTickets(assignedData || []);

        } catch (error) {
            console.error("Dashboard loading failed:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            setError("Unable to load dashboard data.");

        } finally {
            setLoading(false);
        }
    };

    const handleResolve = async (ticketId) => {
        if (!window.confirm("Are you sure you want to resolve this ticket?")) {
            return;
        }

        try {
            setResolvingTicketId(ticketId);
            setError("");

            await resolveTicket(ticketId);
            await loadDashboard();

        } catch (error) {
            console.error("Failed to resolve ticket:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            setError(
                error.response?.data?.message ||
                "Unable to resolve ticket."
            );

        } finally {
            setResolvingTicketId(null);
        }
    };

    const openTickets = tickets.filter(
        (ticket) => ticket.status === "OPEN"
    );

    const resolvedTickets = tickets.filter(
        (ticket) => ticket.status === "RESOLVED"
    );

    const highPriorityTickets = tickets.filter(
        (ticket) => ticket.priority === "HIGH"
    );

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

    if (loading) {
        return (
            <div className="loading-page">
                <div className="loading-spinner"></div>
                <p>Loading dashboard...</p>
            </div>
        );
    }

    return (
        <div className="app-layout">

            {/* SIDEBAR */}
            <Sidebar user={user} />

            {/* MAIN CONTENT */}
            <main className="main-content">

                {/* TOP BAR */}
                <header className="topbar">

                    <div>
                        <h2>Dashboard</h2>
                        <p>
                            Welcome back, {user?.userName}.
                            Here's what's happening with your tickets.
                        </p>
                    </div>

                    <div className="user-profile">

                        <div className="avatar">
                            {user?.userName?.charAt(0).toUpperCase()}
                        </div>

                        <div>
                            <strong>{user?.userName}</strong>
                            <small>
                                {user?.roles?.join(", ") || "Employee"}
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


                {/* STAT CARDS */}
                <section className="stats-grid">

                    <div className="stat-card">
                        <div className="stat-icon employees-icon">
                            🎫
                        </div>
                        <div>
                            <span>My Tickets</span>
                            <strong>{tickets.length}</strong>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon tickets-icon">
                            ◷
                        </div>
                        <div>
                            <span>Open Tickets</span>
                            <strong>{openTickets.length}</strong>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon open-icon">
                            ✓
                        </div>
                        <div>
                            <span>Resolved</span>
                            <strong>{resolvedTickets.length}</strong>
                        </div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-icon high-icon">
                            !
                        </div>
                        <div>
                            <span>High Priority</span>
                            <strong>{highPriorityTickets.length}</strong>
                        </div>
                    </div>

                </section>


                {/* PROFILE */}
                <section className="content-card user-info-card">

                    <div className="card-header">
                        <div>
                            <h3>My Profile</h3>
                            <p>Your employee information</p>
                        </div>
                    </div>

                    <div className="profile-grid">

                        <div>
                            <span>Username</span>
                            <strong>{user?.userName || "-"}</strong>
                        </div>

                        <div>
                            <span>Email</span>
                            <strong>{user?.email || "-"}</strong>
                        </div>

                        <div>
                            <span>Department</span>
                            <strong>{user?.department || "-"}</strong>
                        </div>

                        <div>
                            <span>Role</span>
                            <strong>
                                {user?.roles?.join(", ") || "-"}
                            </strong>
                        </div>

                    </div>

                </section>


                {/* RECENT TICKETS */}
                <section className="content-card">

                    <div className="card-header">

                        <div>
                            <h3>My Recent Tickets</h3>
                            <p>Tickets created by you</p>
                        </div>

                        <button
                            className="view-all"
                            onClick={() => navigate("/tickets")}
                        >
                            View All
                        </button>

                    </div>

                    {tickets.length === 0 ? (

                        <div className="empty-state">
                            <div className="empty-icon">🎫</div>

                            <h3>No tickets yet</h3>

                            <p>
                                You haven't created any tickets.
                            </p>
                        </div>

                    ) : (

                        <div className="ticket-table">

                            <div className="table-row table-header">
                                <span>ID</span>
                                <span>Title</span>
                                <span>Priority</span>
                                <span>Status</span>
                                <span>Created</span>
                            </div>

                            {tickets.slice(0, 5).map((ticket) => (

                                <div
                                    className="table-row"
                                    key={ticket.ticketId}
                                >

                                    <span>
                                        #{ticket.ticketId}
                                    </span>

                                    <span>
                                        {ticket.title}
                                    </span>

                                    <span
                                        className={`priority ${ticket.priority?.toLowerCase()}`}
                                    >
                                        {ticket.priority}
                                    </span>

                                    <span
                                        className={`status ${ticket.status?.toLowerCase()}`}
                                    >
                                        {ticket.status}
                                    </span>

                                    <span>
                                        {formatDate(ticket.createdAt)}
                                    </span>

                                </div>

                            ))}

                        </div>
                    )}

                </section>


                {/* ASSIGNED TICKETS */}
                {assignedTickets.length > 0 && (

                    <section className="content-card assigned-tickets-card">

                        <div className="card-header">

                            <div>
                                <h3>Assigned To Me</h3>
                                <p>
                                    Tickets currently assigned to you
                                </p>
                            </div>

                            <span className="assigned-count">
                                {assignedTickets.length} ticket
                                {assignedTickets.length !== 1 ? "s" : ""}
                            </span>

                        </div>


                        <div className="assigned-ticket-table">

                            <div className="assigned-table-row assigned-table-header">
                                <span>ID</span>
                                <span>Title</span>
                                <span>Priority</span>
                                <span>Status</span>
                                <span>Updated</span>
                                <span>Action</span>
                            </div>


                            {assignedTickets.slice(0, 5).map((ticket) => (

                                <div
                                    className="assigned-table-row"
                                    key={ticket.ticketId}
                                >

                                    <span className="ticket-id">
                                        #{ticket.ticketId}
                                    </span>

                                    <span className="assigned-ticket-title">
                                        {ticket.title}
                                    </span>

                                    <span>
                                        <span
                                            className={`priority-badge ${getPriorityClass(
                                                ticket.priority
                                            )}`}
                                        >
                                            {ticket.priority}
                                        </span>
                                    </span>

                                    <span>
                                        <span
                                            className={`status-badge ${getStatusClass(
                                                ticket.status
                                            )}`}
                                        >
                                            {ticket.status?.replace("_", " ")}
                                        </span>
                                    </span>

                                    <span className="ticket-date">
                                        {formatDate(ticket.updatedAt)}
                                    </span>

                                    <div className="assigned-actions">

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

                                        {ticket.status === "IN_PROGRESS" && (
                                            <button
                                                className="resolve-button"
                                                onClick={() =>
                                                    handleResolve(
                                                        ticket.ticketId
                                                    )
                                                }
                                                disabled={
                                                    resolvingTicketId ===
                                                    ticket.ticketId
                                                }
                                            >
                                                {resolvingTicketId ===
                                                ticket.ticketId
                                                    ? "Resolving..."
                                                    : "Resolve"}
                                            </button>
                                        )}

                                        {ticket.status === "RESOLVED" && (
                                            <span className="resolved-label">
                                                Resolved
                                            </span>
                                        )}

                                        {ticket.status === "CLOSED" && (
                                            <span className="resolved-label">
                                                Closed
                                            </span>
                                        )}

                                    </div>

                                </div>

                            ))}

                        </div>

                    </section>
                )}

            </main>
        </div>
    );
}

export default Dashboard;