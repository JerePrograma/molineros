create table orden_pago_ospim_lista_reintegros (
	id_orden_pago_ospim integer,
	id_lista_reintegro_pago integer not null,
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
	constraint pk_op_lsita_reint  PRIMARY KEY (id_orden_pago_ospim, id_lista_reintegro_pago)
);

--
ALTER TABLE ONLY orden_pago_ospim_lista_reintegros
    ADD CONSTRAINT fk_op_lista_reinte_op FOREIGN KEY (id_orden_pago_ospim) REFERENCES orden_pago_ospim(id_orden_pago);
    
--
ALTER TABLE ONLY orden_pago_ospim_lista_reintegros
    ADD CONSTRAINT fk_op_lista_reinte_list FOREIGN KEY (id_lista_reintegro_pago) REFERENCES lista_reintegro_pago(id);

