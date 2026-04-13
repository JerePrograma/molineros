CREATE TABLE provincia (
    id_provincia integer NOT NULL,
    detalle character varying,
    id_sssalud integer
);


ALTER TABLE public.provincia OWNER TO postgres;

--
ALTER TABLE provincia ALTER COLUMN id_provincia SET DEFAULT nextval('provincia_id_provincia_seq'::regclass);


--
