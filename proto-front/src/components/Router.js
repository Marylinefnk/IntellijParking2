import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useUser } from "../context/UserContext";
import Layout from "./Layout";
import PublicLayout from "./PublicLayout";
import Dashboard from "./Dashboard";
import PlacesPage from "./PlacesPage";
import ReservationsPage from "./ReservationsPage";
import PersonnesPage from "./PersonnesPage";
import VehiculesPage from "./VehiculesPage";
import LoginPage from "./LoginPage";
import RegisterPage from "./RegisterPage";

function ProtectedRoute({ children }) {
    const { user, loading } = useUser();

    if (loading) {
        return (
            <div style={{
                minHeight: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                background: "#f0f4f8"
            }}>
                <div style={{ textAlign: "center" }}>
                    <div style={{ fontSize: "2rem", marginBottom: 16 }}>...</div>
                    <p>Chargement...</p>
                </div>
            </div>
        );
    }

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    return children;
}

function SupervisorRoute({ children }) {
    const { user } = useUser();

    if (user?.typePersonne !== "SUPERVISEUR") {
        return <Navigate to="/" replace />;
    }

    return children;
}

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Public routes */}
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                {/* Public places view */}
                <Route path="/places" element={<PublicLayout />}>
                    <Route index element={<PlacesPage />} />
                </Route>

                {/* Protected routes */}
                <Route path="/" element={
                    <ProtectedRoute>
                        <Layout />
                    </ProtectedRoute>
                }>
                    <Route index element={<Dashboard />} />
                    <Route path="dashboard" element={<Dashboard />} />
                    <Route path="mes-places" element={<PlacesPage />} />
                    <Route path="reservations" element={<ReservationsPage />} />
                    <Route path="personnes" element={
                        <SupervisorRoute>
                            <PersonnesPage />
                        </SupervisorRoute>
                    } />
                    <Route path="vehicules" element={<VehiculesPage />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}
