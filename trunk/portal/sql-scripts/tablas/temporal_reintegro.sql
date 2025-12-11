CREATE TABLE temporal_reintegro (
    registro character varying(15),
    periodo timestamp without time zone NOT NULL,
    id_seccional integer NOT NULL,
    seccional character varying(30) NOT NULL,
    letra character varying(15),
    sucu character varying(15),
    numero character varying(15),
    fecha character varying(15),
    relev character varying(15),
    fevto character varying(15),
    total character varying(15),
    impiva character varying(15),
    poriva character varying(15),
    nro_afiliado character varying(15),
    pare character varying(15),
    afiliado character varying(100),
    usuario character varying(15),
    fech_pres character varying(15),
    codigo character varying(15),
    prestacion character varying(100),
    cantidad character varying(3),
    importe character varying(15),
    sub_total character varying(15),
    debito character varying(15),
    autorizo character varying(50),
    observa character varying(50)
);


ALTER TABLE public.temporal_reintegro OWNER TO postgres;

--
