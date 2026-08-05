CREATE TABLE prestaci_prestadores_plan (
    id_prestacion integer NOT NULL,
    id_prestador integer NOT NULL,
    id_domicilio integer NOT NULL,
    id_plan integer NOT NULL,
    importe numeric(9,2)
);


ALTER TABLE public.prestaci_prestadores_plan OWNER TO postgres;

--
ALTER TABLE ONLY prestaci_prestadores_plan
    ADD CONSTRAINT pk_prestaci_prestadores_plan PRIMARY KEY (id_prestacion, id_prestador, id_plan, id_domicilio);


--
ALTER TABLE ONLY prestaci_prestadores_plan
    ADD CONSTRAINT fk_ppp_prstc_pln FOREIGN KEY (id_prestacion, id_plan) REFERENCES plan_prestacion(id_prestacion, id_plan) MATCH FULL;


--
ALTER TABLE ONLY prestaci_prestadores_plan
    ADD CONSTRAINT fk_ppp_prstc_prstdr FOREIGN KEY (id_prestacion, id_prestador, id_domicilio) REFERENCES prestacion_prestador(id_prestacion, id_prestador, id_domicilio) MATCH FULL;


--
