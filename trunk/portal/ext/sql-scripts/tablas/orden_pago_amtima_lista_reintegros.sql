create table orden_pago_amtima_lista_reintegros (
	id_orden_pago_amtima integer,
	id_lista_reintegro_pago integer not null,
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
	constraint pk_opa_lsita_reint  PRIMARY KEY (id_orden_pago_amtima, id_lista_reintegro_pago)
);

--
ALTER TABLE ONLY orden_pago_amtima_lista_reintegros
    ADD CONSTRAINT fk_opa_lista_reinte_opa FOREIGN KEY (id_orden_pago_amtima) REFERENCES orden_pago_amtima(id_orden_pago);
    
--
ALTER TABLE ONLY orden_pago_amtima_lista_reintegros
    ADD CONSTRAINT fk_opa_lista_reinte_list FOREIGN KEY (id_lista_reintegro_pago) REFERENCES lista_reintegro_farmacia_pago(id);

