CREATE TABLE categoria_laboral (
    id_categoria integer NOT NULL,
    categoria character varying(100) NOT NULL,
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    id_sssuper integer
);


ALTER TABLE public.categoria_laboral OWNER TO postgres;

--
ALTER TABLE ONLY categoria_laboral
    ADD CONSTRAINT pk_categoria_laboral PRIMARY KEY (id_categoria);


--
