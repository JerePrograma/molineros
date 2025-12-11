-- Function: uoma.actualiza_domicilio_correspondencia(integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying)

-- DROP FUNCTION uoma.actualiza_domicilio_correspondencia(integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying);

CREATE OR REPLACE FUNCTION uoma.actualiza_domicilio_correspondencia(id_localidad_v integer, id_provincia_v integer, calle_v character varying, numero_v character varying, piso_v character varying, depto_v character varying, postal_codi_v character varying, observaciones_v character varying, id_domicilio_p integer, username character varying)
  RETURNS integer AS
$BODY$
BEGIN
update uoma.domicilio_correspondencia
set calle=calle_v,
    piso=piso_v,
    depto=depto_v,
    postal_codi=postal_codi_v,
    observaciones=observaciones_v,
    modi_fecha=current_date,
    modi_usr=username,
    provincia=id_provincia_v,
    localidad=id_localidad_v,
    numero=numero_v
where id_domicilio=id_domicilio_p;


return id_domicilio_p;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION uoma.actualiza_domicilio_correspondencia(integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying)
  OWNER TO postgres;

