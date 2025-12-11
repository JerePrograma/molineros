CREATE TABLE detalle_desempleo_anses (
    clave character varying,
    finpago character varying,
    tipo_doc integer,
    nro_doc integer,
    prov_emi character varying,
    cuil character varying,
    fecha_nac date,
    ape_nombre character varying,
    fecha_vig date,
    sexo character varying,
    fecha_ini_rel date,
    fecha_cese_rel date,
    cod_os integer,
    fecha_proceso date,
    cuil_titular character varying,
    cod_paren integer
);


ALTER TABLE public.detalle_desempleo_anses OWNER TO postgres;

--
