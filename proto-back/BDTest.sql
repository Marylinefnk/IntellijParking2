
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

-- =========================
-- 1) PERSONNE
-- =========================
CREATE TABLE IF NOT EXISTS personne (
                                        id_personne INT AUTO_INCREMENT PRIMARY KEY,
                                        nom_personne VARCHAR(50),
                                        prenom_personne VARCHAR(50),
                                        mail VARCHAR(100)
);

INSERT INTO personne (nom_personne, prenom_personne, mail) VALUES
                                                               ('DUPONT', 'JEAN', 'jeandupont@gmail.com'),
                                                               ('DUPUIS', 'PIERRE','pierredupuis@gmail.com'),
                                                               ('LECLERC', 'ANNE', 'anneleclerc@gmail.com'),
                                                               ('VAL', 'LAURE', 'laureval@gmail.com');

-- =========================
-- 2) ZONE
-- =========================
CREATE TABLE IF NOT EXISTS zone (
                                    id_zone INT AUTO_INCREMENT PRIMARY KEY,
                                    nom VARCHAR(50) NOT NULL,
                                    etage INT DEFAULT 0
);

INSERT INTO zone (id_zone, nom, etage) VALUES
                                           (1, 'Zone A', 0),
                                           (2, 'Zone B', 0),
                                           (3, 'Zone VIP', 1);

-- =========================
-- 3) PLACE
-- =========================
CREATE TABLE IF NOT EXISTS place (
                                     id_place INT AUTO_INCREMENT PRIMARY KEY,
                                     numero VARCHAR(50) NOT NULL,
                                     type_place ENUM('STANDARD','PMR','ELECTRIQUE','MOTO','FAMILIALE') NOT NULL,
                                     statut ENUM('LIBRE','OCCUPEE','RESERVEE') NOT NULL DEFAULT 'LIBRE',
                                     position_x DOUBLE,
                                     position_y DOUBLE,
                                     id_zone INT NOT NULL,
                                     CONSTRAINT fk_place_zone FOREIGN KEY (id_zone) REFERENCES zone(id_zone)
);

INSERT INTO place (id_place, numero, type_place, statut, position_x, position_y, id_zone) VALUES
                                                                                              (1, 'A1', 'STANDARD',   'LIBRE',    10.0,  5.0, 1),
                                                                                              (2, 'A2', 'PMR',        'RESERVEE', 10.0, 10.0, 1),
                                                                                              (3, 'A3', 'STANDARD',   'OCCUPEE',  10.0, 15.0, 1),
                                                                                              (4, 'B1', 'ELECTRIQUE', 'LIBRE',    30.0,  5.0, 2);

-- =========================
-- 4) VEHICULE
-- =========================
CREATE TABLE IF NOT EXISTS vehicule (
                                        id_vehicule INT AUTO_INCREMENT PRIMARY KEY,
                                        immatriculation VARCHAR(20) NOT NULL,
                                        type_vehicule ENUM('VOITURE','MOTO') NOT NULL,
                                        id_personne INT NOT NULL,
                                        CONSTRAINT fk_vehicule_personne FOREIGN KEY (id_personne) REFERENCES personne(id_personne)
);

INSERT INTO vehicule (immatriculation, type_vehicule, id_personne) VALUES
                                                                       ('MN-01-66', 'VOITURE', 3),
                                                                       ('QR-345-ST','VOITURE', 1),
                                                                       ('UV-678-WX','MOTO',    2),
                                                                       ('IJ-789-KL','VOITURE', 4);

-- =========================
-- 5) STATIONNEMENT (optionnel mais utile)
-- =========================
CREATE TABLE IF NOT EXISTS stationnement (
                                             id_stationnement INT AUTO_INCREMENT PRIMARY KEY,
                                             date_entree DATETIME NOT NULL,
                                             date_sortie DATETIME,
                                             tarif DOUBLE,
                                             duree_min INT,
                                             id_vehicule INT NOT NULL,
                                             id_place INT,
                                             CONSTRAINT fk_stationnement_vehicule FOREIGN KEY (id_vehicule) REFERENCES vehicule(id_vehicule),
                                             CONSTRAINT fk_stationnement_place FOREIGN KEY (id_place) REFERENCES place(id_place)
);

-- =========================
-- 6) RESERVATION PLACE (UC2)
-- =========================
CREATE TABLE IF NOT EXISTS reservation_place (
                                                 id_reservation_place INT AUTO_INCREMENT PRIMARY KEY,
                                                 id_personne INT NOT NULL,
                                                 id_place INT NOT NULL,
                                                 id_vehicule INT NOT NULL,
                                                 date_debut DATETIME NOT NULL,
                                                 date_fin DATETIME NOT NULL,
                                                 statut ENUM('CONFIRMEE','EN_COURS','TERMINEE','ANNULEE') NOT NULL DEFAULT 'CONFIRMEE',
                                                 CONSTRAINT fk_rp_place FOREIGN KEY (id_place) REFERENCES place(id_place),
                                                 CONSTRAINT fk_rp_personne FOREIGN KEY (id_personne) REFERENCES personne(id_personne),
                                                 CONSTRAINT fk_rp_vehicule FOREIGN KEY (id_vehicule) REFERENCES vehicule(id_vehicule),
                                                 INDEX idx_rp_place_dates (id_place, date_debut, date_fin)
);

INSERT INTO reservation_place (id_personne, id_place, id_vehicule, date_debut, date_fin, statut) VALUES
                                                                                                     (1, 2, 2, '2026-01-08 08:00:00', '2026-01-08 18:00:00', 'CONFIRMEE'),
                                                                                                     (3, 1, 1, '2026-01-08 09:00:00', '2026-01-08 12:00:00', 'EN_COURS');

-- =========================
-- 7) SERVICE + RESERVATION SERVICE (UC3)
-- =========================
CREATE TABLE IF NOT EXISTS service (
                                       id_service INT AUTO_INCREMENT PRIMARY KEY,
                                       type_service ENUM('DEPANNAGE','LAVERIE') NOT NULL,
                                       description VARCHAR(100)
);

INSERT INTO service (type_service, description) VALUES
                                                    ('DEPANNAGE', 'Assistance dépannage'),
                                                    ('LAVERIE',   'Lavage automobile');

CREATE TABLE IF NOT EXISTS reservation_service (
                                                   id_reservation_service INT AUTO_INCREMENT PRIMARY KEY,
                                                   id_personne INT NOT NULL,
                                                   id_service INT NOT NULL,
                                                   date_debut DATETIME NOT NULL,
                                                   date_fin DATETIME NOT NULL,
                                                   statut ENUM('CONFIRMEE','EN_COURS','TERMINEE','ANNULEE') NOT NULL DEFAULT 'CONFIRMEE',
                                                   CONSTRAINT fk_rs_personne FOREIGN KEY (id_personne) REFERENCES personne(id_personne),
                                                   CONSTRAINT fk_rs_service FOREIGN KEY (id_service) REFERENCES service(id_service),
                                                   INDEX idx_rs_service_dates (id_service, date_debut, date_fin)
);

-- =========================
-- 8) INTERSECTION / ALLEE (si vous faites le graph plus tard)
-- =========================
CREATE TABLE IF NOT EXISTS intersection (
                                            id_intersection INT AUTO_INCREMENT PRIMARY KEY,
                                            position_x DOUBLE NOT NULL,
                                            position_y DOUBLE NOT NULL,
                                            type_intersection ENUM('ENTREE','SORTIE','VIRAGE','CROISEMENT') NOT NULL,
                                            id_zone INT NOT NULL,
                                            CONSTRAINT fk_intersection_zone FOREIGN KEY (id_zone) REFERENCES zone(id_zone)
);

CREATE TABLE IF NOT EXISTS allee (
                                     id_allee INT AUTO_INCREMENT PRIMARY KEY,
                                     id_intersection_debut INT NOT NULL,
                                     id_intersection_fin INT NOT NULL,
                                     distance DOUBLE NOT NULL,
                                     largeur DOUBLE DEFAULT 3.0,
                                     sens_circulation ENUM('BIDIRECTIONNEL','SENS_UNIQUE') NOT NULL,
                                     nom_allee VARCHAR(50),
                                     CONSTRAINT fk_allee_debut FOREIGN KEY (id_intersection_debut) REFERENCES intersection(id_intersection),
                                     CONSTRAINT fk_allee_fin FOREIGN KEY (id_intersection_fin) REFERENCES intersection(id_intersection)
);

