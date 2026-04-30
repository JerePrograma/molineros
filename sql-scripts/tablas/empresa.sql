alter table empresa alter column sucursal type character varying(6)

CREATE TABLE empresa (
    cuit character varying(13) NOT NULL,
    sucursal character varying(6) NOT NULL,
    razon_soc character varying(200) NOT NULL,
    nombre_fantasia character varying(200) NOT NULL,
    id_ramo_empresa smallint,
    id_seccional integer,
    contacto character varying(250),
    id_entidad_cam_empresa smallint,
    observaciones character varying(250),
    vigen_fecha timestamp without time zone NOT NULL,
    motivo_baja character varying(3),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    alta_ip character varying(15),
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    modi_ip character varying(15),
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    baja_ip character varying(15),
    id_posicion_iva smallint
);


ALTER TABLE public.empresa OWNER TO postgres;

--
ALTER TABLE ONLY empresa
    ADD CONSTRAINT pk_empresa PRIMARY KEY (cuit, sucursal);


--
ALTER TABLE ONLY empresa
    ADD CONSTRAINT fk_emp_entidad_cam_empresa FOREIGN KEY (id_entidad_cam_empresa) REFERENCES entidad_cam_empresa(id_entidad_cam_empresa) MATCH FULL;


--
ALTER TABLE ONLY empresa
    ADD CONSTRAINT fk_emp_ramo_empresa FOREIGN KEY (id_ramo_empresa) REFERENCES ramo_empresa(id_ramo_empresa) MATCH FULL;


--
ALTER TABLE ONLY empresa
    ADD CONSTRAINT fk_posicion_iva FOREIGN KEY (id_posicion_iva) REFERENCES posicion_iva(id_posicion);


--
