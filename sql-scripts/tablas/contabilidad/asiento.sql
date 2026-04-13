alter table asiento add alta_fecha timestamp without time zone NOT NULL;
alter table asiento add alta_usr character varying(15) NOT NULL;
alter table asiento add modi_fecha timestamp without time zone NOT NULL;
alter table asiento add modi_usr character varying(15) NOT NULL;
alter table asiento add baja_fecha timestamp without time zone;
alter table asiento add baja_usr character varying(15);
    
create table asiento (
 id serial,
 fecha  timestamp without time zone,
 descripcion character varying,
 automatico boolean,
 numero integer,
 ejercicio_desde date,
 ejercicio_hasta date,
 constraint pk_asiento primary key (id)
)
