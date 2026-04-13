CREATE TABLE footer_declaracion_jurada (
    tiporegistro character varying(2) NOT NULL,
    codigoregistro character varying(8) NOT NULL,
    indicadordeproceso character varying(13) NOT NULL,
    fechaproceso timestamp without time zone NOT NULL,
    cantempleadosporig numeric(12,0) NOT NULL,
    cantempleadosprect numeric(12,0) NOT NULL,
    cantregistros numeric(12,0) NOT NULL
);


ALTER TABLE public.footer_declaracion_jurada OWNER TO postgres;

--
ALTER TABLE ONLY footer_declaracion_jurada
    ADD CONSTRAINT pk_footer_declaracion_jurada PRIMARY KEY (tiporegistro, codigoregistro, indicadordeproceso, fechaproceso, cantempleadosporig, cantempleadosprect, cantregistros);


--
