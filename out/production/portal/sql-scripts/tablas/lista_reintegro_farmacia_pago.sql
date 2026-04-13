create table lista_reintegro_farmacia_pago (
	id serial,
	id_seccional integer not null,
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
	constraint pk_lista_pago_farmacia_reintegro  PRIMARY KEY (id),
	constraint fk_lista_pago_reint_farm_secc foreign key (id_seccional) references seccional(id_seccional) 
)