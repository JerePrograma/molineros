CREATE TABLE temporal_creden (
    cuil character varying(13) NOT NULL,
    inte integer NOT NULL,
    afiliado character varying(100) NOT NULL,
    parentesco character varying(50) NOT NULL,
    categoria character varying(50) NOT NULL,
    tipo character varying(13) NOT NULL,
    documento character varying(13) NOT NULL,
    naci_fecha character varying(13),
    ingreso character varying(50),
    baja character varying(13),
    seccional character varying(50) NOT NULL,
    empresa character varying(100) NOT NULL,
    plan character varying(50) NOT NULL,
    ospim integer,
    amtima integer,
    uoma integer,
    impreso character varying(50) NOT NULL,
    por character varying
);


ALTER TABLE public.temporal_creden OWNER TO postgres;

--
