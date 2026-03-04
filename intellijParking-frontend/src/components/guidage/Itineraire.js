import React, { useEffect, useState } from 'react';
import ParkingPage from '../parking/ParkingPage';


export default function Itineraire() {
    const [cheminData, setCheminData] = useState("")

    useEffect(() => {
        fetch("/api/guidage/itineraire/18")
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                setCheminData(data);
                console.log(data);
        })
    }, []);


    return (

        <section>
            <h1> Itinéraire vers votre place </h1>

            <div style={{ position: 'relative'}}>
            <ParkingPage/>
                <svg
                    style={{ position: 'relative', width: '1350px', height: '600px' }}>

                    <polyline
                    points={cheminData.valueOf()}
                    fill="white"
                    stroke="red"
                    strokeWidth="6" />
                </svg>


            </div>



        </section>



    );

}
