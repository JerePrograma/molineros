CREATE OR REPLACE FUNCTION inserta_prestador_externo(
 p_cuit character varying,
 p_descr character varying,
 p_iva integer, 
 p_matricula_tipo character,
 p_matricula_nro integer,
 p_matricula_provincia integer,
 p_matricula_categoria character,
 p_usuario character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
declare resultDom integer;
BEGIN

INSERT INTO prestador_externo(
            cuit, descripcion, id_condicion_de_iva,
            tipo_matricula, nro_matricula, id_mat_provincia, id_mat_categoria, 
            alta_fecha, alta_usr, 
            modi_fecha, modi_usr)
    VALUES (p_cuit, p_descr, p_iva, 
    		p_matricula_tipo, p_matricula_nro, p_matricula_provincia, p_matricula_categoria, 
    		LOCALTIMESTAMP, p_usuario, LOCALTIMESTAMP, p_usuario);

return currval('prestador_externo_id_seq');
END;
$BODY$;
