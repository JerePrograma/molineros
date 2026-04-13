-- DROP FUNCTION listado_reincorporacion_total_periodo(character varying, date, date);

CREATE OR REPLACE FUNCTION listado_reincorporacion_total_periodo(id_tercerizadora_v character varying, fecha_desde date, fecha_hasta date)
  RETURNS SETOF lista_reincorporacion_total_periodo AS
$BODY$
BEGIN
--REINCORPORACION TOTAL
return query
select distinct afi.id_ospim, s.id_seccional||' - '||s.descripcion as seccional, at.id_tercerizadora, a.cuil_titular, a.cuil, a.inte, upper(a.parentesco) as parentesco, upper(a.apellido) as apellido, upper(a.nombre) as nombre,a.documento_tipo,
       a.docu_numero, to_char(afi.naci_fecha, 'DD/MM/YYYY') as naci_fecha,upper(afi.sexo) as sexo,upper(afi.civil_esta) as civil_esta,n.detalle as nacionalidad,p.detalle as provincia,l.detalle as localidad,ad.postal_codi, upper(ad.calle) as calle,ad.numero, ad.piso, ad.depto,
       ad.telefono, cl.categoria,pl.descripcion as plan,to_char(a.vigen_fecha, 'DD/MM/YYYY') as vigen_fecha,afi.id_uoma,al.cuit,to_char(a.alta_fecha, 'DD/MM/YYYY HH12:MI:SS PM') as alta_fecha--, *
from afi_estados_histo  a, seccional s, afi_tercerizadora_servicio at, nacionalidad n, afi_domicilio ad, provincia p, localidad l, afi_situ_laboral al,
     categoria_laboral cl, afi_plan ap, plan pl, afiliado afi
where a.alta_fecha > fecha_desde
and a.alta_fecha < fecha_hasta
and a.baja_fecha is not null and a.baja_fecha < a.alta_fecha
and s.id_seccional=a.id_seccional
and a.cuil_titular=afi.cuil_titular
and a.inte=afi.inte
and at.cuil_titular=a.cuil_titular
and at.inte=0
--and a.modi_usr <> 'admin'
and n.id=a.nacionalidad
and ad.cuil_titular=a.cuil_titular
and ad.inte=0
and (ad.baja_fecha is null or ad.baja_fecha >fecha_hasta)
and p.id_provincia=ad.provincia
and l.id_localidad=ad.localidad
and al.cuil_titular=a.cuil_titular
and al.inte=0
and al.id_categoria=cl.id_categoria
and (al.fecha_egre is null or al.fecha_egre>fecha_hasta)
and al.fecha_ingre=(select max(fecha_ingre) from afi_situ_laboral asl where asl.cuil_titular=al.cuil_titular and asl.inte=al.inte)
and ap.cuil_titular=a.cuil_titular
and ap.alta_fecha=(select max(alta_fecha) from afi_plan afp where afp.cuil_titular=ap.cuil_titular limit 1)
and ap.inte=0
and pl.id_plan=ap.id_plan
and at.id_tercerizadora=id_tercerizadora_V
--ES REINCORPORACION PORQUE EXISTE EN EL HISTORICO UN REGISTRO DE BAJA Y PORQUE EN AFILIADO ESTA DE ALTA (SE REINCORPORÓ)
and exists(select 1 from afi_estados_histo aeh 
	   where a.cuil_titular=aeh.cuil_titular and aeh.inte=0 and alta_fecha > fecha_desde and alta_fecha < fecha_hasta and (baja_fecha is not null and baja_fecha < alta_fecha))
and (afi.baja_fecha > localtimestamp or afi.baja_fecha is null)
order by id_ospim;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION listado_reincorporacion_total_periodo(character varying, date, date) OWNER TO postgres;
