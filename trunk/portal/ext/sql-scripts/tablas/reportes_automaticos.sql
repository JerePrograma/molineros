drop table reportes_automaticos;
create table reportes_automaticos (
	id serial,
	titulo character varying(100),
	stored_procedure character varying(100),
	csv_parameteres character varying(500),
	hora integer,
	diario boolean,
	incluir_fin_de_semana boolean,
	dia_de_la_semana integer,
	dia_del_mes integer,
	fecha_unica_vez timestamp without time zone,
	mails_destino character varying(500),
	ultima_ejecucion timestamp without time zone,
	constraint pk_reportes_automaticos primary key (id)
)

--ej
/*
insert into reportes_automaticos (titulo, stored_procedure, csv_parameteres, dia_de_la_semana, hora, incluir_fin_de_semana, mails_destino)
values ('Libro Banco','libro_banco', '01/01/2011 12:00:00=Date,01/02/2011 12:00:00=Date,2=Integer', 0, 10,true, 'moreyramj@gmail.com' )
*/