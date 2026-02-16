-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS lazaro_shipping;
USE lazaro_shipping;

-- Tabla base de Buques
CREATE TABLE buques (
    codigo_imo VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(255),
    calado_maximo DOUBLE CHECK (calado_maximo >= 5 AND calado_maximo <= 40)
);

-- Tabla de Portacontenedores (Herencia JOINED)
CREATE TABLE portacontenedores (
    buque_imo VARCHAR(20) PRIMARY KEY,
    num_max_teus INT,
    tipo_grua VARCHAR(255),
    FOREIGN KEY (buque_imo) REFERENCES buques(codigo_imo)
);

-- Tabla de Buques Cisterna (Herencia JOINED)
CREATE TABLE buques_cisterna (
    buque_imo VARCHAR(20) PRIMARY KEY,
    capacidad_carga_m3 DOUBLE,
    tipo_doble_casco VARCHAR(255),
    FOREIGN KEY (buque_imo) REFERENCES buques(codigo_imo)
);

-- Tabla de Capitanes
CREATE TABLE capitanes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    registro_maritimo VARCHAR(255),
    millas_navegadas DOUBLE
);

-- Tabla de Intervenciones (Relación ManyToOne)
CREATE TABLE intervenciones_astillero (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_entrada DATE,
    motivo VARCHAR(255),
    coste DOUBLE,
    buque_imo VARCHAR(20) NOT NULL,
    FOREIGN KEY (buque_imo) REFERENCES buques(codigo_imo)
);

-- Tabla intermedia de Certificaciones (Relación ManyToMany)
CREATE TABLE certificaciones_capitan_buque (
    capitan_id BIGINT,
    buque_imo VARCHAR(20),
    PRIMARY KEY (capitan_id, buque_imo),
    FOREIGN KEY (capitan_id) REFERENCES capitanes(id),
    FOREIGN KEY (buque_imo) REFERENCES buques(codigo_imo)
);