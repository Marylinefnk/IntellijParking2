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
import App from "./App";
import HomePage from "./HomePage";
import ParkingPage from "./parking/ParkingPage";
import SimulationPanel from "./SimulationPanel";

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

                {/* Public places view - Default landing page */}
                <Route path="/" element={<PublicLayout />}>
                    <Route index element={<PlacesPage />} />
                </Route>
                <Route path="/ParkingPage" element={<ParkingPage />}>
                    <Route index element={<ParkingPage />} />
                </Route>

                <Route path="/" element={<HomePage />}>
                    <Route index element={<HomePage />} />
                </Route>

                {/* Protected routes */}
                <Route path="/app" element={
                    <ProtectedRoute>
                        <Layout />
                    </ProtectedRoute>
                }>
                    <Route index element={<Dashboard />} />
                    <Route path="dashboard" element={<Dashboard />} />
                    <Route path="places" element={<PlacesPage />} />
                    <Route path="reservations" element={<ReservationsPage />} />
                    <Route path="personnes" element={
                        <SupervisorRoute>
                            <PersonnesPage />
                        </SupervisorRoute>
                    } />
                    <Route path="vehicules" element={<VehiculesPage />} />
                    <Route path="simulation" element={
                        <SupervisorRoute>
                            <SimulationPanel />
                        </SupervisorRoute>
                    } />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}
