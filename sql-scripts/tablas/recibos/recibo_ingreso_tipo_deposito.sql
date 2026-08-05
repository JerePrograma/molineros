create table recibo_ingreso_tipo_deposito (
	id integer,
	descripcion character varying,
	constraint pk_recibo_ingreso_tipo_deposito primary key (id)
)
insert into recibo_ingreso_tipo_deposito values (1, 'Deposito Bancario');
insert into recibo_ingreso_tipo_deposito values (2, 'Transferencia Bancaria');