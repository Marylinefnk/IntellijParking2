import React from 'react';
import ReactDOM from 'react-dom/client';
import './styles/index.css';
import './styles/parking.css';
import Router from "./components/Router";
import { UserProvider } from "./context/UserContext";

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <React.StrictMode>
        <UserProvider>
            <Router />
        </UserProvider>
    </React.StrictMode>
);
