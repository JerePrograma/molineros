CREATE TABLE prestad_lugar_atencion (
    id_prestador integer NOT NULL,
    id_domicilio integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL,
    baja_fecha timestamp without time zone
);


ALTER TABLE public.prestad_lugar_atencion OWNER TO postgres;

--
ALTER TABLE ONLY prestad_lugar_atencion
    ADD CONSTRAINT pk_lugar_atencion PRIMARY KEY (id_prestador, id_domicilio);


--
ALTER TABLE ONLY prestad_lugar_atencion
    ADD CONSTRAINT fk_prestad_lug_domi FOREIGN KEY (id_domicilio) REFERENCES domicilio(id_domicilio) MATCH FULL;


--
ALTER TABLE ONLY prestad_lugar_atencion
    ADD CONSTRAINT fk_prestad_lug_prestad FOREIGN KEY (id_prestador) REFERENCES prestador(id_prestador) MATCH FULL;


--
