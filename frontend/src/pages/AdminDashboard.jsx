import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createManager } from "../services/userService";
import {
    getCurrentUsername
} from "../services/authService";
import { getUserByUsername } from "../services/userService";
import Sidebar from "../components/Sidebar";

function AdminDashboard() {
    const navigate = useNavigate();

    const [user, setUser] = useState(null);

    const [form, setForm] = useState({
        username: "",
        email: "",
        password: "",
        department: "",
    });

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        loadAdmin();
    }, []);

    const loadAdmin = async () => {
        try {
            const username = getCurrentUsername();

            if (!username) {
                navigate("/login");
                return;
            }

            const userData = await getUserByUsername(username);
            setUser(userData);

        } catch (error) {
            console.error("Failed to load admin:", error);

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
            }
        }
    };

    const handleChange = (event) => {
        setForm({
            ...form,
            [event.target.name]: event.target.value,
        });
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            setLoading(true);
            setError("");
            setMessage("");

            await createManager(form);

            setMessage("Manager created successfully.");

            setForm({
                username: "",
                email: "",
                password: "",
                department: "",
            });

        } catch (error) {
            console.error(
                "Failed to create manager:",
                error
            );

            if (error.response?.status === 401) {
                localStorage.removeItem("token");
                navigate("/login");
                return;
            }

            setError(
                error.response?.data?.message ||
                "Unable to create manager."
            );

        } finally {
            setLoading(false);
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
                        <h2>Admin Dashboard</h2>

                        <p>
                            Manage managers and system access.
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
                                {user?.roles?.join(", ") || "ADMIN"}
                            </small>

                        </div>

                    </div>

                </header>


                {/* CREATE MANAGER */}
                <section className="content-card">

                    <div className="card-header">

                        <div>

                            <h3>
                                Create Manager
                            </h3>

                            <p>
                                Create a new manager account.
                            </p>

                        </div>

                    </div>


                    {/* SUCCESS */}
                    {message && (
                        <div className="success-message">
                            {message}
                        </div>
                    )}


                    {/* ERROR */}
                    {error && (
                        <div className="dashboard-error">
                            {error}
                        </div>
                    )}


                    {/* FORM */}
                    <form
                        className="admin-form"
                        onSubmit={handleSubmit}
                    >

                        <div className="form-group">

                            <label>
                                Username
                            </label>

                            <input
                                type="text"
                                name="username"
                                value={form.username}
                                onChange={handleChange}
                                placeholder="Enter username"
                                required
                                disabled={loading}
                            />

                        </div>


                        <div className="form-group">

                            <label>
                                Email
                            </label>

                            <input
                                type="email"
                                name="email"
                                value={form.email}
                                onChange={handleChange}
                                placeholder="Enter email"
                                required
                                disabled={loading}
                            />

                        </div>


                        <div className="form-group">

                            <label>
                                Password
                            </label>

                            <input
                                type="password"
                                name="password"
                                value={form.password}
                                onChange={handleChange}
                                placeholder="Enter password"
                                required
                                disabled={loading}
                            />

                        </div>


                        <div className="form-group">

                            <label>
                                Department
                            </label>

                            <input
                                type="text"
                                name="department"
                                value={form.department}
                                onChange={handleChange}
                                placeholder="Enter department"
                                required
                                disabled={loading}
                            />

                        </div>


                        <button
                            type="submit"
                            className="primary-button"
                            disabled={loading}
                        >
                            {loading
                                ? "Creating..."
                                : "Create Manager"}
                        </button>

                    </form>

                </section>

            </main>

        </div>
    );
}

export default AdminDashboard;