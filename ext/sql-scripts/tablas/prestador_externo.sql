CREATE TABLE prestador_externo (
    id_prestador_externo integer DEFAULT nextval('prestador_externo_id_seq'::regclass) NOT NULL,
    cuit character varying(13),
    tipo_prestador_externo character varying(3), --odo --pre
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

ALTER TABLE public.prestador_externo OWNER TO postgres;
--
ALTER TABLE ONLY prestador_externo
    ADD CONSTRAINT pk_prestador_externo PRIMARY KEY (id_prestador_externo);