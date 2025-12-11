CREATE TABLE os_aportes_detalle (
    fecha_proceso date NOT NULL,
    hora_proceso character varying NOT NULL,
    concepto_transf character varying,
    importe numeric(9,2),
    deb_cred character varying,
    fecha_transf date,
    fecha_recauda date,
    cuit_contribuyente character varying NOT NULL,
    periodo date,
    num_oblig character varying,
    sec_oblic character varying,
    cuil_aportante character varying NOT NULL,
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


ALTER TABLE public.os_aportes_detalle OWNER TO postgres;

--
ALTER TABLE ONLY os_aportes_detalle
    ADD CONSTRAINT fk_os_aportes_detalle_footer FOREIGN KEY (fecha_proceso, hora_proceso) REFERENCES os_aportes_footer(fecha_proceso, hora_proceso) MATCH FULL;


--
