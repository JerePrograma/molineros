CREATE OR REPLACE FUNCTION borra_contactos(id_organismo_p integer, p_user character varying, origen_p integer)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN

if origen_p=1 then
	update organismo_contacto  set baja_fecha=current_timestamp, baja_user=p_user where id_organismo=id_organismo_p;
else if origen_p=2 then
	update area_contacto  set baja_fecha=current_timestamp, baja_user=p_user where id_area=id_organismo_p;
end if;
end if;	
return 0;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
