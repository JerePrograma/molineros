CREATE TABLE empresa01092010 (
    cuit character varying(13) NOT NULL,
    sucursal character varying(4) NOT NULL,
    razon_soc character varying(200) NOT NULL,
    localidad character varying(200) NOT NULL,
    provincia character varying(200) NOT NULL,
    calle character varying(200) NOT NULL,
    altura character varying(200) NOT NULL,
    piso character varying(200) NOT NULL,
    dto character varying(200) NOT NULL,
    tel1 character varying(200) NOT NULL,
    tel2 character varying(200) NOT NULL,
    tel3 character varying(200) NOT NULL,
    fax character varying(200) NOT NULL,
    mail character varying(200) NOT NULL,
    contacto character varying(250),
    id_ramo_empresa character varying(250),
    id_seccional character varying(250)
);


ALTER TABLE public.empresa01092010 OWNER TO postgres;

--
