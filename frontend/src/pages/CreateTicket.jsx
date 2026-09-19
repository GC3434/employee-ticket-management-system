import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createTicket } from "../services/ticketService";

function CreateTicket() {
    const navigate = useNavigate();

    const [title, setTitle] = useState("");
    const [ticketDesc, setTicketDesc] = useState("");
    const [priority, setPriority] = useState("MEDIUM");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!title.trim() || !ticketDesc.trim()) {
            setError("Title and description are required.");
            return;
        }

        try {
            setLoading(true);
            setError("");

            await createTicket({
                title: title.trim(),
                ticketDesc: ticketDesc.trim(),
                priority,
            });

            navigate("/dashboard");
        } catch (error) {
            console.error("Ticket creation failed:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            setError(
                error.response?.data?.message ||
                "Unable to create ticket."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="page-container">

            <div className="page-header">
                <div>
                    <h1>Create Ticket</h1>
                    <p>Create a new support ticket</p>
                </div>

                <button
                    className="secondary-button"
                    onClick={() => navigate("/dashboard")}
                >
                    Back to Dashboard
                </button>
            </div>

            <div className="ticket-form-card">

                {error && (
                    <div className="form-error">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>

                    <div className="form-group">
                        <label>Title</label>

                        <input
                            type="text"
                            value={title}
                            onChange={(event) => setTitle(event.target.value)}
                            placeholder="Enter ticket title"
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label>Description</label>

                        <textarea
                            value={ticketDesc}
                            onChange={(event) => setTicketDesc(event.target.value)}
                            placeholder="Describe the issue..."
                            rows="7"
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label>Priority</label>

                        <select
                            value={priority}
                            onChange={(event) => setPriority(event.target.value)}
                            disabled={loading}
                        >
                            <option value="LOW">Low</option>
                            <option value="MEDIUM">Medium</option>
                            <option value="HIGH">High</option>
                        </select>
                    </div>

                    <div className="form-actions">

                        <button
                            type="button"
                            className="secondary-button"
                            onClick={() => navigate("/dashboard")}
                            disabled={loading}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="primary-button"
                            disabled={loading}
                        >
                            {loading ? "Creating..." : "Create Ticket"}
                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
}

export default CreateTicket;