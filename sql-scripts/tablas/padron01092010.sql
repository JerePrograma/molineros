CREATE TABLE padron01092010 (
    id_seccional character varying,
    seccional character varying(50),
    id_tercerizadora character varying(100),
    cuil character varying(20),
    cuip character varying(20),
    pare character varying(50),
    parentesco character varying(200),
    apellido character varying(100),
    nombre character varying(100),
    tdoc character varying(50),
    documento character varying(50),
    f_nac character varying(50),
    sexo character varying(50),
    est_civil character varying(50),
    nacion character varying(50),
    provincia character varying(50),
    localidad character varying(50),
    cp character varying(50),
    direccion character varying(250),
    altura character varying(50),
    piso character varying(10),
    dpto character varying(10),
    telefono character varying(50),
    plan character varying(100),
    ingreso character varying(50),
    baja character varying(50),
    unifica character varying(50),
    revista character varying,
    disca character varying,
    osanterior character varying,
    sssalud character varying
);


ALTER TABLE public.padron01092010 OWNER TO postgres;

--
