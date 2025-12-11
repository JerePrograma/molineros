CREATE OR REPLACE FUNCTION buscar_recibo_conceptos_por_fechas(p_fecha_ini date, p_fecha_fin date) 
RETURNS TABLE(
    id integer ,
	recibo_id integer ,
	acta_id integer, 
	convenio_id integer, 
	nro_cheque_no_depositado numeric,
	id_banco_no_depositado integer,
	nro_cheque_rechazado numeric,
	id_banco_rechazado integer,
	caja_concepto_id integer,
	concepto_importe_por_cheques numeric(12,2) , 
	concepto_importe_adicional numeric(12,2) ,
	alta_fecha timestamp without time zone ,
    alta_usr character varying(50) ,
    modi_fecha timestamp without time zone ,
    modi_usr character varying(50) ,
    baja_fecha timestamp without time zone,
    baja_usr character varying(50),
    descripcion_otro_concepto character varying
 )
    LANGUAGE sql
    AS $BODY$

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
from recibo_conceptos  rc
inner join recibo r
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
from recibo_ingresos ri 
inner join recibo_conceptos  rc
on ri.id_anticipo_recibo_concepto  = rc.id
inner join recibo r
on ri.recibo_id = r.id
left outer join conceptos c
on rc.caja_concepto_id = c.id_concepto_maestro
and cast(c.valido_desde as date)  <= cast(r.fecha as date)
and cast(c.valido_hasta as date)  >= cast(r.fecha as date)
where (r.fecha >= $1 and r.fecha <=$2)
or (r.baja_fecha >= $1 and r.baja_fecha <=$2);


	
$BODY$;


ALTER FUNCTION public.buscar_recibo_conceptos_por_fechas(p_fecha_ini date, p_fecha_fin date)   OWNER TO postgres;

--