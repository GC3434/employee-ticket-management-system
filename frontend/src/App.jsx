import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import CreateTicket from "./pages/CreateTicket";
import MyTickets from "./pages/MyTickets";
import TicketDetails from "./pages/TicketDetails";
import ManagerDashboard from "./pages/ManagerDashboard";
import AdminDashboard from "./pages/AdminDashboard.jsx";

function App() {
    return (
        <BrowserRouter>
            <Routes>

                <Route
                    path="/"
                    element={<Navigate to="/dashboard" replace />}
                />

                <Route
                    path="/login"
                    element={<Login />}
                />

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/tickets/create"
                    element={
                        <ProtectedRoute>
                            <CreateTicket />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/tickets"
                    element={
                        <ProtectedRoute>
                            <MyTickets />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/tickets/:ticketId"
                    element={
                        <ProtectedRoute>
                            <TicketDetails />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/manager"
                    element={
                        <ProtectedRoute>
                            <ManagerDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/admin"
                    element={
                        <ProtectedRoute>
                            <AdminDashboard />
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;