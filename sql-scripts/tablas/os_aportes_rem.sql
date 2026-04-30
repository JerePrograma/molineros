CREATE TABLE os_aportes_rem (
    fecha_proceso date,
    hora_proceso character varying,
    concepto_transf character varying,
    importe numeric(9,2),
    deb_cred character varying,
    fecha_transf date,
    fecha_recauda date,
    cuit_contribuyente character varying,
    periodo date,
    num_oblig character varying,
    sec_oblic character varying,
    cuil_aportante character varying,
    banco character varying,
    sucur character varying,
    zona character varying,
    porc_reducc integer,
    porc_reducc2 integer,
    porc_reducc3 integer,
    grupo_fliar character varying,
    tipo_pago character varying,
    marca_apro character varying
);


ALTER TABLE public.os_aportes_rem OWNER TO postgres;

--
