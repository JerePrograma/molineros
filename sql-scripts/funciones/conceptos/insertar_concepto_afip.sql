DROP FUNCTION insertar_concepto_afip(
 p_descripcion character varying,
 p_cod_conc character varying,
 p_cod_contra_conc character varying,
 p_liquidable boolean,
 p_debito_credito character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_id integer);

 CREATE OR REPLACE FUNCTION insertar_concepto_afip(
 p_descripcion character varying,
 p_cod_conc character varying,
 p_cod_contra_conc character varying,
 p_liquidable boolean,
 p_debito_credito character varying,
 p_valido_desde date,
 p_valido_hasta date,
 p_concepto_id integer,
 p_usr character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN

	if (not exists (select 1 from conceptos_transf_os where cod_conc =p_cod_conc )) then
		insert into conceptos_transf_os (cod_conc, descripcion, cod_contra_conc, deb_cred, liquidable)
		values (p_cod_conc, p_descripcion, p_cod_contra_conc, p_debito_credito, p_liquidable );
		
		insert into concepto_transferencia  (concepto_transf, liquidable, concepto_id, valido_desde, valido_hasta, alta_fecha, alta_usr, modi_fecha, modi_usr)
		values ( p_cod_conc , p_liquidable, p_concepto_id , p_valido_desde , p_valido_hasta, current_date, p_usr, current_date, p_usr );
	end if;


return (select max(id) from concepto_transferencia);
END;
$BODY$;
