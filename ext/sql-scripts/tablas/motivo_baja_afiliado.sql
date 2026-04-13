CREATE TABLE motivo_baja_afiliado (
    descripcion character varying(100) NOT NULL,
    observaciones character varying(250) NOT NULL,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(15) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(15),
    meses_a_baja integer,
    id_motivo_baja integer
);


ALTER TABLE public.motivo_baja_afiliado OWNER TO postgres;

--
