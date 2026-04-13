 drop FUNCTION actualizar_nomenclador_concepto(p_id_prestacion integer, --1 
 p_id_nomenc_concepto integer, --2
 p_concepto_id integer, --3
 p_Valido_desde date, --4
 p_Valido_hasta date, --5
 p_tipo_id integer); --6
 
 CREATE OR REPLACE FUNCTION actualizar_nomenclador_concepto(p_id_prestacion integer, --1 
 p_id_nomenc_concepto integer, --2
 p_concepto_id integer, --3
 p_Valido_desde date, --4
 p_Valido_hasta date, --5
 p_tipo_id integer,--6
 p_usr character varying) --7 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN

if (p_id_nomenc_concepto is not null) then
	 UPDATE nomenclador_conceptos
	   SET concepto_id = p_concepto_id, modi_fecha = current_date, modi_usr = p_usr
	 WHERE id=p_id_nomenc_concepto;
else
	insert into nomenclador_conceptos (id_prestacion,codigo, descripcion, concepto_id, valido_Desde,valido_hasta, tipo_id, modi_fecha, modi_usr)
 	values (p_id_prestacion, (select codigo from nomenclador where id_prestacion  = p_id_prestacion),
 	(select descripcion from nomenclador where id_prestacion = p_id_prestacion), 
 	p_concepto_id, 
 	p_Valido_desde, p_Valido_hasta, p_tipo_id, current_date, p_usr);
end if;

return 1;
END;
$BODY$;
