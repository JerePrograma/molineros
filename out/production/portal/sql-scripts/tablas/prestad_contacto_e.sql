CREATE TABLE prestad_contacto_e (
    id_prestador integer NOT NULL,
    id_contacto_e integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL
);


ALTER TABLE public.prestad_contacto_e OWNER TO postgres;

--
ALTER TABLE ONLY prestad_contacto_e
    ADD CONSTRAINT pk_prestad_contacto_e PRIMARY KEY (id_prestador, id_contacto_e);


--
ALTER TABLE ONLY prestad_contacto_e
    ADD CONSTRAINT fk_prestad_contacto_e FOREIGN KEY (id_contacto_e) REFERENCES contacto_e(id_contacto_e) MATCH FULL;


--
ALTER TABLE ONLY prestad_contacto_e
    ADD CONSTRAINT fk_prestad_contacto_e_p FOREIGN KEY (id_prestador) REFERENCES prestador(id_prestador) MATCH FULL;


--
