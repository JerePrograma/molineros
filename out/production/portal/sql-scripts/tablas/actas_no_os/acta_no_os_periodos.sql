CREATE TABLE acta_no_os_periodos
(
  id serial NOT NULL,
  acta_id integer,
  periodo date,
  cuil character(11),
  remuneracion_declarada numeric(10,2),
  calculado numeric(10,2),
  decreto numeric(10,2),
  pagado numeric(10,2),
  pagado_fecha date,
  subtotal numeric(10,2),
  interes numeric(10,2),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  apellido character varying(100),
  nombre character varying(100),
  agregado_manual boolean DEFAULT false,
  CONSTRAINT pk_acta_no_os_periodos PRIMARY KEY (id ),
  CONSTRAINT fk_acta_no_os_periodos_acta FOREIGN KEY (acta_id)
      REFERENCES acta_no_os (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

