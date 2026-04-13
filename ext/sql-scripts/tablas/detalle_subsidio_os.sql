CREATE TABLE detalle_subsidio_os (
    fecha_proceso timestamp without time zone NOT NULL,
    tiporegistro character varying(2),
    cuit character varying(11),
    cuil character varying(11),
    codigoos character varying(6),
    periodo timestamp without time zone,
    remuneracionafectos numeric(10,2),
    aportesos numeric(10,2),
    contirbucionos numeric(10,2),
    subsidio numeric(10,2),
    obrasocialrel character varying(6),
    indpartot character varying(1),
    debitocredito character varying(1),
    motivoexcepcion character varying(1),
    capita numeric(12,0),
    hombre0a14 character varying(2),
    hombre15a19 character varying(2),
    hombre50a64 character varying(2),
    hombre65a99 character varying(2),
    mujer0a14 character varying(2),
    mujer15a49 character varying(2),
    mujer50a64 character varying(2),
    mujer65a99 character varying(2)
);


ALTER TABLE public.detalle_subsidio_os OWNER TO postgres;

--
