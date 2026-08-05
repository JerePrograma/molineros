/*create type return_alta_padron_usuario as ("CUIL" varchar, 
					  "NRO. DOC." varchar,
					  "FECHA NAC." date,
					  "APELLIDO" text,
					  "NOMBRE" text,
					  "PARENTESCO" text,
					  "FECHA VIG." date,
					  "CALLE" varchar,
					  "NUMERO" varchar,
					  "COD. POSTAL" varchar,
					  "LOCALIDAD" varchar)*/
					  


CREATE OR REPLACE FUNCTION reporte_alta_padron_usuario(periodo date, usuario varchar)
  RETURNS SETOF return_alta_padron_usuario AS
$BODY$
--declare fecha_inicio date;
begin
return query
select a.cuil_titular as "CUIL",
       a.docu_numero as "NRO. DOC.",
       a.naci_fecha as "FECHA NAC.",
       upper(a.apellido) as "APELLIDO",
       upper(a.nombre) as "NOMBRE",
       upper(a.parentesco) as "PARENTESCO",
       cast(a.vigen_fecha as date) as "FECHA DE VIG.",
       d.calle as "CALLE",
       d.numero as "NUMERO",
       d.postal_codi as "COD.POSTAL",
       l.detalle as "LOCALIDAD"
from afiliado a, afi_domicilio d, localidad l
where a.vigen_fecha>periodo
and a.alta_usr=usuario
and d.cuil_titular=a.cuil_titular
and d.inte=0
and d.alta_fecha=(select max(alta_fecha) from afi_domicilio dd where dd.cuil_titular=d.cuil_titular and inte=0 and (baja_fecha is null or baja_fecha>current_date))
and l.id_localidad=d.localidad
order by a.cuil_titular, a.inte;
END;

$BODY$

  LANGUAGE 'plpgsql' VOLATILE