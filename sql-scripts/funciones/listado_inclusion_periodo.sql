CREATE OR REPLACE FUNCTION listado_inclusion_periodo(id_tercerizadora_v character varying,
 fecha_desde date) 
RETURNS SETOF lista_inclusion_periodo
    LANGUAGE plpgsql
    AS $BODY$
declare fecha_hoy date;
BEGIN
fecha_hoy=current_date;
--recupero hasta 24/11/2010  14:00h
return query
select id_ospim, s.id_seccional||' - '||s.descripcion, at.id_tercerizadora, a.cuil_titular, a.cuil, a.inte, upper(a.parentesco), upper(a.apellido),upper(a.nombre),a.documento_tipo,
       a.docu_numero, to_char(naci_fecha, 'DD/MM/YYYY'),upper(sexo),civil_esta,n.detalle,p.detalle,l.detalle,ad.postal_codi, ad.calle,ad.numero, ad.piso, ad.depto,
       ad.telefono, cl.categoria,pl.descripcion,to_char(a.vigen_fecha, 'DD/MM/YYYY'),id_uoma,al.cuit,to_char(a.alta_fecha, 'DD/MM/YYYY HH12:MI:SS PM')
from afiliado a, seccional s, afi_tercerizadora_servicio at, nacionalidad n, afi_domicilio ad, provincia p, localidad l, afi_situ_laboral al,
     categoria_laboral cl, afi_plan ap, plan pl
where a.alta_fecha>fecha_desde
and s.id_seccional=a.id_seccional
and at.cuil_titular=a.cuil_titular
and a.inte<>0
and a.modi_usr <> 'admin'
and n.id=a.nacionalidad
and ad.cuil_titular=a.cuil_titular
and ad.alta_fecha=(select max(alta_fecha) from afi_domicilio afd where afd.cuil_titular=ad.cuil_titular limit 1)
and ad.inte=0
and p.id_provincia=ad.provincia
and l.id_localidad=ad.localidad
and al.cuil_titular=a.cuil_titular
and (al.fecha_egre is null or al.fecha_egre >fecha_hoy)
and (al.baja_fecha is null or al.baja_fecha > fecha_hoy)
and al.inte=0
and al.id_categoria=cl.id_categoria
and ap.cuil_titular=a.cuil_titular
and (ap.baja_fecha is null or ap.baja_fecha > fecha_hoy)
and ap.alta_fecha=(select max(alta_fecha) from afi_plan afp where afp.cuil_titular=ap.cuil_titular limit 1)
and ap.inte=0
and pl.id_plan=ap.id_plan
and id_tercerizadora=id_tercerizadora_v
and exists(select 1 from afiliado a2 where alta_fecha>fecha_desde and modi_usr <> 'admin' and inte<>0 and a2.cuil=a.cuil)
and to_char(a.alta_fecha,'DDMMYYYY') <> (select to_char(a2.alta_fecha,'DDMMYYYY') from afiliado a2 where a2.cuil_titular=a.cuil_titular and a2.inte=0)
and not exists (select 1 from af_afiliado af where af.af_cuil =a.cuil_titular and cast(af.af_orde as integer)=a.inte and af.af_orde<>'')
order by a.alta_fecha;
END;
$BODY$;


ALTER FUNCTION public.listado_inclusion_periodo(id_tercerizadora_v character varying, fecha_desde date) OWNER TO postgres;

--
