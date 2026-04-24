/*create type reporte_sss_331_padron as (cod_os text, 
                                cuit varchar,
                                cuil_titular varchar,
                                parentesco text,
                                cuil varchar,
                                docu_tipo text,
                                docu_nro text,
                                ape_nom text,
                                sexo text,
                                estado_civil text,
                                fecha_nac text,
                                nacionalidad text,
                                calle text,
                                numero text,
                                piso text,
                                depto text,
                                localidad text,
                                cod_postal text,
                                provincia text,
                                tipo_domi varchar,
                                telefono varchar,
                                situ_revista text,
                                discapacitado text,
                                categoria_laboral int,
                                alta_fecha text,
                                fecha_cierre text,
                                alta text)*/
  
CREATE OR REPLACE FUNCTION reporte_sss_331_padron(fecha_ini_p date, fecha_fin_p date, fecha_ciere_p date, cobertura_p numeric)
  RETURNS SETOF reporte_sss_331_padron AS
$BODY$
BEGIN

drop table if exists padron;

create temp table padron as 
select  distinct '112608' as cod_os,--Código de OS,	
        case when al.id_categoria=5 then '33637617449' when al.cuit='' then '00000000000' else al.cuit end as cuit, --CUIT EMPLEADOR
        a.cuil_titular as cuil_titular, --CUIL TITULAR
        lpad(cast(s.codigo as varchar),2,'0') as parentesco, --parentesco
        a.cuil as cuil, --cuil
        upper(a.documento_tipo) as docu_tipo,--tipo documento
        lpad(a.docu_numero,8,'0') as docu_nro, --nro documento
        rpad(ltrim(rtrim(upper(a.apellido)||', '||upper(a.nombre))),30,' ') as ape_nom, --Apellido y Nombre
        upper(a.sexo) as sexo, --Sexo
        lpad(cast(ecs.codigo as varchar),2,'0') as estado_civil,--ESTADO CIVIL
        to_char(a.naci_fecha,'DDMMYYYY') as fecha_nac, --Fecha nacimiento
        lpad(cast(na.id_sssuper as varchar),3,'0') as nacionalidad, --nacionalidad,
        rpad(rtrim(isNull(ad.calle,'')),20,' ') as calle, --CALLE
        lpad(rtrim(isNull(ad.numero,'')),5,'0')as numero,--NUMERO
        lpad(rtrim(isNull(ad.piso,'')),4,'0') as piso, --PISO
        lpad(rtrim(isNull(ad.depto,'')),4,'0') as depto, --DEPTO        
        rpad(rtrim(isNull(l.detalle,'')),20,' ') as localidad, --LOCALIDAD
        rpad(rtrim(isNull(ad.postal_codi,'')),8,' ') as cod_postal, --COD POSTAL
        lpad(cast(isNull(pr.id_sssalud,0) as varchar),2,'0') as provincia, --PROVINCIA,
        cast(lpad( '', 2, '0') as varchar) as tipo_domi, --TIPO DOMICILIO NO OBLIGATORIO
        cast(lpad( '', 20, ' ') as varchar) as telefono, --TELEFONO
        lpad(cast(sr.id_revista_sssalud as varchar),2,'0') as situ_revista, --Situacion Revista
        lpad(a.discapacitado,2,'0') as discapacitado,
        cl.id_sssuper as categoria_laboral, --Categoria Laboral (TIPO DE BENEFICIARIO)
        to_char(a.alta_fecha,'DDMMYYYY') as alta_fecha, --FECHA DE ALTA EN LA OS (HABLAR CON ANA A VER QUE PASA ELLA A LA SUPER COMO ALTA FECHA EN LA OS)
        to_char(fecha_ciere_p,'DDMMYYYY') as fecha_cierre, --FECHA DE CIERRE DE LA PRESENTACION A LA SUPER!??!?!?!?!?!?!!
        'A'
from liquidacion_farmacia_amtima lfa--liquidacion_farmacia_amtima lfa--
left outer join afiliado a
on lfa.id_ospim = a.id_ospim
and lfa.inte = a.inte
/*and a.id_ospim not in (36653,
40632,
42524,
45046,
45835,
50519
) */
left outer join afi_situ_laboral al
on al.cuil_titular=a.cuil_titular
and al.inte=0
inner join parentesco_sss s
--on upper(a.parentesco)=s.descripcion
on a.id_parentesco_sss=s.codigo
inner join estado_civil_sss ecs
--on upper(a.civil_esta)=ecs.descripcion
on a.id_estado_civil_sss=ecs.codigo
inner join nacionalidad na
on a.nacionalidad=na.id
left outer join afi_domicilio ad
on ad.cuil_titular=a.cuil_titular
and ad.inte=0
and (ad.baja_fecha is null or ad.baja_fecha>=fecha_ini_p or ad.cuil_titular='20219215208') 
and ad.alta_fecha<=fecha_fin_p+interval '1 day'
left outer join localidad l
on ad.localidad=id_localidad
left outer join provincia pr
on ad.provincia=pr.id_provincia
left outer join situacion_revista sr
on al.id_revista=sr.id_situ_revista
left outer join categoria_laboral cl
on al.id_categoria=cl.id_categoria
inner join medicamentos mm
on cast(lfa.nro_troquel as numeric)=mm.troquel
where (mm.pat_cobertura=cobertura_p)
and (pmi is null or rtrim(pmi)='')
and (al.baja_fecha is null or (al.baja_fecha>=fecha_ini_p and al.baja_fecha<=fecha_fin_p))
and (al.fecha_egre is null or fecha_egre>current_date)  --49635 afiliados con trabajos activos
and al.fecha_ingre=(select max(fecha_ingre) 
		    from afi_situ_laboral al2 where al2.cuil_titular=al.cuil_titular and al2.inte=al.inte
		    and (al2.baja_fecha is null or (al2.baja_fecha>=fecha_ini_p and al2.baja_fecha<=fecha_fin_p))
		    and (al2.fecha_egre is null or al2.fecha_egre>current_date))
--and a.parentesco is not null --196 liquidaciones con parentesco desconocido
--and upper(a.civil_esta)<>'DESCONOCIDO' --158 liquidaciones de afiliados con estado civil desconocido
and periodo>=fecha_ini_p
and periodo<= fecha_fin_p
and mm.pat_codigo is not null --QUE TENGA PATOLOGIA
and mm.gen_codigo is not null --QUE TENGA GENERICO
union 
select  distinct '112608' as cod_os,--Código de OS,	
        case when al.id_categoria=5 then '33637617449' when al.cuit='' then '00000000000' else al.cuit end as cuit, --CUIT EMPLEADOR
        a.cuil_titular as cuil_titular, --CUIL TITULAR
        lpad(cast(s.codigo as varchar),2,'0') as parentesco, --parentesco
        a.cuil as cuil, --cuil
        upper(a.documento_tipo) as docu_tipo,--tipo documento
        lpad(a.docu_numero,8,'0') as docu_nro, --nro documento
        rpad(ltrim(rtrim(upper(a.apellido)||', '||upper(a.nombre))),30,' ') as ape_nom, --Apellido y Nombre
        upper(a.sexo) as sexo, --Sexo
        lpad(cast(ecs.codigo as varchar),2,'0') as estado_civil,--ESTADO CIVIL
        to_char(a.naci_fecha,'DDMMYYYY') as fecha_nac, --Fecha nacimiento
        lpad(cast(na.id_sssuper as varchar),3,'0') as nacionalidad, --nacionalidad,
        rpad(rtrim(isNull(ad.calle,'')),20,' ') as calle, --CALLE
        lpad(rtrim(isNull(ad.numero,'')),5,'0')as numero,--NUMERO
        lpad(rtrim(isNull(ad.piso,'')),4,'0') as piso, --PISO
        lpad(rtrim(isNull(ad.depto,'')),4,'0') as depto, --DEPTO        
        rpad(rtrim(isNull(l.detalle,'')),20,' ') as localidad, --LOCALIDAD
        rpad(rtrim(isNull(ad.postal_codi,'')),8,' ') as cod_postal, --COD POSTAL
        lpad(cast(isNull(pr.id_sssalud,0) as varchar),2,'0') as provincia, --PROVINCIA,
        cast(lpad( '', 2, '0') as varchar) as tipo_domi, --TIPO DOMICILIO NO OBLIGATORIO
        cast(lpad( '', 20, ' ') as varchar) as telefono, --TELEFONO
        lpad(cast(sr.id_revista_sssalud as varchar),2,'0') as situ_revista, --Situacion Revista
        lpad(a.discapacitado,2,'0') as discapacitado,
        cl.id_sssuper as categoria_laboral, --Categoria Laboral (TIPO DE BENEFICIARIO)
        to_char(a.alta_fecha,'DDMMYYYY') as alta_fecha, --FECHA DE ALTA EN LA OS (HABLAR CON ANA A VER QUE PASA ELLA A LA SUPER COMO ALTA FECHA EN LA OS)
        to_char(fecha_ciere_p,'DDMMYYYY') as fecha_cierre, --FECHA DE CIERRE DE LA PRESENTACION A LA SUPER!??!?!?!?!?!?!!
        'A'
from liquidacion_farmacia_ospim lfa--liquidacion_farmacia_amtima lfa--
left outer join afiliado a
on lfa.id_ospim = a.id_ospim
and lfa.inte = a.inte
/*and a.id_ospim not in (36653,
40632,
42524,
45046,
45835,
50519
) */
left outer join afi_situ_laboral al
on al.cuil_titular=a.cuil_titular
and al.inte=0
inner join parentesco_sss s
--on upper(a.parentesco)=s.descripcion
on a.id_parentesco_sss=s.codigo
inner join estado_civil_sss ecs
--on upper(a.civil_esta)=ecs.descripcion
on a.id_estado_civil_sss=ecs.codigo
inner join nacionalidad na
on a.nacionalidad=na.id
left outer join afi_domicilio ad
on ad.cuil_titular=a.cuil_titular
and ad.inte=0
and (ad.baja_fecha is null or ad.baja_fecha>=fecha_ini_p or ad.cuil_titular='20219215208') 
and ad.alta_fecha<=fecha_fin_p+interval '1 day'
left outer join localidad l
on ad.localidad=id_localidad
left outer join provincia pr
on ad.provincia=pr.id_provincia
left outer join situacion_revista sr
on al.id_revista=sr.id_situ_revista
left outer join categoria_laboral cl
on al.id_categoria=cl.id_categoria
inner join medicamentos mm
on cast(lfa.nro_troquel as numeric)=mm.troquel
where (mm.pat_cobertura=cobertura_p)
and (pmi is null or rtrim(pmi)='')
and (al.baja_fecha is null or (al.baja_fecha>=fecha_ini_p and al.baja_fecha<=fecha_fin_p))
and (al.fecha_egre is null or fecha_egre>current_date)  --49635 afiliados con trabajos activos
and al.fecha_ingre=(select max(fecha_ingre) 
		    from afi_situ_laboral al2 where al2.cuil_titular=al.cuil_titular and al2.inte=al.inte
		    and (al2.baja_fecha is null or (al2.baja_fecha>=fecha_ini_p and al2.baja_fecha<=fecha_fin_p))
		    and (al2.fecha_egre is null or al2.fecha_egre>current_date))
--and a.parentesco is not null --196 liquidaciones con parentesco desconocido
--and upper(a.civil_esta)<>'DESCONOCIDO' --158 liquidaciones de afiliados con estado civil desconocido
and periodo>=fecha_ini_p
and periodo<= fecha_fin_p
and mm.pat_codigo is not null --QUE TENGA PATOLOGIA
and mm.gen_codigo is not null
order by 3;

return query
select cod_os,--Código de OS,	
       cast(max(cuit) as varchar), --CUIT EMPLEADOR
       cuil_titular, --CUIL TITULAR
       parentesco, --parentesco
       cuil, --cuil
       docu_tipo,--tipo documento
       docu_nro, --nro documento
       ape_nom, --Apellido y Nombre
       sexo, --Sexo
       estado_civil,--ESTADO CIVIL
       fecha_nac, --Fecha nacimiento
       nacionalidad, --nacionalidad,
       calle, --CALLE
       numero,--NUMERO
       piso, --PISO
       depto, --DEPTO        
       localidad, --LOCALIDAD
       cod_postal, --COD POSTAL
       provincia, --PROVINCIA,
       tipo_domi, --TIPO DOMICILIO NO OBLIGATORIO
       telefono, --TELEFONO
       cast(max(situ_revista) as text), --Situacion Revista
       discapacitado,
       cast(max(categoria_laboral) as int), --Categoria Laboral (TIPO DE BENEFICIARIO)
       alta_fecha, --FECHA DE ALTA EN LA OS (HABLAR CON ANA A VER QUE PASA ELLA A LA SUPER COMO ALTA FECHA EN LA OS)
       fecha_cierre, --FECHA DE CIERRE DE LA PRESENTACION A LA SUPER!??!?!?!?!?!?!!
       cast('A' as text)
from padron --liquidacion_farmacia_amtima lfa--
group by cod_os, cuil_titular, parentesco, cuil, docu_tipo, docu_nro, ape_nom, sexo, estado_civil, fecha_nac, nacionalidad, 
         calle, numero, piso, depto, localidad, cod_postal, provincia, tipo_domi, telefono, discapacitado, alta_fecha, fecha_cierre; 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;