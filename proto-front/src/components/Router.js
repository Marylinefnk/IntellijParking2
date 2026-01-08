// import React from 'react';
// import {BrowserRouter, Route, Routes} from "react-router-dom";
// import App from "./App";
// import Sample from "./Sample";
// import Navbar from "./Navbar";
// import NotFound from "./NotFound";
//
// export default function Router () {
//     return (
//         <BrowserRouter>
//             <div>
//                 <Navbar />
//                 <Routes>
//                     <Route path="/" element={<App />}/>
//                     <Route path="/sample" element={<Sample />}/>
//                     <Route path="*" element={<NotFound />}/>
//                 </Routes>
//             </div>
//         </BrowserRouter>
//     );
// };
import { Routes, Route } from "react-router-dom";
import PlacesPage from "./PlacesPage";

export default function Router() {
    return (
        <Routes>
            <Route path="/places" element={<PlacesPage />} />
            {/* autres routes */}
        </Routes>
    );
}
