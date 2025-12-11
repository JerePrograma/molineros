CREATE OR REPLACE FUNCTION reporte_desempleo(prest_v character varying,
 fecha_desde date) 
RETURNS SETOF reporte_desempleo_result
    LANGUAGE plpgsql
    AS $BODY$
declare fecha_hoy date;
BEGIN
fecha_hoy=current_date;
--LIQUIDAMOS Y ELLOS NO 
return query
select d.cuil, d.cuil_titular, a.docu_numero, a.apellido||', '||a.nombre as nombre,
       d.fecha_nac, d.sexo,d.fecha_vig, to_char(d.fecha_proceso,'yyyy/MM') as acredita,
       v.importe as importe , round(v.importe*0.9,2) as neto,prest_v
from detalle_desempleo_anses d, afiliado a, valor_capitas_desempleo v
where fecha_proceso >fecha_desde
and exists (select 1 from afi_tercerizadora_servicio at 
	    where at.cuil_titular=d.cuil_titular and id_tercerizadora=prest_v
	    --and alta_fecha<'20100901'
	    and (fecha_fin_pres is null or fecha_fin_pres>fecha_hoy)
	    and (baja_fecha is null or baja_fecha>fecha_hoy))
and a.cuil_titular=d.cuil_titular 
and a.cuil=d.cuil
and fu_obtener_edad(fecha_nac,fecha_proceso) >v.min
and fu_obtener_edad(fecha_nac,fecha_proceso) <v.max
and d.sexo=v.sexo
and (a.id_motivo_baja is null or a.id_motivo_baja<>2);
END;
$BODY$;


ALTER FUNCTION public.reporte_desempleo(prest_v character varying, fecha_desde date) OWNER TO postgres;

--
