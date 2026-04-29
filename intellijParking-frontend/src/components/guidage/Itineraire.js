import React, { useEffect, useState } from 'react';
import Niveau1 from '../parking/Niveau1';
import { API_GUIDAGE} from '../../constants/back';
import {API_RESERVATIONS_PERSONNE} from '../../constants/back';
import {useUser} from '../../context/UserContext';




export default function Itineraire() {
    const [cheminData, setCheminData] = useState([])
    const {user, authFetch } = useUser()

    useEffect(() => {
        fetchItineraire();
    }, [user]);


    const fetchItineraire = async () => {
        const response = await authFetch(`${API_RESERVATIONS_PERSONNE}/${user.id}`)
        const  reservations  = await response.json()
        console.log(reservations);

        const reservationsActive =
            reservations.find(reservation => reservation.statut === 'EN_COURS')

        if (!reservationsActive) return;

        const itineraire = await authFetch(`${API_GUIDAGE}/${reservationsActive.id}`);
        const data = await itineraire.json();
        console.log(data);
        setCheminData(data);

    }


    const points = () => {
        let count = "";

        for (let i = 0; i<cheminData.length;i++){
            count = count + cheminData[i].x + "," + cheminData[i].y + " "
        } return count
    }

    return (

        <section>
            <h1> Itinéraire vers votre place </h1>

            <div style={{ position: 'relative', width: '1350px', height: '600px' }}>
                <Niveau1/>
                <svg
                    style={{ position: 'absolute', width: '1350px', height: '600px', top: 0, left: 0, zIndex: 999 }}>


                    <polyline
                        points={points()}
                        fill="none"
                        stroke="red"
                        strokeWidth="6" />
                </svg>


            </div>



        </section>



    );

}