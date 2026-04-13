-- Function: actualiza_afiliado_y_domi(character varying, character varying, character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, integer, character varying, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying, character varying)

-- DROP FUNCTION actualiza_afiliado_y_domi(character varying, integer, character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, integer, character varying, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying, character varying);

CREATE OR REPLACE FUNCTION actualiza_afiliado_y_domi(cuil_titular_p character varying, inte_p integer, apellido_p character varying, nombre_p character varying, documento_tipo_p character varying, sexo_p character varying, cuil_p character varying, naci_fecha_p timestamp without time zone, civil_esta_p character varying, nacionalidad_p integer, parentesco_p character varying, id_seccional_p integer, anterior_os_p integer, modi_usr_p character varying, discapacitado_p character varying, docu_numero_p character varying, domi_tipo_p character varying, calle_p character varying, piso_p character varying, depto_p character varying, oficina character varying, postal_codi_p character varying, barrio_p character varying, telefono_p character varying, observaciones_p character varying, domi_val_p character varying, alta_usr_d_p character varying, modi_usr_d_p character varying, provincia_p integer, localidad_p integer, numero_p character varying, domi character varying)
  RETURNS integer AS
$BODY$
declare id_dom_baja integer;
declare id_dom_link integer;
BEGIN

update afiliado set 
apellido=$3,
nombre=$4,
documento_tipo=$5,
sexo=$6,
cuil=$7,
naci_fecha=$8,
civil_esta=$9,
nacionalidad=$10,
parentesco=$11,
id_seccional=$12,
anterior_os=$13,
modi_fecha=LOCALTIMESTAMP,
modi_usr=$14,
discapacitado=$15,
docu_numero=$16
where cuil_titular = $1 and
inte=$2;

IF $32 IS NOT NULL THEN

	update afi_domicilio
	set baja_fecha = LOCALTIMESTAMP, baja_usr = $14
	where
	cuil_titular = $1 and
	inte = $2 and
	baja_fecha is null;
	id_dom_link = inserta_domicilio($1,$2,$17,$18,$19,$20,$21,$22,$23,$24,$25,$26,$27,$28,$29,$30,$31);

END IF;

return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION actualiza_afiliado_y_domi(character varying, integer, character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, integer, character varying, integer, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying, character varying) OWNER TO postgres;

-- Function: borra_afi_aporte(character varying, character varying, integer, date)

-- DROP FUNCTION borra_afi_aporte(character varying, character varying, integer, date);

CREATE OR REPLACE FUNCTION borra_afi_aporte(cuil_p character varying, inte_p integer, id_aporte_p integer, fecha_ingreso_p date)
  RETURNS integer AS
$BODY$
    delete from afi_aportes
    where cuil_titular=$1
    and inte=$2
    and id_aporte=$3    
    and fecha_ingre=$4;
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION borra_afi_aporte(character varying, integer, integer, date) OWNER TO postgres;

-- Function: borra_situ_laboral(character varying, character varying, character varying, character varying, date)

-- DROP FUNCTION borra_situ_laboral(character varying, character varying, character varying, character varying, date);

CREATE OR REPLACE FUNCTION borra_situ_laboral(cuil_p character varying, inte_p integer, cuit_p character varying, sucu_p character varying, fecha_ingreso_p date)
  RETURNS integer AS
$BODY$
    delete from afi_situ_laboral
    where cuil_titular=$1
    and inte=$2
    and cuit=$3
    and sucursal=$4
    and fecha_ingre=$5;
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION borra_situ_laboral(character varying, integer, character varying, character varying, date) OWNER TO postgres;


-- Function: busca_afiliado_incluso_dado_baja_c_i(character, character)

-- DROP FUNCTION busca_afiliado_incluso_dado_baja_c_i(character, character);

CREATE OR REPLACE FUNCTION busca_afiliado_incluso_dado_baja_c_i(IN cuil character, IN inte integer)
  RETURNS TABLE(id_ospim integer, id_uoma integer, id_amtima integer, apellido character varying, nombre character varying, documento_tipo character varying, sexo character varying, cuil character varying, naci_fecha date, civil_esta character varying, nacionalidad integer, parentesco character varying, id_seccional integer, anterior_os integer, vigen_fecha timestamp without time zone, observaciones character varying, pres_ssalud_fecha date, alta_usr character varying, modi_usr character varying, discapacitado character varying, docu_numero character varying, domi_tipo character varying, calle character varying, piso character varying, depto character varying, oficina character varying, postal_codi character varying, barrio character varying, telefono character varying, observaciones_dom character varying, domi_val character varying, alta_usr_d character varying, modi_usr_d character varying, provincia integer, localidad integer, numero character varying) AS
$BODY$

select
a.id_ospim,
a.id_uoma, 
a.id_amtima, 
a.apellido,
a.nombre,
a.documento_tipo,
a.sexo,
a.cuil, 
a.naci_fecha, 
a.civil_esta, 
a.nacionalidad, 
a.parentesco, 
a.id_seccional, 
a.anterior_os, 
a.vigen_fecha, 
a.observaciones, 
a.pres_ssalud_fecha, 
a.alta_usr, 
a.modi_usr, 
a.discapacitado, 
a.docu_numero, 
af.domi_tipo, 
af.calle, 
af.piso, 
af.depto, 
af.oficina, 
af.postal_codi, 
af.barrio, 
af.telefono, 
af.observaciones, 
af.domi_val, 
af.alta_usr, 
af.modi_usr, 
af.provincia, 
af.localidad, 
af.numero

from afiliado a, afi_domicilio af

where 

a.cuil_titular = $1
and a.inte=$2
and af.modi_fecha in (select max(a1.modi_fecha) from afi_domicilio a1  where a1.cuil_titular = $1
			and a1.inte=$2 group by (a1.cuil_titular, a1.inte))
and a.cuil_titular = af.cuil_titular
and a.inte = af.inte;

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION busca_afiliado_incluso_dado_baja_c_i(character, integer) OWNER TO postgres;


-- Function: busca_afiliado_por_cuil_inte(character, character)

-- DROP FUNCTION busca_afiliado_por_cuil_inte(character, character);

CREATE OR REPLACE FUNCTION busca_afiliado_por_cuil_inte(IN cuil character, IN inte integer)
  RETURNS TABLE(id_ospim integer, id_uoma integer, id_amtima integer, apellido character varying, nombre character varying, documento_tipo character varying, sexo character varying, cuil character varying, naci_fecha date, civil_esta character varying, nacionalidad integer, parentesco character varying, id_seccional integer, anterior_os integer, vigen_fecha timestamp without time zone, observaciones character varying, pres_ssalud_fecha date, alta_usr character varying, modi_usr character varying, discapacitado character varying, docu_numero character varying, domi_tipo character varying, calle character varying, piso character varying, depto character varying, oficina character varying, postal_codi character varying, barrio character varying, telefono character varying, observaciones_dom character varying, domi_val character varying, alta_usr_d character varying, modi_usr_d character varying, provincia integer, localidad integer, numero character varying) AS
$BODY$

select
a.id_ospim,
a.id_uoma, 
a.id_amtima, 
a.apellido,
a.nombre,
a.documento_tipo,
a.sexo,
a.cuil, 
a.naci_fecha, 
a.civil_esta, 
a.nacionalidad, 
a.parentesco, 
a.id_seccional, 
a.anterior_os, 
a.vigen_fecha, 
a.observaciones, 
a.pres_ssalud_fecha, 
a.alta_usr, 
a.modi_usr, 
a.discapacitado, 
a.docu_numero, 
af.domi_tipo, 
af.calle, 
af.piso, 
af.depto, 
af.oficina, 
af.postal_codi, 
af.barrio, 
af.telefono, 
af.observaciones, 
af.domi_val, 
af.alta_usr, 
af.modi_usr, 
af.provincia, 
af.localidad, 
af.numero

from afiliado a, afi_domicilio af

where a.cuil_titular = $1
and a.inte=$2
and a.cuil_titular = af.cuil_titular
and a.inte = af.inte
and af.baja_fecha is null;

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION busca_afiliado_por_cuil_inte(character, integer) OWNER TO postgres;


-- Function: buscar_afiliados(character, character, character, character, integer, character, character)

-- DROP FUNCTION buscar_afiliados(character, character, character, character, integer, character, character);

CREATE OR REPLACE FUNCTION buscar_afiliados(IN cuil character, IN inte integer, IN tipodoc character, IN nrodoc character, IN seccional integer, IN apellido character, IN nombre character)
  RETURNS TABLE(cuil character varying, pare integer, nombre character varying, apellido character varying, tdoc character varying, documento character varying, seccional character varying, ingreso date, baja_fecha timestamp without time zone) AS
$BODY$
	select 	cuil_titular,
		inte, 
		apellido,
		nombre,
		documento_tipo,
		docu_numero,
		s.descripcion,
		ingre_fecha,
		a.baja_fecha
	from afiliado a, seccional s
	where cuil_titular=isNull($1,cuil_titular)
	and inte=isNull($2,inte)
	and documento_tipo=isNull($3,documento_tipo)
	and docu_numero=isNull($4,docu_numero)
	and a.id_seccional= s.id_seccional
	and s.id_seccional=isNull(isNull($5),s.id_seccional)
	and apellido like '%'||isNull($6,apellido)||'%'
	and nombre like '%'||isNull($7,nombre)||'%'
	limit 10
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION buscar_afiliados(character, integer, character, character, integer, character, character) OWNER TO postgres;


-- Function: delete_afiliado(character, character, character)

-- DROP FUNCTION delete_afiliado(character, integer, character);

CREATE OR REPLACE FUNCTION delete_afiliado(cuil character, inte integer, baja_fecha character)
  RETURNS integer AS
$BODY$

update afiliado set baja_fecha = LOCALTIMESTAMP, baja_usr = $3  where cuil_titular = $1 and inte = $2;
update afi_domicilio set baja_fecha = LOCALTIMESTAMP, baja_usr = $3 where cuil_titular = $1 and inte = $2 and
baja_fecha is null;

select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION delete_afiliado(character, integer, character) OWNER TO postgres;

-- Function: edita_afi_aporte(character varying, character varying, integer, date, date)

-- DROP FUNCTION edita_afi_aporte(character varying, character varying, integer, date, date);

CREATE OR REPLACE FUNCTION edita_afi_aporte(cuil_p character varying, inte_p integer, id_aporte_p integer, fecha_ingreso_p date, fecha_egreso_p date)
  RETURNS integer AS
$BODY$
    UPDATE afi_aportes
    set fecha_baja=$5
    where cuil_titular=$1
    and inte=$2
    and id_aporte=$3
    and fecha_ingre=$4;    
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION edita_afi_aporte(character varying, integer, integer, date, date) OWNER TO postgres;


-- Function: edita_situ_laboral(character varying, character varying, character varying, character varying, date, date)

-- DROP FUNCTION edita_situ_laboral(character varying, character varying, character varying, character varying, date, date);

CREATE OR REPLACE FUNCTION edita_situ_laboral(cuil_p character varying, inte_p integer, cuit_p character varying, sucu_p character varying, fecha_ingreso_p date, fecha_egreso_p date)
  RETURNS integer AS
$BODY$
    UPDATE afi_situ_laboral
    set fecha_baja=$6
    where cuil_titular=$1
    and inte=$2
    and cuit=$3
    and sucursal=$4
    and fecha_ingre=$5;    
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION edita_situ_laboral(character varying, integer, character varying, character varying, date, date) OWNER TO postgres;


-- Function: inserta_afi_aporte(character varying, character varying, integer, date, date)

-- DROP FUNCTION inserta_afi_aporte(character varying, character varying, integer, date, date);

CREATE OR REPLACE FUNCTION inserta_afi_aporte(cuil_p character varying, inte_p integer, id_aporte_p integer, fecha_ingreso_p date, fecha_egreso_p date)
  RETURNS integer AS
$BODY$
    INSERT INTO afi_aportes(cuil_titular, inte, id_aporte, fecha_ingre, fecha_baja)
    VALUES ($1, $2, $3, $4, $5);
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION inserta_afi_aporte(character varying, integer, integer, date, date) OWNER TO postgres;


-- Function: inserta_afiliado(character, character, character, character)

-- DROP FUNCTION inserta_afiliado(character, character, character, character);

CREATE OR REPLACE FUNCTION inserta_afiliado(cuil character, inte integer, nombre character, apellido character)
  RETURNS integer AS
$BODY$
insert into afiliado (cuil_titular, inte, nombre, apellido) values ($1,$2,$3,$4);
select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION inserta_afiliado(character, integer, character, character) OWNER TO postgres;


-- Function: inserta_afiliado(character varying, character varying, integer, integer, integer, character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, integer, character varying, integer, integer, timestamp without time zone, character varying, timestamp without time zone, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying)

-- DROP FUNCTION inserta_afiliado(character varying, character varying, integer, integer, integer, character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, integer, character varying, integer, integer, timestamp without time zone, character varying, timestamp without time zone, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying);

CREATE OR REPLACE FUNCTION inserta_afiliado(cuil_titular_p character varying, inte_p integer, id_ospim_p integer, id_uoma_p integer, id_amtima_p integer, apellido_p character varying, nombre_p character varying, documento_tipo_p character varying, sexo_p character varying, cuil_p character varying, naci_fecha_p timestamp without time zone, civil_esta_p character varying, nacionalidad_p integer, parentesco_p character varying, id_seccional_p integer, anterior_os_p integer, vigen_fecha_p timestamp without time zone, observaciones_p character varying, pres_ssalud_fecha_p timestamp without time zone, alta_usr_p character varying, modi_usr_p character varying, discapacitado_p character varying, docu_numero_p character varying, domi_tipo_p character varying, calle_p character varying, piso_p character varying, depto_p character varying, oficina character varying, postal_codi_p character varying, barrio_p character varying, telefono_p character varying, observaciones_dom_p character varying, domi_val_p character varying, alta_usr_d_p character varying, modi_usr_d_p character varying, provincia_p integer, localidad_p integer, numero_p character varying)
  RETURNS integer AS
$BODY$
declare resultDom integer;
BEGIN

insert into afiliado (
cuil_titular,
inte,
id_ospim,
id_uoma,
id_amtima,
apellido,
nombre,
documento_tipo,
sexo,
cuil,
naci_fecha,
civil_esta,
nacionalidad,
parentesco,
ingre_fecha,
id_seccional,
anterior_os,
vigen_fecha,
observaciones,
pres_ssalud_fecha,
alta_fecha,
alta_usr,
modi_fecha,
modi_usr,
discapacitado,
docu_numero
)
values
($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,LOCALTIMESTAMP,$15,$16,$17,$18,$19,LOCALTIMESTAMP,$20,LOCALTIMESTAMP,$21,$22,$23);


resultDom=inserta_domicilio($1,$2,$24,$25,$26,$27,$28,$29,$30,$31,$32,$33,$34,$35,$36,$37,$38);
--inseguro!! todo chequear la forma de obtenerlo del store diréctamente
--id_dom = select last_value from domicilio_id_seq;

return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION inserta_afiliado(character varying, integer, integer, integer, integer, character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, integer, character varying, integer, integer, timestamp without time zone, character varying, timestamp without time zone, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying) OWNER TO postgres;


-- Function: inserta_domicilio(character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying)

-- DROP FUNCTION inserta_domicilio(character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying);

CREATE OR REPLACE FUNCTION inserta_domicilio(cuil_tit_d character varying, inte_d integer, domi_tipo_i character varying, calle_i character varying, piso_i character varying, depto_i character varying, oficina_i character varying, postal_codi_i character varying, barrio_i character varying, telefono_i character varying, observaciones_i character varying, domi_val_i character varying, alta_usr_i character varying, modi_usr_i character varying, provincia_i integer, localidad_i integer, numero_i character varying)
  RETURNS integer AS
$BODY$
  begin

  insert into afi_domicilio (
  cuil_titular,
  inte,
  vigen_desde,
  domi_tipo,
  calle,
  piso,
  depto,
  oficina,
  postal_codi,
  barrio,
  telefono,
  observaciones,
  domi_val,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  provincia,
  localidad,
  numero
  ) values ($1,$2,LOCALTIMESTAMP,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,LOCALTIMESTAMP,$13,LOCALTIMESTAMP,$14,$15,$16,$17);  
  return 1;
  end;  
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION inserta_domicilio(character varying, integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, integer, character varying) OWNER TO postgres;


-- Function: inserta_situ_laboral(character varying, character varying, character varying, character varying, date, date)

-- DROP FUNCTION inserta_situ_laboral(character varying, character varying, character varying, character varying, date, date);

CREATE OR REPLACE FUNCTION inserta_situ_laboral(cuil_p character varying, inte_p integer, cuit_p character varying, sucu_p character varying, fecha_ingreso_p date, fecha_egreso_p date)
  RETURNS integer AS
$BODY$
    INSERT INTO afi_situ_laboral(cuil_titular, inte, cuit, sucursal, fecha_ingre, fecha_baja)
    VALUES ($1, $2, $3, $4, $5, $6);
    select 1;
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION inserta_situ_laboral(character varying, integer, character varying, character varying, date, date) OWNER TO postgres;


-- Function: "isnull"(text, text)

-- DROP FUNCTION "isnull"(text, text);

CREATE OR REPLACE FUNCTION "isnull"(text, text)
  RETURNS text AS
$BODY$ 
SELECT (CASE (SELECT $1 is null) 
		WHEN true 
		THEN $2 
		ELSE $1 
	END) AS RESULT
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION "isnull"(text, text) OWNER TO postgres;
COMMENT ON FUNCTION "isnull"(text, text) IS 'Retorna o 2º arg se o 1º for
nulo';


-- Function: "isnull"(integer, integer)

-- DROP FUNCTION "isnull"(integer, integer);

CREATE OR REPLACE FUNCTION "isnull"(integer, integer)
  RETURNS integer AS
$BODY$ 
SELECT (CASE (SELECT $1 is null) 
		WHEN true 
		THEN $2 
		ELSE $1 
	END) AS RESULT
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION "isnull"(integer, integer) OWNER TO postgres;


-- Function: "isnull"(integer)

-- DROP FUNCTION "isnull"(integer);

CREATE OR REPLACE FUNCTION "isnull"(integer)
  RETURNS integer AS
$BODY$ 
SELECT (CASE ($1 =0) 
		WHEN true 
		THEN null 
		ELSE $1 
	END) AS RESULT
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100;
ALTER FUNCTION "isnull"(integer) OWNER TO postgres;


-- Function: trae_empleadores(character varying, character varying, integer)

-- DROP FUNCTION trae_empleadores(character varying, character varying, integer);

CREATE OR REPLACE FUNCTION trae_empleadores(IN cuit_p character varying, IN descripcion_p character varying, IN page integer)
  RETURNS TABLE(cuit character varying, sucursal character varying, descripcion character varying) AS
$BODY$
select cuit,sucursal,razon_soc 
from empresa 
where cuit=isNull($1,cuit)
and razon_soc like '%'||isNull($2,razon_soc)||'%'
order by razon_soc
limit 20
offset $3
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_empleadores(character varying, character varying, integer) OWNER TO postgres;


-- Function: trae_empleadores(character varying, character varying)

-- DROP FUNCTION trae_empleadores(character varying, character varying);

CREATE OR REPLACE FUNCTION trae_empleadores(IN cuit_p character varying, IN descripcion_p character varying)
  RETURNS TABLE(cuit character varying, sucursal character varying, descripcion character varying) AS
$BODY$
select cuit,sucursal,razon_soc 
from empresa 
where cuit=isNull($1,cuit)
and rtrim(razon_soc)=isNull($2,rtrim(razon_soc))
order by razon_soc
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_empleadores(character varying, character varying) OWNER TO postgres;


-- Function: trae_localidades()

-- DROP FUNCTION trae_localidades();

CREATE OR REPLACE FUNCTION trae_localidades()
  RETURNS TABLE(id_localidad integer, detalle character varying) AS
$BODY$
select id_localidad, 
       detalle 
from localidad 
order by detalle
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_localidades() OWNER TO postgres;


-- Function: trae_nacionalidades()

-- DROP FUNCTION trae_nacionalidades();

CREATE OR REPLACE FUNCTION trae_nacionalidades()
  RETURNS TABLE(id integer, detalle character varying) AS
$BODY$
select id, 
       detalle 
from nacionalidad 
order by detalle
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_nacionalidades() OWNER TO postgres;


-- Function: trae_provincias()

-- DROP FUNCTION trae_provincias();

CREATE OR REPLACE FUNCTION trae_provincias()
  RETURNS TABLE(id_provincia integer, detalle character varying) AS
$BODY$
select id_provincia, 
       detalle 
from provincia 
order by detalle
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_provincias() OWNER TO postgres;


-- Function: trae_seccionales()

-- DROP FUNCTION trae_seccionales();

CREATE OR REPLACE FUNCTION trae_seccionales()
  RETURNS TABLE(id_seccional integer, descripcion character varying) AS
$BODY$
select id_seccional,descripcion from seccional order by descripcion
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_seccionales() OWNER TO postgres;


-- Function: trae_situ_laborales(character varying, character varying)

-- DROP FUNCTION trae_situ_laborales(character varying, character varying);

CREATE OR REPLACE FUNCTION trae_situ_laborales(IN cuil_p character varying, IN inte_p integer)
  RETURNS TABLE(cuit character varying, sucursal character varying, razon_social character varying, fecha_ingreso date, fecha_baja date) AS
$BODY$
select a.cuit,a.sucursal,e.razon_soc, fecha_ingre,fecha_baja
from afi_situ_laboral a, empresa e
where cuil_titular=$1
and inte=$2
and e.cuit=a.cuit
and e.sucursal=a.sucursal
order by fecha_ingre
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_situ_laborales(character varying, integer) OWNER TO postgres;


-- Function: trae_tipos_aporte()

-- DROP FUNCTION trae_tipos_aporte();

CREATE OR REPLACE FUNCTION trae_tipos_aporte()
  RETURNS TABLE(id_aporte integer, descripcion character varying) AS
$BODY$
select id_aporte, 
       descripcion 
from aporte
order by descripcion
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_tipos_aporte() OWNER TO postgres;


-- Function: trae_tipos_aporte_afi(character varying, character varying)

-- DROP FUNCTION trae_tipos_aporte_afi(character varying, character varying);

CREATE OR REPLACE FUNCTION trae_tipos_aporte_afi(IN cuil character varying, IN inte integer)
  RETURNS TABLE(id_aporte integer, descripcion character varying, fecha_ingreso date, fecha_egreso date, motivo_baja character varying) AS
$BODY$
select a.id_aporte, 
       t.descripcion,
       a.fecha_ingre,
       a.fecha_baja,
       m.descripcion
from afi_aportes a
INNER JOIN aporte t on (a.id_aporte=t.id_aporte)
LEFT OUTER JOIN motivo_baja m on (a.id_motivo_baja=m.id_motivo_baja)
where a.cuil_titular=$1
and a.inte=$2
order by a.fecha_ingre
$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION trae_tipos_aporte_afi(character varying, integer) OWNER TO postgres;
