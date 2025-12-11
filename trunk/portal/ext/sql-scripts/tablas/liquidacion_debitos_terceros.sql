alter table liquidacion_debitos_terceros add column numero_ndb integer not null default 0

create table liquidacion_debitos_terceros (
	id_liquidacion integer;
	periodo_hasta timestamp without time zone NOT NULL,
	observaciones character varying(2000) NOT NULL,	
	alta_fecha timestamp without time zone NOT NULL,
    alta_usr character varying(50) NOT NULL,
    modi_fecha timestamp without time zone NOT NULL,
    modi_usr character varying(50) NOT NULL,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
    numero_ndb integer not null default 0
	constraint pk_liquidacion_debitos_terceros  PRIMARY KEY (id_liquidacion)
);

ALTER TABLE liquidacion_debitos_terceros alter column id_liquidacion set default nextval('liquidacion_debitos_terceros_id_seq'::regclass);

insert into liquidacion_debitos_terceros values 
(
1, 
'01-08-2010',
'',
'01-08-2010',
'admin',
'01-08-2010',
'admin',
null,
null
)

