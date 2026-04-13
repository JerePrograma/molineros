CREATE OR REPLACE FUNCTION inserta_reintegro(character varying,
 integer,
 timestamp without time zone,
 timestamp without time zone,
 integer,
 character varying,
 integer,
 character varying,
 character varying,
 character varying) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
  begin

  insert into reintegro (
  cuil_titular,
  inte,
  fecha,
  periodo,
  id_seccional,
  alta_fecha,
  alta_usr,
  modi_fecha,
  modi_usr,
  estado,
  entidad,
  tipo_reintegro,
  observaciones
  )
  
  values ($1,$2,$3,$4,$5,LOCALTIMESTAMP,$6,LOCALTIMESTAMP,$6,$7,$8,$9, $10);

  --si es un reintegro de protesis en estado autorizado debe generar un numero de reintegro de tipo protesis
  if $9='pre' then
  	update reintegro r set id_reintegro_user = nextval('reintegro_user_id_seq') where r.id_reintegro = currval('reintegro_id_seq'); 
  end if;
  if (($9='pro') and $7=5) then
  	update reintegro r set id_reintegro_user = nextval('reintegro_protesis_id_seq') where r.id_reintegro = currval('reintegro_id_seq'); 
  end if;
  
  return currval('reintegro_id_seq');  
  end;  
$BODY$;

