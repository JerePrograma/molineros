CREATE TABLE detalle_declaracion_jurada (
    fecha_proceso date NOT NULL,
    codigoobrasocial character varying(6),
    periodo timestamp without time zone,
    cuit character varying(11),
    cuil character varying(11),
    remuneracionafectos numeric(10,2),
    importeadicionalos numeric(6,2),
    zona character varying(2),
    cantgrupofamiliar character varying(2),
    cantadherentesgrupofamiliar character varying(2),
    secobligacion integer,
    condicioncuil character varying(2),
    situacioncuil character varying(2),
    actividad character varying(2),
    modalidad integer,
    codigosiniestro character varying(2),
    aporteadicionalos numeric(6,2),
    versionaplicativo character varying(2),
    remuneraciondecreto1273_02 numeric(9,2),
    esposa character varying(1),
    excedenteaporteos numeric(10,2),
    declaroretenciones boolean,
    declaro boolean,
    fechapresentacion timestamp without time zone,
    fechaproceso timestamp without time zone,
    original character varying(1),
    importebasecontribucionos numeric(9,2)
);


ALTER TABLE public.detalle_declaracion_jurada OWNER TO postgres;

--
