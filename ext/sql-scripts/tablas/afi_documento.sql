CREATE TABLE afi_documento (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    id_documento integer NOT NULL,
    fecha_ini date NOT NULL,
    fecha_vto date,
    observacion character varying(250),
    baja_fecha timestamp without time zone,
    alta_fecha timestamp without time zone,
    modi_fecha timestamp without time zone,
    alta_usr character varying(15),
    modi_usr character varying(15),
    baja_usr character varying(15)
);


ALTER TABLE public.afi_documento OWNER TO postgres;

--
ALTER TABLE ONLY afi_documento
    ADD CONSTRAINT pk_afi_documento PRIMARY KEY (cuil_titular, inte, id_documento, fecha_ini);


--
ALTER TABLE ONLY afi_documento
    ADD CONSTRAINT fk_afi_documento_afi FOREIGN KEY (cuil_titular, inte) REFERENCES afiliado(cuil_titular, inte) MATCH FULL;


--
ALTER TABLE ONLY afi_documento
    ADD CONSTRAINT fk_afi_documento_documento FOREIGN KEY (id_documento) REFERENCES documento(id_documento) MATCH FULL;

--