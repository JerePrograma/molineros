-- Function: reporte_boletas_empleadores(date, date, character varying, character varying, character varying, date, date, character varying, numeric, numeric, character varying, character varying)

-- DROP FUNCTION reporte_boletas_empleadores(date, date, character varying, character varying, character varying, date, date, character varying, numeric, numeric, character varying, character varying);

CREATE OR REPLACE FUNCTION reporte_boletas_empleadores(periodo_desde_v date, periodo_hasta_v date, cuentas character varying, tipo_boletas_v character varying, acta_convenio_v character varying, fecha_recauda_desde_v date, fecha_recauda_hasta_v date, nro_cheque_v character varying, importe_desde numeric, importe_hasta numeric, estado_cheque_v character varying, cuit character varying)
  RETURNS SETOF reporte_boletas_empleadores_type AS
$BODY$
BEGIN
drop table if exists aux_temp;
create temp table aux_temp (descripcion varchar, cuenta_sucursal text, cod_sucursal_nacion integer, nombre_suc_nacion varchar, 
fecha_recauda date, periodo_cod_barras date, cuit varchar, nro_boleta_portal_emple integer, razon_soc varchar, importe numeric, 
nro_cheque numeric, estado_cheque text, nroacta varchar, observacion varchar);


return query
select cn.descripcion, substring(cast(cn.cuenta_suc as varchar),1,5)||'/'||substring(cast(cn.cuenta_suc as varchar),6,2) as cuenta_sucursal, 
       sn.cod_sucursal, sn.nombre, u.fecha_recauda, u.periodo_cod_barras , u.cuit, u.nro_boleta_portal_emple, e.razon_soc, u.importe, u.nro_cheque, 
case when rtrim(u.estado_cheque)='P' then 'PRESENTADO' when rtrim(u.estado_cheque)='L' then 'LIBERADO' when rtrim(u.estado_cheque)='R' then 'RECHAZADO' end as estado_cheque,
case when b.cuotaconvenio is not null then cast(b.nroacta as varchar) || '/'|| cast(b.cuotaconvenio as varchar) else cast(b.nroacta as varchar) end ,
b.observacion
from uoma_aportes u --PARA AMTIMA CAMBIAR TABLA POR amtima_aportes
left outer join sucursales_nacion sn
on u.suc_nacion=sn.cod_sucursal
left outer join convenio_nacion cn
on cn.tipo_boleta=u.tipo_boleta
left outer join empresa e
on e.cuit=u.cuit
and e.sucursal='000'
left outer join boletasinddjj b
on u.nro_boleta_portal_emple=b.numerosecuencia
and u.cuit = b.empresa_cuit
where (rtrim(estado_cheque)='' or rtrim(estado_cheque)='L')
and u.tipo_boleta in (2,3,4,5,7) -- TIPO BOLETA=6 PARA BOLETAS BLANCAS OSPIM -- TIPO BOLETA=1 y 8 PARA AMTIMA
order by fecha_recauda,cuit,estado_cheque
limit 100;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION reporte_boletas_empleadores(date, date, character varying, character varying, character varying, date, date, character varying, numeric, numeric, character varying, character varying)
  OWNER TO postgres;
