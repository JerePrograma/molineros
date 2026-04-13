create type buscar_llamados_result as (cuit varchar, usuario varchar, tipo_contacto varchar, observaciones varchar, fecha timestamp without time zone)
CREATE OR REPLACE FUNCTION buscar_llamados_estudio(cuit_p character varying, curs integer)
  RETURNS SETOF buscar_llamados_result AS
$BODY$
declare offs integer;
BEGIN
if(curs>1) then offs=(curs-1)*100; else offs=0; end if;

RAISE INFO 'CURSOR: %', offs;
return query
select cuit,  usuario, tipo_contacto, observaciones,fecha
from estudio_llamadas_empresas
where cuit=cuit_p
order by fecha desc
offset offs
limit 100;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE


