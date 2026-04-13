CREATE TABLE especialidad (
    id_especialidad integer NOT NULL,
    id_tipo_nomenclador integer NOT NULL,
    descripcion character varying(150) NOT NULL,
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.especialidad OWNER TO postgres;

--
ALTER TABLE ONLY especialidad
    ADD CONSTRAINT pk_especialidad PRIMARY KEY (id_especialidad);


--
ALTER TABLE ONLY especialidad
    ADD CONSTRAINT fk_espe_tipo_nomenc FOREIGN KEY (id_tipo_nomenclador) REFERENCES tipo_nomenclador(id_tipo_nomenclador) MATCH FULL;


--
