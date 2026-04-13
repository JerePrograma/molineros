CREATE TABLE liquidacion_farmacia_ospim
(
  fecha timestamp without time zone NOT NULL,
  periodo timestamp without time zone NOT NULL,
  orden_pago_ospim_id integer NOT NULL,
  nro_liquidacion integer NOT NULL,
  nro_prestador character varying(10) NOT NULL,
  prestador character varying(50) NOT NULL,
  nro_farmacia integer NOT NULL,
  farmacia character varying(50) NOT NULL,
  nro_recetario character varying(10) NOT NULL,
  nro_troquel character varying(10) NOT NULL,
  medicamento character varying(250) NOT NULL,
  cantidad integer NOT NULL,
  pvp numeric(13,2) NOT NULL,
  total_ospim numeric(13,2),
  total_amtima numeric(13,2),
  debito character varying(100),
  dif_ospim numeric(13,2),
  dif_amtima numeric(13,2),
  porcentaje_ospim double precision,
  porcentaje_amtima double precision,
  pmi character varying(3),
  id_ospim integer,
  id_amtima integer,
  id_uoma integer,
  inte integer NOT NULL,
  nombre_apellido character varying(200) NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(15) NOT NULL,
  alta_ip character varying(15),
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(15) NOT NULL,
  modi_ip character varying(15),
  baja_fecha timestamp without time zone,
  baja_usr character varying(15),
  baja_ip character varying(15),
  CONSTRAINT fk_liqui_farma_ospim_op FOREIGN KEY (orden_pago_ospim_id)
      REFERENCES orden_pago_ospim (id_orden_pago) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

