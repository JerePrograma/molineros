CREATE OR REPLACE FUNCTION correo.buscar_fc_prestador_duplicado(cuit_prestador_p character varying, id_punto_venta_p integer, comprob_tipo_p character varying, comprob_letra_p character varying, comprob_numero_p character varying, comprob_sucu_p integer)
  RETURNS integer AS
$BODY$
declare existe integer;

BEGIN

existe = (select 1 from comprobante c 
	     where c.cuit = cuit_prestador_p 
	     and c.id_punto_venta = id_punto_venta_p 
	     and c.compro_tipo = comprob_tipo_p 
	     and c.compro_nro  = lpad(comprob_numero_p,8,'0') 
	     and c.compro_letra = comprob_letra_p
	     and c.compro_sucu = comprob_sucu_p
	     and c.baja_fecha is null);

return existe;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;