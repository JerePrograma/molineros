CREATE TABLE afiliados_vigentes_result (
    id_ospim integer,
    seccional character varying,
    id_tercerizadora character varying(3),
    cuil_titular character varying(13),
    cuil character varying(13),
    inte integer,
    parentesco character varying(100),
    apellido character varying(100),
    nombre character varying(100),
    documento_tipo character varying(4),
    docu_numero character varying(15),
    naci_fecha date,
    sexo character varying(2),
    civil_esta character varying(20),
    nacionalidad character varying,
    provincia character varying,
    localidad character varying,
    postal_codi character varying(4),
    calle character varying(100),
    numero character varying,
    piso character varying(5),
    depto character varying(4),
    telefono character varying(100),
    categoria character(50),
    ramo character(50),
    id_plan integer,
    plan character(50),
    ingre_fecha date,
    baja_fecha timestamp without time zone,
    id_uoma integer,
    cuit character(11),
    fecha_ospim date
);


ALTER TABLE public.afiliados_vigentes_result OWNER TO postgres;

--
