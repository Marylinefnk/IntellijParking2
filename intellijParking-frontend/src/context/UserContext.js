import React, { createContext, useContext, useState, useEffect } from "react";
import { API_AUTH } from "../constants/back";

const UserContext = createContext(null);

export function UserProvider({ children }) {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const savedToken = localStorage.getItem("parkingToken");
        const savedUser = localStorage.getItem("parkingUser");

        if (savedToken && savedUser) {
            setToken(savedToken);
            setUser(JSON.parse(savedUser));

            // on Verifit si le token est valide
            verifyToken(savedToken).then(valid => {
                if (!valid) {
                    logout();
                }
            });
        }
        setLoading(false);
    }, []);

    const verifyToken = async (authToken) => {
        try {
            const res = await fetch(`${API_AUTH}/me`, {
                headers: {
                    "Authorization": `Bearer ${authToken}`
                }
            });
            return res.ok;
        } catch {
            return false;
        }
    };

    const login = (authResponse) => {
        setUser({
            id: authResponse.id,
            nom: authResponse.nom,
            prenom: authResponse.prenom,
            email: authResponse.email,
            typePersonne: authResponse.typePersonne
        });
        setToken(authResponse.token);
        localStorage.setItem("parkingToken", authResponse.token);
        localStorage.setItem("parkingUser", JSON.stringify({
            id: authResponse.id,
            nom: authResponse.nom,
            prenom: authResponse.prenom,
            email: authResponse.email,
            typePersonne: authResponse.typePersonne
        }));
    };

    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem("parkingToken");
        localStorage.removeItem("parkingUser");
    };

    // Helper function to make authenticated API calls
    const authFetch = async (url, options = {}) => {
        const headers = {
            "Content-Type": "application/json",
            ...options.headers
        };

        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }

        return fetch(url, {
            ...options,
            headers
        });
    };

    return (
        <UserContext.Provider value={{ user, token, login, logout, loading, authFetch }}>
            {children}
        </UserContext.Provider>
    );
}

export function useUser() {
    const context = useContext(UserContext);
    if (!context) {
        throw new Error("useUser must be used within a UserProvider");
    }
    return context;
}
