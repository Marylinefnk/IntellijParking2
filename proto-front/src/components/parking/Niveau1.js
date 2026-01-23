import React from 'react';

export default function Level0() {
    return (
        <svg xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 1350 600"
            width="1350"
            height="600">

            <defs>
                <marker id="arrow" markerWidth="10" markerHeight="10"
                    refX="3" refY="5" orient="auto">
                    <path d="M0,0 L10,5 L0,10 Z" fill="white" />
                </marker>

                <style>
                    {`
                    .parking-border {
                        fill: none;
                        stroke: white;
                        stroke-width: 6;
                    }
                    .lane {
                        fill: #555;
                        stroke: #333;
                        stroke-width: 2;
                    }
                    .lane-line {
                        stroke: #888;
                        stroke-width: 2;
                        stroke-dasharray: 5,5;
                    }
                    .arrow {
                        stroke: white;
                        stroke-width: 3;
                    }
                    .slot {
                        cursor: pointer;
                    }
                    .slot:hover .rect-slot {
                        fill: #4CAF50;
                    }
                    .rect-slot {
                        fill: #2E86C1;
                        stroke: #1B4F72;
                        stroke-width: 2;
                        width: 35px;
                        height: 65px;
                        rx: 5;
                    }
                    .pmr .rect-slot {
                        fill: #E74C3C;
                        stroke: #922B21;
                    }
                    .moto {
                        fill: #9B59B6;
                        stroke: #6C3483;
                        stroke-width: 2;
                        width: 20px;
                        height: 35px;
                        rx: 3;
                    }
                    .elec .rect-slot {
                        fill: #F39C12;
                        stroke: #9C640C;
                    }
                    .vip .rect-slot {
                        fill: #17A589;
                        stroke: #117864;
                    }
                    .slot-text {
                        fill: white;
                        font-size: 10px;
                        font-weight: bold;
                        font-family: Arial, sans-serif;
                    }
                    `}
                </style>
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

            {/* RANGÉE HAUT  */}
            <g id="row-top">
                {/* PMR */}
                <g className="pmr" id="PMR01">
                    <rect className="rect-slot" x="0" y="95" />
                    <text className="slot-text" x="17.5" y="127.5" textAnchor="middle" dominantBaseline="middle">PMR1</text>
                </g>
                <g className="pmr" id="PMR02">
                    <rect className="rect-slot" x="32" y="95" />
                    <text className="slot-text" x="49.5" y="127.5" textAnchor="middle" dominantBaseline="middle">PMR2</text>
                </g>

                {/* Places normales */}
                <g className="slot" id="A01">
                    <rect className="rect-slot" x="68" y="95" />
                    <text className="slot-text" x="85.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A01</text>
                </g>
                <g className="slot" id="A02">
                    <rect className="rect-slot" x="110" y="95" />
                    <text className="slot-text" x="127.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A02</text>
                </g>
                <g className="slot" id="A03">
                    <rect className="rect-slot" x="150" y="95" />
                    <text className="slot-text" x="167.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A03</text>
                </g>
                <g className="slot" id="A04">
                    <rect className="rect-slot" x="189" y="95" />
                    <text className="slot-text" x="206.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A04</text>
                </g>
                <g className="slot" id="A05">
                    <rect className="rect-slot" x="231" y="95" />
                    <text className="slot-text" x="248.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A05</text>
                </g>
                <g className="slot" id="A06">
                    <rect className="rect-slot" x="270" y="95" />
                    <text className="slot-text" x="287.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A06</text>
                </g>
                <g className="slot" id="A07">
                    <rect className="rect-slot" x="310" y="95" />
                    <text className="slot-text" x="327.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A07</text>
                </g>
                <g className="slot" id="A08">
                    <rect className="rect-slot" x="352" y="95" />
                    <text className="slot-text" x="369.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A08</text>
                </g>
                <g className="slot" id="A09">
                    <rect className="rect-slot" x="390" y="95" />
                    <text className="slot-text" x="407.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A09</text>
                </g>
                <g className="slot" id="A10">
                    <rect className="rect-slot" x="433" y="95" />
                    <text className="slot-text" x="450.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A10</text>
                </g>
                <g className="slot" id="A11">
                    <rect className="rect-slot" x="475" y="95" />
                    <text className="slot-text" x="492.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A11</text>
                </g>
                <g className="slot" id="A12">
                    <rect className="rect-slot" x="515" y="95" />
                    <text className="slot-text" x="532.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A12</text>
                </g>
                <g className="slot" id="A013">
                    <rect className="rect-slot" x="555" y="95" />
                    <text className="slot-text" x="572.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A13</text>
                </g>
                <g className="slot" id="A14">
                    <rect className="rect-slot" x="595" y="95" />
                    <text className="slot-text" x="612.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A14</text>
                </g>
                <g className="slot" id="A15">
                    <rect className="rect-slot" x="635" y="95" />
                    <text className="slot-text" x="652.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A15</text>
                </g>
                <g className="slot" id="A016">
                    <rect className="rect-slot" x="675" y="95" />
                    <text className="slot-text" x="692.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A16</text>
                </g>
                <g className="slot" id="A17">
                    <rect className="rect-slot" x="722" y="95" />
                    <text className="slot-text" x="739.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A17</text>
                </g>
                <g className="slot" id="A18">
                    <rect className="rect-slot" x="758" y="95" />
                    <text className="slot-text" x="775.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A18</text>
                </g>
                <g className="slot" id="A019">
                    <rect className="rect-slot" x="800" y="95" />
                    <text className="slot-text" x="817.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A19</text>
                </g>
                <g className="slot" id="A20">
                    <rect className="rect-slot" x="840" y="95" />
                    <text className="slot-text" x="857.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A20</text>
                </g>
                <g className="slot" id="A21">
                    <rect className="rect-slot" x="880" y="95" />
                    <text className="slot-text" x="897.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A21</text>
                </g>
                <g className="slot" id="A22">
                    <rect className="rect-slot" x="920" y="95" />
                    <text className="slot-text" x="937.5" y="127.5" textAnchor="middle" dominantBaseline="middle">A22</text>
                </g>
            </g>

            {/* RANGÉE BAS */}
            <g className="slot" id="MA23">
                <rect className="moto" x="0" y="365" />
                <text className="slot-text" x="10.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A23</text>
            </g>

            <g className="slot" id="MA24">
                <rect className="moto" x="30" y="365" />
                <text className="slot-text" x="40.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A24</text>
            </g>
            <g className="slot" id="MA25">
                <rect className="moto" x="68" y="365" />
                <text className="slot-text" x="78.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A25</text>
            </g>
            <g className="slot" id="MA26">
                <rect className="moto" x="110" y="365" />
                <text className="slot-text" x="125.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A26</text>
            </g>

            <g className="slot" id="MA27">
                <rect className="moto" x="150" y="365" width="35" height="65" />
                <text className="slot-text" x="167.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A27</text>
            </g>

            <g className="slot" id="A28">
                <rect className="rect-slot" x="189" y="365" width="37" height="65" />
                <text className="slot-text" x="207.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A28</text>
            </g>
            <g className="slot" id="A29">
                <rect className="rect-slot" x="231" y="365" width="35" height="65" />
                <text className="slot-text" x="248.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A29</text>
            </g>

            <g className="slot" id="A30">
                <rect className="rect-slot" x="270" y="365" width="35" height="65" />
                <text className="slot-text" x="287.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A30</text>
            </g>

            <g className="slot" id="A31">
                <rect className="rect-slot" x="310" y="365" width="35" height="65" />
                <text className="slot-text" x="327.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A31</text>
            </g>
            <g className="slot" id="A32">
                <rect className="rect-slot" x="352" y="365" width="35" height="65" />
                <text className="slot-text" x="369.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A32</text>
            </g>

            <g className="slot" id="A33">
                <rect className="rect-slot" x="390" y="365" width="35" height="65" />
                <text className="slot-text" x="407.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A33</text>
            </g>

            <g className="slot" id="A34">
                <rect className="rect-slot" x="433" y="365" width="35" height="65" />
                <text className="slot-text" x="450.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A34</text>
            </g>
            <g className="slot" id="A35">
                <rect className="rect-slot" x="475" y="365" width="35" height="65" />
                <text className="slot-text" x="492.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A35</text>
            </g>

            <g className="slot" id="A36">
                <rect className="rect-slot" x="515" y="365" width="35" height="65" />
                <text className="slot-text" x="532.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A36</text>
            </g>

            <g className="slot" id="A37">
                <rect className="rect-slot" x="555" y="365" width="37" height="65" />
                <text className="slot-text" x="573.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A37</text>
            </g>
            <g className="slot" id="A38">
                <rect className="rect-slot" x="595" y="365" width="37" height="65" />
                <text className="slot-text" x="613.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A38</text>
            </g>

            <g className="slot" id="A39">
                <rect className="rect-slot" x="635" y="365" width="37" height="65" />
                <text className="slot-text" x="653.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A39</text>
            </g>

            <g className="slot" id="A40">
                <rect className="rect-slot" x="675" y="365" />
                <text className="slot-text" x="692.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A40</text>
            </g>
            <g className="slot" id="A41">
                <rect className="rect-slot" x="722" y="365" />
                <text className="slot-text" x="739.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A41</text>
            </g>

            <g className="slot" id="A42">
                <rect className="rect-slot" x="758" y="365" />
                <text className="slot-text" x="775.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A42</text>
            </g>

            <g className="slot" id="A43">
                <rect className="rect-slot" x="800" y="365" />
                <text className="slot-text" x="817.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A43</text>
            </g>
            <g className="slot" id="A44">
                <rect className="rect-slot" x="840" y="365" />
                <text className="slot-text" x="857.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A44</text>
            </g>

            <g className="slot vip" id="A45">
                <rect className="rect-slot" x="880" y="365" />
                <text className="slot-text" x="897.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A45</text>
            </g>

            <g className="slot vip" id="A46">
                <rect className="rect-slot" x="920" y="365" />
                <text className="slot-text" x="937.5" y="397.5" textAnchor="middle" dominantBaseline="middle">A46</text>
            </g>

            {/* RANGÉE BAS-MILIEU */}
            <g className="slot" id="A47">
                <rect className="rect-slot elec" x="234" y="190" />
                <text className="slot-text" x="251.5" y="222.5" textAnchor="middle">A47</text>
            </g>

            <g className="slot" id="A48">
                <rect className="rect-slot elec" x="274" y="190" />
                <text x="291.5" y="222.5" className="slot-text" textAnchor="middle">A48</text>
            </g>
            <g className="slot" id="A49">
                <rect className="rect-slot elec" x="314" y="190" />
                <text x="331.5" y="222.5" className="slot-text" textAnchor="middle">A49</text>
            </g>

            <g className="slot" id="A50">
                <rect className="rect-slot elec" x="354" y="190" />
                <text x="371.5" y="222.5" className="slot-text" textAnchor="middle">A50</text>
            </g>
            <g className="slot" id="A51">
                <rect className="rect-slot elec" x="392" y="190" />
                <text x="409.5" y="222.5" className="slot-text" textAnchor="middle">A51</text>
            </g>

            <g className="slot" id="A52">
                <rect className="rect-slot" x="432" y="190" />
                <text x="449.5" y="222.5" className="slot-text" textAnchor="middle">A52</text>
            </g>

            <g className="slot" id="A54">
                <rect className="rect-slot" x="475" y="190" />
                <text x="492.5" y="222.5" className="slot-text" textAnchor="middle">A54</text>
            </g>

            <g className="slot" id="A55">
                <rect className="rect-slot" x="515" y="190" />
                <text x="532.5" y="222.5" className="slot-text" textAnchor="middle">A55</text>
            </g>

            <g className="slot" id="A56">
                <rect className="rect-slot" x="555" y="190" />
                <text x="572.5" y="222.5" className="slot-text" textAnchor="middle">A56</text>
            </g>
            <g className="slot" id="A57">
                <rect className="rect-slot" x="595" y="190" />
                <text x="612.5" y="222.5" className="slot-text" textAnchor="middle">A57</text>
            </g>

            <g className="slot" id="A58">
                <rect className="rect-slot" x="635" y="190" />
                <text x="652.5" y="222.5" className="slot-text" textAnchor="middle">A58</text>
            </g>

            <g className="slot" id="A59">
                <rect className="rect-slot" x="675" y="190" />
                <text x="692.5" y="222.5" className="slot-text" textAnchor="middle">A59</text>
            </g>
            <g className="slot" id="A60">
                <rect className="rect-slot" x="722" y="190" />
                <text x="739.5" y="222.5" className="slot-text" textAnchor="middle">A60</text>
            </g>

            <g className="slot" id="A61">
                <rect className="rect-slot" x="758" y="190" />
                <text x="775.5" y="222.5" className="slot-text" textAnchor="middle">A61</text>
            </g>

            <g className="slot" id="A62">
                <rect className="rect-slot" x="800" y="190" />
                <text x="817.5" y="222.5" className="slot-text" textAnchor="middle">A62</text>
            </g>
            <g className="slot" id="A63">
                <rect className="rect-slot" x="840" y="190" />
                <text x="857.5" y="222.5" className="slot-text" textAnchor="middle">A63</text>
            </g>

            <g className="slot" id="A64">
                <rect className="rect-slot" x="880" y="190" />
                <text x="897.5" y="222.5" className="slot-text" textAnchor="middle">A64</text>
            </g>

            <g className="slot" id="A65">
                <rect className="rect-slot" x="920" y="190" />
                <text x="937.5" y="222.5" className="slot-text" textAnchor="middle">A65</text>
            </g>

            {/* milieu bas */}
            <g className="slot elec" id="A66">
                <rect className="rect-slot elec" x="234" y="270" />
                <text x="251.5" y="302.5" className="slot-text" textAnchor="middle">A66</text>
            </g>

            <g className="slot elec" id="A67">
                <rect className="rect-slot elec" x="274" y="270" />
                <text x="291.5" y="302.5" className="slot-text" textAnchor="middle">A67</text>
            </g>
            <g className="slot elec" id="A68">
                <rect className="rect-slot elec" x="314" y="270" />
                <text x="331.5" y="302.5" className="slot-text" textAnchor="middle">A68</text>
            </g>

            <g className="slot elec" id="A69">
                <rect className="rect-slot elec" x="354" y="270" />
                <text x="371.5" y="302.5" className="slot-text" textAnchor="middle">A69</text>
            </g>
            <g className="slot elec" id="A70">
                <rect className="rect-slot elec" x="392" y="270" />
                <text x="409.5" y="302.5" className="slot-text" textAnchor="middle">A70</text>
            </g>

            <g className="slot" id="A71">
                <rect className="rect-slot" x="432" y="270" />
                <text x="449.5" y="302.5" className="slot-text" textAnchor="middle">A71</text>
            </g>

            <g className="slot" id="A72">
                <rect className="rect-slot" x="433" y="270" />
                <text x="450.5" y="302.5" className="slot-text" textAnchor="middle">A72</text>
            </g>
            <g className="slot pmr" id="A73">
                <rect className="rect-slot" x="475" y="270" />
                <text x="492.5" y="302.5" className="slot-text" textAnchor="middle">A73</text>
            </g>

            <g className="slot pmr" id="A74">
                <rect className="rect-slot" x="515" y="270" />
                <text x="532.5" y="302.5" className="slot-text" textAnchor="middle">A74</text>
            </g>

            <g className="slot" id="A75">
                <rect className="rect-slot" x="555" y="270" />
                <text x="572.5" y="302.5" className="slot-text" textAnchor="middle">A75</text>
            </g>
            <g className="slot" id="A76">
                <rect className="rect-slot" x="595" y="270" />
                <text x="612.5" y="302.5" className="slot-text" textAnchor="middle">A76</text>
            </g>

            <g className="slot" id="A77">
                <rect className="rect-slot" x="635" y="270" />
                <text x="652.5" y="302.5" className="slot-text" textAnchor="middle">A77</text>
            </g>

            <g className="slot" id="A78">
                <rect className="rect-slot" x="675" y="270" />
                <text x="692.5" y="302.5" className="slot-text" textAnchor="middle">A78</text>
            </g>
            <g className="slot" id="A79">
                <rect className="rect-slot" x="722" y="270" />
                <text x="739.5" y="302.5" className="slot-text" textAnchor="middle">A79</text>
            </g>

            <g className="slot" id="A80">
                <rect className="rect-slot" x="758" y="270" />
                <text x="775.5" y="302.5" className="slot-text" textAnchor="middle">A80</text>
            </g>

            <g className="slot" id="A81">
                <rect className="rect-slot" x="800" y="270" />
                <text x="817.5" y="302.5" className="slot-text" textAnchor="middle">A81</text>
            </g>
            <g className="slot" id="A82">
                <rect className="rect-slot" x="840" y="270" />
                <text x="857.5" y="302.5" className="slot-text" textAnchor="middle">A82</text>
            </g>

            <g className="slot" id="A83">
                <rect className="rect-slot" x="880" y="270" />
                <text x="897.5" y="302.5" className="slot-text" textAnchor="middle">A83</text>
            </g>

            <g className="slot" id="A84">
                <rect className="rect-slot" x="920" y="270" />
                <text x="937.5" y="302.5" className="slot-text" textAnchor="middle">A84</text>
            </g>

            {/* Locaux */}
            <g id="locals">
                <rect x="130" y="223" width="80" height="50"
                    fill="#dcdcdc" stroke="#2f2f2f" strokeWidth="3" rx="10" ry="10" />
                <text x="136" y="260" fontSize="12" fontWeight="bold" fill="#333">TECHNIQUE</text>
                <rect x="164" y="225" width="12" height="12" fill="#28a745" stroke="#2f2f2f" strokeWidth="1" rx="3" ry="3" />

                <rect x="980" y="223" width="80" height="50"
                    fill="#dcdcdc" stroke="#2f2f2f" strokeWidth="3" rx="10" ry="10" />
                <text x="993" y="260" fontSize="12" fontWeight="bold" fill="#333">LAVERIE</text>
                <rect x="1014" y="225" width="12" height="12" fill="#dc3545" stroke="#2f2f2f" strokeWidth="1" rx="3" ry="3" />
            </g>

            {/* RAMPE */}
            <g id="rampe"></g>
        </svg>
    );
}