CREATE TABLE actividad (
    id_actividad integer NOT NULL,
    descripcion character varying(100) NOT NULL,
    observaciones character varying(250) NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.actividad OWNER TO postgres;

--
ALTER TABLE ONLY actividad
    ADD CONSTRAINT pk_actividad PRIMARY KEY (id_actividad);


--
