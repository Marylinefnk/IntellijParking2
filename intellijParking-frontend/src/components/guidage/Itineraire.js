import React, { useEffect, useState } from 'react';
import ParkingPage from '../parking/ParkingPage';

//const responseItineraire = await fetch('/api/guidage/itineraire/18'); //ici je fais pour la reservation 18
//const responseItineraire = await fetch('/api/guidage/itineraire/${idreservation}'); //pour toute réservation envoyée par api

export default function Itineraire() {
    const [cheminData, setCheminData] = useState([])

    useEffect(() => {
        fetch("/api/guidage/itineraire/${idreservation}")
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                setCheminData(data);
        })
    }, []);


    return (

        <section>
            <h1> Itinéraire vers votre place </h1>
            <ParkingPage/>


        </section>



    );

}
