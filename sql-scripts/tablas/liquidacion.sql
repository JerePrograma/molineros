update liquidacion set tercerizado = '0'

alter table liquidacion add column importe numeric(11,2)
alter table liquidacion add column debitado numeric(11,2)
alter table liquidacion add column observaciones character varying(1000)
alter table liquidacion add column tercerizado character varying(1)

CREATE TABLE liquidacion (
    id_liquidacion integer DEFAULT nextval('liquidacion_id_seq'::regclass) NOT NULL,
    id_prestador integer NOT NULL,
    id_domicilio integer NOT NULL,
    fecha timestamp without time zone,
    periodo timestamp without time zone,
    estado integer,
    entidad character varying,
    compro_a_debitar_tipo character varying(3),
    compro_a_debitar_letra character varying(1),
    sucu integer,
    compro_a_debitar_numero character varying(15),
    fecha_emitido timestamp without time zone,
    fecha_recibido timestamp without time zone,
    fecha_vencimiento timestamp without time zone,
    baja_fecha timestamp without time zone,
    alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(15) NOT NULL,
    modi_fecha timestamp without time zone,
    modi_usr character varying(15),
    baja_usr character varying(15),
    tipo_liquidacion character varying(3) NOT NULL,
    importe numeric(11,2),
    debitado numeric(11,2),
    observaciones character varying(1000),
    tercerizado character varying(1)
);


ALTER TABLE public.liquidacion OWNER TO postgres;

--
ALTER TABLE ONLY liquidacion
    ADD CONSTRAINT pk_liquidacion PRIMARY KEY (id_liquidacion);


--
ALTER TABLE ONLY liquidacion
    ADD CONSTRAINT fk_prestador FOREIGN KEY (id_prestador) REFERENCES prestador(id_prestador) MATCH FULL;


--
alter table liquidacion drop constraint fk_prestador