CREATE TABLE convenio_actas (
    convenio_id integer NOT NULL,
    acta_id integer NOT NULL,
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


ALTER TABLE public.convenio_actas OWNER TO postgres;

--
ALTER TABLE convenio_actas ALTER COLUMN id SET DEFAULT nextval('convenio_actas_id_seq'::regclass);


--
ALTER TABLE ONLY convenio_actas
    ADD CONSTRAINT pk_convenio_actas PRIMARY KEY (id);


--
ALTER TABLE ONLY convenio_actas
    ADD CONSTRAINT "fk_convenio_Actas_acta" FOREIGN KEY (acta_id) REFERENCES acta(id);


--
ALTER TABLE ONLY convenio_actas
    ADD CONSTRAINT "fk_convenio_Actas_conv" FOREIGN KEY (convenio_id) REFERENCES convenio(id);


--
