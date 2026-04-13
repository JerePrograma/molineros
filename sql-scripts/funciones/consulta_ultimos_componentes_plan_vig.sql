CREATE OR REPLACE FUNCTION consulta_ultimos_componentes_plan_vig(IN cuil_titular_p character varying)
  RETURNS TABLE(id_aporte integer, fecha_ingre date, fecha_egre date, id_socio integer, tipo_aporte character, descripcion character varying, modi_fecha timestamp with time zone, modi_usr character) AS
$BODY$

declare id_plan_aux bigint;
declare f_ingre date;
DECLARE _aporte RECORD;
DECLARE _apor_ing RECORD;

BEGIN

--id_plan_aux = (select max(id) from afi_plan where cuil_titular = cuil_titular_p and baja_fecha is null);
-- este cambio lo hice porque a veces insertamos nuevos ids de plan para cargar historicos de planes con viejas vigencias
id_plan_aux = (select max(id) from afi_plan ap where ap.cuil_titular = cuil_titular_p and ap.baja_fecha is null and ap.vigen_desde = (
   select max(ap_.vigen_desde) from afi_plan ap_ where ap.cuil_titular = ap_.cuil_titular and baja_fecha is null));
drop table if exists ids_aux;

create temp table ids_aux as select a.id_aporte, a.fecha_ingre, a.fecha_egre, a.id_socio, 
				    a.tipo_aporte, b.descripcion, a.modi_fecha, a.modi_usr 
				    from afi_aportes a, aporte b 
				    where a.id_aporte=b.id_aporte 
				    and a.id_plan_serial = id_plan_aux
				    order by a.id_aporte;

FOR _aporte IN SELECT * FROM ids_aux order by id_aporte LOOP
	RAISE NOTICE 'aporte: %' , _aporte.id_aporte;
	RAISE NOTICE 'id_socio: %' , _aporte.id_socio;  
	RAISE NOTICE 'fecha_ingre: %' , _aporte.fecha_ingre;  		
	IF(_aporte.id_socio is not null) THEN
		RAISE NOTICE 'id_socio != null';
		f_ingre = min(aa_.fecha_ingre) from afi_aportes aa_
					   where aa_.id_socio = _aporte.id_socio 
					   and aa_.tipo_aporte = _aporte.tipo_aporte;
		RAISE NOTICE 'f_ingre 1: %' , f_ingre;  	
		update ids_aux set fecha_ingre = f_ingre where ids_aux.id_aporte = _aporte.id_aporte;
	ELSE
		RAISE NOTICE 'id_socio es null';
	-- si no hay id_socio, hay que revisar si se hay continuidad de los planes c dicho aporte
		f_ingre = _aporte.fecha_ingre;
			
		FOR _apor_ing IN select aa_.fecha_ingre, aa_.fecha_egre 
				from afi_aportes aa_
				where aa_.cuil_titular = cuil_titular_p
				and aa_.id_aporte = _aporte.id_aporte
				order by aa_.fecha_ingre desc LOOP

			IF _aporte.fecha_ingre = _apor_ing.fecha_ingre THEN
				CONTINUE; -- lo logico que el primer registro de _aporte_ing, 
					  -- sea el mismo obtenido en _aporte			
			END IF;
			
			IF (_aporte.fecha_ingre = cast(_apor_ing.fecha_egre + interval '1 day' as date))
			   OR (_aporte.fecha_ingre = cast(_apor_ing.fecha_egre as date)) -- compat version vieja	 
			   OR (f_ingre = cast(_apor_ing.fecha_egre + interval '1 day' as date)) 
			   OR (f_ingre = cast(_apor_ing.fecha_egre as date)) 	-- compat version vieja
			THEN	
				f_ingre = _apor_ing.fecha_ingre;
				RAISE NOTICE 'f_ingre 2: %' , f_ingre;  	
			ELSE
				RAISE NOTICE 'salir';  	
				EXIT; -- no hay continuidad	
			END IF;
		END LOOP;
				
	   	update ids_aux set fecha_ingre = f_ingre where ids_aux.id_aporte = _aporte.id_aporte;  			
	END IF;

END LOOP;
			       
return query select * from ids_aux;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 100;