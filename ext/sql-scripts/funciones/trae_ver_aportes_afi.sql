CREATE OR REPLACE FUNCTION trae_ver_aportes_afi(IN cuil_titular character varying)
  RETURNS TABLE(periodo date, importe numeric, fecha_transf date, cuil_titular character varying, apellido character varying, nombre character varying, cuit character varying, razon_soc character varying, sucursal character varying) AS
$BODY$

select o.periodo, o.importe, o.fecha_transf, a.cuil_titular, a.apellido, a.nombre, e.cuit, e.razon_soc, e.sucursal 
from os_aportes_detalle o, afiliado a, empresa e
where
o.cuil_aportante=cuil_titular and
o.cuil_aportante = a.cuil_titular and
(a.aportante_titular = 1 or a.inte = 0) and
o.cuit_contribuyente = e.cuit --and 
--e.sucursal = (select min(em.sucursal) from empresa em where em.cuit = e.cuit)
order by periodo

$BODY$
  LANGUAGE 'sql' VOLATILE
  COST 100
  ROWS 1000;