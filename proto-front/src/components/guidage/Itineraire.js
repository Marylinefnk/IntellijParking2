import React, { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import planParking from '../../assets/plan_parking.png';
import marqueurImage from '../../assets/icone.png';

const BOUNDS = [[0, 0], [1200, 1900]];
const ID_RESERVATION = 16; //J'affiche l'itinéraire pour la réservation ayant l'id=16 pour l'instant

export default function Itineraire() {
    const mapRef = useRef(null);
    const [erreur, setErreur] = useState(null);
    const [chargement, setChargement] = useState(true);
    const mapInstanceRef = useRef(null);

    useEffect(() => {


        if (!mapRef.current) return;
        if (mapInstanceRef.current) {
            mapInstanceRef.current.remove();
            mapInstanceRef.current = null;
        }
            //Je crée la carte
            const map = L.map(mapRef.current, {crs: L.CRS.Simple, minZoom: -1,});
            mapInstanceRef.current = map;

            const iconeMarqueur= L.Icon.extend({
                options: {
                    iconUrl: marqueurImage,
                    iconSize:     [38, 95],
                    iconAnchor:   [0, 150],
                    popupAnchor:  [-3, -76]
                }
            })
            const marqueurLogo = new iconeMarqueur();

            //J'affiche l'image du parking ici
            const bounds = [[0, 0], [500, 500]];
            L.imageOverlay(planParking, BOUNDS).addTo(map);
            map.fitBounds(BOUNDS);



            // C'est la requête permettant de récupérer les données de l'itinéraire depuis l'API
            fetch(`http://localhost:8080/api/guidage/itineraire/${ID_RESERVATION}`)
                .then(res => {
                    if (!res.ok) throw new Error("Réservation introuvable");
                    return res.json();
                })
                .then(noeuds => {
                    setChargement(false);
                    const chemin = noeuds.map(n => [n.y, n.x]);
                    L.polyline(chemin, { color: 'red', weight: 4, dashArray: '10' }).addTo(map);
                    if (chemin.length > 0) {
                        L.marker(chemin[0], {icon:marqueurLogo}).addTo(map).bindPopup('Départ');
                    }
                    if (chemin.length > 1) {
                        L.marker(chemin[chemin.length - 1], {icon:marqueurLogo}).addTo(map).bindPopup('Votre place ');
                    }

                    //Lien vers la doc Leaflet pour les marqueurs = https://leafletjs.com/examples/custom-icons/
                })
                .catch(err => {
                    setChargement(false);
                    setErreur('Impossible d afficher l image');
                    console.log(err);
                });
        }, []);

    return (
        <div>
            {chargement && <p> itinéraire en cours de calcul...</p>}
            {erreur && <p> {erreur}</p>}
            <div ref={mapRef} style={{ height: '500px', width: '100%' }} />
        </div>
    );
}