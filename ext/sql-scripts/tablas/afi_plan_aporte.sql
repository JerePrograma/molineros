CREATE TABLE afi_plan_aporte (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    id_plan integer NOT NULL,
    id_aporte integer NOT NULL,
    plan_alta_fecha timestamp without time zone NOT NULL,
    aporte_alta_fecha timestamp with time zone NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15),
    modi_fecha timestamp without time zone,
    modi_usr character varying(15),
    baja_fecha timestamp without time zone,
    baja_usr character varying(15)
);


ALTER TABLE public.afi_plan_aporte OWNER TO postgres;

--
ALTER TABLE ONLY afi_plan_aporte
    ADD CONSTRAINT pk_afi_plan_aporte PRIMARY KEY (cuil_titular, inte, id_plan, id_aporte, plan_alta_fecha, aporte_alta_fecha, alta_fecha);


--
ALTER TABLE ONLY afi_plan_aporte
    ADD CONSTRAINT fk_afi_aportes FOREIGN KEY (cuil_titular, inte, id_aporte, aporte_alta_fecha) REFERENCES afi_aportes(cuil_titular, inte, id_aporte, alta_fecha);


--
ALTER TABLE ONLY afi_plan_aporte
    ADD CONSTRAINT fk_afi_plan FOREIGN KEY (cuil_titular, inte, id_plan, plan_alta_fecha) REFERENCES afi_plan(cuil_titular, inte, id_plan, alta_fecha);


--
