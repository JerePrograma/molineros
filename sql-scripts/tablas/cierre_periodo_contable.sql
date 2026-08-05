alter table cierre_periodo_contable drop constraint pk_cierre_periodo_contable ;
alter table cierre_periodo_contable_asientos drop constraint pk_cierre_periodo_contable_asientos ;

create table cierre_periodo_contable (
	fecha_cierre timestamp without time zone NOT NULL,
	observacion character varying(2000) NOT NULL,
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
);

create table cierre_periodo_contable_asientos (
	fecha_cierre timestamp without time zone NOT NULL,
	observacion character varying(2000) NOT NULL,
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
	constraint pk_cierre_periodo_contable_asientos  PRIMARY KEY (fecha_cierre)
);