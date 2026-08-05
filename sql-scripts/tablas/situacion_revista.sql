CREATE TABLE situacion_revista (
    id_situ_revista integer NOT NULL,
    detalle character varying(100) NOT NULL,
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    id_revista_sssalud integer
);


ALTER TABLE public.situacion_revista OWNER TO postgres;

--
ALTER TABLE ONLY situacion_revista
    ADD CONSTRAINT pk_situacion_revista PRIMARY KEY (id_situ_revista);


--
