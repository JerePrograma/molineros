create or replace function existe_afiliado(cuil_v varchar)
returns integer AS
$BODY$
begin
return 1 from afiliado 
where cuil_titular=cuil_v
and (baja_fecha is null or baja_fecha>current_date);
end;
$BODY$
Language 'plpgsql'
