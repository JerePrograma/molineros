# Compras: Para Autorizar y Autorizado

Los nuevos estados son `6 = PARA AUTORIZAR` y `7 = AUTORIZADO`.
`4 = RECLAMO (RP)` conserva su identidad y sus vinculos existentes.

La migracion es `20261013_autorizacion_requerimiento.sql`. Debe instalarse antes
de publicar el Java/JSP que usa el nuevo flujo. Crear y asignar en el portal el
rol `AUTORIZAR_Compras` para las personas que pueden confirmar la autorizacion.
El autorizador debe conservar el permiso de consulta existente, por ejemplo
"VIEW_Compras", ademas de "AUTORIZAR_Compras". El permiso nuevo solo habilita
la operacion de aprobar; no concede acceso a edicion o cotizacion.
Los permisos se controlan en el servidor; la funcion SQL no recibe roles.

## Persistencia

La funcion `compras.cambiar_estado_autorizacion_requerimiento(INTEGER, INTEGER,
VARCHAR)` permite exclusivamente `4 -> 6` y `6 -> 7`. Bloquea el requerimiento,
el vinculo y el RP con `FOR UPDATE`, exige un requerimiento activo, un vinculo
`VINCULADO` y un RP real activo. Actualiza solamente estado, fecha y usuario de
modificacion. Un segundo envio con el estado ya cambiado se rechaza.

La migracion actualiza las descripciones y el listado de estados. Conserva la
funcion generica `compras.cambiar_estado_requerimiento()` y no instala nuevos
triggers o CHECK. Si hay un CHECK de estados conocido, lo amplia; si existe la
funcion `validar_requerimiento_fila()`, adapta solo sus bloques de autorizacion,
conservando el resto de su cuerpo. Una variante no reconocida aborta toda la
transaccion. El schema y la funcion de validacion conservan los datos en estados
4, 6 y 7; en 4 y 6 solo admiten la transicion prevista y sus datos de auditoria.
La inmutabilidad mediante trigger requiere que este ya este instalado; la
migracion no modifica esa configuracion.

## Evidencia y validacion pendiente

El 13/10/2026 se consulto el catalogo QA configurado, `devmolineros` con
PostgreSQL 9.6.15, en una conexion de solo lectura. No habia datos en estados
6 o 7, CHECK de estado ni trigger asociado a `validar_requerimiento_fila()`.
Ese relevamiento no demuestra el estado de otro ambiente.

La adaptacion de los tres fragmentos coincide exactamente con la definicion
capturada. Los archivos se guardaron como ISO-8859-1 sin BOM y sin cambios de
codificacion del resto del schema. No se aplico esta migracion en QA ni se
probo persistencia real en PostgreSQL 9.6. Las pruebas del flujo autenticado,
los permisos, el doble envio y la concurrencia permanecen pendientes.

Antes de instalar en otro ambiente, guardar sus definiciones actuales y revisar
sus constraints y triggers. Si se necesita revertir despues de usar los estados
6 o 7, resolver primero esos requerimientos de forma funcional y autorizada;
no renumerar estados ni restaurar cuerpos SQL de otro ambiente automaticamente.
