alter table tipo_pago add column contratos boolean default false;

create table tipo_pago (
	id_tipo_pago integer,
	descripcion character varying(50),
	constraint pk_tipo_pago primary key (id_tipo_pago)
);

insert into tipo_pago (id_tipo_pago, descripcion)
values (1,'Debito Bancario');
insert into tipo_pago (id_tipo_pago, descripcion)
values (2,'Debito por autogestion');
insert into tipo_pago (id_tipo_pago, descripcion)
values (3,'Transferencia bancaria');
insert into tipo_pago (id_tipo_pago, descripcion, contratos)
values (4,'Cheque', true);