CREATE TABLE localidad (
    id_localidad integer NOT NULL,
    id_provincia integer,
    detalle character varying
);


ALTER TABLE public.localidad OWNER TO postgres;

--
ALTER TABLE localidad ALTER COLUMN id_localidad SET DEFAULT nextval('localidad_id_localidad_seq'::regclass);


--
