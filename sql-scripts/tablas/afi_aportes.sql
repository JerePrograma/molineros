CREATE TABLE afi_aportes (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    id_aporte integer NOT NULL,
    fecha_ingre date NOT NULL,
    fecha_egre date,
    alta_usr character(50),
    baja_usr character(50),
    baja_fecha timestamp with time zone,
    modi_fecha timestamp with time zone,
    modi_usr character(50),
    alta_fecha timestamp with time zone NOT NULL,
    id_motivo_baja integer
);


ALTER TABLE public.afi_aportes OWNER TO postgres;

--
ALTER TABLE ONLY afi_aportes
    ADD CONSTRAINT pk_afi_aportes PRIMARY KEY (cuil_titular, inte, id_aporte, alta_fecha);


--
ALTER TABLE ONLY afi_aportes
    ADD CONSTRAINT fk_afi_aportes_afi FOREIGN KEY (cuil_titular, inte) REFERENCES afiliado(cuil_titular, inte) MATCH FULL;


--
ALTER TABLE ONLY afi_aportes
    ADD CONSTRAINT fk_afi_aportes_apor FOREIGN KEY (id_aporte) REFERENCES aporte(id_aporte) MATCH FULL;


--
ALTER TABLE ONLY afi_aportes
    ADD CONSTRAINT fk_motivo_baja FOREIGN KEY (id_motivo_baja) REFERENCES motivo_baja(id_motivo_baja);


--
