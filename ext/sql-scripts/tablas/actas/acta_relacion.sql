CREATE TABLE acta_relacion (
    acta_id integer,
    acta_relacionada_id integer,
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


ALTER TABLE public.acta_relacion OWNER TO postgres;

--
ALTER TABLE acta_relacion ALTER COLUMN id SET DEFAULT nextval('acta_relacion_id_seq'::regclass);


--
ALTER TABLE ONLY acta_relacion
    ADD CONSTRAINT pk_acta_relacion PRIMARY KEY (id);


--
ALTER TABLE ONLY acta_relacion
    ADD CONSTRAINT "fk_Acta_relac_relac" FOREIGN KEY (acta_relacionada_id) REFERENCES acta(id);


--
ALTER TABLE ONLY acta_relacion
    ADD CONSTRAINT fk_acta_relac_acta FOREIGN KEY (acta_id) REFERENCES acta(id);


--
