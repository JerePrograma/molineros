alter table reintegro add column id_reintegro_protesis integer

ALTER TABLE reintegro RENAME id_reintegro_protesis TO id_reintegro_user

CREATE TABLE reintegro (
    id_reintegro integer DEFAULT nextval('reintegro_id_seq'::regclass) NOT NULL,
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    fecha timestamp without time zone NOT NULL,
    periodo timestamp without time zone NOT NULL,
    id_seccional integer NOT NULL,
    liquidado timestamp without time zone,
    auditado timestamp without time zone,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    estado integer,
    entidad character varying,
    tipo_reintegro character varying(3),
    observaciones character varying(1000),
    id_reintegro_user integer
);


ALTER TABLE public.reintegro OWNER TO postgres;

--
ALTER TABLE ONLY reintegro
    ADD CONSTRAINT pk_reintegro PRIMARY KEY (id_reintegro);


--
ALTER TABLE ONLY reintegro
    ADD CONSTRAINT "PK_rein_seccional" FOREIGN KEY (id_seccional) REFERENCES seccional(id_seccional) MATCH FULL;
--
