create table reportes_automaticos_configuracion(
	 mail_from character varying(50),
	 pass character varying(50),
	 mails_en_caso_de_error character varying(500)
)

insert into reportes_automaticos_configuracion values ('errores.ospim@gmail.com','eRR0Res!','moreyramj@gmail.com' )