CREATE TABLE liquidacion_desempleo (
    cuil character varying,
    cuil_titular character varying,
    docu_numero character varying,
    nombre text,
    fecha_nac date,
    sexo character varying,
    fecha_vig date,
    acredita text,
    importe numeric,
    neto numeric,
    prestadora character varying
);


ALTER TABLE public.liquidacion_desempleo OWNER TO postgres;

--
