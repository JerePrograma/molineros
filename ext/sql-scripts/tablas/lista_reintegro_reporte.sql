alter table lista_reintegro_reporte drop constraint fk_lista_reporte_reint_secc

create table lista_reintegro_reporte (
	id serial,
	id_seccional integer not null,
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
	constraint pk_lista_reporte_reintegro  PRIMARY KEY (id)	
)

