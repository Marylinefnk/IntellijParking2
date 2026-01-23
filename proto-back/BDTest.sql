
/*CREATE DATABASE IF NOT EXISTS BDTest;
USE BDTest; */



/* username  intellijP IDENTIFIED BY 'toto'; */
/* mysql -u intellijP -p -h 172.31.252.33 BDTest comm pour test la communication bd


--CREATE TABLE IF NOT EXISTS sample (
--                                      id_sample INT AUTO_INCREMENT PRIMARY KEY,
--                                      date_sample DATE NOT NULL,
--                                      string_sample VARCHAR(50),
--    float_sample FLOAT,
--  sample_type ENUM('SAMPLE_TYPE1', 'SAMPLE_TYPE2')
--    );


--Tatiana va modifier ça pour insérer des places
--INSERT INTO sample (date_sample, string_sample, float_sample, sample_type)
--VALUES
  --  ('2017-03-14', '3', 2, 'SAMPLE_TYPE1'),
  --('2018-03-14', '2', 3, 'SAMPLE_TYPE2'),
  --('2017-03-14', '1', 4, 'SAMPLE_TYPE1'),
  --('2019-03-14', '4', 1, 'SAMPLE_TYPE2'); */

CREATE TABLE IF NOT EXISTS personne (
                                      id_personne INT AUTO_INCREMENT PRIMARY KEY,
                                      nom_personne VARCHAR(50),
                                      prenom_personne VARCHAR(50),
                                      mail VARCHAR(50) );


INSERT INTO personne (id_personne, nom_personne, prenom_personne, mail)
VALUES
         (01, 'DUPONT', 'JEAN', 'jeandupont@gmail.com'),
         (02, 'DUPUIS', 'PIERRE','pierredupuis@gmail.com'),
         (03, 'LECLERC', 'ANNE', 'anneleclerct@gmail.com'),
         (04, 'VAL', 'LAURE', 'laureval@gmail.com');

CREATE TABLE IF NOT EXISTS zone (
    id_zone INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    etage INT DEFAULT 0
);

INSERT INTO zone (id_zone, nom, etage) VALUES
    (1, 'Zone A', 0),
    (2, 'Zone B', 0),
    (3, 'Zone VIP', 1);


CREATE TABLE IF NOT EXISTS place (
                                      id_place INT AUTO_INCREMENT PRIMARY KEY,
                                      numero VARCHAR(50),
                                      type_place ENUM('standard', 'PMR', 'electrique', 'moto', 'familiale') NOT NULL,
                                      etat ENUM('libre', 'occupee', 'reservee') DEFAULT 'libre',
                                      position_x DOUBLE NOT NULL,
                                      position_y DOUBLE NOT NULL,

                                      id_zone INT NOT NULL,
                                      FOREIGN KEY (id_zone) REFERENCES Zone(id_zone)
   );


INSERT INTO place (id_place, numero, type_place, etat, position_x, position_y, id_zone)
VALUES
    (1, 'A1', 'standard', 'libre', 10.0, 5.0, 1),
    (2, 'A2', 'PMR', 'reservee', 10.0, 10.0, 1),
    (3, 'A3', 'standard', 'occupee', 10.0, 15.0, 1),
    (4, 'B1', 'electrique', 'libre', 30.0, 5.0, 2);


CREATE TABLE IF NOT EXISTS vehicule (
    idVehicule INT PRIMARY KEY AUTO_INCREMENT,
    immatriculation VARCHAR(255) NOT NULL,
    typeVehicule VARCHAR(255) NOT NULL,
    id_personne INT NOT NULL,
    FOREIGN KEY (id_personne) REFERENCES personne(id_personne)
);

INSERT INTO vehicule (idVehicule,immatriculation,typeVehicule,id_personne)
VALUES
         (01, 'MN-01-66', 'voiture', 03),
         (02, 'QR-345-ST', 'voiture', 01),
         (03, 'UV-678-WX', 'moto', 02),
         (04,'IJ-789-KL' , 'voiture', 04);


CREATE TABLE stationnement (
    id_stationnement INT PRIMARY KEY AUTO_INCREMENT,
    dateEntree DATETIME NOT NULL,
    dateSortie DATETIME,
    tarif DOUBLE,
    duree INT,
    idVehicule INT NOT NULL,
    FOREIGN KEY (idVehicule) REFERENCES Vehicule(idVehicule)
);

INSERT INTO stationnement (idStationnement, dateEntree, dateSortie, tarif, duree, idVehicule)
VALUES
       (1, '2024-12-19 08:30:00', '2024-12-19 12:30:00', 5.50, 240, 01),
       (2, '2024-12-19 09:00:00', '2024-12-19 17:00:00', 12.00, 480, 02);


CREATE TABLE IF NOT EXISTS reservation (
    id_reservation INT PRIMARY KEY AUTO_INCREMENT,
    id_personne INT NOT NULL,
    id_place INT NOT NULL,
    id_vehicule INT NOT NULL,

    date_debut DATETIME NOT NULL,
    date_fin DATETIME NOT NULL,
    typeReservation ENUM('place de parking', 'Service'),
    statut ENUM('confirmee', 'en_cours', 'terminee', 'annulee') DEFAULT 'confirmee',
    FOREIGN KEY (id_place) REFERENCES place(id_place),
    FOREIGN KEY (id_personne) REFERENCES personne(id_personne),
    FOREIGN KEY (id_vehicule) REFERENCES Vehicule(id_vehicule)

);

INSERT INTO reservation (id_personne, id_place, id_vehicule, date_debut, date_fin, typeReservation, statut) VALUES
    (1, 2, 2, '2024-12-20 08:00:00', '2024-12-20 18:00:00', 'place de parking' , 'confirmee'),
    (3, 1, 1, '2024-12-20 09:00:00', '2024-12-20 12:00:00', 'place de parking' , 'en_cours');




CREATE TABLE IF NOT EXISTS noeud (
    id_noeud INT AUTO_INCREMENT PRIMARY KEY,
    type_noeud ENUM('couloir', 'entree', 'sortie', 'place') NOT NULL,
    numero_noeud INT,
    niveau_noeud INT,
    sens_noeud ENUM('horaire', 'antihoraire')
);

CREATE TABLE IF NOT EXISTS arete (
    id_arete INT AUTO_INCREMENT PRIMARY KEY,
    noeud_source INT NOT NULL,
    noeud_destination INT NOT NULL,
    FOREIGN KEY (noeud_source) REFERENCES noeud(id_noeud),
    FOREIGN KEY (noeud_destination) REFERENCES noeud(id_noeud)

);

