create table concepto_comprobante_amtima (
    id_punto_venta smallint NOT NULL,
    compro_tipo character varying(3) NOT NULL,
    compro_nro character varying(50) NOT NULL,
    cuit character(11) DEFAULT 0 NOT NULL,
    compro_letra character varying(1) not null,
    compro_sucu integer not null,
    concepto_id integer not null,
    importe numeric(12,2),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);

ALTER TABLE ONLY concepto_comprobante_amtima
    add CONSTRAINT pk_concepto_comprobante_amtima PRIMARY KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit, concepto_id);


ALTER TABLE ONLY concepto_comprobante_amtima
    ADD CONSTRAINT fk_concepto_comprobante_amtima FOREIGN KEY (concepto_id) REFERENCES conceptos_amtima(id) MATCH FULL;

ALTER TABLE ONLY concepto_comprobante_amtima
    ADD CONSTRAINT fk_concepto_comprobante_comprobante_amtima FOREIGN KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit) REFERENCES comprobante_amtima(id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit) MATCH FULL;