CREATE TABLE seccional_contacto_e (
    id_seccional integer NOT NULL,
    id_contacto_e integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL
);


ALTER TABLE public.seccional_contacto_e OWNER TO postgres;

--
ALTER TABLE ONLY seccional_contacto_e
    ADD CONSTRAINT pk_seccional_contacto_e PRIMARY KEY (id_seccional, id_contacto_e);


--
ALTER TABLE ONLY seccional_contacto_e
    ADD CONSTRAINT fk_seccional_contacto_cont_e FOREIGN KEY (id_contacto_e) REFERENCES contacto_e(id_contacto_e) MATCH FULL;


--
ALTER TABLE ONLY seccional_contacto_e
    ADD CONSTRAINT fk_seccional_contacto_secc FOREIGN KEY (id_seccional) REFERENCES seccional(id_seccional) MATCH FULL;


--
