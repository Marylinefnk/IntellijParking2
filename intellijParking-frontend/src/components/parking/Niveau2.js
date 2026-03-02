import React, { useState } from 'react';
import parkingBg from './parkingVF.png';

export default function Level1() {
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
                <rect x="1192" y="106" width="50" height="210" className="lane" />
                <line x1="1212" y1="215" x2="1212" y2="150" className="arrow" markerEnd="url(#arrow)" />
                <rect x="1115" y="106" width="80" height="40" className="lane" />

                {/* voie sortie */}
                <rect x="1260" y="190" width="40" height="209" className="lane" />
                <line x1="1280" y1="310" x2="1280" y2="235" className="arrow" markerEnd="url(#arrow)" />
                <rect x="1115" y="359" width="160" height="40" className="lane" />

                {/* entree centre com */}
                <g id="entree-centre-commercial">
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
                    <g id="PMR01">
                        <rect className="pmr-rect" x="0" y="95" />
                        <text className="slot-text" x="15" y="117.5" textAnchor="middle" dominantBaseline="middle">PMR1</text>
                    </g>
                    <g id="PMR02">
                        <rect className="pmr-rect" x="32" y="95" />
                        <text className="slot-text" x="47" y="117.5" textAnchor="middle" dominantBaseline="middle">PMR2</text>
                    </g>

                    {[
                        { id: 'A01', x: 68 }, { id: 'A02', x: 110 }, { id: 'A03', x: 150 },
                        { id: 'A04', x: 189 }, { id: 'A05', x: 231 }, { id: 'A06', x: 270 },
                        { id: 'A07', x: 310 }, { id: 'A08', x: 352 }, { id: 'A09', x: 390 },
                        { id: 'A10', x: 433 }, { id: 'A11', x: 475 }, { id: 'A12', x: 515 },
                        { id: 'A13', x: 555 }, { id: 'A14', x: 595 }, { id: 'A15', x: 635 },
                        { id: 'A16', x: 675 }, { id: 'A17', x: 722 }, { id: 'A18', x: 758 },
                        { id: 'A19', x: 800 }, { id: 'A20', x: 840 }, { id: 'A21', x: 880 },
                        { id: 'A22', x: 920 },
                    ].map(({ id, x }) => (
                        <g className="slot" id={id} key={id}>
                            <rect className="rect-slot" x={x} y="95" />
                            <text className="slot-text" x={x + 15} y="117.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                        </g>
                    ))}
                </g>

                {/* RANGÉE BAS - motos */}
                {[
                    { id: 'A23', x: 0 }, { id: 'A24', x: 30 }, { id: 'A25', x: 68 },
                    { id: 'A26', x: 110 }, { id: 'A27', x: 150 },
                ].map(({ id, x }) => (
                    <g className="slot" id={`M${id}`} key={id}>
                        <rect className="moto-rect" x={x} y="365" />
                        <text className="slot-text" x={x + 15} y="387.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* RANGÉE BAS - voitures normales */}
                {[
                    { id: 'A28', x: 189 }, { id: 'A29', x: 231 }, { id: 'A30', x: 270 },
                    { id: 'A31', x: 310 }, { id: 'A32', x: 352 }, { id: 'A33', x: 390 },
                    { id: 'A34', x: 433 }, { id: 'A35', x: 475 }, { id: 'A36', x: 515 },
                    { id: 'A37', x: 555 }, { id: 'A38', x: 595 }, { id: 'A39', x: 635 },
                    { id: 'A40', x: 675 }, { id: 'A41', x: 722 }, { id: 'A42', x: 758 },
                    { id: 'A43', x: 800 }, { id: 'A44', x: 840 },{ id: 'A45', x: 880 }, { id: 'A46', x: 920 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}>
                        <rect className="rect-slot" x={x} y="365" />
                        <text className="slot-text" x={x + 15} y="387.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}


                {/* RANGÉE MILIEU HAUT - électriqu */}
                {[
                    { id: 'A47', x: 234 }, { id: 'A48', x: 274 }, { id: 'A49', x: 314 },
                    { id: 'A50', x: 354 }, { id: 'A51', x: 392 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}>
                        <rect className="elec-rect" x={x} y="190" />
                        <text className="slot-text" x={x + 15} y="212.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* RANGÉE MILIEU HAUT - v normales */}
                {[
                    { id: 'A52', x: 432 }, { id: 'A54', x: 475 }, { id: 'A55', x: 515 },
                    { id: 'A56', x: 555 }, { id: 'A57', x: 595 }, { id: 'A58', x: 635 },
                    { id: 'A59', x: 675 }, { id: 'A60', x: 722 }, { id: 'A61', x: 758 },
                    { id: 'A62', x: 800 }, { id: 'A63', x: 840 }, { id: 'A64', x: 880 },
                    { id: 'A65', x: 920 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}>
                        <rect className="rect-slot" x={x} y="190" />
                        <text className="slot-text" x={x + 15} y="212.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* RANGÉE MILIEU BAS - électrique */}
                {[
                    { id: 'A66', x: 234 }, { id: 'A67', x: 274 }, { id: 'A68', x: 314 },
                    { id: 'A69', x: 354 }, { id: 'A70', x: 392 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}>
                        <rect className="elec-rect" x={x} y="270" />
                        <text className="slot-text" x={x + 15} y="292.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* RANGÉE MILIEU BAS - voiture normales */}
                {[
                    { id: 'A71', x: 432 }, { id: 'A75', x: 555 }, { id: 'A76', x: 595 },
                    { id: 'A77', x: 635 }, { id: 'A78', x: 675 }, { id: 'A79', x: 722 },
                    { id: 'A80', x: 758 }, { id: 'A81', x: 800 }, { id: 'A82', x: 840 },
                    { id: 'A83', x: 880 }, { id: 'A84', x: 920 },{ id: 'A73', x: 475 }, { id: 'A74', x: 515 },
                ].map(({ id, x }) => (
                    <g className="slot" id={id} key={id}>
                        <rect className="rect-slot" x={x} y="270" />
                        <text className="slot-text" x={x + 15} y="292.5" textAnchor="middle" dominantBaseline="middle">{id}</text>
                    </g>
                ))}

                {/* Locaux */}
                {/* Local 1 */}
                <g id="local-vip1">
                    <rect x="130" y="223" width="80" height="50"
                          className="vip-local" rx="10" ry="10" />
                    <text x="136" y="252" fontSize="11" fontWeight="bold" fill="#333">VIP 1</text>
                    <rect id="status-vip1" x="164" y="225" width="12" height="12"
                          fill="#28a745" stroke="#2f2f2f" strokeWidth="1" rx="3" ry="3" />
                </g>

                {/* Local 2 */}
                <g id="local-vip2">
                    <rect x="980" y="223" width="80" height="50"
                          className="vip-local" rx="10" ry="10" />
                    <text x="993" y="252" fontSize="11" fontWeight="bold" fill="#333">VIP 2</text>
                    <rect id="status-vip2" x="1014" y="225" width="12" height="12"
                          fill="#dc3545" stroke="#2f2f2f" strokeWidth="1" rx="3" ry="3" />
                </g>
            </svg>
        </div>
    );
}