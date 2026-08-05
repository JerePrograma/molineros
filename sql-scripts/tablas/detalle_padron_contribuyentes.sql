CREATE TABLE detalle_padron_contribuyentes (
    fecha_proceso date NOT NULL,
    cuit numeric(13,0),
    razonsocial character varying(50),
    calle character varying(20),
    numero character varying(7),
    piso character varying(2),
    dpto character varying(3),
    localidad character varying(20),
    provincia character varying(3),
    codigopostal character varying(5)
);


ALTER TABLE public.detalle_padron_contribuyentes OWNER TO postgres;

--
