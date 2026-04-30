drop table pago_forma

CREATE TABLE pago_forma (
    id_punto_venta smallint NOT NULL,
    compro_tipo character varying(2) NOT NULL,
    compro_nro character varying(15) NOT NULL,
    id_chequera integer,
    fecha timestamp without time zone,
    vencimiento timestamp without time zone,
    importe numeric(9,2),
    id_cta_bco integer,
    nro_tramite character varying(25),
    id_concepto_ret integer,
    comprobante_ret character varying(25),
    cuit character(11) NOT NULL
);


ALTER TABLE public.pago_forma OWNER TO postgres;

--
ALTER TABLE ONLY pago_forma
    ADD CONSTRAINT pk_pago_forma PRIMARY KEY (id_punto_venta, compro_tipo, compro_nro);


--
ALTER TABLE ONLY pago_forma
    ADD CONSTRAINT fk_pago_forma_comprobante FOREIGN KEY (id_punto_venta, compro_tipo, compro_nro, cuit) REFERENCES comprobante(id_punto_venta, compro_tipo, compro_nro, cuit) MATCH FULL;


--
