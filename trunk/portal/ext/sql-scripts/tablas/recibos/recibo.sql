alter table recibo alter column sucursal type character varying(6)

CREATE TABLE recibo (
    id integer NOT NULL,
    numero character varying(10),
    tipo character varying(2),
    fecha date,
    cuit character varying(13),
    sucursal  character varying(6),
    descripcion character varying(500),
    importe numeric(10,2),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
    ex_id character varying(10)
);


ALTER TABLE public.recibo OWNER TO postgres;

--
ALTER TABLE recibo ALTER COLUMN id SET DEFAULT nextval('recibo_id_seq'::regclass);


--
ALTER TABLE ONLY recibo
    ADD CONSTRAINT pk_recibo PRIMARY KEY (id);


alter table recibo
	add constraint fk_recibo_empresa foreign key (cuit, sucursal) references empresa (cuit,sucursal);

--
