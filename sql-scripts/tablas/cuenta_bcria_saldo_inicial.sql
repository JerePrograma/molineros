create table cuenta_bcria_saldo_inicial (
	id_cuenta_bcria integer,
	fecha_inicio_ejercicio date,
	saldo numeric(15,2),
	constraint pk_cuenta_bcria_saldo_inicial primary key (id_cuenta_bcria, fecha_inicio_ejercicio),
	constraint fk_cuenta_bcria_saldo_inicial foreign key (id_cuenta_bcria) references cuenta_bcria(id_cuenta_bcria)
);

insert into cuenta_bcria_saldo_inicial values (1, '20100801', 0);
insert into cuenta_bcria_saldo_inicial values (2, '20100801', 0);
insert into cuenta_bcria_saldo_inicial values (3, '20100801', 0);
insert into cuenta_bcria_saldo_inicial values (4, '20100801', 0);
insert into cuenta_bcria_saldo_inicial values (5, '20100801', 0);