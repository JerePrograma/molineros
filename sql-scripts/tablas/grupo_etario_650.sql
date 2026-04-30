CREATE TABLE grupo_etario_650 (
    min integer,
    max integer,
    id integer NOT NULL
);


ALTER TABLE public.grupo_etario_650 OWNER TO postgres;

--
ALTER TABLE grupo_etario_650 ALTER COLUMN id SET DEFAULT nextval('grupo_etario_650_id_seq'::regclass);


--
