CREATE TABLE convenio_relacion (
    convenio_id integer NOT NULL,
    convenio_relacionado_id integer NOT NULL,
    importe numeric(10,2),
    saldo numeric(10,2),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    alta_ip character varying(15),
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    modi_ip character varying(15),
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    baja_ip character varying(15),
    id integer NOT NULL
);


ALTER TABLE public.convenio_relacion OWNER TO postgres;

--
ALTER TABLE convenio_relacion ALTER COLUMN id SET DEFAULT nextval('convenio_relacion_id_seq'::regclass);


--
ALTER TABLE ONLY convenio_relacion
    ADD CONSTRAINT pk_convenio_relacion PRIMARY KEY (id);


--
ALTER TABLE ONLY convenio_relacion
    ADD CONSTRAINT fk_convenio_rel_c FOREIGN KEY (convenio_id) REFERENCES convenio(id);


--
ALTER TABLE ONLY convenio_relacion
    ADD CONSTRAINT fk_convenio_rel_rel FOREIGN KEY (convenio_relacionado_id) REFERENCES convenio(id);


--
