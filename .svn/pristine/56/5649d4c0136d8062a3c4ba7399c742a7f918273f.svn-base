CREATE TABLE prestad_telefono (
    id_prestador integer NOT NULL,
    id_telefono integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL
);


ALTER TABLE public.prestad_telefono OWNER TO postgres;

--
ALTER TABLE ONLY prestad_telefono
    ADD CONSTRAINT pk_prestad_telefono PRIMARY KEY (id_prestador, id_telefono);


--
ALTER TABLE ONLY prestad_telefono
    ADD CONSTRAINT fk_afi_telefono_t FOREIGN KEY (id_telefono) REFERENCES telefono(id_telefono) MATCH FULL;


--
ALTER TABLE ONLY prestad_telefono
    ADD CONSTRAINT fk_prestad_telefono_p FOREIGN KEY (id_prestador) REFERENCES prestador(id_prestador) MATCH FULL;


--
