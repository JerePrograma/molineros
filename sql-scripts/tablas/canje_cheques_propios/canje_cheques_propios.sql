alter table canje_cheques_propios add id_movimiento integer;
alter table canje_cheques_propios add constraint fk_canje_mov_bcrio foreign key (id_movimiento) references movimiento_banco(id_movimiento);

create table canje_cheques_propios (
	id serial,
	id_orden_pago_ospim integer,
	id_orden_pago_ospim_nueva integer,
	id_movimiento integer,
	alta_fecha timestamp without time zone NOT NULL,
	alta_usr character varying(50) NOT NULL,
	modi_fecha timestamp without time zone NOT NULL,
	modi_usr character varying(50) NOT NULL,
	baja_fecha timestamp without time zone,
	baja_usr character varying(50),
	constraint pk_canje_cheques_propios primary key (id)
	constraint fk_canje_op foreign key (id_orden_pago_ospim) references orden_pago_ospim(id_orden_pago),
	constraint fk_canje_op_nueva foreign key (id_orden_pago_ospim_nueva) references orden_pago_ospim(id_orden_pago),
	constraint fk_canje_mov_bcrio foreign key (id_movimiento) references movimiento_banco(id_movimiento)
)