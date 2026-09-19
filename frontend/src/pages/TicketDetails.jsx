import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    getTicketById
} from "../services/ticketService";

function TicketDetails() {
    const { ticketId } = useParams();
    const navigate = useNavigate();

    const [ticket, setTicket] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        loadTicket();
    }, [ticketId]);

    const loadTicket = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await getTicketById(ticketId);
            setTicket(data);

        } catch (error) {
            console.error("Failed to load ticket:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            if (error.response?.status === 404) {
                setError("Ticket not found.");
                return;
            }

            setError("Unable to load ticket.");
        } finally {
            setLoading(false);
        }
    };

    const handleResolve = async () => {
        const confirmed = window.confirm(
            "Are you sure you want to resolve this ticket?"
        );

        if (!confirmed) {
            return;
        }

        try {
            setResolving(true);
            setError("");

            const updatedTicket = await resolveTicket(ticketId);

            setTicket(updatedTicket);

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
            setResolving(false);
        }
    };

    const formatDate = (date) => {
        if (!date) {
            return "-";
        }

        return new Date(date).toLocaleString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
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
                <p>Loading ticket...</p>
            </div>
        );
    }

    if (error && !ticket) {
        return (
            <div className="page-container">

                <div className="dashboard-error">
                    {error}
                </div>

                <button
                    className="secondary-button"
                    onClick={() => navigate("/tickets")}
                >
                    Back to My Tickets
                </button>

            </div>
        );
    }

    return (
        <div className="page-container">

            {/* Header */}

            <div className="page-header">

                <div>
                    <h1>Ticket #{ticket.ticketId}</h1>
                    <p>Ticket details</p>
                </div>

                <button
                    className="secondary-button"
                    onClick={() => navigate("/tickets")}
                >
                    ← Back to My Tickets
                </button>

            </div>

            {error && (
                <div className="form-error">
                    {error}
                </div>
            )}

            {/* Main ticket card */}

            <div className="ticket-details-card">

                <div className="ticket-details-header">

                    <div>
                        <h2>{ticket.title}</h2>

                        <div className="ticket-badges">

              <span
                  className={`status-badge ${getStatusClass(
                      ticket.status
                  )}`}
              >
                {ticket.status?.replace("_", " ")}
              </span>

                            <span
                                className={`priority-badge ${getPriorityClass(
                                    ticket.priority
                                )}`}
                            >
                {ticket.priority}
              </span>

                        </div>
                    </div>

                </div>

                {/* Description */}

                <div className="ticket-description-section">

                    <h3>Description</h3>

                    <p>
                        {ticket.ticketDesc || "No description provided."}
                    </p>

                </div>

                {/* Information */}

                <div className="ticket-info-grid">

                    <div className="ticket-info-item">
                        <span>Ticket ID</span>
                        <strong>#{ticket.ticketId}</strong>
                    </div>

                    <div className="ticket-info-item">
                        <span>Status</span>
                        <strong>
                            {ticket.status?.replace("_", " ")}
                        </strong>
                    </div>

                    <div className="ticket-info-item">
                        <span>Priority</span>
                        <strong>{ticket.priority}</strong>
                    </div>

                    <div className="ticket-info-item">
                        <span>Created By User ID</span>
                        <strong>
                            {ticket.createdByUserId ?? "-"}
                        </strong>
                    </div>

                    <div className="ticket-info-item">
                        <span>Assigned To User ID</span>
                        <strong>
                            {ticket.assignedToUserId ?? "Unassigned"}
                        </strong>
                    </div>

                    <div className="ticket-info-item">
                        <span>Created At</span>
                        <strong>
                            {formatDate(ticket.createdAt)}
                        </strong>
                    </div>

                    <div className="ticket-info-item">
                        <span>Last Updated</span>
                        <strong>
                            {formatDate(ticket.updatedAt)}
                        </strong>
                    </div>

                </div>

            </div>

        </div>
    );
}

export default TicketDetails;