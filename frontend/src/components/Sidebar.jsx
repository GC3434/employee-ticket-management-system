import { NavLink, useNavigate } from "react-router-dom";

function Sidebar({ user }) {
    const navigate = useNavigate();

    const isManager =
        user?.roles?.includes("MANAGER") ||
        user?.roles?.includes("ADMIN");

    const isAdmin = user?.roles?.includes("ADMIN");

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    };

    return (
        <aside className="sidebar">

            <div className="sidebar-logo">
                <h2>Emp<span>Ntkt</span></h2>
                <p>Employee Ticketing</p>
            </div>

            <nav className="sidebar-nav">

                {!isManager && (
                    <>
                        <NavLink to="/dashboard">
                            Dashboard
                        </NavLink>

                        <NavLink to="/tickets">
                            My Tickets
                        </NavLink>

                        <NavLink to="/tickets/create">
                            Create Ticket
                        </NavLink>
                    </>
                )}

                {isManager && (
                    <NavLink to="/manager">
                        Manager Dashboard
                    </NavLink>
                )}

                {isAdmin && (
                    <NavLink to="/admin">
                        Admin
                    </NavLink>
                )}

            </nav>

            <div className="sidebar-bottom">
                <button onClick={handleLogout}>
                    Logout
                </button>
            </div>

        </aside>
    );
}

export default Sidebar;