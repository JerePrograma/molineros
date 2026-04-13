create type reporte_modif_periodo_result as (cuil_titular varchar, inte int, parentesco varchar, docu_numero varchar, apellido varchar, nombre varchar, cambio varchar, anterior varchar,
ahora varchar, usuario varchar, fecha timestamp without time zone)

CREATE OR REPLACE FUNCTION reporte_modificaciones_periodo(cuil_titular_p character varying, fecha_desde date, fecha_hasta date)
  RETURNS SETOF reporte_modif_periodo_result AS
$BODY$
declare record_ant RECORD;
declare record_sig RECORD;
declare cambio_v varchar;
declare ant_lleno boolean;
declare anterior_v varchar;
declare actual varchar;
declare fecha_hasta_plus date;
BEGIN

fecha_hasta_plus=fecha_hasta+interval '1 day';

ant_lleno=false;

drop table if exists cambios_periodo;
drop table if exists cambios_periodo_aux;
drop table if exists aux_domi;
--drop table if exists domi_cambio;

create temp table cambios_periodo(cuil_titular varchar, inte int, parentesco varchar, docu_numero varchar, apellido varchar, nombre varchar, cambio varchar, anterior varchar, ahora varchar, usuario varchar, fecha timestamp without time zone);

--PRIMERO ALTAS
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select cuil_titular, inte, p.descripcion as parentesco, docu_numero, apellido, nombre, 'ALTA', '-', '-', alta_usr, alta_fecha
from afiliado a, parentesco_sss p
where a.id_parentesco_sss =p.codigo 
and cuil_titular=cuil_titular_p
and (fecha_desde is null or (fecha_desde is not null and alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and alta_fecha<fecha_hasta_plus));

--BAJA TOTAL
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, p.descripcion as parentesco, a.docu_numero, a.apellido, a.nombre, 'BAJA TOTAL', '-', '-', h.alta_usr, h.alta_fecha
from afiliado a 
inner join afi_estados_histo h 
on a.cuil_titular=h.cuil_titular 
and a.inte=h.inte
inner join parentesco_sss p on a.id_parentesco_sss =p.codigo  
where /*(a.baja_fecha is not null) --LO SACO EL 30/08/2012
--and (fecha_desde is null or (fecha_desde is not null and baja_fecha>=fecha_desde))
--and (fecha_hasta is null or (fecha_hasta is not null and baja_fecha<fecha_hasta_plus))
and */a.cuil_titular=cuil_titular_p
and (fecha_desde is null or (fecha_desde is not null and h.alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and h.alta_fecha<fecha_hasta_plus))
and (h.baja_fecha is null or h.baja_fecha>current_date) 
and h.descripcion_operacion='BTO' 
order by cuil_titular, inte;

--BAJA PARCIAL
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, p.descripcion as parentesco, a.docu_numero, a.apellido, a.nombre, 'BAJA PARCIAL', '-', '-', h.alta_usr, h.alta_fecha
from afiliado a
inner join afi_estados_histo h
on a.cuil_titular=h.cuil_titular 
and a.inte=h.inte 
inner join parentesco_sss p on a.id_parentesco_sss =p.codigo  
where /*(a.baja_fecha is not null or a.baja_fecha>=current_date) --LO SACO EL 30/08/2012
--and (fecha_desde is null or (fecha_desde is not null and baja_fecha>=fecha_desde))
--and (fecha_hasta is null or (fecha_hasta is not null and baja_fecha<fecha_hasta_plus))
and*/ a.cuil_titular=cuil_titular_p
and (fecha_desde is null or (fecha_desde is not null and h.alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and h.alta_fecha<fecha_hasta_plus))
and (h.baja_fecha is null or h.baja_fecha>current_date) and descripcion_operacion='BPA' 
order by cuil_titular, inte;

--REINCORPORACIONES
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select cuil_titular, inte, p.descripcion as parentesco, docu_numero, apellido, nombre, 'REINCORPORACION', '-', '-', alta_usr, alta_fecha
from afi_estados_histo a, parentesco_sss p
/*where (baja_fecha is null or baja_fecha>current_date)*/
where a.id_parentesco_sss = p.codigo 
and a.cuil_titular=cuil_titular_p
and descripcion_operacion='REI'
--and exists (select 1 from afi_estados_histo h where a.cuil_titular=h.cuil_titular and a.inte=h.inte and (baja_fecha is not null) and descripcion_operacion='REI' 
and (fecha_desde is null or (fecha_desde is not null and alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and alta_fecha<fecha_hasta_plus))
order by cuil_titular, inte;

--CAMBIOS DE CUIL
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, p.descripcion as parentesco, docu_numero, apellido, nombre, 'CAMBIO DE CUIL', acc.cuil_anterior, acc.cuil, a.alta_usr, a.alta_fecha
from afi_estados_histo a, parentesco_sss p, afi_cambio_cuil acc
where a.id_parentesco_sss = p.codigo 
and a.cuil_titular=cuil_titular_p
and descripcion_operacion='CCU'
and a.cuil_titular = acc.cuil_titular_anterior
and a.inte = acc.inte_anterior
and (fecha_desde is null or (fecha_desde is not null and a.alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and a.alta_fecha<fecha_hasta_plus))
order by a.cuil_titular, a.inte;

--CAMBIOS
create temp table cambios_periodo_aux as 
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, ec.descripcion as civil_esta, p.descripcion as parentesco, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, 
            id_uoma_baja_fecha, id_amtima_baja_fecha,'ALT' as descripcion_operacion
from afiliado a
inner join parentesco_sss p on a.id_parentesco_sss = p.codigo
inner join estado_civil_sss ec on a.id_estado_civil_sss = ec.codigo
where cuil_titular=cuil_titular_p
and (fecha_desde is null or (fecha_desde is not null and modi_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and modi_fecha<fecha_hasta_plus))
union
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, ec.descripcion as civil_esta, p.descripcion as parentesco, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, alta_fecha, alta_usr, alta_fecha as modi_fecha, alta_usr as modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, 
            id_uoma_baja_fecha, id_amtima_baja_fecha, descripcion_operacion
from afi_estados_histo h
inner join parentesco_sss p on h.id_parentesco_sss = p.codigo
inner join estado_civil_sss ec on h.id_estado_civil_sss = ec.codigo
where cuil_titular=cuil_titular_p
and descripcion_operacion='MOD' 
and (fecha_desde is null or (fecha_desde is not null and alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and alta_fecha<fecha_hasta_plus))
order by inte, modi_fecha desc, alta_fecha desc;


--CAMBIOS PERSONALES
FOR record_sig IN
select cuil_titular, inte, id_ospim, id_uoma, id_amtima, apellido, nombre, 
            documento_tipo, sexo, cuil, naci_fecha, civil_esta, parentesco, 
            ingre_fecha, id_seccional, anterior_os, vigen_fecha, observaciones, 
            pres_ssalud_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr, 
            baja_fecha, baja_usr, discapacitado, docu_numero, nacionalidad, 
            aportante_titular, nro_afiliado, id_motivo_baja, id_ospim_baja_fecha, 
            id_uoma_baja_fecha, id_amtima_baja_fecha, descripcion_operacion
from cambios_periodo_aux 
--where descripcion_operacion='MOD'
order by inte, modi_fecha, alta_fecha desc LOOP	
	if ant_lleno=true then
	   if record_sig.inte <> record_ant.inte then		
		ant_lleno=false;
	   end if;
	end if;
	if ant_lleno=false then --record_ant.cuil_titular is null then	   
	   record_ant=record_sig;
	   ant_lleno=true;
        else           
		if record_ant.apellido<>record_sig.apellido then 
		   cambio_v='CAMBIO DE APELLIDO';
		   anterior_v=record_ant.apellido;
		   actual=record_sig.apellido;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;		   
		end if;	
		if record_ant.nombre<>record_sig.nombre then 
		   cambio_v='CAMBIO DE NOMBRE';
		   anterior_v=record_ant.nombre;
		   actual=record_sig.nombre;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;	
		if record_ant.documento_tipo<>record_sig.documento_tipo then 
		   cambio_v='CAMBIO DE TIPO DE DOCUMENTO';
		   anterior_v=record_ant.documento_tipo;
		   actual=record_sig.documento_tipo;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if upper(record_ant.sexo)<>upper(record_sig.sexo) then 
		   cambio_v='CAMBIO DE SEXO';
		   anterior_v=upper(record_ant.sexo);
		   actual=upper(record_sig.sexo);
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.cuil<>record_sig.cuil then 
		   cambio_v='CAMBIO DE CUIL'; 
		   anterior_v=record_ant.cuil;
		   actual=record_sig.cuil;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_sig.modi_usr,
				record_sig.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.naci_fecha<>record_sig.naci_fecha then 
		   cambio_v='CAMBIO FECHA DE NACIMIENTO';
		   anterior_v=record_ant.naci_fecha;
		   actual=record_sig.naci_fecha;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if rtrim(upper(record_ant.civil_esta))<>rtrim(upper(record_sig.civil_esta)) then 
		   cambio_v='CAMBIO ESTADO CIVIL';
		   anterior_v=rtrim(upper(record_ant.civil_esta));
		   actual=rtrim(upper(record_sig.civil_esta));
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if upper(record_ant.parentesco)<>upper(record_sig.parentesco) then 
		   cambio_v='CAMBIO PARENTESCO';
		   anterior_v=upper(record_ant.parentesco);
		   actual=upper(record_sig.parentesco);
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.ingre_fecha<>record_sig.ingre_fecha then 
		   cambio_v='CAMBIO FECHA INGRESO';
		   anterior_v=record_ant.ingre_fecha;
		   actual=record_sig.ingre_fecha;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.id_seccional<>record_sig.id_seccional then 
		   cambio_v='CAMBIO DE SECCIONAL';
		   anterior_v=record_ant.id_seccional;
		   actual=record_sig.id_seccional;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.anterior_os<>record_sig.anterior_os then 
		   cambio_v='CAMBIO OS ANTERIOR';
		   anterior_v=record_ant.anterior_os;
		   actual=record_sig.anterior_os;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.vigen_fecha<>record_sig.vigen_fecha then 
		   cambio_v='CAMBIO DE VIGENCIA';
		   anterior_v=record_ant.vigen_fecha;
		   actual=record_sig.vigen_fecha;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.discapacitado<>record_sig.discapacitado then 
		   cambio_v='CAMBIO DISCAPACIDAD';
		   anterior_v=case when record_ant.discapacitado='1' then 'SI' else 'NO' end ;
		   actual=case when record_sig.discapacitado='1' then 'SI' else 'NO' end ;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.docu_numero<>record_sig.docu_numero then 
		   cambio_v='CAMBIO NUMERO DE DOCUMENTO';
		   anterior_v=record_ant.docu_numero;
		   actual=record_sig.docu_numero;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.nacionalidad<>record_sig.nacionalidad then 
		   cambio_v='CAMBIO DE NACIONALIDAD';
		   anterior_v=record_ant.nacionalidad;
		   actual=record_sig.nacionalidad;		
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;
		if record_ant.observaciones<>record_sig.observaciones then 
		   cambio_v='CAMBIO EN LAS OBSERVACIONES';
		   anterior_v=record_ant.observaciones;
		   actual=record_sig.observaciones;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
                end if;    
                if record_ant.id_ospim<>record_sig.id_ospim then 
		   cambio_v='CAMBIO EN ID DE OSPIM';
		   anterior_v=record_ant.id_ospim;
		   actual=record_sig.id_ospim;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
                end if;    
                if record_ant.id_uoma<>record_sig.id_uoma then 
		   cambio_v='CAMBIO EN ID DE UOMA';
		   anterior_v=record_ant.id_uoma;
		   actual=record_sig.id_uoma;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
                end if;     
		if record_ant.id_amtima<>record_sig.id_amtima then 
		   cambio_v='CAMBIO EN ID DE AMTIMA';
		   anterior_v=record_ant.id_amtima;
		   actual=record_sig.id_amtima;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
                end if;    
                if record_ant.id_motivo_baja<>record_sig.id_motivo_baja then 
		   cambio_v='CAMBIO EN MOTIVO BAJA';
		   anterior_v=record_ant.id_motivo_baja;
		   actual=record_sig.id_motivo_baja;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
                end if;    
                --RAISE INFO 'CAMBIO: %, fecha_alta: %, fecha_mod: %, descripcion_operacion: %',cambio_v, record_ant.alta_fecha, record_ant.modi_fecha, record_ant.descripcion_operacion;
                /*if cambio_v is not null then
			insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				upper(record_sig.parentesco), 
				record_sig.docu_numero, 
				upper(record_sig.apellido), 
				upper(record_sig.nombre),
				cambio_v, 
				anterior_v,
				actual,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			cambio_v=null;
		end if;*/
		record_ant=record_sig;
	END IF;

END LOOP;	

--ACTUALIZO SECCIONAL ANTERIOR
update cambios_periodo c
set anterior=s.descripcion
from seccional s
where s.id_seccional=cast(c.anterior as int)
and c.cambio like '%SECCIONAL%';

update cambios_periodo c
set ahora=s.descripcion
from seccional s
where s.id_seccional=cast(c.ahora as int)
and c.cambio like '%SECCIONAL%';

--ACTUALIZO NACIONALIDAD
update cambios_periodo c
set anterior=s.detalle
from nacionalidad s
where s.id=cast(c.anterior as int)
and cambio like '%NACIONALIDAD%';

update cambios_periodo c
set ahora=s.detalle
from nacionalidad s
where s.id=cast(c.ahora as int)
and cambio like '%NACIONALIDAD%';


ant_lleno=false;
cambio_v=null;

--CAMBIOS DOMICILIO
create temp table aux_domi as
select cuil_titular, inte, vigen_desde, domi_tipo, calle, piso, depto, 
            oficina, postal_codi, barrio, telefono, observaciones, domi_val, 
            alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, 
            provincia, localidad, numero
from afi_domicilio a 
where cuil_titular=cuil_titular_p
and (fecha_desde is null or (fecha_desde is not null and alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and alta_fecha<fecha_hasta_plus));

insert into aux_domi(cuil_titular, inte, vigen_desde, domi_tipo, calle, piso, depto, 
            oficina, postal_codi, barrio, telefono, observaciones, domi_val, 
            alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, 
            provincia, localidad, numero)
select cuil_titular, inte, vigen_desde, domi_tipo, calle, piso, depto, 
            oficina, postal_codi, barrio, telefono, observaciones, domi_val, 
            alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, 
            provincia, localidad, numero
from afi_domicilio ad
where ad.cuil_titular=cuil_titular_p
and ad.alta_fecha=(select max(ad2.alta_fecha) from afi_domicilio ad2 where ad2.cuil_titular=ad.cuil_titular and 
		   not exists (select 1 from aux_domi ax where ad2.alta_fecha=ax.alta_fecha));


FOR record_sig IN
select cuil_titular, inte, vigen_desde, domi_tipo, calle, piso, depto, 
            oficina, postal_codi, barrio, telefono, observaciones, domi_val, 
            alta_fecha, alta_usr, modi_fecha, modi_usr, baja_fecha, baja_usr, 
            provincia, localidad, numero
from aux_domi a 
order by alta_fecha desc LOOP	
	
	if ant_lleno=false then 
	   record_ant=record_sig;
	   ant_lleno=true;
        else           
                cambio_v='CAMBIO DE DOMICILIO';
		if record_ant.calle<>record_sig.calle then 
		   cambio_v=cambio_v||' - '||'CALLE';
		   anterior_v=record_ant.calle;
		   actual=record_sig.calle;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;         
		if (record_sig.piso is null and record_ant.piso is not null) or record_ant.piso<>record_sig.piso then 
		   cambio_v=cambio_v||' - '||'PISO';
		   anterior_v=record_ant.piso;
		   actual=record_sig.piso;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;         
		if (record_sig.depto is null and record_ant.depto is not null) or record_ant.depto<>record_sig.depto then 
		   cambio_v=cambio_v||' - '||'DPTO';
		   anterior_v=record_ant.depto;
		   actual=record_sig.depto;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;      
		if (record_sig.oficina is null and record_ant.oficina is not null) or upper(record_ant.oficina)<>upper(record_sig.oficina) then 
		   cambio_v=cambio_v||' - '||'OFICINA';
		   anterior_v=upper(record_ant.oficina);
		   actual=upper(record_sig.oficina);
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;      
		if record_ant.postal_codi<>record_sig.postal_codi then 
		   cambio_v=cambio_v||' - '||'COD. POSTAL'; 
		   anterior_v=record_ant.postal_codi;
		   actual=record_sig.postal_codi;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;      
		if (record_sig.barrio is null and record_ant.barrio is not null) or (record_ant.barrio<>record_sig.barrio) then 
		   RAISE INFO 'CAMBIO DE BARRIO';
		   cambio_v=cambio_v||' - '||'BARRIO'; 
		   anterior_v=record_ant.barrio;
		   actual=record_sig.barrio;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;      
		if (record_sig.telefono is null and record_ant.telefono is not null) or rtrim(upper(record_ant.telefono))<>rtrim(upper(record_sig.telefono)) then 
		   cambio_v=cambio_v||' - '||'TELEFONO';
		   anterior_v=rtrim(upper(record_ant.telefono));
		   actual=rtrim(upper(record_sig.telefono));
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;      
		if (record_sig.observaciones is null and record_ant.observaciones is not null) or upper(record_ant.observaciones)<>upper(record_sig.observaciones) then 
		   cambio_v=cambio_v||' - '||'OBSERVACIONES';
		   anterior_v=upper(record_ant.observaciones);
		   actual=upper(record_sig.observaciones);
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;      
		if (record_sig.provincia is null and record_ant.provincia is not null) or record_ant.provincia<>record_sig.provincia then 
		   cambio_v=cambio_v||' - '||'PROVINCIA';
		   anterior_v=record_ant.provincia;
		   actual=record_sig.provincia;
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;      
		if (record_sig.localidad is null and record_ant.localidad is not null) or record_ant.localidad<>record_sig.localidad then 
		   cambio_v=cambio_v||' - '||'LOCALIDAD';
		   anterior_v=record_ant.localidad;
		   actual=record_sig.localidad;		
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
		end if;      
		if (record_sig.numero is null and record_ant.numero is not null) or record_ant.numero<>record_sig.numero then 
		   cambio_v=cambio_v||' - '||'NUMERO';
		   anterior_v=record_ant.numero;
		   actual=record_sig.numero;	
		   insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
			select  record_sig.cuil_titular, 
				record_sig.inte, 
				'GRUPO', 
				'', 
				'', 
				'',
				cambio_v, 
				actual,
				anterior_v,
				record_ant.modi_usr,
				record_ant.modi_fecha;
			record_ant=record_sig;			
                end if;      

                RAISE INFO 'LOOP';
                --RAISE INFO 'CALLE: %, PISO: %, DEPTO: %, OFICINA: %, POSTAL_CODI: %, BARRIO: %, TELEFONO: %, OBSERVACIONES: %',record_ant.calle, record_ant.pisto, record_ant.depto, record_ant.oficina, record_ant.postal_codi, record_ant.barrio, , record_ant.telefono, record_ant.observaciones;
                
                if cambio_v is not null then			
			record_ant=record_sig;
			cambio_v=null;
		end if;
	END IF;

END LOOP;	

--ACTUALIZO LOCALIDAD - PROVINCIA
update cambios_periodo c
set anterior=l.detalle
from localidad l
where cambio like '%LOCALIDAD%'
and l.id_localidad=cast(c.anterior as int);

update cambios_periodo c
set ahora=l.detalle
from localidad l
where cambio like '%LOCALIDAD%'
and l.id_localidad=cast(c.ahora as int);

update cambios_periodo c
set anterior=l.detalle
from provincia l
where cambio like '%PROVINCIA%'
and l.id_provincia=cast(c.anterior as int);

update cambios_periodo c
set ahora=l.detalle
from provincia l
where cambio like '%PROVINCIA%'
and l.id_provincia=cast(c.ahora as int);

--ALTAS SITU LABORAL
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select cuil_titular, inte, '', '', '', '', 'ALTA SITUACION LABORAL '||a.cuit, '-', '-', alta_usr, alta_fecha
from afi_situ_laboral a
where cuil_titular=cuil_titular_p
and (fecha_desde is null or (fecha_desde is not null and alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and alta_fecha<fecha_hasta_plus));

ant_lleno=false;
cambio_v=null;

--MODIF SITU LABORAL
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select l.cuil_titular, l.inte, p.descripcion as parentesco ,a.docu_numero , a.apellido, a.nombre,  'MODIFICACION SITUACION LABORAL CUIT '||l.cuit, '', '',l.modi_usr, l.modi_fecha             
from afi_situ_laboral l, afiliado a, parentesco_sss p
where a.id_parentesco_sss = p.codigo
and l.cuil_titular=cuil_titular_p
and a.cuil_titular=l.cuil_titular
and a.inte=l.inte
and l.modi_fecha<>l.alta_fecha
and (fecha_desde is null or (fecha_desde is not null and l.modi_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and l.modi_fecha<fecha_hasta_plus));

	
	
--ALTA PLANES
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, 'GRUPO', '', '', '', 'ALTA PLAN '|| p.descripcion, '-', '-', a.alta_usr, a.alta_fecha
from afi_plan a, plan p
where a.id_plan=p.id_plan
and cuil_titular=cuil_titular_p
and (fecha_desde is null or (fecha_desde is not null and a.alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and a.alta_fecha<fecha_hasta_plus));

--MODIF PLANES
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select cuil_titular, inte, '', '', '', '', 'MODIFICACION PLAN '|| p.descripcion, '-', '-', a.baja_usr, a.modi_fecha
from afi_plan a, plan p
where a.id_plan=p.id_plan
and cuil_titular=cuil_titular_p
and a.baja_fecha is not null
and (fecha_desde is null or (fecha_desde is not null and a.modi_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and a.modi_fecha<fecha_hasta_plus));

--ALTA TERCERIZADORA
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, 'GRUPO', '', '', '', 'ALTA TERCERIZADORA '|| p.descripcion, '-', '-', a.alta_usr, a.alta_fecha
from afi_tercerizadora_servicio a , tercerizadora_servicio p
where a.id_tercerizadora=p.id_tercerizadora
and cuil_titular=cuil_titular_p
and (fecha_desde is null or (fecha_desde is not null and a.alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and a.alta_fecha<fecha_hasta_plus));

--MODIF TERCERIZADORA
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, 'GRUPO', '', '', '', 'MODIF TERCERIZADORA '|| p.descripcion, '-', '-', a.modi_usr, a.modi_fecha
from afi_tercerizadora_servicio a , tercerizadora_servicio p
where a.id_tercerizadora=p.id_tercerizadora
and cuil_titular=cuil_titular_p
and a.modi_fecha<>a.alta_fecha
and (fecha_desde is null or (fecha_desde is not null and a.modi_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and a.modi_fecha<fecha_hasta_plus));

--ALTA DOCUMENTO
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, p.descripcion as parentesco, a.docu_numero, a.apellido, a.nombre, 'ALTA DOCUMENTACION '|| p.descripcion, '-', '-', ad.alta_usr, ad.alta_fecha
from afi_documento ad, documento p, afiliado a, parentesco_sss ps 
where a.id_parentesco_sss = ps.codigo  
and ad.cuil_titular=cuil_titular_p
and ad.inte=a.inte
and ad.id_documento=p.id_documento
and a.cuil_titular=ad.cuil_titular
and (fecha_desde is null or (fecha_desde is not null and ad.alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and ad.alta_fecha<fecha_hasta_plus));

--MODIF DOCUMENTO
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, p.descripcion as parentesco, a.docu_numero, a.apellido, a.nombre, 'MODIFICACION DOCUMENTACION '|| p.descripcion, '-', '-', ad.alta_usr, ad.alta_fecha
from afi_documento ad, documento p, afiliado a, parentesco_sss ps 
where a.id_parentesco_sss = ps.codigo 
and ad.cuil_titular=cuil_titular_p
and ad.id_documento=p.id_documento
and a.cuil_titular=ad.cuil_titular
and ad.modi_fecha<>ad.modi_fecha
and (fecha_desde is null or (fecha_desde is not null and ad.modi_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and ad.modi_fecha<fecha_hasta_plus));

--CREDENCIALES
insert into cambios_periodo (cuil_titular, inte, parentesco, docu_numero, apellido,nombre, cambio, anterior, ahora, usuario, fecha) 
select a.cuil_titular, a.inte, ps.descripcion as parentesco, a.docu_numero, a.apellido, a.nombre, 'IMPRESION CREDENCIAL', '-', '-', ad.alta_usr, ad.alta_fecha
from afi_creden_lote ad, afiliado a, parentesco_sss ps 
where a.id_parentesco_sss = ps.codigo
and ad.cuil_titular=cuil_titular_p
and a.cuil_titular=ad.cuil_titular
and a.inte=ad.inte
and (fecha_desde is null or (fecha_desde is not null and ad.alta_fecha>=fecha_desde))
and (fecha_hasta is null or (fecha_hasta is not null and ad.alta_fecha<fecha_hasta_plus));


return query
select distinct cuil_titular, inte, parentesco, docu_numero, apellido, nombre, cambio, anterior, ahora, usuario, fecha from cambios_periodo order by fecha asc, cuil_titular;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
