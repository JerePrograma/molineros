CREATE TABLE uoma.correspondencia
(
  alta_fecha timestamp without time zone,
  baja_fecha timestamp without time zone,
  modi_fecha timestamp without time zone,
  alta_usr character varying,
  modi_usr character varying,
  baja_usr character varying,
  destino character varying,
  id_domicilio_remitente integer,
  id_domicilio_destinatario integer,
  apellido_remitente character varying,
  nombre_remitente character varying,
  apellido_destinatario character varying,
  nombre_destinatario character varying,
  fecha_envio_recepcion date,
  tipo_correo integer,
  edificio_recep character varying,
  observaciones character varying,
  seccional_remitente integer,
  seccional_destinatario integer,
  edificio_origen character varying,
  edificio_destino character varying,
  id_correspondencia integer NOT NULL DEFAULT nextval('uoma.correspondencia_id_seq'::regclass),
  razon_prestador_remitente character varying,
  razon_prestador_destinatario character varying,
  gastos_seccional boolean,
  reintegro boolean,
  padrones boolean,
  discapacidad boolean,
  otros boolean,
  id_provincia integer,
  id_localidad integer,
  datos_factura character varying,
  tipo_envio character varying,
  codigo_oblea character varying,
  documentacion boolean DEFAULT false,
  facturacion boolean DEFAULT false,
  cod_farmacia character varying,
  farmacia character varying,
  CONSTRAINT pk_correspondencia PRIMARY KEY (id_correspondencia ),
  CONSTRAINT fk_domicilio_destinatario FOREIGN KEY (id_domicilio_destinatario)
      REFERENCES uoma.domicilio_correspondencia (id_domicilio) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_domicilio_remitente FOREIGN KEY (id_domicilio_remitente)
      REFERENCES uoma.domicilio_correspondencia (id_domicilio) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_seccional_destinatario FOREIGN KEY (seccional_destinatario)
      REFERENCES seccional (id_seccional) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_seccional_remitente FOREIGN KEY (seccional_remitente)
      REFERENCES seccional (id_seccional) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_tipo_correspondencia FOREIGN KEY (tipo_correo)
      REFERENCES uoma.tipo_correspondencia (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE uoma.correspondencia
  OWNER TO postgres;

