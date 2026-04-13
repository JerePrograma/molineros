CREATE OR REPLACE FUNCTION edita_documentacion(cuil_p character varying, inte_p integer, id_documento_p integer, fecha_ingreso_p date, fecha_egreso_p date, user_p character varying, id_p integer)
  RETURNS integer AS
$BODY$
 declare actualiza int;
 /*declare posterior int;*/
 declare baja_fecha_afi date;        
 declare id_motivo_baja_p int;
 declare parentesco_p int;
 declare baja_inte_result int;        

BEGIN
    actualiza=actualiza_afiliado from documento where id_documento=id_documento_p;   
    id_motivo_baja_p = id_motivo_baja from documento where id_documento=id_documento_p;  
    parentesco_p=id_parentesco_sss from afiliado where cuil_titular=cuil_p and inte=inte_p;
    /*posterior=1 from afi_documento a 
		  where cuil_titular=cuil_p 
		  and inte=inte_p 
		  and id_documento in (select id_documento from documento where actualiza_afiliado=1)
		  and a.fecha_vto>fecha_egreso_p
		  and a.baja_fecha is null
		  limit 1;
    */		
    RAISE INFO 'ACTUALIZA %',actualiza;
    UPDATE afi_documento
    set fecha_vto=fecha_egreso_p,
	fecha_ini=fecha_ingreso_p,
	modi_fecha=current_timestamp,
	modi_usr=user_p
    where cuil_titular=cuil_p
    and inte = inte_p    
    and id=id_p;

    if actualiza = 1 
       and inte_p<>0 
       and parentesco_p not in (1,2) --'CONYUGE','CONCUBINO/A','CONCUBINO'
    then
	baja_fecha_afi=baja_fecha from afiliado where cuil_titular=cuil_p and inte=inte_p;
	if baja_fecha_afi<fecha_egreso_p or baja_fecha_afi is null then 
		baja_inte_result=baja_inte(cuil_p,inte_p,fecha_egreso_p,id_motivo_baja_p, user_p);
	end if;
    end if;	
    
    /*if actualiza>0 and (posterior is null or posterior<>1) then
	RAISE INFO 'ACTUALIZA';
	update afiliado set baja_fecha=fecha_egreso_p 
	where cuil_titular=cuil_p
	and inte=inte_p;
    end if;*/

    
    return 1;

END;    
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;