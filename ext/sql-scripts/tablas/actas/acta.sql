alter table acta alter column sucursal type character varying(6)

CREATE TABLE acta (
    id integer NOT NULL,
    numero character varying(8),
    cuit character varying(13),
    sucursal character varying(6),
    fecha_inicio timestamp without time zone NOT NULL,
    fecha_pago timestamp without time zone NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    alta_ip character varying(15),
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    modi_ip character varying(15),
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    baja_ip character varying(15),
    otros numeric(10,2),
    interes numeric(10,2),
    capital numeric(10,2),
    deuda_actas_asociadas numeric(10,2),
    cierre_fecha timestamp without time zone,
    cierre_usr character varying(15),
    ex_id character varying(10),
    acta_cerrada boolean not null default false,
    molinera boolean
);


ALTER TABLE public.acta OWNER TO postgres;

--
ALTER TABLE acta ALTER COLUMN id SET DEFAULT nextval('acta_id_seq'::regclass);


--
ALTER TABLE ONLY acta
    ADD CONSTRAINT pk_acta PRIMARY KEY (id);


--
ALTER TABLE ONLY acta
    ADD CONSTRAINT fk_acta_empresa FOREIGN KEY (cuit, sucursal) REFERENCES empresa(cuit, sucursal);


--
