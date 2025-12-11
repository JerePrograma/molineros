CREATE TABLE nacionalidad (
    id integer NOT NULL,
    detalle character varying,
    id_sssuper integer
);


ALTER TABLE public.nacionalidad OWNER TO postgres;

--
ALTER TABLE nacionalidad ALTER COLUMN id SET DEFAULT nextval('nacionalidades_id_seq'::regclass);


--
