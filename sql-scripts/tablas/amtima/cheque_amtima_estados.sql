create table cheque_amtima_estado (
	id integer,
	descripcion character varying (50)
);

ALTER TABLE ONLY cheque_amtima_estado
    ADD CONSTRAINT pk_cheque_amtima_estado PRIMARY KEY (id);

insert into cheque_amtima_estado (id, descripcion) values (1, 'Emitido');
