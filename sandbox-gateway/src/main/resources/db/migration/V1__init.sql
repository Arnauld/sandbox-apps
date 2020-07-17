CREATE SEQUENCE vat_id_seq;

CREATE TABLE vat
(
    id                INTEGER NOT NULL DEFAULT nextval('vat_id_seq'),
    name              VARCHAR(255),
    description       VARCHAR(255),
    rate              INTEGER,
    activation_date   TIMESTAMP with time zone,
    deactivation_date TIMESTAMP with time zone,
    PRIMARY KEY (id)
);
ALTER SEQUENCE vat_id_seq OWNED BY vat.id;


CREATE SEQUENCE fare_family_id_seq;
CREATE TABLE fare_family
(
    id                   INTEGER NOT NULL DEFAULT nextval('fare_family_id_seq'),
    name                 VARCHAR(255),
    description          VARCHAR(255),
    fare_family_id       INTEGER,
    activation_date      TIMESTAMP WITH TIME ZONE,
    deactivation_date    TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);
ALTER SEQUENCE fare_family_id_seq OWNED BY fare_family.id;

CREATE TABLE fare_product
(
    id                   UUID NOT NULL,
    name                 VARCHAR(255),
    description          VARCHAR(255),
    fare_family_id       INTEGER,
    max_journey_duration INTEGER,
    max_connection_nbr   INTEGER,
    antipassback_enabled BOOLEAN,
    vat_id               INT4,
    PRIMARY KEY (id)
);

ALTER TABLE fare_product
    ADD CONSTRAINT fk_vat FOREIGN KEY (vat_id) REFERENCES vat;
ALTER TABLE fare_product
    ADD CONSTRAINT fk_fare_family FOREIGN KEY (fare_family_id) REFERENCES fare_family;
