\c prueba
CREATE TABLE users (
    user_id       INTEGER,
    user_email    VARCHAR(128) NOT NULL,
    user_name     VARCHAR(100) NOT NULL,
    user_surname1 VARCHAR(100) NOT NULL,
    user_surname2 VARCHAR(100),
    PRIMARY KEY (user_id)
);

CREATE SEQUENCE t_user_user_id_seq start 1;

INSERT INTO users VALUES (nextval('t_user_user_id_seq'),'info1@ust-global.com','Nombre 1','Apellido1 1','Apellido2 1');
INSERT INTO users VALUES (nextval('t_user_user_id_seq'),'info2@ust-global.com','Nombre 2','Apellido1 2','Apellido2 2');
INSERT INTO users VALUES (nextval('t_user_user_id_seq'),'info3@ust-global.com','Nombre 3','Apellido1 3','Apellido2 3');
INSERT INTO users VALUES (nextval('t_user_user_id_seq'),'info4@ust-global.com','Nombre 4','Apellido1 4','Apellido2 4');