-- SOLO QA12/devmolineros PostgreSQL9.6. ISO-8859-1 sin BOM.
-- CanonesPG96 TEMPdev8322: query22=099173d3ffea37f61109578b54c06a77; OCnuevo=2d4abcf36abcd2f30cc4d5276b352123; OCold=0c2fc772da54e0be2b5763cdef6700d2.
-- REFERENCIA: OCconsulta existente; 7 cuerpos instalados baseline879c/postb71c/fresh42e7.
-- DO unico atomico SIN COMMIT: permite ensayo BEGIN/DO/SELECT/ROLLBACK.
-- No CREATE DATABASE, admin, cache, FK nuevo, CASCADE ni DML de otros campos.
-- Cuatro firmas/clientshape22 se conservan; su OID/returnOID interno cambia al crear VIEW22.
-- ACL NULL/default y atributos se restauran/verifican; defaultsprivilege no soportado aborta.
-- FORWARD: solo elimina copia derivable, preserva otros21campos/documentos.
DO $normalizar_desc_prestador$
DECLARE
 v_rel oid; v_owner oid; v_att smallint; v_has boolean; v_state text;
 v_legacy boolean; v_normal boolean; v_revert boolean; v_count bigint;
 v_shape text[]; v_before text; v_after text; v_rows bigint; v_rows_after bigint;
 v_meta jsonb; v_meta_after jsonb; v_item jsonb; v_acl aclitem[];
 v_rest_before jsonb; v_rest_after jsonb; v_trg_before jsonb; v_trg_after jsonb;
 v_oc_owner oid; v_oc_acl aclitem[]; v_oc_options text[]; v_oc_oid oid;
BEGIN
 PERFORM set_config('search_path','pg_catalog,public',true);
 PERFORM set_config('lock_timeout','2s',true);
 PERFORM set_config('statement_timeout','15s',true);
 IF host(inet_server_addr()) IS DISTINCT FROM '12.1.1.12' OR inet_server_port() IS DISTINCT FROM 5432
    OR current_database() IS DISTINCT FROM 'devmolineros' THEN RAISE EXCEPTION 'DESC_PRESTADOR_QA12_GUARD'; END IF;
 IF current_setting('server_version_num')::integer NOT BETWEEN 90600 AND 99999
    OR current_setting('server_encoding')<>'UTF8' THEN RAISE EXCEPTION 'DESC_PRESTADOR_PG96_UTF8_GUARD'; END IF;
 v_rel:=to_regclass('compras.requerimiento_presupuesto');
 IF v_rel IS NULL THEN RAISE EXCEPTION 'DESC_PRESTADOR_TABLE_MISSING'; END IF;
 LOCK TABLE ONLY compras.requerimiento_presupuesto IN ACCESS EXCLUSIVE MODE;
 LOCK TABLE ONLY public.prestador IN SHARE MODE;
 IF to_regclass('compras.requerimiento_presupuesto')::oid IS DISTINCT FROM v_rel
    OR NOT EXISTS(SELECT 1 FROM pg_class WHERE oid=v_rel AND relkind='r')
    OR EXISTS(SELECT 1 FROM pg_inherits WHERE inhparent=v_rel OR inhrelid=v_rel) THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_RELATION_IDENTITY_DRIFT'; END IF;
 IF NOT EXISTS(SELECT 1 FROM pg_attribute WHERE attrelid='public.prestador'::regclass AND attname='descripcion'
  AND attnum>0 AND NOT attisdropped AND atttypid='character varying'::regtype AND atttypmod BETWEEN 5 AND 504) THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_MASTER_TYPE_COULD_TRUNCATE'; END IF;
 SELECT relowner,relacl INTO v_owner,v_acl FROM pg_class WHERE oid=v_rel;
 IF v_acl IS NOT NULL OR v_owner<>(SELECT oid FROM pg_roles WHERE rolname=current_user)
    OR EXISTS(SELECT 1 FROM pg_default_acl WHERE defaclrole=v_owner AND defaclobjtype IN('f','r')
       AND defaclnamespace IN(0,'compras'::regnamespace::oid)) THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_OWNER_DEFAULT_ACL_DRIFT'; END IF;
 IF (SELECT count(*) FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace WHERE n.nspname='compras')<>104 THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_FUNCTION_SET_DRIFT'; END IF;
 WITH expected(name,nargs,legacy,normal,reverted) AS(VALUES
        ('agregar_gastos_envio_requerimiento',0,'ef9951e6eac71d8301599030962c816e','ef9951e6eac71d8301599030962c816e','ef9951e6eac71d8301599030962c816e'),
        ('anular_requerimiento',3,'842f786d787a3f52519a2f24c34137e2','842f786d787a3f52519a2f24c34137e2','842f786d787a3f52519a2f24c34137e2'),
        ('baja_cotizacion_empresa_requerimiento',3,'35bb9c98f9d914268cc47ea8117c37e4','35bb9c98f9d914268cc47ea8117c37e4','35bb9c98f9d914268cc47ea8117c37e4'),
        ('baja_requerimiento_presupuesto',3,'de0be822f136f7222c1d8c5c8ad19ef7','de0be822f136f7222c1d8c5c8ad19ef7','de0be822f136f7222c1d8c5c8ad19ef7'),
        ('baja_vinculo_reclamo_prestacional',2,'a05aa540deefc8d2a5a93f042b2ca102','a05aa540deefc8d2a5a93f042b2ca102','a05aa540deefc8d2a5a93f042b2ca102'),
        ('baja_vinculo_reclamo_prestacional_trigger',0,'a33c36ade63ab98bceba282309bf283f','a33c36ade63ab98bceba282309bf283f','a33c36ade63ab98bceba282309bf283f'),
        ('bloquear_creacion_orden_compra',2,'c59938a071c4186fb7802dd2536d5130','c59938a071c4186fb7802dd2536d5130','c59938a071c4186fb7802dd2536d5130'),
        ('bloquear_guardado_comparativa',2,'1e5253ce2a4317576a7d5137f9ad9a6a','1e5253ce2a4317576a7d5137f9ad9a6a','1e5253ce2a4317576a7d5137f9ad9a6a'),
        ('bloquear_requerimiento_reclamo_prestacional',1,'fb30b73d6978cd123703b450faa00971','fb30b73d6978cd123703b450faa00971','fb30b73d6978cd123703b450faa00971'),
        ('borrar_requerimiento_detalle',2,'fb4ea6e9004132109c7b0edaae6526dc','fb4ea6e9004132109c7b0edaae6526dc','fb4ea6e9004132109c7b0edaae6526dc'),
        ('buscar_empresas_cotizacion',4,'e0a0e0aa1636f291fc324ff932a74366','e0a0e0aa1636f291fc324ff932a74366','e0a0e0aa1636f291fc324ff932a74366'),
        ('buscar_empresas_cotizacion_rapida',4,'bb70bccf323e3e15247c80f447ead43c','bb70bccf323e3e15247c80f447ead43c','bb70bccf323e3e15247c80f447ead43c'),
        ('buscar_items_historicos_afiliado',5,'ad5522fc7e052cc67072c49f4d68a3f9','ad5522fc7e052cc67072c49f4d68a3f9','ad5522fc7e052cc67072c49f4d68a3f9'),
        ('buscar_items_historicos_afiliado_clasificado',5,'ce9f3b7a1d50fa1dfd14d1822d704a12','ce9f3b7a1d50fa1dfd14d1822d704a12','ce9f3b7a1d50fa1dfd14d1822d704a12'),
        ('buscar_nomencladores',5,'bee2c043de692312a2e0f00fd1ebe52f','bee2c043de692312a2e0f00fd1ebe52f','bee2c043de692312a2e0f00fd1ebe52f'),
        ('buscar_prestadores_enviados',3,'f138c05cafba45dcd6cfb5890c3e907a','f138c05cafba45dcd6cfb5890c3e907a','f138c05cafba45dcd6cfb5890c3e907a'),
        ('buscar_requerimientos',8,'f8e134684474fc1c7123fbb7e6296bb9','f8e134684474fc1c7123fbb7e6296bb9','f8e134684474fc1c7123fbb7e6296bb9'),
        ('buscar_requerimientos',10,'a095c8c9c30fda68d993a9f2091b41b6','a095c8c9c30fda68d993a9f2091b41b6','a095c8c9c30fda68d993a9f2091b41b6'),
        ('buscar_requerimientos',11,'eb1de5b9f53d23d5c1e7725706774019','eb1de5b9f53d23d5c1e7725706774019','eb1de5b9f53d23d5c1e7725706774019'),
        ('calcular_total_detalle_fila',0,'757c0cf62a2b28b0782fdfad56e49d1a','757c0cf62a2b28b0782fdfad56e49d1a','757c0cf62a2b28b0782fdfad56e49d1a'),
        ('cambiar_estado_autorizacion_requerimiento',3,'1ab171219a04755df951722afef718cf','1ab171219a04755df951722afef718cf','1ab171219a04755df951722afef718cf'),
        ('cambiar_estado_requerimiento',3,'053d77b93a30c80250ddaaa32ee40375','053d77b93a30c80250ddaaa32ee40375','053d77b93a30c80250ddaaa32ee40375'),
        ('completar_baja_requerimiento_fila',0,'f9c7d6751c6580ffce70bbb448a15def','f9c7d6751c6580ffce70bbb448a15def','f9c7d6751c6580ffce70bbb448a15def'),
        ('completar_empresa_cotizacion',5,'508f9b051df3a69964cead8eeafcfc97','508f9b051df3a69964cead8eeafcfc97','508f9b051df3a69964cead8eeafcfc97'),
        ('confirmar_envio_a_cotizar',2,'756448bd5d5c0301a5312ad3c652cf00','756448bd5d5c0301a5312ad3c652cf00','756448bd5d5c0301a5312ad3c652cf00'),
        ('confirmar_orden_compra_requerimiento',2,'13b57466874bc1fcfa9b7e6ddcc15615','13b57466874bc1fcfa9b7e6ddcc15615','13b57466874bc1fcfa9b7e6ddcc15615'),
        ('corregir_item_requerimiento',7,'5985c33f228abe9742bfd44e4b2bb308','5985c33f228abe9742bfd44e4b2bb308','5985c33f228abe9742bfd44e4b2bb308'),
        ('corregir_item_requerimiento',9,'b1d6364f89c221819c283b3f07df8161','b1d6364f89c221819c283b3f07df8161','b1d6364f89c221819c283b3f07df8161'),
        ('diagnosticar_prestadores_notificacion_cotizacion',1,'dc616bff8d95f7d33cf8d93fc9ae4912','dc616bff8d95f7d33cf8d93fc9ae4912','dc616bff8d95f7d33cf8d93fc9ae4912'),
        ('editar_empresa_cotizacion',6,'bf9e3d98a5a6b1680ebe290c307783b5','bf9e3d98a5a6b1680ebe290c307783b5','bf9e3d98a5a6b1680ebe290c307783b5'),
        ('es_prestador_compatible_cotizacion',2,'7f43af5b4c6d0f2b950013d8a11f1357','7f43af5b4c6d0f2b950013d8a11f1357','7f43af5b4c6d0f2b950013d8a11f1357'),
        ('es_requerimiento_habilitado_busqueda_empresa_cotizacion',1,'76b9317ba900b6b6f049aa60a3fadada','76b9317ba900b6b6f049aa60a3fadada','76b9317ba900b6b6f049aa60a3fadada'),
        ('es_sector_seleccionable_compras',1,'73392735e21a026359a70aa3792fef8c','73392735e21a026359a70aa3792fef8c','73392735e21a026359a70aa3792fef8c'),
        ('estado_requerimiento_descripcion',1,'2b33274500388fe0ff7f17fa66412000','2b33274500388fe0ff7f17fa66412000','2b33274500388fe0ff7f17fa66412000'),
        ('existe_requerimiento_duplicado',5,'d4007e470fac05388185ddbbaaeba744','d4007e470fac05388185ddbbaaeba744','d4007e470fac05388185ddbbaaeba744'),
        ('finalizar_cotizacion_prestador',4,'ed68fd49233d909972cb03537328143d','ed68fd49233d909972cb03537328143d','ed68fd49233d909972cb03537328143d'),
        ('finalizar_cotizacion_requerimiento',5,'576ce89ef73a78e4782f41070ee8d28d','576ce89ef73a78e4782f41070ee8d28d','576ce89ef73a78e4782f41070ee8d28d'),
        ('finalizar_notificacion_cotizacion_prestador',5,'0938df17a62e92549b7119adc4fd8f17','0938df17a62e92549b7119adc4fd8f17','0938df17a62e92549b7119adc4fd8f17'),
        ('finalizar_reclamo_prestacional',4,'85d827059d69f0b775a54a08f2c0dd8b','85d827059d69f0b775a54a08f2c0dd8b','85d827059d69f0b775a54a08f2c0dd8b'),
        ('get_documento_requerimiento',3,'69b0eb9994fa4bed0fcd50b6a5c8fca6','077f036691e20e7daf4ba9d41234490d','077f036691e20e7daf4ba9d41234490d'),
        ('get_estado_requerimiento',1,'39f5b83a8f978dee8ae399c59882390c','39f5b83a8f978dee8ae399c59882390c','39f5b83a8f978dee8ae399c59882390c'),
        ('get_estado_requerimiento_for_update',1,'30c8bae30ee864c25de6c72abb20312f','30c8bae30ee864c25de6c72abb20312f','30c8bae30ee864c25de6c72abb20312f'),
        ('get_pedido_cotizacion_prestador',2,'6817f2f7a3e8b2b8eddcbcafb2c72f3c','6817f2f7a3e8b2b8eddcbcafb2c72f3c','6817f2f7a3e8b2b8eddcbcafb2c72f3c'),
        ('get_requerimiento',1,'5782a86f969d9233c0f0811eba6463e4','5782a86f969d9233c0f0811eba6463e4','5782a86f969d9233c0f0811eba6463e4'),
        ('get_requerimiento_compra_pdf',1,'767b95efebd6ea379aa0369720426d81','767b95efebd6ea379aa0369720426d81','767b95efebd6ea379aa0369720426d81'),
        ('get_requerimiento_detalle',1,'1700fb0ddf9e83b093ef25c9af2869f7','1700fb0ddf9e83b093ef25c9af2869f7','1700fb0ddf9e83b093ef25c9af2869f7'),
        ('get_requerimiento_detalle_clasificado',1,'784de20c78313b6cfee8cee272103629','784de20c78313b6cfee8cee272103629','784de20c78313b6cfee8cee272103629'),
        ('get_requerimiento_reclamo_prestacional',1,'158603697ae6d70e22525e4f58147606','158603697ae6d70e22525e4f58147606','158603697ae6d70e22525e4f58147606'),
        ('get_sector_requerimiento',1,'58a080205d3b16780933a465be876561','58a080205d3b16780933a465be876561','58a080205d3b16780933a465be876561'),
        ('guardar_cotizacion_requerimiento',7,'e18fdfba2a125ca50d2768bd2da3165a','e18fdfba2a125ca50d2768bd2da3165a','e18fdfba2a125ca50d2768bd2da3165a'),
        ('guardar_cotizacion_requerimiento_call',7,'bf30a550abd3c123e87ba3123538c224','bf30a550abd3c123e87ba3123538c224','bf30a550abd3c123e87ba3123538c224'),
        ('guardar_empresa_adjudicada',3,'440dd79e03cf4d86215eb7f1e29bcbea','440dd79e03cf4d86215eb7f1e29bcbea','440dd79e03cf4d86215eb7f1e29bcbea'),
        ('guardar_empresa_adjudicada',4,'a341e452cca787734788f3a5dc2b376d','a341e452cca787734788f3a5dc2b376d','a341e452cca787734788f3a5dc2b376d'),
        ('guardar_observacion_interna_requerimiento',3,'c3989b8d103ffd6429134349501ea519','c3989b8d103ffd6429134349501ea519','c3989b8d103ffd6429134349501ea519'),
        ('guardar_requerimiento',23,'a1d7abe55a1c5e445cfdade7f33043c5','a1d7abe55a1c5e445cfdade7f33043c5','a1d7abe55a1c5e445cfdade7f33043c5'),
        ('guardar_requerimiento',24,'3534f0f9fd37b28c12fc5dc7ce9a627d','3534f0f9fd37b28c12fc5dc7ce9a627d','3534f0f9fd37b28c12fc5dc7ce9a627d'),
        ('guardar_requerimiento_detalle',13,'6c3444b2fec8410fcd371be1dbba252d','6c3444b2fec8410fcd371be1dbba252d','6c3444b2fec8410fcd371be1dbba252d'),
        ('guardar_requerimiento_detalle',14,'a7a3160273e597cc3da239f3ed42073b','a7a3160273e597cc3da239f3ed42073b','a7a3160273e597cc3da239f3ed42073b'),
        ('guardar_requerimiento_detalle_clasificado',14,'8410268a0168bd694185364759d50da0','8410268a0168bd694185364759d50da0','8410268a0168bd694185364759d50da0'),
        ('hay_prestadores_pendientes_notificacion',1,'31ab2b64e22a0431a69f4a19ab51f4f3','31ab2b64e22a0431a69f4a19ab51f4f3','31ab2b64e22a0431a69f4a19ab51f4f3'),
        ('liberar_copia_cotizacion_requerimiento',1,'d2c7c4dd663b4d3dd5efc9d3eacfb5b3','d2c7c4dd663b4d3dd5efc9d3eacfb5b3','d2c7c4dd663b4d3dd5efc9d3eacfb5b3'),
        ('liberar_reserva_reclamo_prestacional',3,'287e6c51e779e3fef5111a9027e8ada5','287e6c51e779e3fef5111a9027e8ada5','287e6c51e779e3fef5111a9027e8ada5'),
        ('listar_configuracion_correos_rubro',1,'0b7e9085c82040280f19ccc5982a3e4d','0b7e9085c82040280f19ccc5982a3e4d','0b7e9085c82040280f19ccc5982a3e4d'),
        ('listar_documentos_requerimiento',2,'aec6597f9f2c09f12920768d9eb18d18','94f0d196894330a6058421fe8e801862','94f0d196894330a6058421fe8e801862'),
        ('listar_estados_requerimiento',0,'250c9b89009d3ecc7a2bb452eec99dca','250c9b89009d3ecc7a2bb452eec99dca','250c9b89009d3ecc7a2bb452eec99dca'),
        ('listar_ordenes_medicas_requerimiento',1,'eaebb5c40b157bc603b0d7ce6d96f27b','7cc9c2a15760094f900325291e1db922','7cc9c2a15760094f900325291e1db922'),
        ('listar_prestadores_adjudicados',1,'3f67d9087e9f66a0f6daed34c1f31605','3f67d9087e9f66a0f6daed34c1f31605','3f67d9087e9f66a0f6daed34c1f31605'),
        ('listar_prestadores_adjudicados_batch',1,'86c526157c1a4e48d682e3dae2c08a84','86c526157c1a4e48d682e3dae2c08a84','86c526157c1a4e48d682e3dae2c08a84'),
        ('listar_prestadores_cotizacion_requerimiento',1,'a52a168e5040fe53f0855bf41c49543d','a52a168e5040fe53f0855bf41c49543d','a52a168e5040fe53f0855bf41c49543d'),
        ('listar_prestadores_enviados',2,'54b79e68e1fe4444526bf4732d03a4ab','54b79e68e1fe4444526bf4732d03a4ab','54b79e68e1fe4444526bf4732d03a4ab'),
        ('listar_prestadores_notificacion_cotizacion',1,'af825f3ce9c350f358d7ff966cba2b14','af825f3ce9c350f358d7ff966cba2b14','af825f3ce9c350f358d7ff966cba2b14'),
        ('listar_presupuestos_prestador',2,'226ec5ff5752efc1edf39f524007df1b','41803dac550e05f7e5f3bc4cb447f0ae','41803dac550e05f7e5f3bc4cb447f0ae'),
        ('listar_relaciones_reclamo_prestacional_batch',2,'25ed540a2718030f892256bfb31823b7','25ed540a2718030f892256bfb31823b7','25ed540a2718030f892256bfb31823b7'),
        ('listar_relaciones_reclamo_prestacional_por_reclamo',2,'e4b22bdd529f2b19db349609aa23b851','e4b22bdd529f2b19db349609aa23b851','e4b22bdd529f2b19db349609aa23b851'),
        ('listar_sectores_requerimiento',0,'5bfa14c17b9ca95020b6563548d96a8a','5bfa14c17b9ca95020b6563548d96a8a','5bfa14c17b9ca95020b6563548d96a8a'),
        ('listar_tipos_prestacion',0,'be6abb44ae5db1da36ca324cec542903','be6abb44ae5db1da36ca324cec542903','be6abb44ae5db1da36ca324cec542903'),
        ('marcar_error_reclamo_prestacional',5,'a81847272e179c38432ad15f89a6594d','a81847272e179c38432ad15f89a6594d','a81847272e179c38432ad15f89a6594d'),
        ('normalizar_rubro',1,'d5e98bfb3b62ae409f90e54e31d08d6f','d5e98bfb3b62ae409f90e54e31d08d6f','d5e98bfb3b62ae409f90e54e31d08d6f'),
        ('normalizar_sector',1,'281ed4af9cfbe1dba6ae9ae72b487256','281ed4af9cfbe1dba6ae9ae72b487256','281ed4af9cfbe1dba6ae9ae72b487256'),
        ('normalizar_usuario',1,'9f5395581d8c537874902cc27ed46bbd','9f5395581d8c537874902cc27ed46bbd','9f5395581d8c537874902cc27ed46bbd'),
        ('puede_anular_requerimiento',1,'695c34cbb8dedfbd002f628a9df1f6c3','695c34cbb8dedfbd002f628a9df1f6c3','695c34cbb8dedfbd002f628a9df1f6c3'),
        ('reactivar_cotizacion_empresa_requerimiento',2,'6f0856ee10af171578ec9fb53f509fac','6f0856ee10af171578ec9fb53f509fac','6f0856ee10af171578ec9fb53f509fac'),
        ('reactivar_requerimiento_presupuesto',2,'4b8b89f6f40d8cf9099a4e50fa14ccff','4b8b89f6f40d8cf9099a4e50fa14ccff','4b8b89f6f40d8cf9099a4e50fa14ccff'),
        ('registrar_cotizacion_prestador',3,'9dbf33dfcff38b7a544ef9ec450090ca','9dbf33dfcff38b7a544ef9ec450090ca','9dbf33dfcff38b7a544ef9ec450090ca'),
        ('registrar_pedido_cotizacion_documento',10,'ece745d0753e82b23e0e9af92cda5e80','ece745d0753e82b23e0e9af92cda5e80','ece745d0753e82b23e0e9af92cda5e80'),
        ('registrar_requerimiento_orden_medica',11,'9b906b67c31c9dd709592c094db9023d','6b8af2e9db66c649982f895e21296bde','9b906b67c31c9dd709592c094db9023d'),
        ('registrar_requerimiento_presupuesto',11,'865541ee92400fbbd6a5287ae687dbf1','a87961808ad3af29376c6be0dd4d2710','865541ee92400fbbd6a5287ae687dbf1'),
        ('registrar_requerimiento_presupuesto',15,'51657135fb04476a2a8d9fc8d3b311fb','e4fad7c8790ddc14b99c369cab5f697e','51657135fb04476a2a8d9fc8d3b311fb'),
        ('requerimiento_base',0,'e2e8b3b2220471368688ca029772b360','e2e8b3b2220471368688ca029772b360','e2e8b3b2220471368688ca029772b360'),
        ('reservar_copia_cotizacion_requerimiento',1,'b92ec038c530eee6742bceabf7041913','b92ec038c530eee6742bceabf7041913','b92ec038c530eee6742bceabf7041913'),
        ('reservar_notificacion_cotizacion_prestador',3,'0920a72525c6760f2b6a50027979dd07','0920a72525c6760f2b6a50027979dd07','0920a72525c6760f2b6a50027979dd07'),
        ('reservar_reclamo_prestacional',3,'1fd9d9739ae15ad285f5ab326e405366','1fd9d9739ae15ad285f5ab326e405366','1fd9d9739ae15ad285f5ab326e405366'),
        ('resolver_email_cotizacion_prestador',1,'279d952a28dd67cb35efd211aa3e918f','279d952a28dd67cb35efd211aa3e918f','279d952a28dd67cb35efd211aa3e918f'),
        ('resolver_emails_cotizacion_prestador',1,'a2550cd264666ed0cfdf593d38807fd6','a2550cd264666ed0cfdf593d38807fd6','a2550cd264666ed0cfdf593d38807fd6'),
        ('resolver_sector_nomenclador',2,'c365367fa56bfbbd71229c0838016e4f','c365367fa56bfbbd71229c0838016e4f','c365367fa56bfbbd71229c0838016e4f'),
        ('tiene_situacion_medica_vigente',2,'3c6bdf4fe700eab54af7634ab2e95294','3c6bdf4fe700eab54af7634ab2e95294','3c6bdf4fe700eab54af7634ab2e95294'),
        ('trae_tipos_prestacion',0,'b39a6bd78b43780531bff9071ae2765a','b39a6bd78b43780531bff9071ae2765a','b39a6bd78b43780531bff9071ae2765a'),
        ('validar_codigo_prestacion_detalle_fila',0,'ecd4d086066dc445e06d1e8152d57966','ecd4d086066dc445e06d1e8152d57966','ecd4d086066dc445e06d1e8152d57966'),
        ('validar_nomenclador_configurado',4,'2ada9393c89315cc86b02d83ac7efdf0','2ada9393c89315cc86b02d83ac7efdf0','2ada9393c89315cc86b02d83ac7efdf0'),
        ('validar_prestador_cotizacion_fila',0,'f6dad299d043abfc32d1957fed4a933d','f6dad299d043abfc32d1957fed4a933d','f6dad299d043abfc32d1957fed4a933d'),
        ('validar_requerimiento_detalle_fila',0,'bf28f6bca88baf59fc899be4c2e82635','bf28f6bca88baf59fc899be4c2e82635','bf28f6bca88baf59fc899be4c2e82635'),
        ('validar_requerimiento_fila',0,'aa053cab087a1a0852f340775bd09caa','aa053cab087a1a0852f340775bd09caa','aa053cab087a1a0852f340775bd09caa'),
        ('validar_tipo_prestacion_detalle_fila',0,'5b5611c7997f745cffe707493efb188b','5b5611c7997f745cffe707493efb188b','5b5611c7997f745cffe707493efb188b'),
        ('validar_tipo_prestacion_detalle_nuevo',0,'2d898521c5e3924ec61a431c1bbeb775','2d898521c5e3924ec61a431c1bbeb775','2d898521c5e3924ec61a431c1bbeb775')
 ), actual AS(SELECT e.*,count(p.oid) AS n,max(md5(replace(pg_get_functiondef(p.oid),E'\r\n',E'\n'))) AS actual
  FROM expected e LEFT JOIN pg_proc p ON p.pronamespace='compras'::regnamespace AND p.proname=e.name AND p.pronargs=e.nargs
  GROUP BY e.name,e.nargs,e.legacy,e.normal,e.reverted)
 SELECT bool_and(n=1 AND actual=legacy),bool_and(n=1 AND actual=normal),bool_and(n=1 AND actual=reverted)
 INTO v_legacy,v_normal,v_revert FROM actual;
 SELECT attnum INTO v_att FROM pg_attribute WHERE attrelid=v_rel AND attname='descripcion_prestador'AND attnum>0 AND NOT attisdropped;
 v_has:=v_att IS NOT NULL;
 SELECT array_agg(attname||':'||format_type(atttypid,atttypmod) ORDER BY attnum) INTO v_shape
 FROM pg_attribute WHERE attrelid=v_rel AND attnum>0 AND NOT attisdropped;
 IF v_has AND v_legacy AND v_shape=ARRAY['id_requerimiento_presupuesto:integer','id_requerimiento:integer','tipo_documento:smallint','fecha_documento:date','numero_receta:character varying(100)','id_prestador:integer','empresa_cuit:character varying(11)','empresa_sucursal:character varying(6)','descripcion_empresa:character varying(200)','dl_group_id:bigint','dl_folder_id:bigint','dl_file_entry_id:bigint','dl_file_uuid:character varying(75)','nombre_original:character varying(255)','nombre_persistido:character varying(255)','titulo:character varying(240)','descripcion_prestador:character varying(500)','alta_fecha:timestamp without time zone','alta_usr:character varying(100)','baja_fecha:timestamp without time zone','baja_usr:character varying(100)','empresa_adjudicada:boolean']::text[] THEN v_state:='legacy';
 ELSIF NOT v_has AND v_normal AND v_shape=ARRAY['id_requerimiento_presupuesto:integer','id_requerimiento:integer','tipo_documento:smallint','fecha_documento:date','numero_receta:character varying(100)','id_prestador:integer','empresa_cuit:character varying(11)','empresa_sucursal:character varying(6)','descripcion_empresa:character varying(200)','dl_group_id:bigint','dl_folder_id:bigint','dl_file_entry_id:bigint','dl_file_uuid:character varying(75)','nombre_original:character varying(255)','nombre_persistido:character varying(255)','titulo:character varying(240)','alta_fecha:timestamp without time zone','alta_usr:character varying(100)','baja_fecha:timestamp without time zone','baja_usr:character varying(100)','empresa_adjudicada:boolean']::text[] THEN v_state:='normal';
 ELSIF v_has AND v_revert AND v_shape=ARRAY['id_requerimiento_presupuesto:integer','id_requerimiento:integer','tipo_documento:smallint','fecha_documento:date','numero_receta:character varying(100)','id_prestador:integer','empresa_cuit:character varying(11)','empresa_sucursal:character varying(6)','descripcion_empresa:character varying(200)','dl_group_id:bigint','dl_folder_id:bigint','dl_file_entry_id:bigint','dl_file_uuid:character varying(75)','nombre_original:character varying(255)','nombre_persistido:character varying(255)','titulo:character varying(240)','alta_fecha:timestamp without time zone','alta_usr:character varying(100)','baja_fecha:timestamp without time zone','baja_usr:character varying(100)','empresa_adjudicada:boolean','descripcion_prestador:character varying(500)']::text[] THEN v_state:='reverted';
 ELSE RAISE EXCEPTION 'DESC_PRESTADOR_STATE_FUNCTION_OR_COLUMNS_DRIFT'; END IF;
 IF (SELECT count(*) FROM pg_constraint WHERE conrelid=v_rel)<>3
    OR EXISTS(SELECT 1 FROM pg_constraint WHERE conrelid=v_rel AND contype NOT IN('p','u','f')) THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_CONSTRAINT_DRIFT'; END IF;
 IF (SELECT count(*) FROM pg_index WHERE indrelid=v_rel)<>8 THEN RAISE EXCEPTION 'DESC_PRESTADOR_INDEX_DRIFT'; END IF;
 SELECT oid,relowner,relacl,reloptions INTO v_oc_oid,v_oc_owner,v_oc_acl,v_oc_options FROM pg_class
 WHERE oid=to_regclass('compras.requerimiento_orden_compra_consulta') AND relkind='v';
 IF v_oc_oid IS NULL OR md5(replace(pg_get_viewdef(v_oc_oid,true),E'\r\n',E'\n')) IS DISTINCT FROM CASE WHEN v_state='normal' THEN '2d4abcf36abcd2f30cc4d5276b352123' ELSE '0c2fc772da54e0be2b5763cdef6700d2' END THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_OC_VIEW_DRIFT'; END IF;
 IF v_state<>'legacy' THEN
  IF to_regclass('compras.requerimiento_presupuesto_consulta') IS NULL
     OR md5(replace(pg_get_viewdef('compras.requerimiento_presupuesto_consulta'::regclass,true),E'\r\n',E'\n')) IS DISTINCT FROM '099173d3ffea37f61109578b54c06a77' THEN
   RAISE EXCEPTION 'DESC_PRESTADOR_QUERY_VIEW_DRIFT'; END IF;
  SELECT array_agg(attname||':'||format_type(atttypid,atttypmod) ORDER BY attnum) INTO v_shape
  FROM pg_attribute WHERE attrelid='compras.requerimiento_presupuesto_consulta'::regclass AND attnum>0 AND NOT attisdropped;
  IF v_shape<>ARRAY['id_requerimiento_presupuesto:integer','id_requerimiento:integer','tipo_documento:smallint','fecha_documento:date','numero_receta:character varying(100)','id_prestador:integer','empresa_cuit:character varying(11)','empresa_sucursal:character varying(6)','descripcion_empresa:character varying(200)','dl_group_id:bigint','dl_folder_id:bigint','dl_file_entry_id:bigint','dl_file_uuid:character varying(75)','nombre_original:character varying(255)','nombre_persistido:character varying(255)','titulo:character varying(240)','descripcion_prestador:character varying(500)','alta_fecha:timestamp without time zone','alta_usr:character varying(100)','baja_fecha:timestamp without time zone','baja_usr:character varying(100)','empresa_adjudicada:boolean']::text[] THEN RAISE EXCEPTION 'DESC_PRESTADOR_CLIENT_SHAPE_DRIFT'; END IF;
  IF NOT EXISTS(SELECT 1 FROM pg_class c JOIN pg_type t ON t.oid=c.reltype WHERE c.oid='compras.requerimiento_presupuesto_consulta'::regclass AND c.relkind='v'AND c.relowner=v_owner AND c.relacl IS NULL AND c.reloptions IS NULL AND t.typowner=v_owner AND t.typacl IS NULL) THEN
   RAISE EXCEPTION 'DESC_PRESTADOR_QUERY_OWNER_ACL_OPTIONS_DRIFT'; END IF;
 ELSIF to_regclass('compras.requerimiento_presupuesto_consulta') IS NOT NULL THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_UNEXPECTED_QUERY_VIEW'; END IF;
 SELECT jsonb_agg(jsonb_build_object('oid',p.oid,'owner',p.proowner,'acl',p.proacl::text,'config',p.proconfig,'volatile',p.provolatile,'strict',p.proisstrict,'leak',p.proleakproof,'parallel',p.proparallel,'cost',p.procost,'rows',p.prorows,'security',p.prosecdef) ORDER BY p.oid) INTO v_rest_before FROM pg_proc p WHERE p.pronamespace='compras'::regnamespace AND (p.proname,p.pronargs) NOT IN(('registrar_requerimiento_orden_medica',11),('registrar_requerimiento_presupuesto',11),('registrar_requerimiento_presupuesto',15),('get_documento_requerimiento',3),('listar_documentos_requerimiento',2),('listar_ordenes_medicas_requerimiento',1),('listar_presupuestos_prestador',2));
 SELECT jsonb_agg(jsonb_build_object('oid',t.oid,'function',t.tgfoid,'enabled',t.tgenabled,'definition',pg_get_triggerdef(t.oid)) ORDER BY t.oid) INTO v_trg_before FROM pg_trigger t JOIN pg_class c ON c.oid=t.tgrelid JOIN pg_namespace n ON n.oid=c.relnamespace JOIN pg_proc p ON p.oid=t.tgfoid JOIN pg_namespace pn ON pn.oid=p.pronamespace WHERE NOT t.tgisinternal AND(n.nspname='compras'OR pn.nspname='compras');
 IF coalesce(jsonb_array_length(v_trg_before),0)<>5 THEN RAISE EXCEPTION 'DESC_PRESTADOR_TRIGGER_COUNT_DRIFT'; END IF;
 WITH expected(schema_name,table_name,name,hash)AS(VALUES ('autorizaciones','reclamos_prestacionales','trg_compras_baja_vinculo_reclamo','9238c37e7b7745fe7e9d049bb3fd5183'),('compras','requerimiento','trg_compras_requerimiento_completar_baja','d08e74884d3e9f56c5d1c95e2e8ccdf6'),('compras','requerimiento','trg_compras_requerimiento_gastos_envio','b788c73806a48ead0a4d732108b6fc69'),('compras','requerimiento_detalle','tr_compras_detalle_codigo_prestacion','2439e7d743aeff6522b705306e46210d'),('compras','requerimiento_detalle','trg_compras_detalle_calcular_total','71a3dd1695448c2fe75421aeec6e06b4')) SELECT count(*) INTO v_count FROM expected e JOIN pg_namespace n ON n.nspname=e.schema_name JOIN pg_class c ON c.relnamespace=n.oid AND c.relname=e.table_name JOIN pg_trigger t ON t.tgrelid=c.oid AND t.tgname=e.name WHERE NOT t.tgisinternal AND t.tgenabled='O'AND md5(pg_get_triggerdef(t.oid))=e.hash;
 IF v_count<>5 THEN RAISE EXCEPTION 'DESC_PRESTADOR_FIVE_TRIGGERS_DRIFT'; END IF;
 SELECT count(*),md5(coalesce(string_agg(md5((to_jsonb(rp)-'descripcion_prestador')::text),''ORDER BY md5((to_jsonb(rp)-'descripcion_prestador')::text)),''))
 INTO v_rows,v_before FROM compras.requerimiento_presupuesto rp;
 SELECT jsonb_agg(jsonb_build_object('name',p.proname,'types',oidvectortypes(p.proargtypes),'owner',pg_get_userbyid(p.proowner),'acl',p.proacl::text,'config',p.proconfig,'volatile',p.provolatile,'strict',p.proisstrict,'leak',p.proleakproof,'parallel',p.proparallel,'cost',p.procost,'rows',p.prorows,'security',p.prosecdef,'names',p.proargnames,'modes',p.proargmodes,'allargs',p.proallargtypes,'defaults',p.proargdefaults::text,'comment',obj_description(p.oid,'pg_proc')) ORDER BY p.proname) INTO v_meta FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace WHERE n.nspname='compras' AND p.proname IN('get_documento_requerimiento','listar_documentos_requerimiento','listar_ordenes_medicas_requerimiento','listar_presupuestos_prestador');
 IF EXISTS(SELECT 1 FROM pg_proc p WHERE p.pronamespace='compras'::regnamespace AND p.proname IN('get_documento_requerimiento','listar_documentos_requerimiento','listar_ordenes_medicas_requerimiento','listar_presupuestos_prestador')
  AND(p.proacl IS NOT NULL OR p.proconfig IS NOT NULL OR p.provolatile<>'s'OR p.prosecdef OR p.proisstrict OR p.proleakproof
      OR p.proparallel<>'u'OR p.procost<>100 OR p.prorows<>1000 OR p.pronargdefaults<>0)) THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_READER_ATTRIBUTES_DRIFT'; END IF;
 IF v_state='normal' THEN RETURN; END IF;
 IF EXISTS(SELECT 1 FROM compras.requerimiento_presupuesto rp LEFT JOIN public.prestador p ON p.id_prestador=rp.id_prestador
 WHERE(rp.tipo_documento=1 AND(rp.id_prestador IS NULL OR rp.id_prestador<=0 OR p.id_prestador IS NULL))
 OR(rp.tipo_documento<>1 AND(rp.id_prestador IS NOT NULL OR rp.descripcion_prestador IS NOT NULL))) THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_FREE_TEXT_OR_ORPHAN'; END IF;
 IF EXISTS(WITH chars AS(SELECT string_agg(chr(i),''ORDER BY i)AS trims FROM generate_series(1,32)i), data AS(
 SELECT rp.descripcion_prestador AS stored,p.descripcion AS raw,nullif(btrim(p.descripcion::text,chars.trims),'')AS d,nullif(btrim(p.cuit::text,chars.trims),'')AS c
 FROM compras.requerimiento_presupuesto rp JOIN public.prestador p ON p.id_prestador=rp.id_prestador CROSS JOIN chars
 WHERE rp.tipo_documento=1 AND rp.descripcion_prestador IS NOT NULL)
 SELECT 1 FROM data WHERE stored IS DISTINCT FROM raw AND CASE WHEN d IS NOT NULL AND octet_length(d)<>char_length(d)THEN true
 ELSE stored::text IS DISTINCT FROM CASE WHEN d IS NULL THEN coalesce(c,'')WHEN c IS NULL THEN upper(d COLLATE "C")ELSE upper(d COLLATE "C")||' - '||c END END)THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_COPY_NOT_DERIVED_ASCII_OR_RAW'; END IF;
 IF v_state='legacy' AND EXISTS(SELECT 1 FROM pg_depend d JOIN pg_proc p ON d.refclassid='pg_proc'::regclass AND d.refobjid=p.oid WHERE p.pronamespace='compras'::regnamespace AND p.proname IN('get_documento_requerimiento','listar_documentos_requerimiento','listar_ordenes_medicas_requerimiento','listar_presupuestos_prestador'))THEN RAISE EXCEPTION 'DESC_PRESTADOR_READERS_HAVE_DEPENDENTS'; END IF;
 EXECUTE $querycreate$CREATE OR REPLACE VIEW compras.requerimiento_presupuesto_consulta AS
SELECT rp.id_requerimiento_presupuesto,
    rp.id_requerimiento,
    rp.tipo_documento,
    rp.fecha_documento,
    rp.numero_receta,
    rp.id_prestador,
    rp.empresa_cuit,
    rp.empresa_sucursal,
    rp.descripcion_empresa,
    rp.dl_group_id,
    rp.dl_folder_id,
    rp.dl_file_entry_id,
    rp.dl_file_uuid,
    rp.nombre_original,
    rp.nombre_persistido,
    rp.titulo,
    CASE WHEN rp.id_prestador IS NULL THEN NULL::character varying WHEN p.id_prestador IS NULL THEN '[PRESTADOR NO RESUELTO]'::character varying ELSE p.descripcion END::character varying(500) AS descripcion_prestador,
    rp.alta_fecha,
    rp.alta_usr,
    rp.baja_fecha,
    rp.baja_usr,
    rp.empresa_adjudicada
FROM compras.requerimiento_presupuesto rp
LEFT JOIN public.prestador p ON p.id_prestador = rp.id_prestador;$querycreate$;
 EXECUTE format('ALTER VIEW compras.requerimiento_presupuesto_consulta OWNER TO %I',pg_get_userbyid(v_owner));
 IF v_state='legacy' THEN
 FOR v_item IN SELECT * FROM jsonb_array_elements(v_meta) LOOP EXECUTE format('DROP FUNCTION compras.%I(%s) RESTRICT',v_item->>'name',v_item->>'types'); END LOOP;
 END IF;
 EXECUTE $def_0$CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_orden_medica(p_id_requerimiento integer, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_fecha_documento date, p_numero_receta character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_cuil_titular VARCHAR(20);
    v_inte INTEGER;
    v_numero_receta VARCHAR;
    v_id_requerimiento_duplicado INTEGER;
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL
       OR p_id_requerimiento <= 0 THEN

        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_fecha_documento IS NULL THEN
        RAISE EXCEPTION
            'Debe informar la fecha del adjunto.';
    END IF;

    IF p_dl_group_id IS NULL
       OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL
       OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL
       OR p_dl_file_entry_id <= 0
       OR NULLIF(btrim(p_dl_file_uuid), '') IS NULL THEN

        RAISE EXCEPTION
            'La identidad del adjunto no es válida.';
    END IF;

    IF NULLIF(btrim(p_nombre_original), '') IS NULL
       OR NULLIF(btrim(p_nombre_persistido), '') IS NULL THEN

        RAISE EXCEPTION
            'Los nombres del adjunto no son válidos.';
    END IF;

    /*
     * p_titulo es opcional y se conserva en la firma para no romper callers.
     * Si llega vacío se utiliza el nombre original como título técnico.
     */

    IF p_numero_receta IS NOT NULL
       AND length(p_numero_receta) > 100 THEN

        RAISE EXCEPTION
            'El número de receta admite hasta 100 caracteres.';
    END IF;

    v_numero_receta :=
        NULLIF(
            regexp_replace(
                upper(btrim(COALESCE(p_numero_receta, ''))),
                '[[:space:]]+',
                '',
                'g'
            ),
            ''
        );

    IF v_numero_receta IS NOT NULL
       AND length(v_numero_receta) > 100 THEN

        RAISE EXCEPTION
            'El número de receta admite hasta 100 caracteres.';
    END IF;

    v_usuario :=
        COALESCE(
            NULLIF(btrim(p_usuario), ''),
            'sistema'
        );

    SELECT
        r.estado,
        r.afiliado_cuil_titular,
        r.afiliado_int
    INTO
        v_estado_requerimiento,
        v_cuil_titular,
        v_inte
    FROM compras.requerimiento r
    WHERE r.id_requerimiento = p_id_requerimiento
      AND r.baja_fecha IS NULL
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No existe el requerimiento activo informado.';
    END IF;

    IF v_estado_requerimiento <> 1 THEN
        RAISE EXCEPTION
            'El adjunto solo puede registrarse durante '
            'el alta de un requerimiento PENDIENTE.';
    END IF;

    /*
     * La receta es opcional. Si se informa y el requerimiento tiene
     * afiliado, el advisory lock serializa la clave funcional antes
     * de revalidar el duplicado exacto. Las colisiones del hash solo
     * amplían la serialización: nunca producen un falso duplicado.
     */
    IF v_numero_receta IS NOT NULL
       AND NULLIF(btrim(v_cuil_titular), '') IS NOT NULL
       AND v_inte IS NOT NULL THEN

        PERFORM pg_advisory_xact_lock(
            hashtext('compras.orden_medica.receta'),
            hashtext(
                v_cuil_titular
                || chr(31)
                || v_inte::VARCHAR
                || chr(31)
                || to_char(p_fecha_documento, 'YYYY-MM-DD')
                || chr(31)
                || v_numero_receta
            )
        );

        SELECT rp.id_requerimiento
        INTO v_id_requerimiento_duplicado
        FROM compras.requerimiento_presupuesto rp
        INNER JOIN compras.requerimiento r
            ON r.id_requerimiento = rp.id_requerimiento
        WHERE rp.tipo_documento = 2
          AND rp.baja_fecha IS NULL
          AND rp.numero_receta = v_numero_receta
          AND rp.fecha_documento = p_fecha_documento
          AND r.estado <> 99
          AND r.afiliado_cuil_titular = v_cuil_titular
          AND r.afiliado_int = v_inte
        ORDER BY
            rp.id_requerimiento,
            rp.id_requerimiento_presupuesto
        LIMIT 1;

        IF FOUND THEN
            RAISE EXCEPTION
                'El adjunto ya fue cargado con fecha % y número de receta % en el requerimiento %.',
                to_char(p_fecha_documento, 'DD-MM-YYYY'),
                v_numero_receta,
                v_id_requerimiento_duplicado;
        END IF;
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
        tipo_documento,
        fecha_documento,
        numero_receta,
        id_prestador,
        dl_group_id,
        dl_folder_id,
        dl_file_entry_id,
        dl_file_uuid,
        nombre_original,
        nombre_persistido,
        titulo,
        alta_usr
    )
    VALUES (
        p_id_requerimiento,
        2,
        p_fecha_documento,
        v_numero_receta,
        NULL,
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        btrim(p_dl_file_uuid),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        COALESCE(
            NULLIF(btrim(p_titulo), ''),
            btrim(p_nombre_original)
        ),
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    RETURN v_id;
END;
$function$
$def_0$;
 EXECUTE $def_1$CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(p_id_requerimiento integer, p_id_prestador integer, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_descripcion_prestador character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_estado_envio VARCHAR(20);
    v_usuario VARCHAR(100);
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION
            'El requerimiento informado no es válido.';
    END IF;

    IF p_id_prestador IS NULL OR p_id_prestador <= 0 THEN
        RAISE EXCEPTION
            'El prestador informado no es válido.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN
        RAISE EXCEPTION
            'La identidad del documento de presupuesto no es válida.';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT r.estado
      INTO v_estado_requerimiento
      FROM compras.requerimiento r
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE;

    IF NOT FOUND OR v_estado_requerimiento <> 2 THEN
        RAISE EXCEPTION
            'El requerimiento no se encuentra activo y en estado A COTIZAR.';
    END IF;

    SELECT rcp.estado_envio
      INTO v_estado_envio
      FROM compras.requerimiento_cotizacion_prestador rcp
     WHERE rcp.id_requerimiento = p_id_requerimiento
       AND rcp.id_prestador = p_id_prestador
     FOR UPDATE;

    IF NOT FOUND OR v_estado_envio <> 'ENVIADO' THEN
        RAISE EXCEPTION
            'El prestador no se encuentra ENVIADO y disponible para cotizar.';
    END IF;

    IF EXISTS (
        SELECT 1
          FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 1
           AND rp.id_prestador = p_id_prestador
           AND rp.baja_fecha IS NULL
    ) THEN
        RAISE EXCEPTION
            'El prestador ya tiene un presupuesto activo para este requerimiento.';
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento,
        tipo_documento,
        fecha_documento,
        id_prestador,
        dl_group_id,
        dl_folder_id,
        dl_file_entry_id,
        dl_file_uuid,
        nombre_original,
        nombre_persistido,
        titulo,
        alta_usr
    )
    VALUES (
        p_id_requerimiento,
        1,
        NULL,
        p_id_prestador,
        p_dl_group_id,
        p_dl_folder_id,
        p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''),
        btrim(p_nombre_original),
        btrim(p_nombre_persistido),
        btrim(p_titulo),
        v_usuario
    )
    RETURNING id_requerimiento_presupuesto
    INTO v_id;

    UPDATE compras.requerimiento_cotizacion_prestador
       SET estado_envio = 'COTIZADO',
           modi_fecha = now(),
           modi_usr = v_usuario
     WHERE id_requerimiento = p_id_requerimiento
       AND id_prestador = p_id_prestador
       AND estado_envio = 'ENVIADO';

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'No se pudo marcar como COTIZADO al prestador del presupuesto.';
    END IF;

    RETURN v_id;
END;
$function$
$def_1$;
 EXECUTE $def_2$CREATE OR REPLACE FUNCTION compras.registrar_requerimiento_presupuesto(p_id_requerimiento integer, p_tipo_documento smallint, p_id_prestador integer, p_empresa_cuit character varying, p_empresa_sucursal character varying, p_descripcion_empresa character varying, p_dl_group_id bigint, p_dl_folder_id bigint, p_dl_file_entry_id bigint, p_dl_file_uuid character varying, p_nombre_original character varying, p_nombre_persistido character varying, p_titulo character varying, p_descripcion_prestador character varying, p_usuario character varying)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id INTEGER;
    v_estado_requerimiento INTEGER;
    v_sector_descripcion VARCHAR(200);
    v_usuario VARCHAR(100);
    v_cuit VARCHAR;
    v_sucursal VARCHAR;
BEGIN
    IF p_id_requerimiento IS NULL OR p_id_requerimiento <= 0 THEN
        RAISE EXCEPTION 'El requerimiento informado no es valido.';
    END IF;

    IF p_tipo_documento IS NULL OR p_tipo_documento <> 3 THEN
        RAISE EXCEPTION
            'El tipo documental no corresponde a una cotizacion de Empresa.';
    END IF;

    IF p_id_prestador IS NOT NULL
       OR NULLIF(btrim(p_descripcion_prestador), '') IS NOT NULL THEN
        RAISE EXCEPTION
            'Una cotizacion de Empresa no puede asociarse a un prestador.';
    END IF;

    IF NULLIF(btrim(p_descripcion_empresa), '') IS NULL
       OR length(btrim(p_descripcion_empresa)) > 200 THEN
        RAISE EXCEPTION 'Debe informar el nombre de la Empresa de la cotizacion.';
    END IF;

    v_cuit := NULLIF(btrim(p_empresa_cuit), '');
    v_sucursal := NULLIF(btrim(p_empresa_sucursal), '');
    IF v_cuit IS NOT NULL AND v_cuit !~ '^[0-9]{11}$' THEN
        RAISE EXCEPTION 'El CUIT de la Empresa de la cotizacion no es valido.';
    END IF;
    IF v_sucursal IS NOT NULL
       AND (v_cuit IS NULL OR length(v_sucursal) > 6) THEN
        RAISE EXCEPTION 'La sucursal requiere un CUIT valido de Empresa.';
    END IF;

    IF p_dl_group_id IS NULL OR p_dl_group_id <= 0
       OR p_dl_folder_id IS NULL OR p_dl_folder_id < 0
       OR p_dl_file_entry_id IS NULL OR p_dl_file_entry_id <= 0 THEN
        RAISE EXCEPTION 'La identidad del documento de cotizacion no es valida.';
    END IF;

    v_usuario := COALESCE(NULLIF(btrim(p_usuario), ''), 'sistema');

    SELECT r.estado, sr.descripcion
      INTO v_estado_requerimiento, v_sector_descripcion
      FROM compras.requerimiento r
      JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector
     WHERE r.id_requerimiento = p_id_requerimiento
       AND r.baja_fecha IS NULL
     FOR UPDATE OF r;

    IF NOT FOUND OR v_estado_requerimiento <> 1
       OR compras.normalizar_sector(v_sector_descripcion)
            NOT IN ('RRHH', 'SISTEMAS') THEN
        RAISE EXCEPTION
            'La cotizacion de Empresa requiere un requerimiento activo de RRHH o SISTEMAS en estado PENDIENTE.';
    END IF;

    -- La sucursal identifica una vinculacion al padron; el CUIT solo puede
    -- informarse sin dar de alta ni seleccionar una Empresa maestra.
    IF v_sucursal IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM compras.buscar_empresas_cotizacion(
                v_cuit, NULL, v_sucursal, 1)
    ) THEN
        RAISE EXCEPTION 'La Empresa no existe o no esta activa en el padron.';
    END IF;

    IF v_cuit IS NOT NULL AND EXISTS (
        SELECT 1 FROM compras.requerimiento_presupuesto rp
         WHERE rp.id_requerimiento = p_id_requerimiento
           AND rp.tipo_documento = 3 AND rp.baja_fecha IS NULL
           AND rp.empresa_cuit = v_cuit
           AND COALESCE(rp.empresa_sucursal, '') = COALESCE(v_sucursal, '')
    ) THEN
        RAISE EXCEPTION
            'La Empresa ya tiene una cotizacion activa para este requerimiento.';
    END IF;

    INSERT INTO compras.requerimiento_presupuesto (
        id_requerimiento, tipo_documento, fecha_documento, id_prestador,
        empresa_cuit, empresa_sucursal, descripcion_empresa,
        dl_group_id, dl_folder_id, dl_file_entry_id, dl_file_uuid,
        nombre_original, nombre_persistido, titulo, alta_usr
    ) VALUES (
        p_id_requerimiento, 3, NULL, NULL,
        v_cuit, v_sucursal, btrim(p_descripcion_empresa),
        p_dl_group_id, p_dl_folder_id, p_dl_file_entry_id,
        NULLIF(btrim(p_dl_file_uuid), ''), btrim(p_nombre_original),
        btrim(p_nombre_persistido), btrim(p_titulo), v_usuario
    )
    RETURNING id_requerimiento_presupuesto INTO v_id;

    RETURN v_id;
END;
$function$
$def_2$;
 EXECUTE $def_3$CREATE OR REPLACE FUNCTION compras.get_documento_requerimiento(p_id_requerimiento_presupuesto integer, p_id_requerimiento integer, p_tipo_documento integer)
 RETURNS SETOF compras.requerimiento_presupuesto_consulta
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.*
    FROM compras.requerimiento_presupuesto_consulta rp
    WHERE rp.id_requerimiento_presupuesto = $1
      AND rp.id_requerimiento = $2
      AND rp.tipo_documento = $3
      AND rp.baja_fecha IS NULL;
$function$
$def_3$;
 EXECUTE $def_4$CREATE OR REPLACE FUNCTION compras.listar_documentos_requerimiento(p_id_requerimiento integer, p_tipo_documento integer)
 RETURNS SETOF compras.requerimiento_presupuesto_consulta
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.*
    FROM compras.requerimiento_presupuesto_consulta rp
    WHERE rp.id_requerimiento = $1
      AND rp.tipo_documento = $2
      AND rp.baja_fecha IS NULL
    ORDER BY
        rp.alta_fecha DESC,
        rp.id_requerimiento_presupuesto DESC;
$function$
$def_4$;
 EXECUTE $def_5$CREATE OR REPLACE FUNCTION compras.listar_ordenes_medicas_requerimiento(p_id_requerimiento integer)
 RETURNS SETOF compras.requerimiento_presupuesto_consulta
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.*
    FROM compras.requerimiento_presupuesto_consulta rp
    WHERE rp.id_requerimiento = $1
      AND rp.tipo_documento = 2
      AND rp.baja_fecha IS NULL;
$function$
$def_5$;
 EXECUTE $def_6$CREATE OR REPLACE FUNCTION compras.listar_presupuestos_prestador(p_id_requerimiento integer, p_id_prestador integer)
 RETURNS SETOF compras.requerimiento_presupuesto_consulta
 LANGUAGE sql
 STABLE
AS $function$
    SELECT rp.*
    FROM compras.requerimiento_presupuesto_consulta rp
    WHERE rp.id_requerimiento = $1
      AND rp.id_prestador = $2
      AND rp.tipo_documento = 1
      AND rp.baja_fecha IS NULL
    ORDER BY rp.id_requerimiento_presupuesto;
$function$
$def_6$;
 FOR v_item IN SELECT * FROM jsonb_array_elements(v_meta) LOOP
  EXECUTE format('ALTER FUNCTION compras.%I(%s) OWNER TO %I',v_item->>'name',v_item->>'types',v_item->>'owner');
  IF v_item->>'comment' IS NOT NULL THEN EXECUTE format('COMMENT ON FUNCTION compras.%I(%s) IS %L',v_item->>'name',v_item->>'types',v_item->>'comment'); END IF;
 END LOOP;
 EXECUTE $occreate$CREATE OR REPLACE VIEW compras.requerimiento_orden_compra_consulta AS
 SELECT o.id_orden_compra,
    o.id_requerimiento,
    o.id_presupuesto,
    o.fecha_emision,
    om.fecha AS fecha_orden_medica,
    NULL::date AS fecha_recepcion,
    'ARS'::character varying(10) AS moneda,
        CASE
            WHEN rp.tipo_documento = 3 THEN rp.descripcion_empresa::text
            ELSE p.descripcion::text
        END::character varying AS proveedor,
        CASE
            WHEN rp.tipo_documento = 3 THEN COALESCE(rp.empresa_cuit, ''::character varying)
            ELSE COALESCE(p.cuit, ''::character varying)
        END AS proveedor_cuit,
    NULL::character varying AS proveedor_domicilio,
    NULL::character varying AS proveedor_contacto,
    NULL::character varying AS proveedor_email,
    NULL::character varying AS proveedor_telefono,
    o.condicion,
    o.forma_pago,
    o.plazo_pago,
    o.entrega,
    o.observaciones,
    o.alta_usr AS confeccionado_por,
    NULL::character varying AS telefono_compras,
    r.alta_usr AS solicitante,
    NULL::character varying AS plan,
    COALESCE(rr.id_reclamo_prestacional, 0) AS id_reclamo_prestacional,
    o.iva,
    o.iibb,
    o.alta_fecha,
    o.alta_usr,
    o.modi_fecha,
    o.modi_usr,
    o.version_edicion
   FROM compras.requerimiento_orden_compra o
     JOIN compras.requerimiento r ON r.id_requerimiento = o.id_requerimiento
     LEFT JOIN compras.requerimiento_presupuesto rp ON rp.id_requerimiento_presupuesto = o.id_presupuesto AND rp.id_requerimiento = o.id_requerimiento
     LEFT JOIN prestador p ON p.id_prestador = rp.id_prestador
     LEFT JOIN compras.requerimiento_reclamo_prestacional rr ON rr.id_requerimiento = o.id_requerimiento
     LEFT JOIN LATERAL ( SELECT
                CASE
                    WHEN count(DISTINCT requerimiento_presupuesto.fecha_documento) = 1 THEN min(requerimiento_presupuesto.fecha_documento)
                    ELSE NULL::date
                END AS fecha
           FROM compras.requerimiento_presupuesto
          WHERE requerimiento_presupuesto.id_requerimiento = o.id_requerimiento AND requerimiento_presupuesto.tipo_documento = 2 AND requerimiento_presupuesto.baja_fecha IS NULL) om ON true;$occreate$;
 ALTER TABLE ONLY compras.requerimiento_presupuesto DROP COLUMN descripcion_prestador RESTRICT;

 SELECT array_agg(attname||':'||format_type(atttypid,atttypmod) ORDER BY attnum) INTO v_shape
 FROM pg_attribute WHERE attrelid='compras.requerimiento_presupuesto_consulta'::regclass AND attnum>0 AND NOT attisdropped;
 IF md5(replace(pg_get_viewdef('compras.requerimiento_presupuesto_consulta'::regclass,true),E'\r\n',E'\n')) IS DISTINCT FROM '099173d3ffea37f61109578b54c06a77' OR md5(replace(pg_get_viewdef(v_oc_oid,true),E'\r\n',E'\n')) IS DISTINCT FROM '2d4abcf36abcd2f30cc4d5276b352123' THEN RAISE EXCEPTION 'DESC_PRESTADOR_POST_VIEW_CANON_DRIFT'; END IF;
 IF v_shape<>ARRAY['id_requerimiento_presupuesto:integer','id_requerimiento:integer','tipo_documento:smallint','fecha_documento:date','numero_receta:character varying(100)','id_prestador:integer','empresa_cuit:character varying(11)','empresa_sucursal:character varying(6)','descripcion_empresa:character varying(200)','dl_group_id:bigint','dl_folder_id:bigint','dl_file_entry_id:bigint','dl_file_uuid:character varying(75)','nombre_original:character varying(255)','nombre_persistido:character varying(255)','titulo:character varying(240)','descripcion_prestador:character varying(500)','alta_fecha:timestamp without time zone','alta_usr:character varying(100)','baja_fecha:timestamp without time zone','baja_usr:character varying(100)','empresa_adjudicada:boolean']::text[] OR NOT EXISTS(SELECT 1 FROM pg_class c JOIN pg_type t ON t.oid=c.reltype
 WHERE c.oid='compras.requerimiento_presupuesto_consulta'::regclass AND c.relkind='v'
 AND c.relowner=v_owner AND c.relacl IS NULL AND c.reloptions IS NULL AND t.typowner=v_owner AND t.typacl IS NULL) THEN
 RAISE EXCEPTION 'DESC_PRESTADOR_QUERY_SHAPE_OWNER_ACL_CHANGED'; END IF;
 SELECT jsonb_agg(jsonb_build_object('oid',p.oid,'owner',p.proowner,'acl',p.proacl::text,'config',p.proconfig,'volatile',p.provolatile,'strict',p.proisstrict,'leak',p.proleakproof,'parallel',p.proparallel,'cost',p.procost,'rows',p.prorows,'security',p.prosecdef) ORDER BY p.oid) INTO v_rest_after FROM pg_proc p WHERE p.pronamespace='compras'::regnamespace AND (p.proname,p.pronargs) NOT IN(('registrar_requerimiento_orden_medica',11),('registrar_requerimiento_presupuesto',11),('registrar_requerimiento_presupuesto',15),('get_documento_requerimiento',3),('listar_documentos_requerimiento',2),('listar_ordenes_medicas_requerimiento',1),('listar_presupuestos_prestador',2));
 SELECT jsonb_agg(jsonb_build_object('oid',t.oid,'function',t.tgfoid,'enabled',t.tgenabled,'definition',pg_get_triggerdef(t.oid)) ORDER BY t.oid) INTO v_trg_after FROM pg_trigger t JOIN pg_class c ON c.oid=t.tgrelid JOIN pg_namespace n ON n.oid=c.relnamespace JOIN pg_proc p ON p.oid=t.tgfoid JOIN pg_namespace pn ON pn.oid=p.pronamespace WHERE NOT t.tgisinternal AND(n.nspname='compras'OR pn.nspname='compras');
 IF v_rest_before IS DISTINCT FROM v_rest_after OR v_trg_before IS DISTINCT FROM v_trg_after THEN
 RAISE EXCEPTION 'DESC_PRESTADOR_OTHER97_ATTRIBUTES_OR5TRIGGERS_CHANGED'; END IF;
 SELECT count(*),md5(coalesce(string_agg(md5((to_jsonb(rp)-'descripcion_prestador')::text),''ORDER BY md5((to_jsonb(rp)-'descripcion_prestador')::text)),''))
 INTO v_rows_after,v_after FROM compras.requerimiento_presupuesto rp;
 IF v_rows_after<>v_rows OR v_after IS DISTINCT FROM v_before THEN RAISE EXCEPTION 'DESC_PRESTADOR_OTHER21_FIELDS_CHANGED'; END IF;
 SELECT jsonb_agg(jsonb_build_object('name',p.proname,'types',oidvectortypes(p.proargtypes),'owner',pg_get_userbyid(p.proowner),'acl',p.proacl::text,'config',p.proconfig,'volatile',p.provolatile,'strict',p.proisstrict,'leak',p.proleakproof,'parallel',p.proparallel,'cost',p.procost,'rows',p.prorows,'security',p.prosecdef,'names',p.proargnames,'modes',p.proargmodes,'allargs',p.proallargtypes,'defaults',p.proargdefaults::text,'comment',obj_description(p.oid,'pg_proc')) ORDER BY p.proname) INTO v_meta_after FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace WHERE n.nspname='compras' AND p.proname IN('get_documento_requerimiento','listar_documentos_requerimiento','listar_ordenes_medicas_requerimiento','listar_presupuestos_prestador');
 IF v_meta_after IS DISTINCT FROM v_meta THEN RAISE EXCEPTION 'DESC_PRESTADOR_READER_PERMISSIONS_ATTRIBUTES_CHANGED'; END IF;
 IF NOT EXISTS(SELECT 1 FROM pg_class WHERE oid=v_oc_oid AND relowner=v_oc_owner AND relacl IS NOT DISTINCT FROM v_oc_acl AND reloptions IS NOT DISTINCT FROM v_oc_options) THEN
  RAISE EXCEPTION 'DESC_PRESTADOR_OC_PERMISSIONS_CHANGED'; END IF;
 IF (SELECT count(*) FROM pg_proc WHERE pronamespace='compras'::regnamespace)<>104 THEN RAISE EXCEPTION 'DESC_PRESTADOR_POST_FUNCTION_COUNT'; END IF;
 WITH expected(name,nargs,legacy,normal,reverted)AS(VALUES
        ('agregar_gastos_envio_requerimiento',0,'ef9951e6eac71d8301599030962c816e','ef9951e6eac71d8301599030962c816e','ef9951e6eac71d8301599030962c816e'),
        ('anular_requerimiento',3,'842f786d787a3f52519a2f24c34137e2','842f786d787a3f52519a2f24c34137e2','842f786d787a3f52519a2f24c34137e2'),
        ('baja_cotizacion_empresa_requerimiento',3,'35bb9c98f9d914268cc47ea8117c37e4','35bb9c98f9d914268cc47ea8117c37e4','35bb9c98f9d914268cc47ea8117c37e4'),
        ('baja_requerimiento_presupuesto',3,'de0be822f136f7222c1d8c5c8ad19ef7','de0be822f136f7222c1d8c5c8ad19ef7','de0be822f136f7222c1d8c5c8ad19ef7'),
        ('baja_vinculo_reclamo_prestacional',2,'a05aa540deefc8d2a5a93f042b2ca102','a05aa540deefc8d2a5a93f042b2ca102','a05aa540deefc8d2a5a93f042b2ca102'),
        ('baja_vinculo_reclamo_prestacional_trigger',0,'a33c36ade63ab98bceba282309bf283f','a33c36ade63ab98bceba282309bf283f','a33c36ade63ab98bceba282309bf283f'),
        ('bloquear_creacion_orden_compra',2,'c59938a071c4186fb7802dd2536d5130','c59938a071c4186fb7802dd2536d5130','c59938a071c4186fb7802dd2536d5130'),
        ('bloquear_guardado_comparativa',2,'1e5253ce2a4317576a7d5137f9ad9a6a','1e5253ce2a4317576a7d5137f9ad9a6a','1e5253ce2a4317576a7d5137f9ad9a6a'),
        ('bloquear_requerimiento_reclamo_prestacional',1,'fb30b73d6978cd123703b450faa00971','fb30b73d6978cd123703b450faa00971','fb30b73d6978cd123703b450faa00971'),
        ('borrar_requerimiento_detalle',2,'fb4ea6e9004132109c7b0edaae6526dc','fb4ea6e9004132109c7b0edaae6526dc','fb4ea6e9004132109c7b0edaae6526dc'),
        ('buscar_empresas_cotizacion',4,'e0a0e0aa1636f291fc324ff932a74366','e0a0e0aa1636f291fc324ff932a74366','e0a0e0aa1636f291fc324ff932a74366'),
        ('buscar_empresas_cotizacion_rapida',4,'bb70bccf323e3e15247c80f447ead43c','bb70bccf323e3e15247c80f447ead43c','bb70bccf323e3e15247c80f447ead43c'),
        ('buscar_items_historicos_afiliado',5,'ad5522fc7e052cc67072c49f4d68a3f9','ad5522fc7e052cc67072c49f4d68a3f9','ad5522fc7e052cc67072c49f4d68a3f9'),
        ('buscar_items_historicos_afiliado_clasificado',5,'ce9f3b7a1d50fa1dfd14d1822d704a12','ce9f3b7a1d50fa1dfd14d1822d704a12','ce9f3b7a1d50fa1dfd14d1822d704a12'),
        ('buscar_nomencladores',5,'bee2c043de692312a2e0f00fd1ebe52f','bee2c043de692312a2e0f00fd1ebe52f','bee2c043de692312a2e0f00fd1ebe52f'),
        ('buscar_prestadores_enviados',3,'f138c05cafba45dcd6cfb5890c3e907a','f138c05cafba45dcd6cfb5890c3e907a','f138c05cafba45dcd6cfb5890c3e907a'),
        ('buscar_requerimientos',8,'f8e134684474fc1c7123fbb7e6296bb9','f8e134684474fc1c7123fbb7e6296bb9','f8e134684474fc1c7123fbb7e6296bb9'),
        ('buscar_requerimientos',10,'a095c8c9c30fda68d993a9f2091b41b6','a095c8c9c30fda68d993a9f2091b41b6','a095c8c9c30fda68d993a9f2091b41b6'),
        ('buscar_requerimientos',11,'eb1de5b9f53d23d5c1e7725706774019','eb1de5b9f53d23d5c1e7725706774019','eb1de5b9f53d23d5c1e7725706774019'),
        ('calcular_total_detalle_fila',0,'757c0cf62a2b28b0782fdfad56e49d1a','757c0cf62a2b28b0782fdfad56e49d1a','757c0cf62a2b28b0782fdfad56e49d1a'),
        ('cambiar_estado_autorizacion_requerimiento',3,'1ab171219a04755df951722afef718cf','1ab171219a04755df951722afef718cf','1ab171219a04755df951722afef718cf'),
        ('cambiar_estado_requerimiento',3,'053d77b93a30c80250ddaaa32ee40375','053d77b93a30c80250ddaaa32ee40375','053d77b93a30c80250ddaaa32ee40375'),
        ('completar_baja_requerimiento_fila',0,'f9c7d6751c6580ffce70bbb448a15def','f9c7d6751c6580ffce70bbb448a15def','f9c7d6751c6580ffce70bbb448a15def'),
        ('completar_empresa_cotizacion',5,'508f9b051df3a69964cead8eeafcfc97','508f9b051df3a69964cead8eeafcfc97','508f9b051df3a69964cead8eeafcfc97'),
        ('confirmar_envio_a_cotizar',2,'756448bd5d5c0301a5312ad3c652cf00','756448bd5d5c0301a5312ad3c652cf00','756448bd5d5c0301a5312ad3c652cf00'),
        ('confirmar_orden_compra_requerimiento',2,'13b57466874bc1fcfa9b7e6ddcc15615','13b57466874bc1fcfa9b7e6ddcc15615','13b57466874bc1fcfa9b7e6ddcc15615'),
        ('corregir_item_requerimiento',7,'5985c33f228abe9742bfd44e4b2bb308','5985c33f228abe9742bfd44e4b2bb308','5985c33f228abe9742bfd44e4b2bb308'),
        ('corregir_item_requerimiento',9,'b1d6364f89c221819c283b3f07df8161','b1d6364f89c221819c283b3f07df8161','b1d6364f89c221819c283b3f07df8161'),
        ('diagnosticar_prestadores_notificacion_cotizacion',1,'dc616bff8d95f7d33cf8d93fc9ae4912','dc616bff8d95f7d33cf8d93fc9ae4912','dc616bff8d95f7d33cf8d93fc9ae4912'),
        ('editar_empresa_cotizacion',6,'bf9e3d98a5a6b1680ebe290c307783b5','bf9e3d98a5a6b1680ebe290c307783b5','bf9e3d98a5a6b1680ebe290c307783b5'),
        ('es_prestador_compatible_cotizacion',2,'7f43af5b4c6d0f2b950013d8a11f1357','7f43af5b4c6d0f2b950013d8a11f1357','7f43af5b4c6d0f2b950013d8a11f1357'),
        ('es_requerimiento_habilitado_busqueda_empresa_cotizacion',1,'76b9317ba900b6b6f049aa60a3fadada','76b9317ba900b6b6f049aa60a3fadada','76b9317ba900b6b6f049aa60a3fadada'),
        ('es_sector_seleccionable_compras',1,'73392735e21a026359a70aa3792fef8c','73392735e21a026359a70aa3792fef8c','73392735e21a026359a70aa3792fef8c'),
        ('estado_requerimiento_descripcion',1,'2b33274500388fe0ff7f17fa66412000','2b33274500388fe0ff7f17fa66412000','2b33274500388fe0ff7f17fa66412000'),
        ('existe_requerimiento_duplicado',5,'d4007e470fac05388185ddbbaaeba744','d4007e470fac05388185ddbbaaeba744','d4007e470fac05388185ddbbaaeba744'),
        ('finalizar_cotizacion_prestador',4,'ed68fd49233d909972cb03537328143d','ed68fd49233d909972cb03537328143d','ed68fd49233d909972cb03537328143d'),
        ('finalizar_cotizacion_requerimiento',5,'576ce89ef73a78e4782f41070ee8d28d','576ce89ef73a78e4782f41070ee8d28d','576ce89ef73a78e4782f41070ee8d28d'),
        ('finalizar_notificacion_cotizacion_prestador',5,'0938df17a62e92549b7119adc4fd8f17','0938df17a62e92549b7119adc4fd8f17','0938df17a62e92549b7119adc4fd8f17'),
        ('finalizar_reclamo_prestacional',4,'85d827059d69f0b775a54a08f2c0dd8b','85d827059d69f0b775a54a08f2c0dd8b','85d827059d69f0b775a54a08f2c0dd8b'),
        ('get_documento_requerimiento',3,'69b0eb9994fa4bed0fcd50b6a5c8fca6','077f036691e20e7daf4ba9d41234490d','077f036691e20e7daf4ba9d41234490d'),
        ('get_estado_requerimiento',1,'39f5b83a8f978dee8ae399c59882390c','39f5b83a8f978dee8ae399c59882390c','39f5b83a8f978dee8ae399c59882390c'),
        ('get_estado_requerimiento_for_update',1,'30c8bae30ee864c25de6c72abb20312f','30c8bae30ee864c25de6c72abb20312f','30c8bae30ee864c25de6c72abb20312f'),
        ('get_pedido_cotizacion_prestador',2,'6817f2f7a3e8b2b8eddcbcafb2c72f3c','6817f2f7a3e8b2b8eddcbcafb2c72f3c','6817f2f7a3e8b2b8eddcbcafb2c72f3c'),
        ('get_requerimiento',1,'5782a86f969d9233c0f0811eba6463e4','5782a86f969d9233c0f0811eba6463e4','5782a86f969d9233c0f0811eba6463e4'),
        ('get_requerimiento_compra_pdf',1,'767b95efebd6ea379aa0369720426d81','767b95efebd6ea379aa0369720426d81','767b95efebd6ea379aa0369720426d81'),
        ('get_requerimiento_detalle',1,'1700fb0ddf9e83b093ef25c9af2869f7','1700fb0ddf9e83b093ef25c9af2869f7','1700fb0ddf9e83b093ef25c9af2869f7'),
        ('get_requerimiento_detalle_clasificado',1,'784de20c78313b6cfee8cee272103629','784de20c78313b6cfee8cee272103629','784de20c78313b6cfee8cee272103629'),
        ('get_requerimiento_reclamo_prestacional',1,'158603697ae6d70e22525e4f58147606','158603697ae6d70e22525e4f58147606','158603697ae6d70e22525e4f58147606'),
        ('get_sector_requerimiento',1,'58a080205d3b16780933a465be876561','58a080205d3b16780933a465be876561','58a080205d3b16780933a465be876561'),
        ('guardar_cotizacion_requerimiento',7,'e18fdfba2a125ca50d2768bd2da3165a','e18fdfba2a125ca50d2768bd2da3165a','e18fdfba2a125ca50d2768bd2da3165a'),
        ('guardar_cotizacion_requerimiento_call',7,'bf30a550abd3c123e87ba3123538c224','bf30a550abd3c123e87ba3123538c224','bf30a550abd3c123e87ba3123538c224'),
        ('guardar_empresa_adjudicada',3,'440dd79e03cf4d86215eb7f1e29bcbea','440dd79e03cf4d86215eb7f1e29bcbea','440dd79e03cf4d86215eb7f1e29bcbea'),
        ('guardar_empresa_adjudicada',4,'a341e452cca787734788f3a5dc2b376d','a341e452cca787734788f3a5dc2b376d','a341e452cca787734788f3a5dc2b376d'),
        ('guardar_observacion_interna_requerimiento',3,'c3989b8d103ffd6429134349501ea519','c3989b8d103ffd6429134349501ea519','c3989b8d103ffd6429134349501ea519'),
        ('guardar_requerimiento',23,'a1d7abe55a1c5e445cfdade7f33043c5','a1d7abe55a1c5e445cfdade7f33043c5','a1d7abe55a1c5e445cfdade7f33043c5'),
        ('guardar_requerimiento',24,'3534f0f9fd37b28c12fc5dc7ce9a627d','3534f0f9fd37b28c12fc5dc7ce9a627d','3534f0f9fd37b28c12fc5dc7ce9a627d'),
        ('guardar_requerimiento_detalle',13,'6c3444b2fec8410fcd371be1dbba252d','6c3444b2fec8410fcd371be1dbba252d','6c3444b2fec8410fcd371be1dbba252d'),
        ('guardar_requerimiento_detalle',14,'a7a3160273e597cc3da239f3ed42073b','a7a3160273e597cc3da239f3ed42073b','a7a3160273e597cc3da239f3ed42073b'),
        ('guardar_requerimiento_detalle_clasificado',14,'8410268a0168bd694185364759d50da0','8410268a0168bd694185364759d50da0','8410268a0168bd694185364759d50da0'),
        ('hay_prestadores_pendientes_notificacion',1,'31ab2b64e22a0431a69f4a19ab51f4f3','31ab2b64e22a0431a69f4a19ab51f4f3','31ab2b64e22a0431a69f4a19ab51f4f3'),
        ('liberar_copia_cotizacion_requerimiento',1,'d2c7c4dd663b4d3dd5efc9d3eacfb5b3','d2c7c4dd663b4d3dd5efc9d3eacfb5b3','d2c7c4dd663b4d3dd5efc9d3eacfb5b3'),
        ('liberar_reserva_reclamo_prestacional',3,'287e6c51e779e3fef5111a9027e8ada5','287e6c51e779e3fef5111a9027e8ada5','287e6c51e779e3fef5111a9027e8ada5'),
        ('listar_configuracion_correos_rubro',1,'0b7e9085c82040280f19ccc5982a3e4d','0b7e9085c82040280f19ccc5982a3e4d','0b7e9085c82040280f19ccc5982a3e4d'),
        ('listar_documentos_requerimiento',2,'aec6597f9f2c09f12920768d9eb18d18','94f0d196894330a6058421fe8e801862','94f0d196894330a6058421fe8e801862'),
        ('listar_estados_requerimiento',0,'250c9b89009d3ecc7a2bb452eec99dca','250c9b89009d3ecc7a2bb452eec99dca','250c9b89009d3ecc7a2bb452eec99dca'),
        ('listar_ordenes_medicas_requerimiento',1,'eaebb5c40b157bc603b0d7ce6d96f27b','7cc9c2a15760094f900325291e1db922','7cc9c2a15760094f900325291e1db922'),
        ('listar_prestadores_adjudicados',1,'3f67d9087e9f66a0f6daed34c1f31605','3f67d9087e9f66a0f6daed34c1f31605','3f67d9087e9f66a0f6daed34c1f31605'),
        ('listar_prestadores_adjudicados_batch',1,'86c526157c1a4e48d682e3dae2c08a84','86c526157c1a4e48d682e3dae2c08a84','86c526157c1a4e48d682e3dae2c08a84'),
        ('listar_prestadores_cotizacion_requerimiento',1,'a52a168e5040fe53f0855bf41c49543d','a52a168e5040fe53f0855bf41c49543d','a52a168e5040fe53f0855bf41c49543d'),
        ('listar_prestadores_enviados',2,'54b79e68e1fe4444526bf4732d03a4ab','54b79e68e1fe4444526bf4732d03a4ab','54b79e68e1fe4444526bf4732d03a4ab'),
        ('listar_prestadores_notificacion_cotizacion',1,'af825f3ce9c350f358d7ff966cba2b14','af825f3ce9c350f358d7ff966cba2b14','af825f3ce9c350f358d7ff966cba2b14'),
        ('listar_presupuestos_prestador',2,'226ec5ff5752efc1edf39f524007df1b','41803dac550e05f7e5f3bc4cb447f0ae','41803dac550e05f7e5f3bc4cb447f0ae'),
        ('listar_relaciones_reclamo_prestacional_batch',2,'25ed540a2718030f892256bfb31823b7','25ed540a2718030f892256bfb31823b7','25ed540a2718030f892256bfb31823b7'),
        ('listar_relaciones_reclamo_prestacional_por_reclamo',2,'e4b22bdd529f2b19db349609aa23b851','e4b22bdd529f2b19db349609aa23b851','e4b22bdd529f2b19db349609aa23b851'),
        ('listar_sectores_requerimiento',0,'5bfa14c17b9ca95020b6563548d96a8a','5bfa14c17b9ca95020b6563548d96a8a','5bfa14c17b9ca95020b6563548d96a8a'),
        ('listar_tipos_prestacion',0,'be6abb44ae5db1da36ca324cec542903','be6abb44ae5db1da36ca324cec542903','be6abb44ae5db1da36ca324cec542903'),
        ('marcar_error_reclamo_prestacional',5,'a81847272e179c38432ad15f89a6594d','a81847272e179c38432ad15f89a6594d','a81847272e179c38432ad15f89a6594d'),
        ('normalizar_rubro',1,'d5e98bfb3b62ae409f90e54e31d08d6f','d5e98bfb3b62ae409f90e54e31d08d6f','d5e98bfb3b62ae409f90e54e31d08d6f'),
        ('normalizar_sector',1,'281ed4af9cfbe1dba6ae9ae72b487256','281ed4af9cfbe1dba6ae9ae72b487256','281ed4af9cfbe1dba6ae9ae72b487256'),
        ('normalizar_usuario',1,'9f5395581d8c537874902cc27ed46bbd','9f5395581d8c537874902cc27ed46bbd','9f5395581d8c537874902cc27ed46bbd'),
        ('puede_anular_requerimiento',1,'695c34cbb8dedfbd002f628a9df1f6c3','695c34cbb8dedfbd002f628a9df1f6c3','695c34cbb8dedfbd002f628a9df1f6c3'),
        ('reactivar_cotizacion_empresa_requerimiento',2,'6f0856ee10af171578ec9fb53f509fac','6f0856ee10af171578ec9fb53f509fac','6f0856ee10af171578ec9fb53f509fac'),
        ('reactivar_requerimiento_presupuesto',2,'4b8b89f6f40d8cf9099a4e50fa14ccff','4b8b89f6f40d8cf9099a4e50fa14ccff','4b8b89f6f40d8cf9099a4e50fa14ccff'),
        ('registrar_cotizacion_prestador',3,'9dbf33dfcff38b7a544ef9ec450090ca','9dbf33dfcff38b7a544ef9ec450090ca','9dbf33dfcff38b7a544ef9ec450090ca'),
        ('registrar_pedido_cotizacion_documento',10,'ece745d0753e82b23e0e9af92cda5e80','ece745d0753e82b23e0e9af92cda5e80','ece745d0753e82b23e0e9af92cda5e80'),
        ('registrar_requerimiento_orden_medica',11,'9b906b67c31c9dd709592c094db9023d','6b8af2e9db66c649982f895e21296bde','9b906b67c31c9dd709592c094db9023d'),
        ('registrar_requerimiento_presupuesto',11,'865541ee92400fbbd6a5287ae687dbf1','a87961808ad3af29376c6be0dd4d2710','865541ee92400fbbd6a5287ae687dbf1'),
        ('registrar_requerimiento_presupuesto',15,'51657135fb04476a2a8d9fc8d3b311fb','e4fad7c8790ddc14b99c369cab5f697e','51657135fb04476a2a8d9fc8d3b311fb'),
        ('requerimiento_base',0,'e2e8b3b2220471368688ca029772b360','e2e8b3b2220471368688ca029772b360','e2e8b3b2220471368688ca029772b360'),
        ('reservar_copia_cotizacion_requerimiento',1,'b92ec038c530eee6742bceabf7041913','b92ec038c530eee6742bceabf7041913','b92ec038c530eee6742bceabf7041913'),
        ('reservar_notificacion_cotizacion_prestador',3,'0920a72525c6760f2b6a50027979dd07','0920a72525c6760f2b6a50027979dd07','0920a72525c6760f2b6a50027979dd07'),
        ('reservar_reclamo_prestacional',3,'1fd9d9739ae15ad285f5ab326e405366','1fd9d9739ae15ad285f5ab326e405366','1fd9d9739ae15ad285f5ab326e405366'),
        ('resolver_email_cotizacion_prestador',1,'279d952a28dd67cb35efd211aa3e918f','279d952a28dd67cb35efd211aa3e918f','279d952a28dd67cb35efd211aa3e918f'),
        ('resolver_emails_cotizacion_prestador',1,'a2550cd264666ed0cfdf593d38807fd6','a2550cd264666ed0cfdf593d38807fd6','a2550cd264666ed0cfdf593d38807fd6'),
        ('resolver_sector_nomenclador',2,'c365367fa56bfbbd71229c0838016e4f','c365367fa56bfbbd71229c0838016e4f','c365367fa56bfbbd71229c0838016e4f'),
        ('tiene_situacion_medica_vigente',2,'3c6bdf4fe700eab54af7634ab2e95294','3c6bdf4fe700eab54af7634ab2e95294','3c6bdf4fe700eab54af7634ab2e95294'),
        ('trae_tipos_prestacion',0,'b39a6bd78b43780531bff9071ae2765a','b39a6bd78b43780531bff9071ae2765a','b39a6bd78b43780531bff9071ae2765a'),
        ('validar_codigo_prestacion_detalle_fila',0,'ecd4d086066dc445e06d1e8152d57966','ecd4d086066dc445e06d1e8152d57966','ecd4d086066dc445e06d1e8152d57966'),
        ('validar_nomenclador_configurado',4,'2ada9393c89315cc86b02d83ac7efdf0','2ada9393c89315cc86b02d83ac7efdf0','2ada9393c89315cc86b02d83ac7efdf0'),
        ('validar_prestador_cotizacion_fila',0,'f6dad299d043abfc32d1957fed4a933d','f6dad299d043abfc32d1957fed4a933d','f6dad299d043abfc32d1957fed4a933d'),
        ('validar_requerimiento_detalle_fila',0,'bf28f6bca88baf59fc899be4c2e82635','bf28f6bca88baf59fc899be4c2e82635','bf28f6bca88baf59fc899be4c2e82635'),
        ('validar_requerimiento_fila',0,'aa053cab087a1a0852f340775bd09caa','aa053cab087a1a0852f340775bd09caa','aa053cab087a1a0852f340775bd09caa'),
        ('validar_tipo_prestacion_detalle_fila',0,'5b5611c7997f745cffe707493efb188b','5b5611c7997f745cffe707493efb188b','5b5611c7997f745cffe707493efb188b'),
        ('validar_tipo_prestacion_detalle_nuevo',0,'2d898521c5e3924ec61a431c1bbeb775','2d898521c5e3924ec61a431c1bbeb775','2d898521c5e3924ec61a431c1bbeb775')
 )SELECT count(*) INTO v_count FROM expected e JOIN pg_proc p ON p.pronamespace='compras'::regnamespace AND p.proname=e.name AND p.pronargs=e.nargs
 WHERE md5(replace(pg_get_functiondef(p.oid),E'\r\n',E'\n'))=e.normal;
 IF v_count<>104 THEN RAISE EXCEPTION 'DESC_PRESTADOR_POST_FULL104_MANIFEST'; END IF;
END;
$normalizar_desc_prestador$;
