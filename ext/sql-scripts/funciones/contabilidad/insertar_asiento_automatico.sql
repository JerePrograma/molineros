
CREATE OR REPLACE FUNCTION insertar_asiento_automatico(
 p_fecha  timestamp without time zone,
 p_descripcion character varying,
 p_automatico boolean,
 p_ejercicio_desde date,
 p_ejercicio_hasta date,
 p_username character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare v_nro  integer;
declare v_exite_cierre  boolean;
BEGIN

v_exite_cierre = true where exists (select 1 from cierre_periodo_contable where fecha_cierre = p_fecha and baja_fecha is null);
if v_exite_cierre is null then
	insert into cierre_periodo_contable (fecha_cierre,	observacion, alta_fecha, alta_usr, modi_fecha, modi_usr)
	values (p_fecha, 'Cierre por generacion de asientos automaticos', localtimestamp, p_username, localtimestamp, p_username);
end if;

v_nro = numero from asiento where ejercicio_desde = p_ejercicio_desde and descripcion = p_descripcion and automatico = true and fecha = p_fecha;
delete from detalle_asiento where asiento_id = (select id from asiento where ejercicio_desde = p_ejercicio_desde and descripcion = p_descripcion and automatico = true and fecha = p_fecha);
delete from asiento where id = (select id from asiento where ejercicio_desde = p_ejercicio_desde and descripcion = p_descripcion and automatico = true and fecha = p_fecha);

if v_nro is null then
 v_nro = (select max(numero) +1  from asiento where cast(ejercicio_desde as date) = cast(p_ejercicio_desde as date));
end if;
if v_nro is null then
 v_nro = 2;
end if;

insert into asiento(fecha, descripcion, automatico, numero, ejercicio_desde, ejercicio_hasta, alta_fecha, alta_usr, modi_fecha, modi_usr)
values (p_fecha, p_descripcion, p_automatico, v_nro, p_ejercicio_desde, p_ejercicio_hasta, localtimestamp, p_username, localtimestamp, p_username);
 
return currval('asiento_id_seq');
END;
$BODY$;
