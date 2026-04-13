drop table reintegro_prestacion_odo

CREATE TABLE reintegro_prestacion_odo_protesis (
	id integer not null,
    id_reintegro integer NOT NULL,
    id_prestador_odonto integer NOT NULL,
    id_prestacion integer NOT NULL,
    id_plan integer NOT NULL,
    fecha_prestacion timestamp without time zone NOT NULL,
    pieza character varying(2),
    cara character varying(5),
    honorarios numeric(9,2),
    gastos numeric(9,2),
    tercerizado boolean,
    importe numeric(9,2),
    compro_a_debitar_tipo character varying(3),
    compro_a_debitar_numero character varying(15),
    ex_id numeric,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
);

ALTER TABLE public.reintegro_prestacion_odo_protesis OWNER TO postgres;

--
ALTER TABLE ONLY reintegro_prestacion_odo_protesis
    ADD CONSTRAINT pk_reintegro_prestacion_odo_protesis PRIMARY KEY (id);

ALTER TABLE ONLY reintegro_prestacion_odo_protesis
    ADD CONSTRAINT fk_reint_prestc_prot_reint FOREIGN KEY (id_reintegro) REFERENCES reintegro(id_reintegro) MATCH FULL;
