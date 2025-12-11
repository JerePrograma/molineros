create type result_trae_empleadores as (cuit varchar, sucursal varchar, id_ramo_empresa smallint, descripcion varchar)

CREATE OR REPLACE FUNCTION trae_empleadores(cuit_p character varying, descripcion_p character varying, page_p integer)
  RETURNS SETOF result_trae_empleadores AS
$BODY$
begin

	if cuit_p is not null then
		return query select cuit,sucursal,id_ramo_empresa,razon_soc 
		from empresa 
		where cuit=cuit_p
		order by razon_soc
		limit 20
		offset page_p;
	end if;

	if cuit_p is null then
		return query select cuit,sucursal,id_ramo_empresa,razon_soc 
		from empresa 
		where  razon_soc like '%'||isNull(descripcion_p,razon_soc)||'%'
		order by razon_soc
		limit 20
		offset page_p;
	end if;

end;
$BODY$
  LANGUAGE plpgsql VOLATILE

