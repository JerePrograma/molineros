create table acreedores_saldo_inicial (
	cuit_acreedor  character varying(13),
	sucu_acreedor character varying(4),
	seccional integer,
	fecha_inicio_ejercicio date,
	saldo numeric(15,2)
);

alter table acreedores_saldo_inicial add constraint fk_acreedores_saldo_inicial_empre foreign key (cuit_acreedor,sucu_acreedor) references empresa(cuit,sucursal);
alter table acreedores_saldo_inicial add constraint fk_acreedores_saldo_inicial_Secc foreign key (seccional) references seccional(id_seccional);



insert into acreedores_saldo_inicial (cuit_acreedor, sucu_acreedor, seccional, fecha_inicio_ejercicio, saldo)
select distinct cuit_acreedor, sucu_acreedor, id_seccional, cast('20100801' as date), 0 from orden_pago_ospim  
