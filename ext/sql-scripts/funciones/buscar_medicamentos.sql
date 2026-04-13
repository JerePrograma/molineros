CREATE OR REPLACE FUNCTION buscar_medicamentos(troquel_v integer, registro_v integer, nombre_v character varying, presentacion_v character varying, laboratorio_v character varying, cod_barras_v character varying)
  RETURNS SETOF result_medicamento AS
$BODY$
BEGIN
  return query
  select m.id_medicamento, m.troquel, m.nro_registro, m.nombre, m.presentacion, m.laboratorio, m.accion, m.droga, trunc(m.precio,2), 
  case when trunc(v.porc_ospim,2) is null then 0 else trunc(v.porc_ospim,2)  end , 
  case when trunc(v.porc_amtima,2) is null then 0 else trunc(v.porc_amtima,2) end , 
  case when trunc(v.porc_sssalud,2) is null then 0 else trunc(v.porc_sssalud,2) end ,  
  case when v.pmoe_n is null then 0 else v.pmoe_n end , m.cod_barra	  
  from medicamentos m
  left outer join vademecum v
  on v.registro=m.nro_registro
  where (troquel_v is null or (troquel_v is not null and m.troquel=troquel_v))  	
  and (registro_v is null or (registro_v is not null and m.nro_registro=registro_v))
  and (nombre_v is null or (nombre_v is not null and upper(m.nombre) like '%'||upper(nombre_v)||'%'))
  and (presentacion_v is null or (presentacion_v is not null and upper(m.presentacion) like '%'||upper(presentacion_v)||'%'))
  and (laboratorio_v is null or (laboratorio_v is not null and upper(m.laboratorio) like '%'||upper(laboratorio_v)||'%'))
  and (cod_barras_v is null or (cod_barras_v is not null and upper(m.cod_barra) like '%'||upper(cod_barras_v)||'%'))
  and m.fecha<current_date
  and m.fecha=(select max(fecha) from medicamentos m2 where m2.nro_registro=m.nro_registro);
  
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_medicamentos(integer, integer, character varying, character varying, character varying, character varying) OWNER TO postgres;

