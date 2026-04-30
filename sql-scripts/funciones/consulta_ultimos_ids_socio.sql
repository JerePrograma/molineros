-- Type: ids_socio_con_fechas_vigencia

-- DROP TYPE ids_socio_con_fechas_vigencia CASCADE;

CREATE TYPE ids_socio_con_fechas_vigencia AS
   (f_inicio_id_ospim date,
    f_fin_id_ospim date,
    id_ospim integer,
    f_inicio_id_amtima date,
    f_fin_id_amtima date,
    id_amtima integer,
    f_inicio_id_uoma date,
    f_fin_id_uoma date,
    id_uoma integer,
    modi_fecha_ospim timestamp,
    modi_user_ospim character(50),
    modi_fecha_amtima timestamp,
    modi_user_amtima character(50),
    modi_fecha_uoma timestamp,
    modi_user_uoma character(50));

/********************************************************************************/
    
CREATE OR REPLACE FUNCTION consulta_ultimos_ids_socio(cuil_titular_p character varying)
  RETURNS SETOF ids_socio_con_fechas_vigencia AS
$BODY$
declare f_inicio_id_ospim date;
declare f_fin_id_ospim date;
declare id_ospim integer;

declare f_inicio_id_amtima date;
declare f_fin_id_amtima date;
declare id_amtima integer;

declare f_inicio_id_uoma date;
declare f_fin_id_uoma date;
declare id_uoma integer;

begin

id_ospim  = (select max(id_socio) from afi_aportes where tipo_aporte = 'O' and cuil_titular = cuil_titular_p and baja_fecha is null);
id_amtima = (select max(id_socio) from afi_aportes where tipo_aporte = 'A' and cuil_titular = cuil_titular_p and baja_fecha is null);
id_uoma   = (select max(id_socio) from afi_aportes where tipo_aporte = 'U' and cuil_titular = cuil_titular_p and baja_fecha is null);

-- obtener minima fecha de ingreso del aporte (si existe el aporte)
if(id_ospim > 0) then f_inicio_id_ospim = (select min(fecha_ingre) from afi_aportes 
					  where cuil_titular = cuil_titular_p and id_socio = id_ospim and baja_fecha is null); end if;
if(id_amtima > 0) then f_inicio_id_amtima = (select min(fecha_ingre) from afi_aportes 
					  where cuil_titular = cuil_titular_p and id_socio = id_amtima and baja_fecha is null); end if;
if(id_uoma > 0) then f_inicio_id_uoma = (select min(fecha_ingre) from afi_aportes 
					  where cuil_titular = cuil_titular_p and id_socio = id_uoma and baja_fecha is null); end if;			
					  
-- obtener ultima fecha de ingreso del aporte (puede ser vacia si hay vigencia o porque no tiene el aporte)
if(id_ospim > 0) then f_fin_id_ospim = (select fecha_egre from afi_aportes 
							 where cuil_titular = cuil_titular_p
							 and id_socio = id_ospim
							 and baja_fecha is null
							 and fecha_ingre in (select max(fecha_ingre) 
									     from afi_aportes 
									     where cuil_titular = cuil_titular_p 
									     and id_socio = id_ospim 
									     and baja_fecha is null)); end if;
if(id_amtima > 0) then f_fin_id_amtima = (select fecha_egre from afi_aportes 
							 where cuil_titular = cuil_titular_p
							 and id_socio = id_amtima
							 and baja_fecha is null
							 and fecha_ingre in (select max(fecha_ingre) 
									     from afi_aportes 
									     where cuil_titular = cuil_titular_p 
									     and id_socio = id_amtima
									     and baja_fecha is null)); end if;

if(id_uoma > 0) then f_fin_id_uoma = (select fecha_egre from afi_aportes 
							 where cuil_titular = cuil_titular_p
							 and id_socio = id_uoma
							 and baja_fecha is null
							 and fecha_ingre in (select max(fecha_ingre) 
									     from afi_aportes 
									     where cuil_titular = cuil_titular_p 
									     and id_socio = id_uoma
									     and baja_fecha is null)); end if;									     					  					  

drop table if exists ids_aux;
create temp table ids_aux as select f_inicio_id_ospim, f_fin_id_ospim, id_ospim as ospim_id, 
f_inicio_id_amtima, f_fin_id_amtima, id_amtima as amtima_id,
f_inicio_id_uoma, f_fin_id_uoma, id_uoma as uoma_id, 
cast(null as timestamp) as fech_modi_id_ospim, cast('' as character(50)) as usr_modi_id_ospim,
cast(null as timestamp) as fech_modi_id_amtima, cast('' as character(50)) as usr_modi_id_amtima,
cast(null as timestamp) as fech_modi_id_uoma, cast('' as character(50)) as usr_modi_id_uoma;

update ids_aux set fech_modi_id_ospim = ospim.modi_fecha, 
		   usr_modi_id_ospim = ospim.modi_usr 
		     from afi_aportes ospim
		     where cuil_titular=cuil_titular_p and id_socio=isNull(id_ospim)
		     and modi_fecha = (select max(modi_fecha)
				       from afi_aportes
				       where cuil_titular=cuil_titular_p
				       and id_socio=isNull(id_ospim) );
				          
update ids_aux set fech_modi_id_amtima = amtima.modi_fecha, 
		   usr_modi_id_amtima = amtima.modi_usr 
		     from afi_aportes amtima
		     where cuil_titular=cuil_titular_p and id_socio=isNull(id_amtima)
		     and modi_fecha = (select max(modi_fecha)
				       from afi_aportes
				       where cuil_titular=cuil_titular_p
				       and id_socio=isNull(id_amtima) );
				       
update ids_aux set fech_modi_id_uoma = uoma.modi_fecha, 
		   usr_modi_id_uoma = uoma.modi_usr 
		     from afi_aportes uoma
		     where cuil_titular=cuil_titular_p and id_socio=isNull(id_uoma)
		     and modi_fecha = (select max(modi_fecha)
				       from afi_aportes
				       where cuil_titular=cuil_titular_p
				       and id_socio=isNull(id_uoma) ); 				        
				       
return query select * from ids_aux;
/*return query select f_inicio_id_ospim, f_fin_id_ospim, id_ospim, f_inicio_id_amtima, f_fin_id_amtima, id_amtima,
f_inicio_id_uoma, f_fin_id_uoma, id_uoma,
ospim.modi_fecha as fech_modi_id_ospim, ospim.modi_usr as usr_modi_id_ospim,
amtima.modi_fecha as fech_modi_id_amtima, amtima.modi_usr as usr_modi_id_amtima,
uoma.modi_fecha as fech_modi_id_uoma, uoma.modi_usr as usr_modi_id_uoma
from (select isNull(cast(modi_fecha as date),  cast('18000101' as date)) as modi_fecha, cast(isNull(modi_usr,'') as character(50)) as modi_usr
             from afi_aportes
             where cuil_titular=cuil_titular_p and id_socio=isNull(id_ospim)
             and modi_fecha = (select max(modi_fecha)
                               from afi_aportes
                               where cuil_titular=cuil_titular_p
                               and id_socio=isNull(id_ospim) )) ospim, 
	(select isNull(cast(modi_fecha as date),  cast('18000101' as date)) as modi_fecha, cast(isNull(modi_usr,'') as character(50)) as modi_usr
	     from afi_aportes
	     where cuil_titular=cuil_titular_p and id_socio=isNull(id_amtima)
	     and modi_fecha = (select max(modi_fecha)
			       from afi_aportes
			       where cuil_titular=cuil_titular_p
			       and id_socio=isNull(id_amtima) )) amtima,
	(select isNull(cast(modi_fecha as date),  cast('18000101' as date)) as modi_fecha, cast(isNull(modi_usr,'') as character(50)) as modi_usr
	     from afi_aportes
	     where cuil_titular=cuil_titular_p and id_socio=isNull(id_uoma)
	     and modi_fecha = (select max(modi_fecha)
			       from afi_aportes
			       where cuil_titular=cuil_titular_p
			       and id_socio=isNull(id_uoma) )) uoma ; */

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
