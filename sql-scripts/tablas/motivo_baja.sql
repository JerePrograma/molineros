CREATE TABLE motivo_baja (
    id_motivo_baja smallint NOT NULL,
    descripcion character varying(100) NOT NULL,
    observaciones character varying(250) NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    meses_a_baja integer DEFAULT 0
);


ALTER TABLE public.motivo_baja OWNER TO postgres;

--
ALTER TABLE ONLY motivo_baja
    ADD CONSTRAINT pk_motivo_baja PRIMARY KEY (id_motivo_baja);


--
