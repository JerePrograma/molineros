CREATE OR REPLACE FUNCTION buscar_organismos(IN nombre character varying, IN ambito character varying, IN linea character varying, IN sigla character varying, IN orbita_p character varying)
  RETURNS TABLE(id_organismo integer, denominacion character varying, ambito character varying, telefono character varying, web character varying, lineas text, sigla character varying, orbita character varying) AS
$BODY$
select o.id_organismo, o.denominacion, o.ambito, o.telefono, o.web, array_to_string(array(SELECT ol.linea 
										          FROM organismo_linea ol
										          where ol.id_organismo=o.id_organismo
										          and ol.baja_fecha is null), ' - '),
       o.sigla,
       o.orbita									          	
from organismo o
where upper(o.denominacion)= case when $1 is null then upper(o.denominacion) else upper($1) end
and upper(o.sigla)= case when $4 is null then upper(o.sigla) else upper($4) end
and upper(o.orbita)= case when $5 is null then upper(o.orbita) else upper($5) end
and upper(o.ambito)= case when $2 is null then upper(o.ambito) else upper($2) end
and (exists (select 1 from organismo_linea ol2
	    where ol2.id_organismo=o.id_organismo
	    and ol2.baja_fecha is null
	    and upper(ol2.linea)=case when $3 is null then upper(ol2.linea) else upper($3) end)
     or exists (select 1 from area a, area_linea al2
	    where a.id_organismo=o.id_organismo
	    and al2.id_area=a.id_area
	    and al2.baja_fecha is null
	    and upper(al2.linea)=case when $3 is null then upper(al2.linea) else upper($3) end)) 	    
and o.baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE
