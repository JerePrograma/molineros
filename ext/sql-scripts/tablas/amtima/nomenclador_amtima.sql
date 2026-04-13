CREATE TABLE nomenclador_amtima
(
  id_prestacion integer NOT NULL DEFAULT nextval('nomenclador_amtima_id_seq'::regclass),
  descripcion character varying(150),
  marca_rein_liq smallint,
  observaciones character varying(250),
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  id_tipo_nomenclador integer,
  id_especialidad integer,
  codigo character varying(10),
  importe numeric(11,2),
  q_gal numeric(10,2),
  q_gal_ay numeric(10,2),
  anest numeric(10,2),
  gto numeric(10,2),
  q_ay numeric(10,2),
  norma character varying(500),
  unidad_arancelaria character varying(500),
  forma_facturacion character varying(500),
  cotiza_honorario numeric(10,4),
  cotiza_gastos numeric(10,4),
  importe_gastos numeric(10,6),
  importe_honorarios numeric(10,6),
  coef_gastos numeric(10,6),
  coef_honorarios numeric(10,6),
  CONSTRAINT pk_nomenclador_amtima PRIMARY KEY (id_prestacion)
)
WITH (
  OIDS=FALSE
);
