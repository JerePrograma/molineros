CREATE TABLE contacto_e (
    id_contacto_e integer DEFAULT nextval('contacto_e_id_seq'::regclass) NOT NULL,
    tipo_contacto_e character varying(1) NOT NULL,
    vigen_desde timestamp without time zone NOT NULL,
    contacto character varying(100),
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.contacto_e OWNER TO postgres;

--
ALTER TABLE ONLY contacto_e
    ADD CONSTRAINT pk_contacto_e PRIMARY KEY (id_contacto_e);


--
ALTER TABLE ONLY contacto_e
    ADD CONSTRAINT fk_tipo_contacto FOREIGN KEY (tipo_contacto_e) REFERENCES tipo_contacto_e(id_tipo_contacto_e) MATCH FULL;


--
