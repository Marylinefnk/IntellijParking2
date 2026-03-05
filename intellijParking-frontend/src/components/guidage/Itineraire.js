import React, { useEffect, useState } from 'react';
import Niveau1 from '../parking/Niveau1';



export default function Itineraire() {
    const [cheminData, setCheminData] = useState([])

    useEffect(() => {
        fetch("/api/guidage/itineraire/1255")
            .then((response) => response.json())
            .then((data) => {
                setCheminData(data);
                console.log(data);
        })
    }, []);

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
                    style={{ position: 'absolute', width: '1350px', height: '600px', top: 0, left: 0, zIndex: 99 }}>

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
