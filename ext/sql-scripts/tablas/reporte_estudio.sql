CREATE TABLE reporte_estudio (
    cuit_contribuyente character varying,
    cuil_aportante character varying,
    periodo date,
    aporte numeric,
    contribucion numeric,
    cant_afiliados integer,
    cant_afiliados_pagados integer,
    rem_pagada double precision,
    rem_declarada double precision,
    pagado double precision,
    calculado double precision,
    porc double precision,
    razon character varying,
    localidad character varying,
    provincia_id character varying,
    codigopostal character varying,
    numero character varying,
    ramo integer
);


ALTER TABLE public.reporte_estudio OWNER TO postgres;

--
