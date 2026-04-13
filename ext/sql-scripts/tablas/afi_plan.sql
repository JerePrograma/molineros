CREATE TABLE afi_plan (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    id_plan integer NOT NULL,
    id_tarifa integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15),
    modi_fecha timestamp without time zone,
    modi_usr character varying(15),
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    id_motivo_baja integer
);


ALTER TABLE public.afi_plan OWNER TO postgres;

--
ALTER TABLE ONLY afi_plan
    ADD CONSTRAINT pk_afi_plan PRIMARY KEY (cuil_titular, inte, id_plan, alta_fecha);


--
ALTER TABLE ONLY afi_plan
    ADD CONSTRAINT fk_afi_plan_afi FOREIGN KEY (cuil_titular, inte) REFERENCES afiliado(cuil_titular, inte) MATCH FULL;


--
ALTER TABLE ONLY afi_plan
    ADD CONSTRAINT fk_afi_plan_plan FOREIGN KEY (id_plan) REFERENCES plan(id_plan) MATCH FULL;


--
ALTER TABLE ONLY afi_plan
    ADD CONSTRAINT fk_motivo_baja FOREIGN KEY (id_motivo_baja) REFERENCES motivo_baja(id_motivo_baja);


--
