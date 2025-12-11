
CREATE OR REPLACE FUNCTION uoma.editar_incidente_uoma(id_seccional_v integer, fecha_v timestamp without time zone, cuil_titular_v character varying, inte_v integer, id_localidad_v integer, id_provincia_v integer, calle_v character varying, numero_v character varying, piso_v character varying, depto_v character varying, postal_codi_v character varying, observaciones_v character varying, incidente_v character varying, seguimiento_v character varying, username character varying, id_incidente_v integer, compara integer, id_domicilio_v integer, fecha_recepcion_v date)
  RETURNS integer AS
$BODY$
declare id_domicilio_p integer;
BEGIN

if compara=-2 or compara=-3 then 

   update domicilio 
   set baja_fecha= current_date,
       baja_usr=username
   where id_domicilio=id_domicilio_v;

   INSERT INTO uoma.domicilio(calle, piso, depto, oficina, postal_codi, barrio, 
            telefono, observaciones, domi_val, alta_fecha, alta_usr, modi_fecha, 
            modi_usr, baja_fecha, baja_usr, provincia, localidad, numero, 
            localidad_nombre, provincia_nombre) 
            values (calle_v, piso_v, depto_v, null, postal_codi_v, null, null, observaciones_v, '0', current_timestamp, username,
            current_timestamp, username, null, null, id_provincia_v, id_localidad_v, numero_v, null, null);
            
   id_domicilio_p=currval('uoma.domicilio_uoma_id_seq');

   update uoma.incidente_unidad_operativa 
   set id_domicilio=id_domicilio_p
   where id_incidente=id_incidente_v;
   
END IF;

IF compara=-1 or compara=-3 or compara=-7 then
	INSERT INTO uoma.incidente_unidad_operativa_historico(cuil_titular, inte, fecha, id_domicilio, alta_fecha, alta_usr, modi_fecha, modi_usr, 
		    baja_fecha, baja_usr, id_seccional, detalle_incidente, id_incidente, fecha_recepcion)
	select cuil_titular, inte, fecha, id_domicilio, current_timestamp, username, modi_fecha, modi_usr, 
		    baja_fecha, baja_usr, id_seccional, detalle_incidente, id_incidente, fecha_recepcion
        from uoma.incidente_unidad_operativa
	where id_incidente=id_incidente_v;            
            
	update uoma.incidente_unidad_operativa
	set cuil_titular=cuil_titular_v,	    
	    fecha=fecha_v,
	    fecha_recepcion=fecha_recepcion_v, 
	    inte=inte_v,
	    detalle_incidente=incidente_v,
	    id_seccional=id_seccional_v,	    
	    modi_usr=username,
	    modi_fecha=current_timestamp
	where id_incidente=id_incidente_v;
END IF;	    

if compara =-4 or compara=-7 or compara=-6 THEN
INSERT INTO uoma.incidente_unidad_operativa_detalle(
            fecha, seguimiento_incidente, alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, 
            id_incidente)
            values(current_timestamp, seguimiento_v, current_timestamp, username, current_timestamp, username, null, null, id_incidente_v);
END IF;
return 0;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE

