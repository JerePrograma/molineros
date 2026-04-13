CREATE TABLE liquidacion_actas (
    numero character varying(100),
    cuit character varying(100),
    empresa character varying(100),
    cuil character varying(100),
    periodo character varying(100),
    remune character varying(100),
    omint character varying(100),
    afiliado character varying(100),
    nuevo boolean DEFAULT false,
    fecha_obligacion character varying
);


ALTER TABLE public.liquidacion_actas OWNER TO postgres;

--
