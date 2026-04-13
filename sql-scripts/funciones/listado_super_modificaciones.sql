CREATE OR REPLACE FUNCTION listado_super_modificaciones(fecha_desde date, fecha_hasta date, registrar_envio boolean)
  RETURNS SETOF listado_super_altas AS
$BODY$
BEGIN

drop table if exists result;
create table result as 
select  distinct cast('112608' as varchar) as obra_social,
	cuit,
	a.cuil_titular,
	a.inte,
	--lpad( cast (p.id_sssuper as varchar), 2, '0') as parentesco,
	lpad( cast (a.id_parentesco_sss as varchar), 2, '0') as parentesco,
	a.cuil,
	a.documento_tipo,
	lpad( cast (a.docu_numero as varchar), 8, '0') as docu_numero,
	rpad( cast (upper(a.apellido)||' '||upper(a.nombre)  as character(30)), 30, ' ') as ape_nombre,
	upper(a.sexo) as sexo,
	--cast(lpad( cast (s.id_sssuper as varchar), 2, '0') as varchar) as estado_civil,
	lpad( cast (a.id_estado_civil_sss as varchar), 2, '0') as estado_civil,
	to_char(a.naci_fecha, 'DDMMYYYY') as naci_fecha,
        lpad( cast (n.id_sssuper as varchar), 3, '0') as nacionalidad,
        rpad( cast (calle as character(20)), 20, ' ') as calle,
        case when numero is null then '     ' else rpad( cast (numero as character(5)), 5, ' ') end as numero,
        case when piso is null then '    ' else rpad( cast (piso as character(4)), 4, ' ') end as piso,
        case when depto is null then '    ' else rpad( cast (depto as character(4)), 4, ' ') end as depto,
        rpad( cast (lo.detalle as character(20)), 20, ' ') as localidad,
        lpad( cast (postal_codi as character(6)), 6, '0') as postal_codi,
        lpad( cast (pr.id_sssalud as varchar), 2, '0') as provincia,
        cast('01' as varchar) as tipo_domi,
        case when telefono is null then '                    ' else rpad( cast (telefono as character(20)), 20, ' ') end as telefono,
        lpad( cast (id_revista_sssalud as varchar), 2, '0') as situ_revista,
        lpad( cast (a.discapacitado as varchar), 2, '0') as discapacitado,
        lpad( cast (cl.id_sssuper as varchar), 2, '0') as tipo_beneficiario,
        to_char(a.alta_fecha, 'DDMMYYYY') as fecha_alta_os,
        cast(to_char(fecha_hasta, 'DDMMYYYY')as varchar) as fecha_cierre
from afiliado a
left outer join afi_situ_laboral l
on a.cuil_titular=l.cuil_titular
and (l.fecha_egre is null or l.fecha_egre > fecha_hasta)
and (l.baja_fecha is null or l.baja_fecha> fecha_hasta)
and l.fecha_ingre=(select max(fecha_ingre) 
		   from afi_situ_laboral asl2 
		   where asl2.cuil_titular=l.cuil_titular 
		   and (asl2.fecha_egre is null or asl2.fecha_egre > fecha_hasta) 
		   and (asl2.baja_fecha is null or asl2.baja_fecha> fecha_hasta)
		   and asl2.inte=case when a.aportante_titular<>1 then 0 else a.inte end
		   and asl2.cuit=(select max(cuit) 
			          from afi_situ_laboral asl3 where asl3.cuil_titular=asl2.cuil_titular 
				  and asl3.fecha_ingre=asl2.fecha_ingre
		                  and (asl3.fecha_egre is null or asl3. fecha_egre > fecha_hasta) 
		                  and (asl3.baja_fecha is null or asl3.baja_fecha> fecha_hasta)
		                  and asl3.inte=case when a.aportante_titular<>1 then 0 else a.inte end))
--left outer join tipo_paren_super p
--on upper (a.parentesco)= p.descripcion 
--left outer join civil_esta_super s
--on upper(a.civil_esta)=s.descripcion
left outer join nacionalidad n
on a.nacionalidad=n.id
left outer join afi_domicilio d
on a.cuil_titular=d.cuil_titular
and d.inte=0
and d.vigen_desde=(select max(d2.vigen_desde) 
		   from afi_domicilio d2 
		   where d2.cuil_titular=d.cuil_titular
		   and d2.inte=d.inte
		   and (d2.baja_fecha is null or d2.baja_fecha>fecha_hasta))
left outer join provincia pr
on d.provincia=pr.id_provincia
left outer join situacion_revista r
on l.id_revista=r.id_situ_revista
left outer join categoria_laboral cl
on l.id_categoria=cl.id_categoria
left outer join localidad lo 
on d.localidad=lo.id_localidad
inner join afi_estados_histo af
on a.cuil_titular=af.cuil_titular
and a.inte=af.inte
and af.descripcion_operacion='MOD'
and af.alta_fecha>fecha_desde
and af.alta_fecha<fecha_hasta
and (a.baja_fecha is null or a.baja_fecha>fecha_hasta)
order by cuil_titular asc;

if registrar_envio then
	RAISE INFO 'REGISTRO';
	insert into informes.fechas_informe_super(cuil, fecha_mod)
	select r.cuil, a.fecha_mod_super
	from afiliado a, result r
	where a.cuil_titular=r.cuil_titular
	and a.inte=r.inte
	and a.fecha_mod_super is not null;

	update afiliado	a	
	set fecha_mod_super=current_date
	from result r
	where a.cuil_titular=r.cuil_titular
	and a.inte=r.inte;
end if;  
return query
select  obra_social,
        cuit,
        cuil_titular,
	parentesco,
	cuil,
	documento_tipo,
	docu_numero,
	ape_nombre,
	sexo,
	estado_civil,
	naci_fecha,
        nacionalidad,
        calle,
        numero,
        piso,
        depto,
        localidad,
        postal_codi,
        provincia,
        tipo_domi,
        telefono,
        situ_revista,
        discapacitado,
        tipo_beneficiario,
        fecha_alta_os,
        fecha_cierre
from result;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;