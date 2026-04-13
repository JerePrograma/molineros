CREATE TABLE os_aportes_footer (
    fecha_proceso date NOT NULL,
    hora_proceso character varying NOT NULL,
    cant_reg integer NOT NULL,
    importe_total double precision NOT NULL,
    deb_cred character varying NOT NULL,
    cant_reg_tn integer NOT NULL,
    cant_trf_nom integer NOT NULL,
    importe_trf_nom double precision NOT NULL,
    deb_cred2 character varying NOT NULL
);


ALTER TABLE public.os_aportes_footer OWNER TO postgres;

--
ALTER TABLE ONLY os_aportes_footer
    ADD CONSTRAINT pk_os_aportes_footer PRIMARY KEY (fecha_proceso, hora_proceso);


--
