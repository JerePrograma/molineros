create table orden_pago_ospim_liquidaciones (
	id_orden_pago_ospim integer,
	id_liquidacion integer not null,
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
	constraint pk_op_lista_liqui  PRIMARY KEY (id_orden_pago_ospim, id_liquidacion)
);

ALTER TABLE ONLY orden_pago_ospim_liquidaciones
    ADD CONSTRAINT fk_op_liquidacion_op FOREIGN KEY (id_orden_pago_ospim) REFERENCES orden_pago_ospim(id_orden_pago);
    
--
ALTER TABLE ONLY orden_pago_ospim_liquidaciones
    ADD CONSTRAINT fk_op_liquidacion_liquidacion FOREIGN KEY (id_liquidacion) REFERENCES liquidacion (id_liquidacion);