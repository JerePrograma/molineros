CREATE OR REPLACE FUNCTION borrar_acta_no_os_relacionada(p_acta_rel_id integer)
  RETURNS integer AS
$BODY$
BEGIN
delete from acta__no_os_pagos where acta_relacion_id = p_acta_rel_id;
delete from acta__no_os_relacion where id =  p_acta_rel_id;
    
return 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE

