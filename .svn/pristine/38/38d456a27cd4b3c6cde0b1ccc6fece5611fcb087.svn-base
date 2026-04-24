CREATE TABLE telefono (
    id_telefono integer DEFAULT nextval('telefono_id_seq'::regclass) NOT NULL,
    tipo_tele character varying(1) NOT NULL,
    vigen_desde timestamp without time zone NOT NULL,
    codigo_pais character varying(4),
    codigo_area character varying,
    numero character varying(30) NOT NULL,
    extension character varying(8),
    observaciones character varying(250),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.telefono OWNER TO postgres;

--
ALTER TABLE ONLY telefono
    ADD CONSTRAINT pk_telefonos PRIMARY KEY (id_telefono);


--
ALTER TABLE ONLY telefono
    ADD CONSTRAINT fk_tipo_telefono FOREIGN KEY (tipo_tele) REFERENCES tipo_telefono(id_tipo_telefono) MATCH FULL;


--
