CREATE TYPE afi_aportes_aporte AS
   (
    afiaporte_cuil_titular character varying(13),
    afiaporte_inte integer,
    afiaporte_id_aporte integer,
    afiaporte_fecha_ingre date,
    afiaporte_fecha_egre date,
    afiaporte_alta_usr character(50),
    afiaporte_baja_usr character(50),
    afiaporte_baja_fecha timestamp with time zone,
    afiaporte_modi_fecha timestamp with time zone,
    afiaporte_modi_usr character(50),
    afiaporte_alta_fecha timestamp with time zone,
    afiaporte_id_motivo_baja integer,
    afiaporte_id_plan_serial bigint,
    afiaporte_id bigint,
    afiaporte_id_socio integer,
    afiaporte_tipo_aporte character(1),
    aporte_id_aporte integer,
    aporte_tipo_aporte character varying(3),
    aporte_plan character varying(3),
    aporte_descripcion character varying(100),
    aporte_observaciones character varying(250),
    aporte_alta_fecha timestamp without time zone,
    aporte_alta_usr character varying(15),
    aporte_modi_fecha timestamp without time zone,
    aporte_modi_usr character varying(15),
    aporte_baja_fecha timestamp without time zone,
    aporte_baja_usr character varying(15),
    aporte_genera_id_socio character(1),
    aporte_es_os boolean,
    motbaja_id_motivo_baja smallint,
    motbaja_descripcion character varying(100),
    motbaja_observaciones character varying(250),
    motbaja_alta_fecha timestamp without time zone,
    motbaja_alta_usr character varying(15),
    motbaja_modi_fecha timestamp without time zone,
    motbaja_modi_usr character varying(15),
    motbaja_baja_fecha timestamp without time zone,
    motbaja_baja_usr character varying(15),
    motbaja_meses_a_baja integer);

CREATE OR REPLACE FUNCTION buscar_afiaportes_por_afiplan(IN id_plan_serial_p double precision)
  RETURNS SETOF afi_aportes_aporte AS
$BODY$
BEGIN

return query
select 
  aa.cuil_titular as afiaporte_cuil_titular,
  aa.inte as afiaporte_inte,
  aa.id_aporte as afiaporte_id_aporte,
  aa.fecha_ingre as afiaporte_fecha_ingre,
  aa.fecha_egre as afiaporte_fecha_egre,
  aa.alta_usr as afiaporte_alta_usr,
  aa.baja_usr as afiaporte_baja_usr,
  aa.baja_fecha as afiaporte_baja_fecha,
  aa.modi_fecha as afiaporte_modi_fecha,
  aa.modi_usr as afiaporte_modi_usr,
  aa.alta_fecha as afiaporte_alta_fecha,
  aa.id_motivo_baja as afiaporte_id_motivo_baja,
  aa.id_plan_serial as afiaporte_id_plan_serial,
  aa.id as afiaporte_id,
  aa.id_socio as afiaporte_id_socio,
  aa.tipo_aporte as afiaporte_tipo_aporte,
  a.id_aporte as aporte_id_aporte, 
  a.tipo_aporte as aporte_tipo_aporte, 
  a.plan as aporte_plan, 
  a.descripcion as aporte_descripcion, 
  a.observaciones as aporte_observaciones, 
  a.alta_fecha as aporte_alta_fecha, 
  a.alta_usr as aporte_alta_usr, 
  a.modi_fecha as aporte_modi_fecha, 
  a.modi_usr as aporte_modi_usr, 
  a.baja_fecha as aporte_baja_fecha, 
  a.baja_usr as aporte_baja_usr, 
  a.genera_id_socio as aporte_genera_id_socio, 
  a.es_os as aporte_es_os,
  mb.id_motivo_baja as motbaja_id_motivo_baja, 
  mb.descripcion as motbaja_descripcion, 
  mb.observaciones as motbaja_observaciones, 
  mb.alta_fecha as motbaja_alta_fecha, 
  mb.alta_usr as motbaja_alta_usr, 
  mb.modi_fecha as motbaja_modi_fecha, 
  mb.modi_usr as motbaja_modi_usr, 
  mb.baja_fecha as motbaja_baja_fecha, 
  mb.baja_usr as motbaja_baja_usr, 
  mb.meses_a_baja as motbaja_meses_a_baja 
from afi_aportes aa 
inner join aporte a on aa.id_aporte=a.id_aporte 
left join motivo_baja mb on aa.id_motivo_baja = mb.id_motivo_baja 
where aa.id_plan_serial = id_plan_serial_p;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 300;