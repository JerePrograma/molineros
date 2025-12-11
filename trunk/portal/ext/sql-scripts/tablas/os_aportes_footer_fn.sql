CREATE TABLE os_aportes_footer_fn (
    fecha_proceso date NOT NULL,
    hora_proceso character varying NOT NULL,
    cant_trf_nom integer NOT NULL,
    importe_nom numeric(9,2) NOT NULL,
    deb_cred character varying NOT NULL,
    cant_trf_fdo integer NOT NULL,
    importe_fdo_res numeric(9,2) NOT NULL,
    deb_cred2 character varying NOT NULL,
    cant_trf_ant integer NOT NULL,
    importe_ant numeric(9,2) NOT NULL,
    deb_cred3 character varying NOT NULL,
    saldo_ant_sin_nominar numeric(9,2) NOT NULL,
    secuencia_reg character varying
);


ALTER TABLE public.os_aportes_footer_fn OWNER TO postgres;

--
