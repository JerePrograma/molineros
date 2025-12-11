CREATE TYPE afiliado_opciones AS
   (id_ospim integer,
    id_uoma integer,
    id_amtima integer,
    apellido character varying,
    nombre character varying,
    documento_tipo character varying,
    sexo character varying,
    cuil character varying,
    naci_fecha date,
    id_estado_civil_sss integer,
    civil_esta character varying,
    nacionalidad integer,
    id_parentesco_sss integer,
    parentesco character varying,
    id_seccional integer,
    anterior_os integer,
    vigen_fecha timestamp without time zone,
    observaciones character varying,
    pres_ssalud_fecha date,
    alta_usr character varying,
    modi_usr character varying,
    discapacitado character varying,
    docu_numero character varying,
    domi_tipo character varying,
    calle character varying,
    piso character varying,
    depto character varying,
    oficina character varying,
    postal_codi character varying,
    barrio character varying,
    telefono character varying,
    tel_laboral character varying,
    celular character varying,
    email character varying,
    observaciones_dom character varying,
    domi_val character varying,
    alta_usr_d character varying,
    modi_usr_d character varying,
    provincia integer,
    localidad integer,
    numero character varying,
    aportante_titular integer,
    baja_f timestamp without time zone,
    baja_u character varying,
    ingre_f date,
    id_motivo_baja integer,
    id_amtima_baja_fecha timestamp without time zone,
    id_ospim_baja_fecha timestamp without time zone,
    id_uoma_baja_fecha timestamp without time zone,
    descripcion character varying,
    cuit character varying,
    razon_soc character varying);
    

CREATE OR REPLACE FUNCTION busca_afiliado_opciones(cuil character)
  RETURNS SETOF afiliado_opciones AS
$BODY$
BEGIN
drop table if exists result;
create temp table result as 
select
0 as id_ospim,
0 as id_uoma,
0 as id_amtima,
a.apellido as apellido,
a.nombre as nombre,
cast('DU' as varchar) as documento_tipo,
cast(case when a.sexo is null then '' else a.sexo end as varchar),
cast(a.cuil as varchar),
cast(null as date) as naci_fecha,
1 as id_estado_civil_sss,
cast('SOLTERO' as varchar) as civil_esta,
10 as nacionalidad,  --ARGENTINA
0 as id_parentesco_sss,
cast('TITULAR' as varchar) as parentesco,
0 as id_seccional,
id_delegacion,
a.delegacion as seccional,
a.os_anterior,
cast(a.fecha_entrega as timestamp without time zone) as vigen_fecha,
cast('ALTA DE ARCHIVO DE OPCIONES DE LA SS' as varchar) as observaciones,
a.fecha_elecc as pres_ssalud_fecha,
cast(null as varchar) as alta_usr,
cast(null as varchar) as modi_usr,
cast(null as varchar) as discapacitado,
substring(a.cuil,3,8) as docu_numero,
cast('P' as varchar) as domi_tipo,
a.calle,
cast(a.piso as varchar) as piso,
a.departamento as depto,
cast(null as varchar) as oficina,
a.postal_codi,
cast(null as varchar) as barrio,
telefono_particular as telefono,
telefono_laboral as tel_laboral,
telefono_celular as celular,
email as email,
--case when telefono_particular is not null and rtrim(telefono_particular)<>'' then a.telefono_particular 
--     when (telefono_particular is null or rtrim(telefono_particular)='') and telefono_celular is not null  and rtrim(telefono_celular)<>'' then telefono_celular
--     else telefono_laboral end as telefono, 
cast(null as varchar) as observaciones_dom,
cast(false as varchar) as domi_val,
cast(null as varchar) as alta_usr_d,
cast(null as varchar) as modi_usr_d,
0 as provincia,
cast(null as integer) as localidad,
a.localidad as localidad_char,
a.numero,
0 as aportante_titular, 
cast(null as timestamp without time zone) as baja_f, --38
cast(null as varchar) as baja_u,
current_date as ingre_f,
0 as id_motivo_baja,
cast(null as timestamp without time zone) as id_amtima_baja_fecha,
cast(null as timestamp without time zone) as id_ospim_baja_fecha,
cast(null as timestamp without time zone) as id_uoma_baja_fecha,
cast(null as varchar) as descripcion,
cuit,
cast('' as varchar) as razon_soc
from afi_opciones_sss a
where a.cuil= $1
and de_alta_portal=false
and baja_fecha is null;

/*update result r
set id_seccional=s.id_seccional
from seccional s
where r.seccional like '%'||s.descripcion||'%';*/
update result r
set id_seccional=sd.id_seccional
from seccional_delegacion sd
where id_delegacion = id_delegacion_sss;

update result r
set localidad=id_localidad,
    provincia=id_provincia,
    postal_codi=cast(cod_postal as varchar)
from localidad l
--where r.localidad=l.id_localidadesss;
where cast(r.localidad_char as integer)=l.id_localidadesss;

/*
update result r
set localidad=id_localidad,
    provincia=id_provincia
from localidad l
where rtrim(r.localidad_char) like '%'||rtrim(l.detalle)||'%';

update result r
set localidad=id_localidad,
    provincia=id_provincia
from localidad l
where r.localidad=l.id_localidadesss
and r.provincia=0;
*/

update result r
set razon_soc=e.razon_soc
from empresa e
where r.cuit=e.cuit
and e.sucursal='000';


return query
select  id_ospim, 
	id_uoma, 
	id_amtima, 
	apellido, 
	nombre, 
	documento_tipo, 
	sexo, 
	cast(r.cuil as varchar), 
	naci_fecha, 
	id_estado_civil_sss,
	civil_esta, 
	nacionalidad, 
	id_parentesco_sss,
	parentesco, 
	id_seccional, 
	os_anterior, 
	vigen_fecha, 
	observaciones, 
	pres_ssalud_fecha,
	alta_usr, 
	modi_usr, 
	discapacitado, 
	cast(docu_numero as varchar), 
	domi_tipo, 
	calle, 
	piso, 
	depto, 
	oficina, 
	postal_codi, 
	barrio, 
	--telefono,
	telefono,
	tel_laboral,
	celular,
	email,
	observaciones_dom, 
	domi_val, 
	alta_usr_d, 
	modi_usr_d, 
	provincia, 
	localidad, 
	numero, 
	aportante_titular, 
	baja_f, 
	baja_u, 
	ingre_f, 
	id_motivo_baja, 
	id_amtima_baja_fecha, 
	id_ospim_baja_fecha, 
	id_uoma_baja_fecha, 
	descripcion,
	cuit, 
	razon_soc 
from result r;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;    
    