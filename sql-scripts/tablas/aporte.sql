CREATE TABLE aporte (
    id_aporte integer NOT NULL,
    tipo_aporte character varying(3) NOT NULL,
    plan character varying(3) NOT NULL,
    descripcion character varying(100) NOT NULL,
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    genera_id_socio character(1)
);


ALTER TABLE public.aporte OWNER TO postgres;

--
ALTER TABLE ONLY aporte
    ADD CONSTRAINT pk_aporte PRIMARY KEY (id_aporte);


--
