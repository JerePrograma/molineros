CREATE TYPE certif_result AS
   (nomape character varying,
    dni character varying,
    entidad character varying,
    pais character varying,
    fechaingreso character varying,
    idafiliado character varying,
    tipobeneficiario character varying,
    fechanac character varying,
    nacionalidad character varying,
    sexo character varying,
    estacivil character varying,
    domicilio character varying,
    localidad character varying,
    provincia character varying,
    cp character varying,
    telefono character varying,
    cuil character varying,
    cuit character varying,
    domicilioempleador character varying,
    razonsoc character varying,
    fechainirela character varying);
    

CREATE OR REPLACE FUNCTION traer_datos_certificado_afiliacion(cuit_p character varying, inte_p integer)
  RETURNS SETOF certif_result AS
$BODY$
BEGIN   
	drop table if exists aux;
	create temp table aux as
	select  cast(upper(a.nombre)||' '||upper(a.apellido) as varchar) as nomape,
		a.docu_numero,
		cast('RENAPER' as character varying) as entidad_emisora,
		cast(upper(n.detalle) as varchar) as pais, 
		cast(to_char(a.vigen_fecha,'dd/MM/yyyy') as varchar) as fecha_ingreso,
		case when id_ospim_baja_fecha is null then cast(a.id_ospim as varchar) else ' ' end as id_ospim_baja_fecha,
		--cast(upper(a.parentesco) as varchar) as parentesco,
		pa.descripcion as parentesco, 
		cast(to_char(a.naci_fecha, 'dd/MM/yyyy') as varchar) as naci_fecha,
		cast(upper(n.detalle) as varchar) as nacionalidad,
		cast(case when upper(a.sexo)='M' then 'MASCULINO' else 'FEMENINO' end as varchar) as sexo, 
		--cast(upper(civil_esta) as varchar) as civil_esta, 
		ec.descripcion as civil_esta,
		cast('' as varchar) as domi_afi,--
		cast('' as varchar) as localidad,
		cast('' as varchar) as provincia,
		cast('' as varchar) as postal_codi,
		cast('' as varchar) as telefono,
		a.cuil_titular,
		cast('' as varchar) as cuit,
		cast('' as varchar) as domi_empresa,
		cast('' as varchar) as razon_soc,
		cast(0 as integer) as id_categoria,	
		cast(to_char(a.vigen_fecha,'dd/MM/yyyy') as varchar) as ingre_fecha	
	from afiliado a, nacionalidad n, parentesco_sss pa, estado_civil_sss ec	     
	where a.nacionalidad=n.id
	and a.id_parentesco_sss = pa.codigo
	and a.id_estado_civil_sss = ec.codigo		
	and a.cuil_titular=$1
	and a.inte=$2;

	update aux a
	set cuit=al.cuit,
	    id_categoria=al.id_categoria	    
	from afi_situ_laboral al
	where al.cuil_titular=a.cuil_titular
	and al.inte=0
	and (al.baja_fecha is null)
	and (al.fecha_egre is null);

	update aux a
	set cuit=al.cuit,
	    id_categoria=al.id_categoria	    
	from afi_situ_laboral al
	where al.cuil_titular=a.cuil_titular
	and al.inte=0
	and a.cuit is null	
	and (al.fecha_egre is not null and al.fecha_egre=(select max(fecha_egre) from afi_situ_laboral al2 where al2.cuil_titular=al.cuil_titular and al2.inte=al.inte));     

	update aux a
	set domi_afi=cast(ad.calle||' '||ad.numero||case when (ad.piso is not null and rtrim(ad.piso)<>'') then ' Piso '||ad.piso else '' end||case when (ad.depto is not null and rtrim(ad.depto)<>'')  then ' Depto. '||ad.depto else '' end  || 
		     ' '|| l.detalle ||' '|| case when (id_localidad<>265) then cast(p.detalle as varchar) else ' ' end as varchar),
	    domi_empresa=cast(ad.calle||' '||ad.numero||case when (ad.piso is not null and rtrim(ad.piso)<>'') then ' Piso '||ad.piso else '' end||case when (ad.depto is not null and rtrim(ad.depto)<>'')  then ' Depto. '||ad.depto else '' end  || 
		' '|| l.detalle ||' '|| case when (id_localidad<>265) then cast(p.detalle as varchar) else ' ' end as varchar),
	    localidad=l.detalle,
	    provincia=p.detalle,
	    postal_codi=ad.postal_codi,
	    telefono=ad.telefono
	from afi_domicilio ad,	     
	     localidad l,
	     provincia p
	where ad.cuil_titular=a.cuil_titular
	and ad.inte=0
	and (ad.baja_fecha is null)-- or 
	     --(ad.baja_fecha is not null and ad.baja_fecha=(select max(baja_fecha) from afi_domicilio ad2 where ad2.cuil_titular=ad.cuil_titular and ad2.inte=ad.inte)))
	and l.id_localidad=ad.localidad
	and p.id_provincia=ad.provincia;


	update aux a
	set domi_afi=cast(ad.calle||' '||ad.numero||case when (ad.piso is not null and rtrim(ad.piso)<>'') then ' Piso '||ad.piso else '' end||case when (ad.depto is not null and rtrim(ad.depto)<>'')  then ' Depto. '||ad.depto else '' end  || 
		     ' '|| l.detalle ||' '|| case when (id_localidad<>265) then cast(p.detalle as varchar) else ' ' end as varchar),
	    domi_empresa=cast(ad.calle||' '||ad.numero||case when (ad.piso is not null and rtrim(ad.piso)<>'') then ' Piso '||ad.piso else '' end||case when (ad.depto is not null and rtrim(ad.depto)<>'')  then ' Depto. '||ad.depto else '' end  || 
		' '|| l.detalle ||' '|| case when (id_localidad<>265) then cast(p.detalle as varchar) else ' ' end as varchar),
	    localidad=l.detalle,
	    provincia=p.detalle,
	    postal_codi=ad.postal_codi,
	    telefono=ad.telefono
	from afi_domicilio ad,	     
	     localidad l,
	     provincia p
	where ad.cuil_titular=a.cuil_titular
	and ad.inte=0
	and (ad.baja_fecha is null)-- or 
	     --(ad.baja_fecha is not null and ad.baja_fecha=(select max(baja_fecha) from afi_domicilio ad2 where ad2.cuil_titular=ad.cuil_titular and ad2.inte=ad.inte)))
	and l.id_localidad=ad.localidad
	and p.id_provincia=ad.provincia;



	
	
	--EMPRESA
	update aux a
	set domi_empresa= d.calle||' '||d.numero||
	                  case when (d.piso is not null and rtrim(d.piso) <>'') then ' Piso '||d.piso else '' end||
	                  case when (d.depto is not null and rtrim(d.depto) <>'') then ' Depto. '||d.depto else '' end || 
			  ' '||l.detalle||' '|| 
			  case when (id_localidad<>265) then cast(p.detalle as varchar) else ' ' end,
	    razon_soc=e.razon_soc	    
	from empresa e,
	     emp_domicilio ep,
	     domicilio d,
	     localidad l,
	     provincia p
	where e.cuit=a.cuit
	and e.sucursal='000'
	and ep.cuit=e.cuit
	and ep.sucursal=e.sucursal
	and d.id_domicilio=ep.id_domicilio
	and l.id_provincia=d.provincia
	and l.id_localidad=d.localidad
	and p.id_provincia=d.provincia
	and a.id_categoria in (11,9,1,2,3);     



        return query
	select  nomape,
		docu_numero,
		entidad_emisora,
		pais, 
		fecha_ingreso,--cast('14/06/2010' as character varying),
		id_ospim_baja_fecha,--cast('1200' as character varying),
		parentesco, --cast('Hijo' as character varying),
		naci_fecha,--cast(a.cast('22/10/1979' as character varying),
		nacionalidad,--cast('ARGENTINA' as character varying),
		sexo, --cast('MASCULINO' as character varying),
		civil_esta, --cast('CASADO' as character varying),
		domi_afi,
		localidad,
		provincia,
		postal_codi,
		telefono,
		cuil_titular,
		cuit,
		domi_empresa,
		razon_soc,
		ingre_fecha
        from aux		
	limit 1;
	
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;