import React, { useEffect, useState } from 'react';
import planParking from '../../assets/plan_parking.png';
import ParkingPage from '../parking/ParkingPage';

const ID_RESERVATION = 18;

export default function Itineraire() {
    return (
        <section>
            <h1> Itinéraire vers votre place </h1>
            <ParkingPage/>


        </section>



    );
}
