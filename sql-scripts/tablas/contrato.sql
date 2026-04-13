drop table contrato;

CREATE TABLE contrato
(
  id_contrato integer NOT NULL DEFAULT nextval('contrato_id_seq'::regclass),  
  id_prestador integer NOT NULL, 
  estado integer NOT NULL,-- 1. cargado, 2. aprobado, 3. rechazado.
  dia_recepcion integer,
  condicion_de_pago character varying (10), --30 días, 60 días.
  id_tipo_pago int,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  CONSTRAINT pk_contrato PRIMARY KEY (id_contrato),
  CONSTRAINT fk_contrato_tipo_pago FOREIGN KEY (id_tipo_pago) REFERENCES tipo_pago (id_tipo_pago)
);
  