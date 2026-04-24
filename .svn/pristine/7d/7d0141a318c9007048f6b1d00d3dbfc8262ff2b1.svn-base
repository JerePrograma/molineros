alter table contrato_detalle drop constraint fk_contrato_detalle_prestacion

alter table contrato_detalle add column servicio character varying(30); 

-- Table: contrato_detalle

-- DROP TABLE contrato_detalle

CREATE TABLE contrato_detalle
(
  id_contrato_detalle integer NOT NULL DEFAULT nextval('contrato_detalle_id_seq'::regclass),
  id_contrato integer NOT NULL,
  fecha_desde timestamp without time zone NOT NULL,
  fecha_hasta timestamp without time zone,
  id_prestacion_desde integer not null,
  codigo_desde character varying(10) NOT NULL,
  id_prestacion_hasta integer,
  codigo_hasta character varying(10),
  id_plan integer, --todos los id_plan mas la opción 'todos' que es 0
  id_cartilla integer, --no dr usa por ahroa
  coseguro numeric(11,2),
  tipo_valorizacion character varying(10),
  honorarios numeric(11,2),
  gastos numeric(11,2),
  importe_total numeric(11,2),  
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  servicio character varying(30)

  CONSTRAINT pk_contrato_detalle PRIMARY KEY (id_contrato_detalle),
  CONSTRAINT fk_contrato_detalle_contrato  FOREIGN KEY (id_contrato)
      REFERENCES contrato (id_contrato) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
