CREATE TABLE documento (
    id_documento integer NOT NULL,
    descripcion character varying(100) NOT NULL,
    observaciones character varying(250) NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    actualiza_afiliado smallint DEFAULT 0 NOT NULL,
    id_motivo_baja integer
);


ALTER TABLE public.documento OWNER TO postgres;

--
ALTER TABLE ONLY documento
    ADD CONSTRAINT pk_documento PRIMARY KEY (id_documento);


--
ALTER TABLE ONLY documento
    ADD CONSTRAINT fk_id_motivo_baja FOREIGN KEY (id_motivo_baja) REFERENCES motivo_baja(id_motivo_baja);


--
