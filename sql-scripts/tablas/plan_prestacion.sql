CREATE TABLE plan_prestacion (
    id_prestacion integer NOT NULL,
    id_plan integer NOT NULL,
    tope_cantidad smallint,
    tope_importe numeric(9,2),
    tope_individ_cantidad smallint,
    tope_individ_importe numeric(9,2)
);


ALTER TABLE public.plan_prestacion OWNER TO postgres;

--
ALTER TABLE ONLY plan_prestacion
    ADD CONSTRAINT pk_plan_prestacion PRIMARY KEY (id_prestacion, id_plan);


--
ALTER TABLE ONLY plan_prestacion
    ADD CONSTRAINT fk_plan_prest_nomenc FOREIGN KEY (id_prestacion) REFERENCES nomenclador(id_prestacion) MATCH FULL;


--
ALTER TABLE ONLY plan_prestacion
    ADD CONSTRAINT fk_plan_prest_plan FOREIGN KEY (id_plan) REFERENCES plan(id_plan) MATCH FULL;


--
