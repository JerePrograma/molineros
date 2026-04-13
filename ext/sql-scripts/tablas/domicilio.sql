CREATE TABLE domicilio (
    id_domicilio integer DEFAULT nextval('domicilio_id_seq'::regclass) NOT NULL,
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
    numero character varying,
    localidad_nombre character(50),
    provincia_nombre character(50)
);


ALTER TABLE public.domicilio OWNER TO postgres;

--
ALTER TABLE ONLY domicilio
    ADD CONSTRAINT pk_domicilio PRIMARY KEY (id_domicilio);


--
