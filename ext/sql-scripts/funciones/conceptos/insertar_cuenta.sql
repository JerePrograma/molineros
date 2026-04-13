DROP FUNCTION insertar_cuenta(
 p_cuenta character varying,
 p_numero character varying,
 p_imputable boolean);

 
  CREATE OR REPLACE FUNCTION insertar_cuenta(
 p_cuenta character varying,
 p_numero character varying,
 p_imputable boolean,
 p_tipo character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_usr character varying)
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN
	
		
		insert into plan_cuentas_maestro (cuenta, numero, imputable, tipo)
	  	values (p_cuenta, p_numero, p_imputable, p_tipo);
	  	
		insert into plan_cuentas (cuenta, numero, imputable, alta_fecha, alta_usr, modi_fecha, modi_usr, valido_desde, valido_hasta, id_cuenta_maestro, tipo)
	  	values (p_cuenta, p_numero, p_imputable, localtimestamp, p_usr, localtimestamp, p_usr,	p_valido_desde ,  p_valido_hasta, currval('plan_cuentas_id_seq'), p_tipo);

return currval('plan_cuentas_id_seq');
END;
$BODY$;
