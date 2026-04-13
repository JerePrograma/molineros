CREATE TABLE ramo_empresa (
    id_ramo_empresa smallint NOT NULL,
    descripcion character varying(100) NOT NULL,
    observaciones character varying(250) NOT NULL,
    molinera boolean,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.ramo_empresa OWNER TO postgres;

--
ALTER TABLE ONLY ramo_empresa
    ADD CONSTRAINT pk_ramo_empresa PRIMARY KEY (id_ramo_empresa);


--
