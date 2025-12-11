CREATE OR REPLACE FUNCTION unificaaportes() 
RETURNS character varying
    LANGUAGE plpgsql
    AS $BODY$	
DECLARE _record 	RECORD;
DECLARE	cuil_titular_v	VARCHAR(13);
DECLARE	unifica_v    	VARCHAR(13);
DECLARE	inte_v     	INTEGER;

BEGIN

 --Define output columns
 FOR _record IN SELECT cuil_titular, inte, unifica FROM unifica_final a
-- FOR _record IN SELECT distinct cuil_titular FROM plan_prueba where id_plan is null
 LOOP
  cuil_titular_v = _record.cuil_titular;
  unifica_v = _record.unifica;
  inte_v = _record.inte;

  --Hacer lo que dice Ana
	--2do paso la damos de baja como titular
	update afiliado set baja_fecha=current_timestamp, baja_usr='proceso_unifica' where cuil_titular=unifica_v;

	--3ro cambiamos el cuil_titular/inte de afi_situ_laboral
	update afi_situ_laboral set cuil_titular=cuil_titular_v,inte=inte_v, modi_fecha=current_timestamp,modi_usr='proceso_unifica'  where cuil_titular=unifica_v;
	 
	--4to lo mismo con afi_aportes
	update afi_aportes set cuil_titular=cuil_titular_v,inte=inte_v, modi_fecha=current_timestamp, modi_usr='proceso_unifica'  where cuil_titular=unifica_v;

	--5to lo mismo con afi_documento
	update afi_documento set cuil_titular=cuil_titular_v,inte=inte_v, modi_fecha=current_timestamp, modi_usr='proceso_unifica'  where cuil_titular=unifica_v and not exists 
	(select 1 from afi_documento a where a.cuil_titular=cuil_titular_v and a.inte=inte_v);

	--6to baja a afi_tercerizadora_servicio
	update afi_tercerizadora_servicio set baja_fecha=current_timestamp,baja_usr='procesoUnifica' where cuil_titular=unifica_v;

	--7mo baja a afi_domicilio
	update afi_domicilio set baja_fecha=current_timestamp,baja_usr='procesoUnifica' where cuil_titular=unifica_v;

	--8vo baja a afi_plan
	update afi_plan set baja_fecha=current_timestamp,baja_usr='procesoUnifica' where cuil_titular=unifica_v;

  --Fin de hacer lo que dice Ana (Comas)
 END LOOP;

 RETURN cuil_titular_v;

END;
	
$BODY$;


ALTER FUNCTION public.unificaaportes() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
