CREATE TYPE novedades_ws_omint_result AS
   (id_ospim integer,
    id_amtima integer,
    id_uoma integer,
    seccional character varying,
    id_tercerizadora character varying,
    cuil_titular character varying,
    cuil character varying,
    inte integer,
    id_parentesco_sss integer,
    parentesco character varying,
    apellido character varying,
    nombre character varying,
    documento_tipo character varying,
    docu_numero character varying,
    naci_fecha date,
    sexo character varying,
    /*id_civil_esta integer,*/
    id_estado_civil_sss integer,
    id_nacionalidad integer,
    provincia character varying,
    localidad character varying,
    postal_codi character varying,
    calle character varying,
    numero character varying,
    piso character varying,
    depto character varying,
    telefono character varying,
    ingre_fecha date,
    baja_fecha date,
    cuit character varying,
    razon_soc character varying,
    plan_omint character varying,
    id_categoria integer,
    fpp date,
    discapacitado character varying,
    operacion integer);
    
CREATE OR REPLACE FUNCTION informes.lista_novedades_ws()
  RETURNS SETOF novedades_ws_omint_result AS
$BODY$
declare ALTA_TOTAL_P integer;
declare ALTA_BENEFICIARIO integer;
declare MODIF_BENEFICIARIO integer;
declare MODIF_PLAN integer;
declare BAJA_TOTAL integer;
declare BAJA_BENEFICIARIO integer;
BEGIN
drop table if exists result_ws;
drop table if exists aux_ws;

ALTA_TOTAL_P=0;
ALTA_BENEFICIARIO=1;
MODIF_BENEFICIARIO=2;
BAJA_TOTAL=3;
BAJA_BENEFICIARIO=4;
MODIF_PLAN=5;

drop table if exists result_ws;
drop table if exists aux_ws;

create temp table result_ws  
            (id_ospim integer, seccional varchar, id_tercerizadora varchar, cuil_titular varchar, cuil varchar, inte integer, 
            id_parentesco_sss integer, parentesco varchar, apellido varchar, nombre varchar, documento_tipo varchar, docu_numero varchar, 
            naci_fecha date, sexo varchar, id_estado_civil_sss integer, civil_esta varchar, nacionalidad varchar, provincia varchar, 
            localidad varchar, postal_codi varchar, calle varchar, numero varchar, piso varchar, depto varchar, telefono varchar, 
            categoria varchar, ramo varchar, id_plan integer, plan varchar, ingre_fecha date, baja_fecha date, id_uoma integer, 
            cuit varchar, razon_soc varchar, fecha_ospim date, os_anterior varchar, discapacitado varchar, pmi date, 
            id_transaction numeric, message_code varchar, message_description varchar, operacion int);


--PRIMERO TRAIGO EL PADRON AL DIA DE HOY
create  temp table aux_ws AS
select * from listado_vigentes('CSA', false, 1, cast( to_char(current_date + Interval '1 month','yyyyMM')||'01' as date));

RAISE INFO 'ALTA GRUPO FLIAR';

--ALTAS GRUPO FLIAR...
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacidad, pmi, ALTA_TOTAL_P
from aux_ws w
where not exists(select 1 from informes.padron_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=0)       
and not exists (select 1 from informes.padron_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte);


--ALTAS TITULARES RECHAZADAS EN PROCESOS ANTERIORES.
--PRIMERO LAS GUARDO EN UNA TABLA HISTORICA...
insert into informes.padron_omint_ws_histo(
            id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, alta_fecha)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, current_timestamp
from informes.padron_omint_ws w
where operacion=ALTA_TOTAL_P --ESTAS SON ALTAS TOTALES
and (id_transaction is null or id_transaction<7)
and exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular)
and not exists (select 1 from result_ws rw
		where rw.cuil_titular=w.cuil_titular
		and rw.inte=w.inte);    


--AHORA LAS INFORMO...
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacidad, pmi, ALTA_TOTAL_P
from aux_ws w
where exists (select 1 from informes.padron_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=0 and (id_transaction is null or id_transaction<7)
		 and operacion=ALTA_TOTAL_P)
and not exists (select 1 from result_ws rw
		where rw.cuil_titular=w.cuil_titular
		and rw.inte=w.inte);      


--ALTAS BENEFICIARIOS
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco,id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacidad, pmi, ALTA_BENEFICIARIO
from aux_ws w
where inte<>0
and not exists(select 1 from informes.padron_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte and id_transaction>7)       
and exists(select 1 from informes.padron_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=0 and id_transaction>7)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte); 		 

RAISE INFO 'ALTA BENEFICIARIO';

--ALTAS BENEFICIARIOS RECHAZADAS EN PROCESOS ANTERIORES.
--PRIMERO LAS GUARDO EN UNA TABLA HISTORICA...
insert into informes.padron_omint_ws_histo(
            id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, alta_fecha)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, current_timestamp
from informes.padron_omint_ws w
where operacion=ALTA_BENEFICIARIO 
and (id_transaction is null or id_transaction<7)
and exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte)
and not exists (select 1 from result_ws rw
		where rw.cuil_titular=w.cuil_titular
		and rw.inte=w.inte);    
		 
--AHORA LAS INFORMO..
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacidad, pmi, ALTA_BENEFICIARIO
from aux_ws w
where exists (select 1 from informes.padron_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte and (id_transaction is null or id_transaction<7)
		 and operacion=ALTA_BENEFICIARIO) 
and not exists (select 1 from result_ws rw
		where rw.cuil_titular=w.cuil_titular
		and rw.inte=w.inte)		    
order by cuil_titular, inte;


RAISE INFO 'MODIF BENEFICIARIO';
--MODIFICACION BENEFICIARIOS
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select w.id_ospim, w.seccional, w.id_tercerizadora, w.cuil_titular, w.cuil, w.inte, 
            w.id_parentesco_sss, w.parentesco, w.id_estado_civil_sss, w.apellido, w.nombre, w.documento_tipo, w.docu_numero, w.naci_fecha, 
            w.sexo, w.civil_esta, w.nacionalidad, w.provincia, w.localidad, w.postal_codi, 
            w.calle, w.numero, w.piso, w.depto, w.telefono, w.categoria, w.ramo, w.id_plan, 
            w.plan, w.ingre_fecha, w.baja_fecha, w.id_uoma, w.cuit, w.razon_soc, w.fecha_ospim, 
            w.os_anterior, w.discapacidad, w.pmi, MODIF_BENEFICIARIO
from aux_ws w, informes.padron_omint_ws p
where p.cuil_titular=w.cuil_titular
and p.inte=w.inte 
and id_transaction>7
and (operacion=ALTA_TOTAL_P or operacion=ALTA_BENEFICIARIO or operacion=MODIF_BENEFICIARIO or operacion=MODIF_PLAN)
and  (w.seccional<> p.seccional or
	w.cuil_titular<>p.cuil_titular or w.cuil <>p.cuil or  w.inte<>p.inte or w.parentesco<>p.parentesco or w.id_parentesco_sss<>p.id_parentesco_sss or
	w.apellido<>p.apellido or w.nombre <>p.nombre or w.documento_tipo<>p.documento_tipo or w.docu_numero<>p.docu_numero or
        w.naci_fecha<>p.naci_fecha or w.sexo<>p.sexo or w.id_estado_civil_sss<>p.id_estado_civil_sss or w.civil_esta<>p.civil_esta or w.nacionalidad<>p.nacionalidad or
        w.provincia<>p.provincia or w.localidad<>p.localidad or w.postal_codi<>p.postal_codi or w.calle<>p.calle or
        w.numero<>p.numero or w.piso<>p.piso or w.depto<>p.depto or w.telefono<>p.telefono or w.categoria<>p.categoria or
        w.ingre_fecha<>p.ingre_fecha or w.baja_fecha<>p.baja_fecha or w.cuit<>p.cuit or
        w.razon_soc<>p.razon_soc or w.discapacidad<>p.discapacitado or w.pmi<>p.fpp)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte);


--GUARDO EL ESTADO ANTERIOR
insert into informes.padron_omint_ws_histo(
            id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, alta_fecha)
select p.id_ospim, p.seccional, p.id_tercerizadora, p.cuil_titular, p.cuil, p.inte, 
            p.id_parentesco_sss, p.parentesco, p.id_estado_civil_sss, p.apellido, p.nombre, p.documento_tipo, p.docu_numero, p.naci_fecha, 
            p.sexo, p.civil_esta, p.nacionalidad, p.provincia, p.localidad, p.postal_codi, 
            p.calle, p.numero, p.piso, p.depto, p.telefono, p.categoria, p.ramo, p.id_plan, 
            p.plan, p.ingre_fecha, p.baja_fecha, p.id_uoma, p.cuit, p.razon_soc, p.fecha_ospim, 
            p.os_anterior, p.discapacitado, p.fpp, p.id_transaction, p.message_code, p.message_description, 
            p.fecha_informe, MODIF_BENEFICIARIO, current_timestamp
from aux_ws w, informes.padron_omint_ws p
where p.cuil_titular=w.cuil_titular
and p.inte=w.inte 
and id_transaction>7
and (operacion=ALTA_TOTAL_P or operacion=ALTA_BENEFICIARIO or operacion=MODIF_BENEFICIARIO or operacion=MODIF_PLAN)
and  (w.seccional<> p.seccional or
	w.cuil_titular<>p.cuil_titular or w.cuil <>p.cuil or  w.inte<>p.inte or w.parentesco<>p.parentesco or w.id_parentesco_sss<>p.id_parentesco_sss or
	w.apellido<>p.apellido or w.nombre <>p.nombre or w.documento_tipo<>p.documento_tipo or w.docu_numero<>p.docu_numero or
        w.naci_fecha<>p.naci_fecha or w.sexo<>p.sexo or w.civil_esta<>p.civil_esta or w.id_estado_civil_sss<>p.id_estado_civil_sss or w.nacionalidad<>p.nacionalidad or
        w.provincia<>p.provincia or w.localidad<>p.localidad or w.postal_codi<>p.postal_codi or w.calle<>p.calle or
        w.numero<>p.numero or w.piso<>p.piso or w.depto<>p.depto or w.telefono<>p.telefono or w.categoria<>p.categoria or
        w.ingre_fecha<>p.ingre_fecha or w.baja_fecha<>p.baja_fecha or w.cuit<>p.cuit or
        w.razon_soc<>p.razon_soc or w.discapacidad<>p.discapacitado or w.pmi<>p.fpp)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte);
            
RAISE INFO 'MODIFI BENE 2';

--MODIF BENEFICIARIOS RECHAZADAS EN PROCESOS ANTERIORES.
--PRIMERO LAS GUARDO EN UNA TABLA HISTORICA...
insert into informes.padron_omint_ws_histo(
            id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, alta_fecha)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, current_timestamp
from informes.padron_omint_ws w
where operacion=MODIF_BENEFICIARIO 
and (id_transaction is null or id_transaction<7)
and exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte); 
		 
--AHORA LAS INFORMO..
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacidad, pmi, MODIF_BENEFICIARIO
from aux_ws w
where exists (select 1 from informes.padron_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte and (id_transaction is null or id_transaction<7)
		 and operacion=MODIF_BENEFICIARIO)  
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte);

RAISE INFO 'CAMBIO PLAN';

--CAMBIO PLAN
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select w.id_ospim, w.seccional, w.id_tercerizadora, w.cuil_titular, w.cuil, w.inte, 
            w.id_parentesco_sss, w.parentesco, w.id_estado_civil_sss, w.apellido, w.nombre, w.documento_tipo, w.docu_numero, w.naci_fecha, 
            w.sexo, w.civil_esta, w.nacionalidad, w.provincia, w.localidad, w.postal_codi, 
            w.calle, w.numero, w.piso, w.depto, w.telefono, w.categoria, w.ramo, w.id_plan, 
            w.plan, w.ingre_fecha, w.baja_fecha, w.id_uoma, w.cuit, w.razon_soc, w.fecha_ospim, 
            w.os_anterior, w.discapacidad, w.pmi, MODIF_PLAN/*MODIF_BENEFICIARIO*/
from aux_ws w, informes.padron_omint_ws p
where p.cuil_titular=w.cuil_titular
and w.inte=0
and p.inte=w.inte 
and id_transaction>7
and (operacion=ALTA_TOTAL_P or operacion=ALTA_BENEFICIARIO or operacion=MODIF_BENEFICIARIO and operacion=MODIF_PLAN)
and  (w.plan<> p.plan)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte);

RAISE INFO 'CAMBIO PLAN 2';
--MODIF PLAN RECHAZADAS EN PROCESOS ANTERIORES.
--PRIMERO LAS GUARDO EN UNA TABLA HISTORICA...
insert into informes.padron_omint_ws_histo(
            id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, alta_fecha)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, current_timestamp
from informes.padron_omint_ws w
where operacion=MODIF_PLAN 
and (id_transaction is null or id_transaction<7)
and w.inte=0
and exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte); 

--AHORA LAS INFORMO..
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado,pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacidad, pmi, MODIF_BENEFICIARIO
from aux_ws w
where w.inte=0
and exists (select 1 from informes.padron_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte and (id_transaction is null or id_transaction<7)
		 and operacion=MODIF_PLAN)    
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte); 

RAISE INFO 'BAJA GRUPO';		 

--BAJAS DE GRUPO
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, BAJA_TOTAL
from informes.padron_omint_ws w
where w.inte = 0 --solo el titular del grupo manda en la baja
and not exists(select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=0)       
/*and not exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte)*/
and not exists (select 1 from informes.bajas_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte
		 and operacion=BAJA_TOTAL 
		 and (id_transaction is null or id_transaction<7));

RAISE INFO 'BAJA GRUPO 2';		 
--BAJAS TITULARES RECHAZADAS EN PROCESOS ANTERIORES.
--PRIMERO LAS GUARDO EN UNA TABLA HISTORICA...
insert into informes.padron_omint_ws_histo(
            id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, alta_fecha)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, current_timestamp
from informes.bajas_omint_ws w
where operacion=BAJA_TOTAL 
and (id_transaction is null or id_transaction<7)
and not exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte);     


--AHORA LAS INFORMO...
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, BAJA_TOTAL
from informes.bajas_omint_ws w
where operacion=BAJA_TOTAL 
and (id_transaction is null or id_transaction<7)
and not exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte); 

RAISE INFO 'BAJA BENEF';		 
--BAJAS DE BENEFICIARIO
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, BAJA_BENEFICIARIO
from informes.padron_omint_ws w
where not exists(select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte)       
and exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=0)
and w.inte<>0		 
and not exists (select 1 from informes.bajas_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=w.inte
		 and (operacion=BAJA_TOTAL or operacion=BAJA_BENEFICIARIO)
		 and (id_transaction is null or id_transaction<7))
--agrego q no envie baja beneficiario si hay baja del titular		 
and not exists (select 1 from informes.bajas_omint_ws p
		 where p.cuil_titular=w.cuil_titular
		 and p.inte=0
		 and (operacion=BAJA_TOTAL)
		 and (id_transaction is null or id_transaction<7))
--		 
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte); 

--BAJAS BENEFICIARIOS RECHAZADAS EN PROCESOS ANTERIORES.
--PRIMERO LAS GUARDO EN UNA TABLA HISTORICA...
insert into informes.padron_omint_ws_histo(
            id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, alta_fecha)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, id_transaction, message_code, message_description, 
            fecha_informe, operacion, current_timestamp
from informes.bajas_omint_ws w
where operacion=BAJA_BENEFICIARIO
and (id_transaction is null or id_transaction<7)
and not exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular and p.inte=w.inte)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte);

RAISE INFO 'BAJA BENEF 2';
--AHORA LAS INFORMO...
insert into result_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, pmi, operacion)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, BAJA_BENEFICIARIO
from informes.bajas_omint_ws w
where operacion=BAJA_BENEFICIARIO
and (id_transaction is null or id_transaction<7)
and not exists (select 1 from aux_ws p
		 where p.cuil_titular=w.cuil_titular and p.inte=w.inte)
and not exists (select 1 from result_ws rw where rw.cuil_titular=w.cuil_titular and rw.inte=w.inte);    		 


RAISE INFO 'INFORMO';
--TENGO TODO EN RESULT.
--INSERTO EN LA HISTORIA LAS BAJAS
insert into informes.bajas_omint_ws(id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, operacion, alta_fecha)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            cast(os_anterior as integer), discapacitado, pmi, operacion, current_timestamp
from result_ws r
where operacion in (BAJA_TOTAL, BAJA_BENEFICIARIO)
and not exists (select 1 from informes.bajas_omint_ws b where b.cuil_titular=r.cuil_titular and b.inte=r.inte);

RAISE INFO 'BORRO';
--BORRO DEL PADRON LAS BAJAS y las ALTAS/MODIF que ya guardé en el histórico
delete from informes.padron_omint_ws p
where exists (select 1 
	      from result_ws r
	      where p.cuil_titular=r.cuil_titular
	      and p.inte=r.inte);

--ACTUALIZO EL PADRON CON LAS ALTAS NUEVAS
insert into informes.padron_omint_ws (id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            os_anterior, discapacitado, fpp, operacion, alta_fecha)
select id_ospim, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
            id_parentesco_sss, parentesco, id_estado_civil_sss, apellido, nombre, documento_tipo, docu_numero, naci_fecha, 
            sexo, civil_esta, nacionalidad, provincia, localidad, postal_codi, 
            calle, numero, piso, depto, telefono, categoria, ramo, id_plan, 
            plan, ingre_fecha, baja_fecha, id_uoma, cuit, razon_soc, fecha_ospim, 
            cast(os_anterior as integer), discapacitado, pmi, operacion, current_timestamp
from result_ws r
where operacion not in (BAJA_TOTAL, BAJA_BENEFICIARIO);

--alter table result_ws add id_civil_esta integer;
alter table result_ws add id_nacionalidad integer;
alter table result_ws add id_categoria integer;
--alter table result_ws add id_parentesco varchar;
alter table result_ws add id_tipo_doc varchar;

/*update result_ws ws
set id_civil_esta=ec.id
from informes.estado_civil ec
where upper(ws.civil_esta)=upper(ec.descripcion_ospim);*/

update result_ws ws
set id_nacionalidad=n.id_omint
from nacionalidad n
where upper(n.detalle)=upper(ws.nacionalidad);

update result_ws ws
set id_categoria=n.id_omint
from categoria_laboral n
where upper(n.categoria)=upper(ws.categoria);

update result_ws ws
set parentesco=n.codigo_omint
from informes.parentesco_omint n
where n.id_parentesco_sss=ws.id_parentesco_sss;
/*update result_ws ws
set id_parentesco=n.codigo_omint
from informes.parentesco_omint n
where upper(n.descripcion_ospim)=upper(ws.parentesco);*/

update result_ws ws
set id_tipo_doc=n.tipo_doc_omint
from informes.tipos_documento n
where upper(n.tipo_doc_ospim)=upper(ws.documento_tipo);


--DEVUELVO TODO
return query
select id_ospim, 0, id_uoma, seccional, id_tercerizadora, cuil_titular, cuil, inte, 
       id_parentesco_sss, parentesco, apellido, nombre, id_tipo_doc, docu_numero, naci_fecha, 
       sexo, id_estado_civil_sss, id_nacionalidad, provincia, localidad, postal_codi, calle, 
       cast(isNull(case when numero='' then null else numero end,'0') as varchar), 
       piso, depto, telefono, ingre_fecha, baja_fecha, cuit, razon_soc, plan, id_categoria, PMI,
       discapacitado, operacion
from result_ws
order by operacion, cuil_titular, inte;        


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;