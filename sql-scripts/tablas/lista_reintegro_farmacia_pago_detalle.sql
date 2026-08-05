--alter table lista_reintegro_farmacia_pago_detalle add column tipo_reintegro character varying 

create table lista_reintegro_farmacia_pago_detalle (
	id_lista_reintegro_pago integer not null,
	id_reintegro integer not null,
	importe numeric(10,2), 
	tipo_reintegro character varying
);

--
ALTER TABLE ONLY lista_reintegro_farmacia_pago_detalle
    ADD CONSTRAINT pk_lista_reintegro_farmacia_pago_d PRIMARY KEY (id_lista_reintegro_pago, id_reintegro);

--
ALTER TABLE ONLY lista_reintegro_farmacia_pago_detalle
    ADD CONSTRAINT fk_lista_reintegro_farmacia_pago_d_l FOREIGN KEY (id_lista_reintegro_pago) REFERENCES lista_reintegro_farmacia_pago(id);