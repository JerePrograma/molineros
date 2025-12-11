
CREATE OR REPLACE FUNCTION borra_documentacion(cuil_p character varying, inte_p integer, id_documento_p integer, fecha_ingreso_p date, user_p character varying, fecha_baja_usr date, id_p integer)
  RETURNS integer AS
$BODY$
BEGIN
    update afi_documento
    set baja_fecha=current_timestamp,
    baja_usr=user_p    
    where cuil_titular=$1    
    and id_documento=$3    
    and id=$7;

    if fecha_baja_usr is not null then
	    update afiliado 
	    set baja_fecha=fecha_baja_usr
	    where cuil_titular=cuil_p
	    and inte=inte_p;   
    end if;
    return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

