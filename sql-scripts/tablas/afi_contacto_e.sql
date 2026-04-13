CREATE TABLE afi_contacto_e (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    id_contacto_e integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL
);


ALTER TABLE public.afi_contacto_e OWNER TO postgres;

--
ALTER TABLE ONLY afi_contacto_e
    ADD CONSTRAINT pk_afi_contacto_e PRIMARY KEY (cuil_titular, inte, id_contacto_e);


--
ALTER TABLE ONLY afi_contacto_e
    ADD CONSTRAINT fk_afi_contacto_e_afi FOREIGN KEY (cuil_titular, inte) REFERENCES afiliado(cuil_titular, inte) MATCH FULL;


--
ALTER TABLE ONLY afi_contacto_e
    ADD CONSTRAINT fk_afi_contacto_e_contacto_e FOREIGN KEY (id_contacto_e) REFERENCES contacto_e(id_contacto_e) MATCH FULL;


--
