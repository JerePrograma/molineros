CREATE OR REPLACE FUNCTION reporte_boletas_empleadores_todas(periodo_desde date, periodo_hasta date, cuit_p character varying)
  RETURNS SETOF reporte_boletas_empleadores_todas_type AS
$BODY$
BEGIN

return query
select d.empresa_cuit,
e.razon_soc,
camara,
to_char(fecha_ingreso, 'DD/MM/YYYY') as fecha_ing,
categoriasalarial,
remuneracion,
case when aportesocialuoma then cast(importeaportesocialuoma as character varying) else '---' end as aportesocialuoma,
case when articulo46 then cast(importearticulo46 as character varying) else '---' end as articulo46 ,
case when cuotaamtima then cast(importecuotaamtima as character varying) else '---' end as cuotaamtima ,
case when cuotasocialuoma then cast(importecuotasocialuoma as character varying) else '---' end as cuotasocialuoma,
case when cuotausufructo then cast(importecuotausufructo as character varying) else '---' end as cuotausufructo,
case when adherenteamtima then cast(importeadherenteamtima as character varying) else '---' end as adherenteamtima,
upper(a.apellido || ' ' || a.nombre) as apellido,
a.cuil_titular,
dd.remuneracion,
dd.importenoremunerativo
from detalledeclaracionjurada dd
inner join declaracionjurada d 
on dd.declaracionjurada_id=d.id
inner join afiliado a
on dd.afiliado_cuil_titular = a.cuil_titular
and dd.afiliado_inte = a.inte
left outer join empresa e
on e.cuit=d.empresa_cuit
and e.sucursal='000'
where (d.periodo>=periodo_desde and d.periodo<=periodo_hasta)
and (cuit_p is null or (cuit_p is not null and empresa_cuit=cuit_p))

and d.numerosecuencia=(select max(dj2.numerosecuencia) 
			from declaracionjurada dj2 where dj2.empresa_cuit=d.empresa_cuit
			and dj2.periodo=d.periodo
			and dj2.cerrada=true)
order by empresa_cuit, a.apellido asc;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION reporte_boletas_empleadores_todas(date, date, character varying)
  OWNER TO postgres;
