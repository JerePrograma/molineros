CREATE TABLE footer_padron_contribuyentes (
    tiporegistro character varying(2) NOT NULL,
    codigoregistro character varying(8) NOT NULL,
    indicadordeproceso character varying(13) NOT NULL,
    fechaproceso timestamp without time zone NOT NULL,
    cantregistros numeric(12,0) NOT NULL
);


ALTER TABLE public.footer_padron_contribuyentes OWNER TO postgres;

--
ALTER TABLE ONLY footer_padron_contribuyentes
    ADD CONSTRAINT fk_footer_padron_contribuyentes PRIMARY KEY (tiporegistro, codigoregistro, indicadordeproceso, fechaproceso, cantregistros);


--
