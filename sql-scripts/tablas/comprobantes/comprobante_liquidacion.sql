alter table comprobante_liquidacion drop constraint pk_comprobante_liquidacion

alter table comprobante_liquidacion add column alta_fecha timestamp without time zone NOT NULL  

CREATE TABLE comprobante_liquidacion (
    id_punto_venta smallint NOT NULL,
    id_liquidacion integer NOT NULL,
    compro_tipo character varying(3) NOT NULL,
    compro_nro character varying(50) NOT NULL,
    compro_letra character varying(1) not null,
    compro_sucu integer not null,
    cuit character(11) DEFAULT 0 NOT NULL,
    alta_fecha timestamp without time zone NOT NULL
);


ALTER TABLE public.comprobante_liquidacion OWNER TO postgres;

--
ALTER TABLE ONLY comprobante_liquidacion
    ADD CONSTRAINT pk_comprobante_liquidacion PRIMARY KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit);


--
ALTER TABLE ONLY comprobante_liquidacion
    ADD CONSTRAINT fk_comprobante_liquidacion_l FOREIGN KEY (id_liquidacion) REFERENCES liquidacion(id_liquidacion) MATCH FULL;