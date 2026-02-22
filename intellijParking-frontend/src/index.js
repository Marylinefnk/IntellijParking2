import React from 'react';
import ReactDOM from 'react-dom/client';
import './styles/index.css';
import './styles/parking.css';
import Router from "./components/Router";
import { UserProvider } from "./context/UserContext";
import { NotificationProvider } from "./context/NotificationContext";

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <React.StrictMode>
        <NotificationProvider>
            <UserProvider>
                <Router />
            </UserProvider>
        </NotificationProvider>
    </React.StrictMode>
);
