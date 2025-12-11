CREATE TABLE entidad_cam_empresa (
    id_entidad_cam_empresa smallint NOT NULL,
    descripcion character varying(100) NOT NULL,
    observaciones character varying(250) NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.entidad_cam_empresa OWNER TO postgres;

--
ALTER TABLE ONLY entidad_cam_empresa
    ADD CONSTRAINT pk_entidad_cam_empresa PRIMARY KEY (id_entidad_cam_empresa);


--
