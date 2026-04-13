CREATE OR REPLACE FUNCTION anular_comprobante_amtima(c_pto_venta integer, c_compro_tipo character varying, c_compro_nro character varying,
c_compro_letra character varying, c_compro_sucu integer, c_cuit character varying, p_usr character varying, p_borrar boolean) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
update comprobante_amtima set baja_fecha = case when p_borrar = true then localtimestamp else null end, 
					baja_usr = case when p_borrar = true then p_usr else null end,
					anulado_fecha = case when p_borrar = false then localtimestamp else null end, 
					anulado_usr = case when p_borrar = false  then p_usr else null end
where id_punto_venta = $1 and compro_tipo =$2 and compro_nro =$3 and
 compro_letra = $4 and compro_sucu =$5 and c_cuit =$6;

return 1;
END;
$BODY$;
