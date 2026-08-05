drop table lista_reintegro_farmacia_reporte;
create table lista_reintegro_farmacia_reporte (
	id serial,
	id_seccional integer not null,
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
	constraint pk_lista_reporte_reintegro_farmacia  PRIMARY KEY (id)	
)
