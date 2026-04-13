CREATE OR REPLACE FUNCTION inserta_liquidacion_debitos_terceros(
 l_periodo_hasta timestamp without time zone,
 l_observaciones character varying,
 l_usuario character varying
 ) 
RETURNS integer
    LANGUAGE plpgsql
    AS $BODY$
    declare sec integer;
BEGIN	
	  
update compro_tipo ct set secuencia = secuencia + 1 where ct.compro_tipo = 'NDB';
	
sec = secuencia 
from compro_tipo
where compro_tipo = 'NDB';
--ver es posible que tenga que generar el número del débito en este stored.

INSERT INTO liquidacion_debitos_terceros(
            periodo_hasta, observaciones, alta_fecha, alta_usr, modi_fecha, modi_usr, numero_ndb)
    VALUES (
    l_periodo_hasta, l_observaciones, localtimestamp, l_usuario, localtimestamp, l_usuario, sec
    );
    
return currval('liquidacion_debitos_terceros_id_seq');
END;
$BODY$;