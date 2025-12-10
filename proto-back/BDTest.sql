
CREATE DATABASE IF NOT EXISTS BDTest;
USE BDTest;


-- username  intellijP IDENTIFIED BY 'toto'; --
-- mysql -u intellijP -p -h 172.31.252.33 BDTest comm pour test la communication bd --


CREATE TABLE IF NOT EXISTS sample (
                                      id_sample INT AUTO_INCREMENT PRIMARY KEY,
                                      date_sample DATE NOT NULL,
                                      string_sample VARCHAR(50),
    float_sample FLOAT,
    sample_type ENUM('SAMPLE_TYPE1', 'SAMPLE_TYPE2')
    );


INSERT INTO sample (date_sample, string_sample, float_sample, sample_type)
VALUES
    ('2017-03-14', '3', 2, 'SAMPLE_TYPE1'),
    ('2018-03-14', '2', 3, 'SAMPLE_TYPE2'),
    ('2017-03-14', '1', 4, 'SAMPLE_TYPE1'),
    ('2019-03-14', '4', 1, 'SAMPLE_TYPE2');

