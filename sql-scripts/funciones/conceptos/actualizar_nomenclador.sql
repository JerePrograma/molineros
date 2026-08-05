drop FUNCTION actualizar_nomenclador(
 p_id_prestacion integer,
 p_codigo character varying,
 p_descripcion character varying,
 p_coef_gastos numeric,
 p_coef_honorarios numeric,
 p_user character varying, 
 p_marca_rein_liq integer,
  p_usr character varying);
 
 CREATE OR REPLACE FUNCTION actualizar_nomenclador(
 p_id_prestacion integer,
 p_codigo character varying,
 p_descripcion character varying,
 p_coef_gastos numeric,
 p_coef_honorarios numeric,
 p_user character varying, 
 p_marca_rein_liq integer)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
update nomenclador set codigo = $2, descripcion = $3, coef_gastos = $4 ,
	coef_honorarios =  $5, modi_usr = $6, modi_fecha = current_date,
  marca_rein_liq = $7   where id_prestacion = $1;

return 1;
END;
$BODY$;
