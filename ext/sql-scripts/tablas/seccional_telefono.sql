CREATE TABLE seccional_telefono (
    id_seccional integer NOT NULL,
    id_telefono integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL
);


ALTER TABLE public.seccional_telefono OWNER TO postgres;

--
ALTER TABLE ONLY seccional_telefono
    ADD CONSTRAINT pk_seccional_telefono PRIMARY KEY (id_seccional, id_telefono);


--
ALTER TABLE ONLY seccional_telefono
    ADD CONSTRAINT fk_seccional_telefono_secc FOREIGN KEY (id_seccional) REFERENCES seccional(id_seccional) MATCH FULL;


--
ALTER TABLE ONLY seccional_telefono
    ADD CONSTRAINT fk_seccional_telefono_tel FOREIGN KEY (id_telefono) REFERENCES telefono(id_telefono) MATCH FULL;


--
