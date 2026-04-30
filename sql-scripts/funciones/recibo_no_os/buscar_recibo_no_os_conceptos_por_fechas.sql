-- Function: buscar_recibo_no_os_conceptos_por_fechas(date, date)

-- DROP FUNCTION buscar_recibo_no_os_conceptos_por_fechas(date, date);

CREATE OR REPLACE FUNCTION buscar_recibo_no_os_conceptos_por_fechas(IN p_fecha_ini date, IN p_fecha_fin date)
  RETURNS TABLE(id integer, recibo_id integer, acta_id integer, convenio_id integer, nro_cheque_no_depositado numeric, id_banco_no_depositado integer, nro_cheque_rechazado numeric, id_banco_rechazado integer, caja_concepto_id integer, concepto_importe_por_cheques numeric, concepto_importe_adicional numeric, alta_fecha timestamp without time zone, alta_usr character varying, modi_fecha timestamp without time zone, modi_usr character varying, baja_fecha timestamp without time zone, baja_usr character varying, descripcion_otro_concepto character varying) AS
$BODY$

select 	 rc.id,
	r.id,
	rc.acta_id, 
	rc.convenio_id, 
	rc.nro_cheque_no_depositado,
	rc.id_banco_no_depositado,
	rc.nro_cheque_rechazado,
	rc.id_banco_rechazado,
	rc.caja_concepto_id,
	rc.concepto_importe_por_cheques, 
	rc.concepto_importe_adicional,
	rc.alta_fecha ,
    rc.alta_usr,
    rc.modi_fecha,
    rc.modi_usr,
    rc.baja_fecha,
    rc.baja_usr,
    c.descripcion
from recibo_no_os_conceptos  rc
inner join recibo_no_os r
on rc.recibo_id = r.id
left outer join conceptos c
on rc.caja_concepto_id = c.id_concepto_maestro
and cast(c.valido_desde as date)  <= cast(r.fecha as date)
and cast(c.valido_hasta as date)  >= cast(r.fecha as date)
where (r.fecha >= $1 and r.fecha <=$2)
or (r.baja_fecha >= $1 and r.baja_fecha <=$2)
union 
select 	 rc.id,
	r.id,
	rc.acta_id, 
	rc.convenio_id, 
	rc.nro_cheque_no_depositado,
	rc.id_banco_no_depositado,
	rc.nro_cheque_rechazado,
	rc.id_banco_rechazado,
	rc.caja_concepto_id,
	-1*rc.concepto_importe_por_cheques, 
	-1*rc.concepto_importe_adicional,
	rc.alta_fecha ,
    rc.alta_usr,
    rc.modi_fecha,
    rc.modi_usr,
    rc.baja_fecha,
    rc.baja_usr,
    c.descripcion
from recibo_no_os_ingresos ri 
inner join recibo_no_os_conceptos  rc
on ri.id_anticipo_recibo_concepto  = rc.id
inner join recibo_no_os r
on ri.recibo_id = r.id
left outer join conceptos c
on rc.caja_concepto_id = c.id_concepto_maestro
and cast(c.valido_desde as date)  <= cast(r.fecha as date)
and cast(c.valido_hasta as date)  >= cast(r.fecha as date)
where (r.fecha >= $1 and r.fecha <=$2)
or (r.baja_fecha >= $1 and r.baja_fecha <=$2);


	
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_recibo_no_os_conceptos_por_fechas(date, date)
  OWNER TO postgres;

