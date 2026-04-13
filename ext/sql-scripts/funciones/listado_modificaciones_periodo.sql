-- Function: listado_modificaciones_periodo(character varying, date, date)

-- DROP FUNCTION listado_modificaciones_periodo(character varying, date, date);

CREATE OR REPLACE FUNCTION listado_modificaciones_periodo(id_terc character varying, fecha_desde date, fecha_hasta date)
  RETURNS SETOF cambios_periodo_result AS
$BODY$
BEGIN

drop table if exists cambios_periodo;
drop table if exists domi_cambio;

create temp table cambios_periodo(cuil_titular varchar, inte int, parentesco varchar, docu_numero varchar, apellido varchar, nombre varchar, cambio varchar, anterior varchar, ahora varchar);

insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora) 
--PRIMERO CAMBIOS DE DATOS PERSONALES...
select  a.cuil_titular, 
	a.inte, 
	upper(a.parentesco), 
	a.docu_numero, 
	upper(a.apellido), 
	upper(a.nombre),
	case 
	when h.apellido<>a.apellido then 'CAMBIO DE APELLIDO'
        when h.nombre<>a.nombre then 'CAMBIO DE NOMBRE'
	when h.documento_tipo<>a.documento_tipo then 'CAMBIO DE TIPO DE DOCUMENTO'
	when upper(h.sexo)<>upper(a.sexo) then 'CAMBIO DE SEXO'
	when h.cuil<>a.cuil then 'CAMBIO DE CUIL'
	when h.naci_fecha<>a.naci_fecha then 'CAMBIO FECHA DE NACIMIENTO'
        when rtrim(upper(h.civil_esta))<>rtrim(upper(a.civil_esta)) then 'CAMBIO ESTADO CIVIL'  
	when upper(h.parentesco)<>upper(a.parentesco) then 'CAMBIO PARENTESCO'
	when h.ingre_fecha<>a.ingre_fecha then 'CAMBIO FECHA INGRESO'
	when h.id_seccional<>a.id_seccional then 'CAMBIO DE SECCIONAL'
	when h.anterior_os<>a.anterior_os then 'CAMBIO OS ANTERIOR'
	when h.vigen_fecha<>a.vigen_fecha then 'CAMBIO DE VIGENCIA' 
	when h.discapacitado<>a.discapacitado then 'CAMBIO DISCAPACIDAD'
	when h.docu_numero<>a.docu_numero then 'CAMBIO NUMERO DE DOCUMENTO'
	when h.nacionalidad<>a.nacionalidad then 'CAMBIO DE NACIONALIDAD'
	when h.aportante_titular<>a.aportante_titular then 'CAMBIO APORTANTE'
	end as cambio, 
	case 
	when h.apellido<>a.apellido then h.apellido
        when h.nombre<>a.nombre then h.nombre
	when h.documento_tipo<>a.documento_tipo then h.documento_tipo
	when upper(h.sexo)<>upper(a.sexo) then h.sexo
	when h.cuil<>a.cuil then h.cuil
	when h.naci_fecha<>a.naci_fecha then to_char(h.naci_fecha,'dd/MM/yyyy')
        when rtrim(upper(h.civil_esta))<>rtrim(upper(a.civil_esta)) then upper(h.civil_esta)
	when upper(h.parentesco)<>upper(a.parentesco) then upper(h.parentesco)
	when h.ingre_fecha<>a.ingre_fecha then to_char(h.ingre_fecha,'dd/MM/yyyy')
	when h.id_seccional<>a.id_seccional then se2.descripcion
	when h.anterior_os<>a.anterior_os then cast(h.anterior_os as varchar)
	when h.vigen_fecha<>a.vigen_fecha then to_char(h.vigen_fecha,'dd/MM/yyyy')
	when h.discapacitado<>a.discapacitado then h.discapacitado
	when h.docu_numero<>a.docu_numero then h.docu_numero
	when h.nacionalidad<>a.nacionalidad then cast(h.nacionalidad as varchar)
	when h.aportante_titular<>a.aportante_titular then cast(h.aportante_titular as varchar) end as anterior,
	case 
	when h.apellido<>a.apellido then a.apellido
        when h.nombre<>a.nombre then a.nombre
	when h.documento_tipo<>a.documento_tipo then a.documento_tipo
	when upper(h.sexo)<>upper(a.sexo) then a.sexo
	when h.cuil<>a.cuil then a.cuil
	when h.naci_fecha<>a.naci_fecha then to_char(a.naci_fecha,'dd/MM/yyyy')
        when rtrim(upper(h.civil_esta))<>rtrim(upper(a.civil_esta)) then upper(a.civil_esta)
	when upper(h.parentesco)<>upper(a.parentesco) then upper(a.parentesco)
	when h.ingre_fecha<>a.ingre_fecha then to_char(a.ingre_fecha,'dd/MM/yyyy')
	when h.id_seccional<>a.id_seccional then se.descripcion
	when h.anterior_os<>a.anterior_os then cast(a.anterior_os as varchar)
	when h.vigen_fecha<>a.vigen_fecha then to_char(a.vigen_fecha,'dd/MM/yyyy')
	when h.discapacitado<>a.discapacitado then a.discapacitado
	when h.docu_numero<>a.docu_numero then a.docu_numero
	when h.nacionalidad<>a.nacionalidad then cast(a.nacionalidad as varchar)
	when h.aportante_titular<>a.aportante_titular then cast(a.aportante_titular as varchar) end as actual
from afiliado a, afi_estados_histo h, afi_tercerizadora_servicio s, seccional se, seccional se2
where a.cuil_titular=h.cuil_titular
and a.inte=h.inte
--and a.cuil_titular='20208051734'
and h.descripcion_operacion='MOD'
and h.alta_fecha>=fecha_desde
and h.alta_fecha<=fecha_hasta
and (h.apellido<>a.apellido or h.nombre<>a.nombre or h.documento_tipo<>a.documento_tipo or upper(h.sexo)<>upper(a.sexo) or h.cuil<>a.cuil 
    or h.naci_fecha<>a.naci_fecha or rtrim(upper(h.civil_esta))<>rtrim(upper(a.civil_esta)) or upper(h.parentesco)<>upper(a.parentesco) 
    or h.ingre_fecha<>a.ingre_fecha or h.id_seccional<>a.id_seccional or h.anterior_os<>a.anterior_os or h.vigen_fecha<>a.vigen_fecha 
    or h.discapacitado<>a.discapacitado or h.docu_numero<>a.docu_numero or h.nacionalidad<>a.nacionalidad or h.aportante_titular<>a.aportante_titular) 
and a.cuil_titular=s.cuil_titular
and s.id_tercerizadora=id_terc
and (s.baja_fecha is null or s.baja_fecha>=fecha_hasta)
and (s.fecha_fin_pres is null or fecha_fin_pres>=fecha_hasta)
and se.id_seccional=a.id_seccional
and se2.id_seccional=h.id_seccional;

--CAMBIOS EN PLANES
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido, nombre, cambio, anterior, ahora) 
select ap.cuil_titular, a.inte, a.parentesco, a.docu_numero, a.apellido, a.nombre, 'CAMBIO DE PLAN',cast('' as varchar) as anterior, p.descripcion as actual
from afi_plan ap, afiliado a, plan p, afi_tercerizadora_servicio t
where ap.alta_fecha>=fecha_desde
and ap.alta_fecha<=fecha_hasta
and exists (select 1 from afi_plan app where app.baja_fecha>=fecha_desde and app.baja_fecha<=fecha_hasta and app.cuil_titular=ap.cuil_titular 
			               and app.baja_fecha is not null and app.baja_fecha<=fecha_hasta )
and (ap.baja_fecha is null or ap.baja_fecha>fecha_hasta)
and ap.cuil_titular=a.cuil_titular
and ap.inte=a.inte
and ap.id_plan=p.id_plan
and t.cuil_titular=ap.cuil_titular
and t.inte=ap.inte
and t.id_tercerizadora=id_terc
and (t.baja_fecha is null or t.baja_fecha>=fecha_hasta)
and (t.fecha_fin_pres is null or t.fecha_fin_pres>=fecha_hasta);


update cambios_periodo c
set anterior=p.descripcion
from afi_plan ap, plan p
where c.cuil_titular=ap.cuil_titular
and ap.id_plan=p.id_plan
and c.cambio='CAMBIO DE PLAN'
and ap.baja_fecha>=fecha_desde
and ap.cuil_titular=ap.cuil_titular 
and ap.baja_fecha is not null 
and ap.baja_fecha=(select min(baja_fecha) from afi_plan app where app.cuil_titular=ap.cuil_titular and app.inte=ap.inte 
					                    and app.baja_fecha>=fecha_desde);
create temp table domi_cambio as
select  ad.cuil_titular, 
	ad.inte, 
	upper(a.parentesco) as parentesco, 
	a.docu_numero, 
	upper(a.apellido) as apellido, 
	upper(a.nombre) as nombre, 
	cast('' as varchar) as cambio,
	ad.calle as calle_actual, 
        ad.piso as piso_actual, 
	ad.depto as depto_actual,
	ad.oficina as oficina_actual,
	ad.postal_codi as cp_actual,
	ad.barrio as barrio_actual,
        ad.telefono as telefono_actual,
	ad.provincia as provincia_actual,
	ad.localidad as localidad_actual,
	ad.numero as numero_actual,
	ad.calle as calle_anterior, 
        ad.piso as piso_anterior, 
	ad.depto as depto_anterior,
	ad.oficina as oficina_anterior,
	ad.postal_codi as cp_anterior,
	ad.barrio as barrio_anterior,
        ad.telefono as telefono_anterior,
	ad.provincia as provincia_anterior,
	ad.localidad as localidad_anterior,
	ad.numero as numero_anterior
--into temp domi_cambio
from afi_domicilio ad, afi_tercerizadora_servicio t, afiliado a
where ad.alta_fecha>=fecha_desde
and ad.alta_fecha<=fecha_hasta
and exists (select 1 from afi_domicilio app where app.baja_fecha>=fecha_desde and app.baja_fecha<=fecha_hasta and app.cuil_titular=ad.cuil_titular and app.inte=0
			               and app.baja_fecha is not null)	               
and (ad.baja_fecha is null or ad.baja_fecha>fecha_hasta)
and ad.cuil_titular=a.cuil_titular
and ad.inte=0
and a.inte=ad.inte
and t.cuil_titular=ad.cuil_titular
and t.inte=ad.inte
and t.id_tercerizadora='CSA'
and (t.baja_fecha is null or t.baja_fecha>=fecha_hasta)
and (t.fecha_fin_pres is null or t.fecha_fin_pres>=fecha_hasta);



update domi_cambio dc
set	calle_anterior=ad.calle, 
	piso_anterior=ad.piso, 
	depto_anterior=ad.depto,
	oficina_anterior=ad.oficina,
	cp_anterior=ad.postal_codi,
	barrio_anterior=ad.barrio,
        telefono_anterior=ad.telefono,
	provincia_anterior=ad.provincia,
	localidad_anterior=ad.localidad,
	numero_anterior=ad.numero	
from afi_domicilio ad
where ad.baja_fecha>=fecha_desde
and AD.baja_fecha<=fecha_hasta 
and AD.cuil_titular=ad.cuil_titular 
and AD.inte=0
and AD.baja_fecha is not null
and ad.cuil_titular=dc.cuil_titular
and ad.inte=dc.inte;


update domi_cambio
set cambio=case 
	when calle_anterior<>calle_actual then 'CAMBIO DE CALLE'
        when piso_anterior<>piso_actual then 'CAMBIO DE PISO'
	when depto_anterior<>depto_actual then 'CAMBIO DE DEPARTAMENTO'
	when oficina_anterior<>oficina_actual then 'CAMBIO DE OFICINA'
	when cp_anterior<>cp_actual then 'CAMBIO DE CODIGO POSTAL'
	when barrio_anterior<>barrio_actual then 'CAMBIO DE BARRIO'
        when telefono_anterior<>telefono_actual then 'CAMBIO DE TELEFONO'  
	when provincia_anterior<>provincia_actual then 'CAMBIO DE PROVINCIA'
	when localidad_anterior<>localidad_actual then 'CAMBIO DE LOCALIDAD'
	when numero_anterior<>numero_actual then 'CAMBIO DE NUMERO' end;

insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora) 
select cuil_titular, inte, parentesco, docu_numero, apellido, nombre, cambio,
       case when calle_anterior<>calle_actual then calle_anterior
        when piso_anterior<>piso_actual then piso_anterior
	when depto_anterior<>depto_actual then depto_anterior
	when oficina_anterior<>oficina_actual then oficina_anterior
	when cp_anterior<>cp_actual then cp_anterior
	when barrio_anterior<>barrio_actual then barrio_anterior
        when telefono_anterior<>telefono_actual then telefono_anterior
	when provincia_anterior<>provincia_actual then pr1.detalle
	when localidad_anterior<>localidad_actual then l1.detalle
	when numero_anterior<>numero_actual then numero_anterior end,
	case when calle_anterior<>calle_actual then calle_actual
        when piso_anterior<>piso_actual then piso_actual
	when depto_anterior<>depto_actual then depto_actual
	when oficina_anterior<>oficina_actual then oficina_actual
	when cp_anterior<>cp_actual then cp_actual
	when barrio_anterior<>barrio_actual then barrio_actual
        when telefono_anterior<>telefono_actual then telefono_actual
	when provincia_anterior<>provincia_actual then pr2.detalle
	when localidad_anterior<>localidad_actual then l2.detalle
	when numero_anterior<>numero_actual then numero_actual end
from domi_cambio dc, provincia pr1,provincia pr2, localidad l1, localidad l2
where cambio is not null
and pr1.id_provincia=provincia_anterior
and pr2.id_provincia=provincia_actual
and l1.id_localidad=localidad_anterior
and l2.id_localidad=localidad_actual;

					                    

delete from cambios_periodo
where rtrim(anterior) in ('COBERTURA','COBERTURA - USUFRUCTO','INTEGRAL', 'TOTAL')
       and rtrim(ahora) in ('USUFRUCTO','AMTIMA - SINDICATO','SINDICATO'); --ESTAS SON BAJAS

       
delete from cambios_periodo
where rtrim(anterior) in ('USUFRUCTO','AMTIMA - SINDICATO','SINDICATO')
       and rtrim(ahora) in ('USUFRUCTO','AMTIMA - SINDICATO','SINDICATO'); --ESTAS NO DEBEN FIGURAR

delete from cambios_periodo
where rtrim(anterior) in ('USUFRUCTO','AMTIMA - SINDICATO','SINDICATO')
       and rtrim(ahora) in ('COBERTURA','COBERTURA - USUFRUCTO','INTEGRAL', 'TOTAL'); --ESTAS SON ALTAS

return query
select distinct cuil_titular, inte, parentesco, docu_numero, apellido, nombre, cambio, anterior, ahora 
from cambios_periodo 
order by cuil_titular;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION listado_modificaciones_periodo(character varying, date, date) OWNER TO postgres;

