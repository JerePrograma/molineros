CREATE OR REPLACE FUNCTION buscar_subscribers(IN tratamiento_p character varying, IN nombre_p character varying, IN apellido_p character varying, IN email_p character varying)
  RETURNS TABLE(id_destinatario integer, nombre character varying, apellido character varying, tratamiento character varying, email character varying) AS
$BODY$
select s.id, s.nombre, s.apellido, s.tratamiento, s.email
from mail_subscriber s
where upper(s.tratamiento)= case when $1 is null then upper(s.tratamiento) else upper($1) end
and upper(s.nombre)= case when $2 is null then upper(s.nombre) else upper($2) end
and upper(s.apellido)= case when $3 is null then upper(s.apellido) else upper($3) end
and upper(s.email)= case when $4 is null then upper(s.email) else upper($4) end
and s.baja_fecha is null
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
