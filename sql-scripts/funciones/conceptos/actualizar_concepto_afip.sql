DROP FUNCTION actualizar_concepto_afip(
 p_descripcion character varying,
 p_cod_conc character varying,
 p_cod_contra_conc character varying,
 p_liquidable boolean,
 p_debito_credito character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_id integer,
 p_id integer) ;

 CREATE OR REPLACE FUNCTION actualizar_concepto_afip(
 p_descripcion character varying,
 p_cod_conc character varying,
 p_cod_contra_conc character varying,
 p_liquidable boolean,
 p_debito_credito character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_id integer,
 p_id integer,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN

update conceptos_transf_os 
set deb_cred = p_debito_credito, liquidable = p_liquidable, descripcion = p_descripcion
where cod_conc = p_cod_conc;

update concepto_transferencia  
set valido_desde = p_valido_desde,
valido_hasta = p_valido_hasta,
liquidable = p_liquidable,
concepto_id = p_concepto_id,
modi_fecha = current_date,
modi_usr = p_usr
where id = p_id;

return 1;
END;
$BODY$;
