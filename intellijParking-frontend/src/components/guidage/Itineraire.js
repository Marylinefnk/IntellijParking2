import React, { useEffect, useRef, useState } from 'react';
import planParking from '../../assets/plan_parking.png';
import ParkingPage from '../parking/ParkingPage';
import Niveau1 from '../parking/Niveau1';
import Niveau2 from '../parking/Niveau2';
import Niveau3 from '../parking/Niveau3';
import parkingVF from '../parking/parkingVF.png';

const ID_RESERVATION = 16; //J'affiche l'itinéraire pour la réservation ayant l'id=16 pour l'instant

export default function Itineraire() {
    return (
        <section>
            <h1> Itinéraire vers votre place </h1>
            <Niveau1/> //Si votre place est au RDC
            <Niveau2/> //si votre place est au niveau 1
            <Niveau3/> //Si votre place est au niveau 2
            <ParkingPage/>


        </section>



    );
}
