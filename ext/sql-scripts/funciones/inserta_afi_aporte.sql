CREATE SEQUENCE afi_aportes_id_sequence;
ALTER TABLE afi_aportes ALTER COLUMN id SET DEFAULT nextval('afi_aportes_id_sequence');

SELECT setval('public.afi_aportes_id_sequence', 177911, true); --- 9999 se debe calcular con el " select max(id) from afi_aportes; "

**********************************************************************************************************************

CREATE OR REPLACE FUNCTION inserta_afi_aporte(genera_id_socio_p boolean, cuil_titular_p character varying, inte_p integer, id_aporte_p integer, 
fecha_ingre_p date, fecha_egre_p date, motivo_baja_p integer, id_plan_serial_p numeric, tipo_aporte_p character, usr_p character varying)
  RETURNS bigint AS
$BODY$

declare id_socio_aux integer;

BEGIN

-- Por cada aporte evaluaremos si se indica generar id nuevo para ospim, uoma, amtima segun el aporte y el parametro genera_id_socio_p
-- Si se genera nuevo id, sacaremos el maximo de la tabla afiliado, si no se genera intentaremos obtener el anterior id del aporte,
-- de los aportes anteriores (tabla afi_aportes)

id_socio_aux = case when genera_id_socio_p then (select case when tipo_aporte_p='U' then max(id_uoma) +1
when tipo_aporte_p='O' then max(id_ospim) +1
when tipo_aporte_p='A' then max(id_amtima)+1  end
 from afiliado )
 else 
 (select max(id_socio) 
 from afi_aportes 
 where cuil_titular = cuil_titular_p and tipo_aporte = tipo_aporte_p and baja_fecha is null) end;

-- Se insertara cada aporte correspondiente al plan del afiliado. No se mantendra continuidad del mismo aporte, como era antes, 
-- en cambio la continuidad la dara las fechas de ingreso y egreso de los mismos aportes
INSERT INTO afi_aportes(
            cuil_titular, inte, id_aporte, fecha_ingre, fecha_egre, id_motivo_baja,
            id_plan_serial, id_socio, tipo_aporte, alta_fecha, alta_usr, 
            modi_fecha, modi_usr)
    VALUES (cuil_titular_p, inte_p, id_aporte_p, fecha_ingre_p, fecha_egre_p, motivo_baja_p,
			id_plan_serial_p, id_socio_aux, tipo_aporte_p, LOCALTIMESTAMP, usr_p, 
			LOCALTIMESTAMP, usr_p);

--actualizamos los nuevos ids de los afiliados, (previamente pasamos al historico operacion 'MOD')
UPDATE afiliado
   SET id_ospim = case when (tipo_aporte_p = 'O' and genera_id_socio_p) then id_socio_aux else id_ospim end,   
       id_uoma  = case when (tipo_aporte_p = 'U' and genera_id_socio_p) then id_socio_aux else id_uoma end,
       id_amtima= case when (tipo_aporte_p = 'A' and genera_id_socio_p) then id_socio_aux else id_amtima end,
       modi_fecha=LOCALTIMESTAMP, modi_usr=usr_p
 WHERE cuil_titular=cuil_titular_p;

 return currval('afi_aportes_id_sequence');
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;