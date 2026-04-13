CREATE OR REPLACE FUNCTION actualiza_prestador_externo(
 p_cuit character varying,
 p_descr character varying,
 p_iva integer, 
 p_matricula_tipo character,
 p_matricula_nro integer,
 p_matricula_provincia integer,
 p_matricula_categoria character,
 p_usuario character varying,
 p_id_prestador integer) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN

update prestador_externo set cuit = p_cuit, descripcion = p_descr, id_condicion_de_iva = p_iva,
            tipo_matricula = p_matricula_tipo, nro_matricula = p_matricula_nro, id_mat_provincia = p_matricula_provincia, 
            id_mat_categoria = p_matricula_categoria,             
            modi_fecha = localtimestamp, modi_usr = p_usuario
	where id_prestador_externo = p_id_prestador;

	return 1;
END;
$BODY$;