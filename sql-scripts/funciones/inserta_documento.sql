CREATE OR REPLACE FUNCTION inserta_documento(cuil_p character varying, inte_p integer, id_documento_p integer, fecha_ingreso_p date, fecha_egreso_p date, user_p character varying, id_motivo_baja_p integer)
  RETURNS integer AS
$BODY$            
    declare baja_inte_result int;        
    declare baja_fecha_afi date;        
    declare parentesco_p integer;
BEGIN

    parentesco_p=id_parentesco_sss from afiliado where cuil_titular=cuil_p and inte=inte_p;

    INSERT INTO afi_documento(cuil_titular, inte, id_documento, fecha_ini, fecha_vto, alta_fecha, alta_usr)
    VALUES (cuil_p, inte_p, id_documento_p, fecha_ingreso_p, fecha_egreso_p,current_timestamp,user_p);

	
    if (id_motivo_baja_p=4 OR id_motivo_baja_p=5 or id_documento_p=16) and inte_p<>0 
	and parentesco_p not in (1,2) --'CONYUGE','CONCUBINO/A','CONCUBINO'
    then
	baja_fecha_afi=baja_fecha from afiliado where cuil_titular=cuil_p and inte=inte_p;
	if baja_fecha_afi<fecha_egreso_p or baja_fecha_afi is null then 
		baja_inte_result=baja_inte(cuil_p,inte_p,fecha_egreso_p,id_motivo_baja_p, user_p);
	end if;
    end if;	
    
    return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;