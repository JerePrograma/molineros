CREATE OR REPLACE FUNCTION buscar_id_liquidacion_comprobante(
 p_pto_vta numeric,
 p_tipo character varying,
 p_nro character varying,
 p_cuit character varying,
 p_compro_letra character varying,
 p_compro_sucu integer
 ) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare id integer;
  begin

id = cl.id_liquidacion
from  comprobante_liquidacion cl
where  cl.id_punto_venta = p_pto_vta
   and cl.compro_tipo = p_tipo
   and cl.compro_nro = p_nro
   and cl.cuit = p_cuit
   and cl.compro_letra = p_compro_letra
   and cl.compro_sucu = p_compro_sucu;

return id;
end; 
$BODY$;