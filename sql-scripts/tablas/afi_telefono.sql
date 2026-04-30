CREATE TABLE afi_telefono (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    id_telefono integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL
);


ALTER TABLE public.afi_telefono OWNER TO postgres;

--
ALTER TABLE ONLY afi_telefono
    ADD CONSTRAINT pk_afi_telefono PRIMARY KEY (cuil_titular, inte, id_telefono);


--
ALTER TABLE ONLY afi_telefono
    ADD CONSTRAINT fk_afi_telefono_afi FOREIGN KEY (cuil_titular, inte) REFERENCES afiliado(cuil_titular, inte) MATCH FULL;


--
ALTER TABLE ONLY afi_telefono
    ADD CONSTRAINT fk_afi_telefono_telefono FOREIGN KEY (id_telefono) REFERENCES telefono(id_telefono) MATCH FULL;


--
