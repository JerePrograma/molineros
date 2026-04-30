--create type result_emails as (email varchar)
create or replace function lista_emails_vademecum_amtima()
returns SETOF result_emails AS
$BODY$
BEGIN
return query
select email from amtima_vademecum_mail;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE