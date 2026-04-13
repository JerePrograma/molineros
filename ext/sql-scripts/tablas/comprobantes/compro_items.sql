alter table compro_items add column motivo integer;


CREATE TABLE compro_items (
    id_punto_venta smallint NOT NULL,
    compro_tipo character varying(3) NOT NULL,
    compro_nro character varying(13) NOT NULL,
    item integer NOT NULL,

    porcentaje numeric(12,2) NOT NULL,
    valor numeric(12,2) NOT NULL,
    ivains numeric(12,2) NOT NULL,
    ivanins numeric(12,2) NOT NULL,
    ivaexen numeric(12,2) NOT NULL,
    saldo numeric(12,2) NOT NULL,
    
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    
    cuit character varying(11) NOT NULL,
    compro_letra character varying(1) not null,
    compro_sucu integer not null,
    motivo integer

);

ALTER TABLE public.compro_items OWNER TO postgres;

--
ALTER TABLE ONLY compro_items
    ADD CONSTRAINT pk_compro_items PRIMARY KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit, item);

--
ALTER TABLE ONLY compro_items
    ADD CONSTRAINT fk_compro_items_compro FOREIGN KEY (id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit) REFERENCES comprobante(id_punto_venta, compro_tipo, compro_letra, compro_sucu, compro_nro, cuit) MATCH FULL;


--
