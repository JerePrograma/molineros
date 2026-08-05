create type result_trae_empleadores as (cuit varchar, sucursal varchar, id_ramo_empresa smallint, descripcion varchar)

-- DROP FUNCTION trae_empleadores_filtro(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION trae_empleadores_filtro(cuit_p character varying, descripcion_p character varying, p_sucu character varying)
  RETURNS SETOF result_trae_empleadores AS
$BODY$
    DECLARE _razon varchar;
begin
	if cuit_p is not null then
		return query select cuit,sucursal,id_ramo_empresa,razon_soc 
		from empresa 
		where cuit=cuit_p
		and (p_sucu is null or (p_sucu is not null and sucursal like p_sucu || '%'))
		order by razon_soc
		limit 20;
	end if;

	if cuit_p is null then
		set enable_seqscan = off;
		_razon = upper(descripcion_p) || '%';
		if p_sucu is not null then
			return query select cuit,sucursal,id_ramo_empresa,razon_soc 
			from empresa 
			where  razon_soc like _razon
			and  sucursal=p_sucu
			order by razon_soc
			limit 20;
		else
			return query select cuit,sucursal,id_ramo_empresa,razon_soc 
			from empresa 
			where  razon_soc like _razon
			order by razon_soc
			limit 20;
		end if ;
		set enable_seqscan = on;
	end if;

end;
$BODY$
  LANGUAGE plpgsql VOLATILE

