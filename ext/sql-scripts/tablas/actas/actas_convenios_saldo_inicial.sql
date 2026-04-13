	create table actas_convenios_saldo_inicial (
		cuit  character varying(13),
		sucu character varying(4),
		fecha_inicio_ejercicio date,
		saldo numeric(15,2)
	);

	alter table actas_convenios_saldo_inicial add constraint fk_actas_convenios_saldo_inicial_empre foreign key (cuit, sucu) references empresa(cuit, sucursal);


	insert into actas_convenios_saldo_inicial (cuit, sucu, fecha_inicio_ejercicio, saldo)
	select distinct cuit, sucursal,  cast('20100801' as date), 0 
	from acta  
