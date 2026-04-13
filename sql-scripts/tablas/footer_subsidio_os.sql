CREATE TABLE footer_subsidio_os (
    fecha_proceso timestamp without time zone NOT NULL,
    tiporegistro character varying(2),
    identificador character varying(9),
    codigoos character varying(6),
    cantidadregistrosde numeric(10,0),
    cantidadregistrosto numeric(10,0),
    cantidadregistrosdt numeric(10,0),
    cantidadregistros numeric(10,0),
    importesubsidio numeric(10,2),
    debitocredito character varying(1),
    importesubsidioreal numeric(10,2)
);


ALTER TABLE public.footer_subsidio_os OWNER TO postgres;

--
ALTER TABLE ONLY footer_subsidio_os
    ADD CONSTRAINT pk_footer_subsidio_os PRIMARY KEY (fecha_proceso);


--
