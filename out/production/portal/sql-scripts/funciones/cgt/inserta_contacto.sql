CREATE OR REPLACE FUNCTION inserta_contacto(nombre_p character varying, apellido_p character varying, id_cargo_p character varying, telefono_p character varying, email_p character varying, tratamiento_p character varying, id_organismo_p integer, p_user character varying, origen_p integer)
  RETURNS integer AS
$BODY$

BEGIN
INSERT INTO contacto(
            cargo, email, telefono, apellido, nombre, tratamiento, alta_fecha, alta_user, 
            modi_fecha, modi_usr)
values(id_cargo_p, email_p, telefono_p, apellido_p, nombre_p, tratamiento_p, LOCALTIMESTAMP, p_user, LOCALTIMESTAMP, p_user);

if origen_p=1 then
	insert into organismo_contacto(id_organismo, id_contacto ) values (id_organismo_p,currval('contacto_id_seq'));
else 	if origen_p=2 then
		insert into area_contacto(id_area, id_contacto ) values (id_organismo_p,currval('contacto_id_seq'));
	end if;
end if;
return 0;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
