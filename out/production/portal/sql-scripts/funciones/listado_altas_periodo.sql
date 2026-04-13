CREATE OR REPLACE FUNCTION listado_altas_periodo(id_tercerizadora character varying,
 fecha_desde date) 
RETURNS SETOF lista_alta_periodo
    LANGUAGE plpgsql
    AS $BODY$
BEGIN
--ALTAS TOTALES
return query
select id_ospim, s.id_seccional||' - '||s.descripcion as seccional, at.id_tercerizadora, a.cuil_titular, a.cuil, a.inte, upper(a.parentesco) as parentesco, 
       upper(a.apellido) as apellido, upper(a.nombre) as nombre,a.documento_tipo, a.docu_numero, to_char(naci_fecha, 'DD/MM/YYYY') as naci_fecha,upper(sexo) as sexo,
       upper(civil_esta) as civil_esta,n.detalle as nacionalidad,p.detalle as provincia,l.detalle as localidad,ad.postal_codi, upper(ad.calle) as calle, ad.numero, ad.piso, ad.depto,
       ad.telefono, cl.categoria,pl.descripcion as plan,to_char(a.vigen_fecha, 'DD/MM/YYYY') as fecha_vigencia ,id_uoma,al.cuit,
       to_char(a.alta_fecha, 'DD/MM/YYYY HH12:MI:SS PM') as alta_fecha
from afiliado a, seccional s, afi_tercerizadora_servicio at, nacionalidad n, afi_domicilio ad, provincia p, localidad l, afi_situ_laboral al,
     categoria_laboral cl, afi_plan ap, plan pl
where a.alta_fecha>fecha_desde
and s.id_seccional=a.id_seccional
and at.cuil_titular=a.cuil_titular
and at.inte=0
and a.modi_usr <> 'admin'
and n.id=a.nacionalidad
and ad.cuil_titular=a.cuil_titular
and ad.inte=0
and p.id_provincia=ad.provincia
and l.id_localidad=ad.localidad
and al.cuil_titular=a.cuil_titular
and al.inte=0
and al.id_categoria=cl.id_categoria
and ap.cuil_titular=a.cuil_titular
and ap.inte=0
and ap.alta_fecha=(select max(alta_fecha) from afi_plan afp where afp.cuil_titular=ap.cuil_titular limit 1)
and pl.id_plan=ap.id_plan
and at.id_tercerizadora=id_tercerizadora
and exists(select 1 from afiliado a2 where alta_fecha>fecha_desde and modi_usr <> 'admin' and inte=0 and a2.cuil_titular=a.cuil_titular)
and not exists (select 1 from af_afiliado af where af.af_cuil =a.cuil_titular)
order by id_ospim;
END;
$BODY$;


ALTER FUNCTION public.listado_altas_periodo(id_tercerizadora character varying, fecha_desde date) OWNER TO postgres;

--
