CREATE TABLE prestador (
    id_prestador integer DEFAULT nextval('prestador_id_seq'::regclass) NOT NULL,
    cuit character varying(13),
    id_tipo_prestador smallint,
    tipo_matricula character(1),
    nro_matricula integer,
    contacto character varying(100),
    id_seccional integer,
    observaciones character varying(250),
    rein_liqui smallint,
    id_condicion_de_iva smallint,
    cheque_a_nombre_de character varying(150),
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    descripcion character varying(250),
    id_mat_categoria character(1),
    mat_provincia character varying,
    id_mat_provincia integer
);


ALTER TABLE public.prestador OWNER TO postgres;

--
ALTER TABLE ONLY prestador
    ADD CONSTRAINT pk_prestador PRIMARY KEY (id_prestador);


--
ALTER TABLE ONLY prestador
    ADD CONSTRAINT fk_tipo_prestador FOREIGN KEY (id_tipo_prestador) REFERENCES tipo_prestador(id_tipo_prestador) MATCH FULL;


--
