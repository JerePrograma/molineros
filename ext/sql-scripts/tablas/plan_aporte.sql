CREATE TABLE plan_aporte (
    id_plan integer NOT NULL,
    id_aporte integer NOT NULL
);


ALTER TABLE public.plan_aporte OWNER TO postgres;

--
ALTER TABLE ONLY plan_aporte
    ADD CONSTRAINT pk_plan_aporte PRIMARY KEY (id_plan, id_aporte);


--
ALTER TABLE ONLY plan_aporte
    ADD CONSTRAINT fk_plan_aporte_aporte FOREIGN KEY (id_aporte) REFERENCES aporte(id_aporte) MATCH FULL;


--
ALTER TABLE ONLY plan_aporte
    ADD CONSTRAINT fk_plan_aporte_plan FOREIGN KEY (id_plan) REFERENCES plan(id_plan) MATCH FULL;


--
