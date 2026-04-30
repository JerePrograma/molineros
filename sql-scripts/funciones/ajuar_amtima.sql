CREATE OR REPLACE FUNCTION ajuar_amtima(id_lote_p character varying)
  RETURNS SETOF ajuar_amtima_result AS
$BODY$
BEGIN
return query
select to_char(current_date,'dd') as dia,
       case when to_char(current_date,'MM')='01' then 'Enero' 
	    when to_char(current_date,'MM')='02' then 'Febrero' 
	    when to_char(current_date,'MM')='03' then 'Marzo' 
	    when to_char(current_date,'MM')='04' then 'Abril' 
	    when to_char(current_date,'MM')='05' then 'Mayo' 
	    when to_char(current_date,'MM')='06' then 'Junio' 
	    when to_char(current_date,'MM')='07' then 'Julio' 
	    when to_char(current_date,'MM')='08' then 'Agosto' 
	    when to_char(current_date,'MM')='09' then 'Septiembre' 
	    when to_char(current_date,'MM')='10' then 'Octubre' 
	    when to_char(current_date,'MM')='11' then 'Noviembre' 
	    when to_char(current_date,'MM')='12' then 'Diciembre' end 
       as mes,
       to_char(current_date,'yyyy') as anio,
       a.id_seccional||' - ' as id_seccional,
       cast(s.descripcion as text) as seccional,
       cast(p.descripcion as text),
       cast(a.id_amtima as text) as id_afiliado,
       a.apellido||', '||a.nombre as titular,
       cast(1 as text) as cantidad,
       to_char(ad.fecha_vto,'dd/MM/yyyy')
from amtima_ajuares aj
inner join afiliado a
on a.id_amtima=aj.id_amtima
and a.inte=0
inner join seccional s
on a.id_seccional=s.id_seccional
inner join afi_documento ad
on ad.cuil_titular=a.cuil_titular
and ad.inte=aj.inte
inner join afi_plan ap
on ap.cuil_titular=a.cuil_titular
and ap.inte=0
inner join plan p
on ap.id_plan=p.id_plan
where (ap.baja_fecha is null or ap.baja_fecha>current_date)
and aj.id_lote=cast(id_lote_p as integer)
and ad.id_documento=12
and ad.alta_fecha=(select max(alta_fecha) from afi_documento aad where aad.id_documento=12 and aad.cuil_titular=ad.cuil_titular and aad.inte=ad.inte);
/*return query
select '14' as dia, 'septiembre' as mes, '2011' as anio, '105 - ' as id_seccional, 'CAÑUELAS' as seccional,
       'AMTIMA' as plan, '12345' as id_afiliado, 'CAMPOMAGNO, JUAN CARLOS' as titular, '20' as cantidad,
       '01/05/2011' as fecha_parto
UNION
select '15' as dia, 'OCTUBRE' as mes, '2009' as anio, '102 - ' as id_seccional, 'BUENOS AIRES' as seccional,
       'AMTIMA' as plan, '54321' as id_afiliado, 'ARISTIZABAL, GASTON EDUARDO' as titular, '10' as cantidad,
       '09/05/2011' as fecha_parto;*/
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
