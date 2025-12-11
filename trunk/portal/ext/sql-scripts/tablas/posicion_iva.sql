CREATE TABLE posicion_iva (
    id_posicion integer NOT NULL,
    detalle character varying
);


ALTER TABLE public.posicion_iva OWNER TO postgres;

--
ALTER TABLE posicion_iva ALTER COLUMN id_posicion SET DEFAULT nextval('posicion_iva_id_posicion_seq'::regclass);


--
ALTER TABLE ONLY posicion_iva
    ADD CONSTRAINT pk_posicion_iva PRIMARY KEY (id_posicion);


--
