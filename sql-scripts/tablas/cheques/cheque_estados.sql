create table cheque_estado (
	id integer,
	descripcion character varying (50)
);

ALTER TABLE ONLY cheque_estado
    ADD CONSTRAINT pk_cheque_estado PRIMARY KEY (id);

insert into cheque_estado (id, descripcion) values (1, 'Emitido');
insert into cheque_estado (id, descripcion) values (2, 'Cargado');
insert into cheque_estado (id, descripcion) values (3, 'Recibido');
insert into cheque_estado (id, descripcion) values (4, 'Depositado');
insert into cheque_estado (id, descripcion) values (5, 'Rechazado');
insert into cheque_estado (id, descripcion) values (6, 'Sustituido');

--