create table canje_cheques_propios_nuevos (
	canje_id integer,
	nro_cheque numeric(15,0) NOT NULL,
	id_banco integer NOT NULL,
	constraint fk_ccp_n foreign key (canje_id) references canje_cheques_propios(id),
	constraint pk_canje_cheques_propios_nuevos primary key (canje_id, nro_cheque, id_banco),
	constraint fk_ccp_n_ch foreign key (nro_cheque, id_banco) references cheque(nro_cheque, id_banco)
)