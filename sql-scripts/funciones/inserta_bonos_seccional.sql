CREATE OR REPLACE FUNCTION inserta_bonos_seccional(tipo_bono_v integer, seccional_v integer, fecha_envio_v date, nro_bono_desde integer, nro_bono_hasta integer, username character varying)
  RETURNS integer AS
$BODY$
declare id_envio_v integer;
begin

id_envio_v=cast (max(id_envio)+1 as int) from bonos_seccional;

if (id_envio_v is null or id_envio_v=0) then
 id_envio_v=1;
end if;

FOR i  IN nro_bono_desde..nro_bono_hasta LOOP
	INSERT INTO bonos_seccional(tipo_bono, id_seccional, fecha_envio, nro_bono, id_envio, alta_usr, alta_fecha)VALUES (tipo_bono_v, seccional_v, fecha_envio_v,i, id_envio_v, username, current_date);
END LOOP;	

return id_envio_v;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION inserta_bonos_seccional(integer, integer, date, integer, integer, character varying) OWNER TO postgres;
