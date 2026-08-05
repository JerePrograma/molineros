CREATE OR REPLACE FUNCTION inserta_bonos(tipo_bono_v integer, nro_bono_desde integer, nro_bono_hasta integer, username character varying)
  RETURNS integer AS
$BODY$
declare cant_reg integer;
begin
cant_reg = 0;

FOR i  IN nro_bono_desde..nro_bono_hasta LOOP
	INSERT INTO bonos(tipo_bono, nro_bono, alta_usr, alta_fecha)VALUES (tipo_bono_v, i, username, current_date);
	cant_reg = cant_reg +1;
END LOOP;	

return cant_reg;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE