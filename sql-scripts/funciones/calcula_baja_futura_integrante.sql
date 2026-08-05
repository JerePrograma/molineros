CREATE OR REPLACE FUNCTION calcula_baja_futura_integrante(cuil_titular_p character varying, inte_p integer)
  RETURNS date AS
$BODY$

declare fecha_baja date;
BEGIN

fecha_baja = max(fecha_vto) from afi_documento ad, afiliado a
where ad.cuil_titular = a.cuil_titular
and ad.inte = a.inte 
and ad.cuil_titular = cuil_titular_p and ad.inte = inte_p
and ((ad.id_documento in(4) and a.discapacitado = '0') 
    or
    (ad.id_documento in(5,15) and a.discapacitado = '1'));

RETURN fecha_baja;	
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;