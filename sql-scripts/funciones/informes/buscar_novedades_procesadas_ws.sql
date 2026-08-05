CREATE OR REPLACE FUNCTION informes.buscar_novedades_procesadas_ws(IN fecha_proceso_p date)
  RETURNS TABLE(id_ospim integer, seccional character varying, id_tercerizadora character varying, cuil_titular character varying, cuil character varying, inte integer, parentesco character varying, apellido character varying, nombre character varying, documento_tipo character varying, docu_numero character varying, naci_fecha date, sexo character varying, civil_esta character varying, nacionalidad character varying, provincia character varying, localidad character varying, postal_codi character varying, calle character varying, numero character varying, piso character varying, depto character varying, telefono character varying, categoria character, ramo character, id_plan integer, plan character, ingre_fecha date, baja_fecha timestamp without time zone, id_uoma integer, cuit character, razon_soc character varying, fecha_ospim date, os_anterior integer, discapacitado character varying, id_transaction integer, message_code character varying, message_description character varying, fecha_informe timestamp without time zone, operacion integer, fpp date) AS
$BODY$
BEGIN

return query

select pows.id_ospim, pows.seccional, pows.id_tercerizadora, pows.cuil_titular, pows.cuil, pows.inte, pows.parentesco, pows.apellido, pows.nombre, pows.documento_tipo, 
pows.docu_numero, pows.naci_fecha, pows.sexo, pows.civil_esta, pows.nacionalidad, pows.provincia, pows.localidad, pows.postal_codi, pows.calle, pows.numero, pows.piso, pows.depto, 
pows.telefono, pows.categoria, pows.ramo, pows.id_plan, pows.plan, pows.ingre_fecha, pows.baja_fecha, pows.id_uoma, pows.cuit, pows.razon_soc, pows.fecha_ospim, pows.os_anterior, 
pows.discapacitado, pows.id_transaction, pows.message_code, pows.message_description, pows.fecha_informe, pows.operacion, pows.fpp from informes.padron_omint_ws pows where alta_fecha > fecha_proceso_p
union
select bows.id_ospim, bows.seccional, bows.id_tercerizadora, bows.cuil_titular, bows.cuil, bows.inte, bows.parentesco, bows.apellido, bows.nombre, bows.documento_tipo, 
bows.docu_numero, bows.naci_fecha, bows.sexo, bows.civil_esta, bows.nacionalidad, bows.provincia, bows.localidad, bows.postal_codi, bows.calle, bows.numero, bows.piso, bows.depto, 
bows.telefono, bows.categoria, bows.ramo, bows.id_plan, bows.plan, bows.ingre_fecha, bows.baja_fecha, bows.id_uoma, bows.cuit, bows.razon_soc, bows.fecha_ospim, bows.os_anterior, 
bows.discapacitado, bows.id_transaction, bows.message_code, bows.message_description, bows.fecha_informe, bows.operacion, bows.fpp from informes.bajas_omint_ws bows where alta_fecha > fecha_proceso_p
order by 38, 4; -- message_description, cuil_titular;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1500;