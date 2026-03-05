import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useUser } from "../context/UserContext";
import Layout from "./navigation/Layout";
import PublicLayout from "./Pages_Authentification/PublicLayout";
import Dashboard from "./navigation/Dashboard";
import PlacesPage from "./navigation/PlacesPage";
import ReservationsPage from "./navigation/ReservationsPage";
import PersonnesPage from "./navigation/PersonnesPage";
import VehiculesPage from "./navigation/VehiculesPage";
import LoginPage from "./Pages_Authentification/LoginPage";
import RegisterPage from "./Pages_Authentification/RegisterPage";
import App from "./App";
import HomePage from "./Pages_Authentification/HomePage";
import ParkingPage from "./parking/ParkingPage";
import SimulationPanel from "./navigation/SimulationPanel";

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

                <Route path="/HomePage" element={<HomePage />}>
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
