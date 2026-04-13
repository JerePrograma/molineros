alter table acta_periodos add column agregado_manual boolean default false;
CREATE TABLE acta_periodos (
    id integer NOT NULL,
    acta_id integer,
    periodo date,
    cuil character(11),
    remuneracion_declarada numeric(10,2),
    calculado numeric(10,2),
    decreto numeric(10,2),
    pagado numeric(10,2),
    pagado_fecha date,
    subtotal numeric(10,2),
    interes numeric(10,2),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    alta_ip character varying(15),
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    modi_ip character varying(15),
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    baja_ip character varying(15),
    apellido character varying(100),
    nombre character varying(100)
);


ALTER TABLE public.acta_periodos OWNER TO postgres;

--
ALTER TABLE acta_periodos ALTER COLUMN id SET DEFAULT nextval('acta_periodos_id_seq'::regclass);


--
ALTER TABLE ONLY acta_periodos
    ADD CONSTRAINT pk_acta_periodos PRIMARY KEY (id);


--
ALTER TABLE ONLY acta_periodos
    ADD CONSTRAINT fk_acta_periodos_acta FOREIGN KEY (acta_id) REFERENCES acta(id);


--
