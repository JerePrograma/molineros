CREATE TABLE afi_domicilio (
    cuil_titular character varying(13) NOT NULL,
    inte integer NOT NULL,
    vigen_desde timestamp without time zone NOT NULL,
    domi_tipo character varying(1) NOT NULL,
    calle character varying(100) NOT NULL,
    piso character varying(5),
    depto character varying(4),
    oficina character varying(10),
    postal_codi character varying(4) NOT NULL,
    barrio character varying(50),
    telefono character varying(100),
    observaciones character varying(250),
    domi_val character varying(1) NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    provincia integer,
    localidad integer,
    numero character varying
);


ALTER TABLE public.afi_domicilio OWNER TO postgres;

--
ALTER TABLE ONLY afi_domicilio
    ADD CONSTRAINT pk_afi_domicilio PRIMARY KEY (cuil_titular, inte, vigen_desde);


--
ALTER TABLE ONLY afi_domicilio
    ADD CONSTRAINT fk_afi_domicilio_afi FOREIGN KEY (cuil_titular, inte) REFERENCES afiliado(cuil_titular, inte) MATCH FULL;


--
