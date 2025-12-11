CREATE TABLE afi_creden_lote (
    id_lote integer NOT NULL,
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    alta_fecha timestamp with time zone,
    alta_usr character(50),
    baja_fecha timestamp with time zone,
    baja_usr character(50),
    modi_fecha timestamp with time zone,
    modi_usr character(50)
);


ALTER TABLE public.afi_creden_lote OWNER TO postgres;

--
ALTER TABLE ONLY afi_creden_lote
    ADD CONSTRAINT pk_afi_creden_lote PRIMARY KEY (id_lote, cuil_titular, inte);


--
