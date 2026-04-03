import React, { useState, useEffect } from 'react';
import parkingBg from './parkingVF.png';
import {API_STATUT_CARTE} from "../../constants/back";

export default function Level1() {
    const [placesStatut, setPlacesStatut] = useState({});
    useEffect(() => {
        fetch(API_STATUT_CARTE)
            .then(res => res.json())
            .then(data => setPlacesStatut(data))
            .catch(err => console.error('Erreur:', err));
    }, []);



    const [tooltip, setTooltip] = useState({
        visible: false,
        x: 0,
        y: 0,
        content: ""
    });
    const handleMouseEnter = (event, id, type, statut) => {
        setTooltip({
            visible: true,
            x: event.clientX,
            y: event.clientY,
            id,
            type,
            statut: placesStatut[id] ?? 'Libre'
        });
    };
    const handleMouseMove = (event) => {
        setTooltip(prev => ({
            ...prev,
            x: event.clientX,
            y: event.clientY
        }));
    };
    const handleMouseLeave = () => {
        setTooltip(prev => ({
            ...prev,
            visible: false
        }));
    };
    const formatStatut = (statut) => {
        const map = {
            'EN_COURS': 'Occupée',
            'LIBRE': 'Libre',
            'RESERVEE': 'Réservée',
        };
        return map[statut] ?? statut;
    };



    const [tooltip2, setTooltip2] = useState({ visible: false, x: 0, y: 0, content: '' });
    const handleMouseEnter2 = (event, content) => {
        setTooltip2({ visible: true, x: event.clientX, y: event.clientY, content });
    };
    const handleMouseMove2 = (event) => {
        setTooltip2(prev => ({ ...prev, x: event.clientX, y: event.clientY }));
    };
    const handleMouseLeave2 = () => {
        setTooltip2(prev => ({ ...prev, visible: false }));
    };



    return (
        <div style={{ position: 'relative', width: '1350px', height: '600px' }}>

            {/* IMAGE DE FOND */}
            <img
                src={parkingBg}
                alt="Parking réel"
                style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '1350px',
                    height: '600px',
                    opacity: 0.55,
                    zIndex: 1,
                }}
            />

            <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 1350 600"
                width="1350"
                height="600"
                style={{ position: 'absolute', top: 0, left: 0, zIndex: 2, background: 'transparent' }}
            >
                <defs>
                    <marker id="arrow" markerWidth="10" markerHeight="10"
                            refX="3" refY="5" orient="auto">
                        <path d="M0,0 L10,5 L0,10 Z" fill="white" />
                    </marker>

                    <style>{`
                        .parking-border {
                            fill: none;
                            stroke: rgba(0,0,0,0.5);
                            stroke-width: 3;
                        }
                        .lane {
                            fill: #6b6b6b;
                        }
                        .lane-line {
                            stroke: white;
                            stroke-width: 1;
                            stroke-dasharray: 20 10;
                            stroke-linecap: round;
                        }
                        .arrow {
                            stroke: white;
                            stroke-width: 2;
                        }
                        .slot {
                            cursor: pointer;
                        }
                        .rect-slot {
                            width: 30px;
                            height: 45px;
                            fill: rgba(120,220,160,0.45);
                            stroke: white;
                            stroke-width: 2;
                        }
                        .slot:hover .rect-slot {
                            fill: rgba(120,220,160,0.75);
                        }
                        .pmr-rect {
                            width: 30px;
                            height: 45px;
                            fill: rgba(255,240,200,0.6);
                            stroke: white;
                            stroke-width: 2;
                        }
                        .moto-rect {
                            width: 30px;
                            height: 45px;
                            fill: rgb(120, 60, 160);
                            stroke: white;
                            stroke-width: 2;
                        }
                        .elec-rect {
                            width: 30px;
                            height: 45px;
                            fill: rgba(80,150,255,0.45);
                            stroke: white;
                            stroke-width: 2;
                        }
                        .vip-rect {
                            width: 30px;
                            height: 45px;
                            fill: rgba(255,215,0,0.45);
                            stroke: white;
                            stroke-width: 2;
                        }
                        .slot-text {
                            fill: #303030;
                            font-size: 10px;
                            font-weight: bold;
                            font-family: Arial, sans-serif;
                            pointer-events: none;
                        }
                        .vip-local {
                            fill: #ffa500;
                            stroke: #333;
                            stroke-width: 3;
                        }
                    `}</style>
                </defs>

                {/* CONTOUR */}
                <rect x="0" y="75" width="1350" height="355" className="parking-border" />

                {/* ALLÉES */}
                <rect x="55" y="145" width="1100" height="40" className="lane" />
                <line x1="75" y1="165" x2="1100" y2="165" className="lane-line" />

                <rect x="55" y="320" width="1100" height="40" className="lane" />
                <line x1="75" y1="340" x2="1100" y2="340" className="lane-line" />

                <rect x="55" y="150" width="40" height="180" className="lane" />
                <line x1="75" y1="170" x2="75" y2="330" className="lane-line" />

                {/* voie entrée */}
                <rect x="1192" y="106" width="50" height="210" className="lane"
                      onMouseEnter={(e) => handleMouseEnter2(e, 'ENTREE VOITURES')}
                      onMouseMove={handleMouseMove2}
                      onMouseLeave={handleMouseLeave2}/>
                <line x1="1212" y1="215" x2="1212" y2="150" className="arrow" markerEnd="url(#arrow)" />
                <rect x="1115" y="106" width="80" height="40" className="lane"
                      onMouseEnter={(e) => handleMouseEnter2(e, 'ENTREE VOITURES')}
                      onMouseMove={handleMouseMove2}
                      onMouseLeave={handleMouseLeave2}/>

                {/* voie sortie */}
                <rect x="1260" y="190" width="40" height="209" className="lane"
                      onMouseEnter={(e) => handleMouseEnter2(e, 'SORTIE VOITURES')}
                      onMouseMove={handleMouseMove2}
                      onMouseLeave={handleMouseLeave2}/>
                <line x1="1280" y1="310" x2="1280" y2="235" className="arrow" markerEnd="url(#arrow)" />
                <rect x="1115" y="359" width="160" height="40" className="lane"
                      onMouseEnter={(e) => handleMouseEnter2(e, 'SORTIE VOITURES')}
                      onMouseMove={handleMouseMove2}
                      onMouseLeave={handleMouseLeave2}/>

                {/* entree centre com */}
                <g id="entree-centre-commercial"
                   onMouseEnter={(e) => handleMouseEnter2(e, 'ENTREE PIETONS')}
                   onMouseMove={handleMouseMove2}
                   onMouseLeave={handleMouseLeave2}>
                    <rect x="0" y="220" width="20" height="80"
                          fill="#3498db" stroke="#2980b9" strokeWidth="3" />
                    <text x="10" y="260" fontSize="12" fill="white"
                          textAnchor="middle" dominantBaseline="middle"
                          transform="rotate(-90, 10, 260)" fontWeight="bold">
                        ENTRÉE
                    </text>
                </g>

                {/* FLÈCHES */}
                <line x1="750" y1="165" x2="600" y2="165" className="arrow" markerEnd="url(#arrow)" />
                <line x1="750" y1="340" x2="900" y2="340" className="arrow" markerEnd="url(#arrow)" />

                {/* RANGÉE HAUT */}
                <g id="row-top">
                    {/* PMR */}
                    <g id="PMR01"
                       onMouseEnter={(e) => handleMouseEnter(e, 'PMR01B', 'PMR', '')}
                       onMouseMove={handleMouseMove}
                       onMouseLeave={handleMouseLeave}>
                        <rect className="pmr-rect" x="0" y="95" />
                        <text className="slot-text" x="15" y="117.5" textAnchor="middle" dominantBaseline="middle">PMR1</text>
                    </g>
                    <g id="PMR02"
                       onMouseEnter={(e) => handleMouseEnter(e, 'PMR02B', 'PMR', '')}
                       onMouseMove={handleMouseMove}
                       onMouseLeave={handleMouseLeave}>>
                        <rect className="pmr-rect" x="32" y="95" />
                        <text className="slot-text" x="47" y="117.5" textAnchor="middle" dominantBaseline="middle">PMR2</text>
                    </g>

                    {[
                        { id: 'B01', x: 68 }, { id: 'B02', x: 110 }, { id: 'B03', x: 150 },
                        { id: 'B04', x: 189 }, { id: 'B05', x: 231 }, { id: 'B06', x: 270 },
                        { id: 'B07', x: 310 }, { id: 'B08', x: 352 }, { id: 'B09', x: 390 },
                        { id: 'B10', x: 433 }, { id: 'B11', x: 475 }, { id: 'B12', x: 515 },
                        { id: 'B13', x: 555 }, { id: 'B14', x: 595 }, { id: 'B15', x: 635 },
                        { id: 'B16', x: 675 }, { id: 'B17', x: 722 }, { id: 'B18', x: 758 },
                        { id: 'B19', x: 800 }, { id: 'B20', x: 840 }, { id: 'B21', x: 880 },
                        { id: 'B22', x: 920 },
                    ].map(({ id, x }) => (
                        <g className="slot" id={id} key={id}
                           onMouseEnter={(e) => handleMouseEnter(e, id, 'NORMALE', '')}
                           onMouseMove={handleMouseMove}
                           onMouseLeave={handleMouseLeave}>
                            <rect className="rect-slot" x={x} y="95" />
                            <text className="slot-text" x={x + 15} y="117.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                        </g>
                    ))}
                </g>

                {/* RANGÉE BAS - motos */}
                {[
                    { id: 'B23', x: 0 }, { id: 'B24', x: 30 }, { id: 'B25', x: 68 },
                    { id: 'B26', x: 110 }, { id: 'B27', x: 150 },
                ].map(({ id, x }) => (
                    <g className="slot" id={`M${id}`} key={id}
                       onMouseEnter={(e) => handleMouseEnter(e, id, 'MOTO', '')}
                       onMouseMove={handleMouseMove}
                       onMouseLeave={handleMouseLeave}>
                        <rect className="moto-rect" x={x} y="365" />
                        <text className="slot-text" x={x + 15} y="387.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* RANGÉE BAS - voitures normales */}
                {[
                    { id: 'B28', x: 189 }, { id: 'B29', x: 231 }, { id: 'B30', x: 270 },
                    { id: 'B31', x: 310 }, { id: 'B32', x: 352 }, { id: 'B33', x: 390 },
                    { id: 'B34', x: 433 }, { id: 'B35', x: 475 }, { id: 'B36', x: 515 },
                    { id: 'B37', x: 555 }, { id: 'B38', x: 595 }, { id: 'B39', x: 635 },
                    { id: 'B40', x: 675 }, { id: 'B41', x: 722 }, { id: 'B42', x: 758 },
                    { id: 'B43', x: 800 }, { id: 'B44', x: 840 }, { id: 'B45', x: 880 }, { id: 'B46', x: 920 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}
                       onMouseEnter={(e) => handleMouseEnter(e, id, 'NORMALE', '')}
                       onMouseMove={handleMouseMove}
                       onMouseLeave={handleMouseLeave}>
                        <rect className="rect-slot" x={x} y="365" />
                        <text className="slot-text" x={x + 15} y="387.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}


                {/* RANGÉE MILIEU HAUT - électrique */}
                {[
                    { id: 'B47', x: 234 }, { id: 'B48', x: 274 }, { id: 'B49', x: 314 },
                    { id: 'B50', x: 354 }, { id: 'B51', x: 392 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}
                       onMouseEnter={(e) => handleMouseEnter(e, id, 'ELECTRIQUE', '')}
                       onMouseMove={handleMouseMove}
                       onMouseLeave={handleMouseLeave}>
                        <rect className="elec-rect" x={x} y="190" />
                        <text className="slot-text" x={x + 15} y="212.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* RANGÉE MILIEU HAUT - normales */}
                {[
                    { id: 'B52', x: 432 }, { id: 'B54', x: 475 }, { id: 'B55', x: 515 },
                    { id: 'B56', x: 555 }, { id: 'B57', x: 595 }, { id: 'B58', x: 635 },
                    { id: 'B59', x: 675 }, { id: 'B60', x: 722 }, { id: 'B61', x: 758 },
                    { id: 'B62', x: 800 }, { id: 'B63', x: 840 }, { id: 'B64', x: 880 },
                    { id: 'B65', x: 920 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}
                       onMouseEnter={(e) => handleMouseEnter(e, id, 'NORMALE', '')}
                       onMouseMove={handleMouseMove}
                       onMouseLeave={handleMouseLeave}>
                        <rect className="rect-slot" x={x} y="190" />
                        <text className="slot-text" x={x + 15} y="212.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* RANGÉE MILIEU BAS - électrique */}
                {[
                    { id: 'B66', x: 234 }, { id: 'B67', x: 274 }, { id: 'B68', x: 314 },
                    { id: 'B69', x: 354 }, { id: 'B70', x: 392 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}
                       onMouseEnter={(e) => handleMouseEnter(e, id, 'ELECTRIQUE', '')}
                       onMouseMove={handleMouseMove}
                       onMouseLeave={handleMouseLeave}>
                        <rect className="elec-rect" x={x} y="270" />
                        <text className="slot-text" x={x + 15} y="292.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* RANGÉE MILIEU BAS - normales */}
                {[
                    { id: 'B71', x: 432 }, { id: 'B75', x: 555 }, { id: 'B76', x: 595 },
                    { id: 'B77', x: 635 }, { id: 'B78', x: 675 }, { id: 'B79', x: 722 },
                    { id: 'B80', x: 758 }, { id: 'B81', x: 800 }, { id: 'B82', x: 840 },
                    { id: 'B83', x: 880 }, { id: 'B84', x: 920 }, { id: 'B73', x: 475 }, { id: 'B74', x: 515 }
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}
                       onMouseEnter={(e) => handleMouseEnter(e, id, 'NORMALE', '')}
                       onMouseMove={handleMouseMove}
                       onMouseLeave={handleMouseLeave}>
                        <rect className="rect-slot" x={x} y="270" />
                        <text className="slot-text" x={x + 15} y="292.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}
                {/* Locaux */}
                {/* Local 1 */}
                <g id="local-vip1"
                   onMouseEnter={(e) => handleMouseEnter2(e, 'LOCAL VIP1')}
                   onMouseMove={handleMouseMove2}
                   onMouseLeave={handleMouseLeave2}>
                    <rect x="130" y="223" width="80" height="50"
                          className="vip-local" rx="10" ry="10" />
                    <text x="136" y="252" fontSize="11" fontWeight="bold" fill="#333">VIP 1</text>
                    <rect id="status-vip1" x="164" y="225" width="12" height="12"
                          fill="#28a745" stroke="#2f2f2f" strokeWidth="1" rx="3" ry="3" />
                </g>

                {/* Local 2 */}
                <g id="local-vip2"
                   onMouseEnter={(e) => handleMouseEnter2(e, 'LOCAL VIP2')}
                   onMouseMove={handleMouseMove2}
                   onMouseLeave={handleMouseLeave2}>
                    <rect x="980" y="223" width="80" height="50"
                          className="vip-local" rx="10" ry="10" />
                    <text x="993" y="252" fontSize="11" fontWeight="bold" fill="#333">VIP 2</text>
                    <rect id="status-vip2" x="1014" y="225" width="12" height="12"
                          fill="#dc3545" stroke="#2f2f2f" strokeWidth="1" rx="3" ry="3" />
                </g>
            </svg>
            {tooltip.visible && (
                <div
                    style={{
                        position: "fixed",
                        top: tooltip.y + 10,
                        left: tooltip.x + 10,
                        backgroundColor: "white",
                        padding: "6px 10px",
                        borderRadius: "6px",
                        boxShadow: "0 2px 8px rgba(0,0,0,0.3)",
                        fontSize: "12px",
                        pointerEvents: "none",
                        zIndex: 1000
                    }}
                >
                    <strong>{tooltip.id}</strong><br/>
                    <span>Type : {tooltip.type}</span><br/>
                    <span>Statut : {formatStatut(tooltip.statut)}</span>

                </div>
            )}
            {tooltip2.visible && (
                <div style={{
                    position: "fixed",
                    top: tooltip2.y + 10,
                    left: tooltip2.x + 10,
                    backgroundColor: "white",
                    padding: "6px 10px",
                    borderRadius: "6px",
                    boxShadow: "0 2px 8px rgba(0,0,0,0.3)",
                    fontSize: "12px",
                    pointerEvents: "none",
                    zIndex: 1000
                }}>
                    <strong>{tooltip2.content}</strong><br/>
                </div>
            )}
        </div>
    );
}