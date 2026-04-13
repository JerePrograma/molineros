CREATE TABLE orden_pago_ospim_lista_reintegros_farmacia
(
  id_orden_pago_ospim integer NOT NULL,
  id_lista_reintegro_pago integer NOT NULL,
  alta_fecha timestamp without time zone NOT NULL,
  alta_usr character varying(50) NOT NULL,
  modi_fecha timestamp without time zone NOT NULL,
  modi_usr character varying(50) NOT NULL,
  baja_fecha timestamp without time zone,
  baja_usr character varying(50),
  CONSTRAINT pk_op_lsita_reint_farm PRIMARY KEY (id_orden_pago_ospim , id_lista_reintegro_pago ),
  CONSTRAINT fk_op_lista_reinte_farm_list FOREIGN KEY (id_lista_reintegro_pago)
      REFERENCES lista_reintegro_farmacia_pago (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_op_lista_reinte_farmacia_op FOREIGN KEY (id_orden_pago_ospim)
      REFERENCES orden_pago_ospim (id_orden_pago) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE orden_pago_ospim_lista_reintegros_farmacia
  OWNER TO postgres;

