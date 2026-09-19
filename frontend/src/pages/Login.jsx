import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login, getCurrentUsername } from "../services/authService";
import { getUserByUsername } from "../services/userService";


function Login() {
    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();

        setError("");
        setLoading(true);

        try {
            await login(userName, password);

            const username = getCurrentUsername();
            const user = await getUserByUsername(username);

            if (user.roles?.includes("ROLE_ADMIN")) {
                navigate("/admin");
            } else if (user.roles?.includes("ROLE_MANAGER")) {
                navigate("/manager");
            } else {
                navigate("/dashboard");
            }
        } catch (error) {
            console.error("Login failed:", error);

            if (error.response) {
                console.log("Status:", error.response.status);
                console.log("Response:", error.response.data);

                setError(
                    `Login failed (${error.response.status})`
                );
            } else {
                console.log("Network error:", error.message);

                setError(
                    "Cannot connect to the backend."
                );
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-page">

            <div className="login-card">

                <div className="login-brand">

                    <div className="brand-icon">
                        E
                    </div>

                    <h1>Employee Hub</h1>

                    <p>
                        Employee Management & Ticketing
                    </p>

                </div>

                <form onSubmit={handleLogin}>

                    <div className="form-group">

                        <label>
                            Username
                        </label>

                        <input
                            type="text"
                            placeholder="Enter your username"
                            value={userName}
                            onChange={(event) =>
                                setUserName(event.target.value)
                            }
                            required
                        />

                    </div>

                    <div className="form-group">

                        <label>
                            Password
                        </label>

                        <input
                            type="password"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(event) =>
                                setPassword(event.target.value)
                            }
                            required
                        />

                    </div>

                    {error && (
                        <div className="login-error">
                            {error}
                        </div>
                    )}

                    <button
                        className="login-button"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? "Signing in..." : "Sign In"}
                    </button>

                </form>

                <p className="login-footer">
                    Employee Management System
                </p>

            </div>

        </div>
    );
}

export default Login;